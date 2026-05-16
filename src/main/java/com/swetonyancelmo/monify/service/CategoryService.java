package com.swetonyancelmo.monify.service;

import com.swetonyancelmo.monify.config.JWTUserData;
import com.swetonyancelmo.monify.domain.Category;
import com.swetonyancelmo.monify.domain.User;
import com.swetonyancelmo.monify.dto.request.CreateCategoryDto;
import com.swetonyancelmo.monify.dto.request.UpdateCategoryDto;
import com.swetonyancelmo.monify.dto.response.CategoryResponseDto;
import com.swetonyancelmo.monify.exception.BusinessException;
import com.swetonyancelmo.monify.exception.ResourceNotFoundException;
import com.swetonyancelmo.monify.repository.CategoryRepository;
import com.swetonyancelmo.monify.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    /**
     * Retorna TODAS as categorias do usuário autenticado.
     * Garante que apenas categorias pertencentes ao usuário sejam retornadas.
     *
     * @return {@link List} de {@link CategoryResponseDto} com as categorias do usuário
     */
    @Transactional(readOnly = true)
    public Page<CategoryResponseDto> findAllCategories(Pageable pageable) {
        JWTUserData userData = (JWTUserData) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return categoryRepository.findByUserId(userData.userId(), pageable)
                .map(c -> new CategoryResponseDto(c.getId(), c.getName(), c.getType(), c.getUser().getId()));
    }

    /**
     * Cria uma nova categoria validando propriedade do usuário e unicidade do nome.
     *
     * <p><b>Processo:</b></p>
     * <ol>
     *   <li>Busca o usuário pelo email fornecido</li>
     *   <li>Verifica se já existe categoria com mesmo nome para este usuário</li>
     *   <li>Cria e persiste a categoria</li>
     * </ol>
     *
     * @param dto Dados da categoria (nome, tipo)
     * @param userEmail Email do usuário autenticado
     * @return {@link CategoryResponseDto} com dados da categoria criada
     * @throws ResourceNotFoundException se usuário não existir
     * @throws BusinessException se categoria com mesmo nome já existe para este usuário
     */
    @Transactional
    public CategoryResponseDto createCategory(CreateCategoryDto dto, String userEmail) {
        User userData = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário com o email " + userEmail + " não encontrado"));

        if (categoryRepository.existsByNameAndUserId(dto.name(), userData.getId())) {
            throw new BusinessException("Você já possui uma categoria com o nome '" + dto.name() + "'");
        }

        Category category = new Category();
        category.setName(dto.name());
        category.setType(dto.type());
        category.setUser(userData);

        Category categorySaved = categoryRepository.save(category);

        return new CategoryResponseDto(
                categorySaved.getId(),
                categorySaved.getName(),
                categorySaved.getType(),
                categorySaved.getUser().getId()
        );
    }

    /**
     * Atualiza uma categoria verificando propriedade do usuário.
     *
     * <p><b>Processo:</b></p>
     * <ol>
     *   <li>Busca a categoria pelo ID e ID do usuário (garante propriedade)</li>
     *   <li>Atualiza nome se fornecido e válido</li>
     *   <li>Atualiza tipo se fornecido</li>
     *   <li>Persiste as alterações</li>
     * </ol>
     *
     * @param dto Novos dados da categoria (nome e/ou tipo)
     * @param id ID da categoria a atualizar
     * @param userId ID do usuário autenticado
     * @return {@link CategoryResponseDto} com dados atualizados
     * @throws ResourceNotFoundException se categoria não pertencer ao usuário
     */
    @Transactional
    public CategoryResponseDto updateCategory(UpdateCategoryDto dto, UUID id, UUID userId) {
        Category category = categoryRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada ou não existente com ID: "+ id));

        if (dto.name() != null && !dto.name().isEmpty()) {
            category.setName(dto.name());
        }

        if (dto.type() != null) {
            category.setType(dto.type());
        }

        Category categoryUpdated = categoryRepository.save(category);

        return new CategoryResponseDto(categoryUpdated.getId(), categoryUpdated.getName(), categoryUpdated.getType(), categoryUpdated.getUser().getId());
    }

    /**
     * Deleta uma categoria do usuário autenticado.
     *
     * <p><b>Processo:</b></p>
     * <ol>
     *   <li>Busca a categoria pelo ID e ID do usuário (garante propriedade)</li>
     *   <li>Remove a categoria do banco de dados</li>
     * </ol>
     *
     * @param id ID da categoria a deletar
     * @param userId ID do usuário autenticado
     * @throws ResourceNotFoundException se categoria não pertencer ao usuário
     */
    @Transactional
    public void deleteCategory(UUID id, UUID userId) {
        Category category = categoryRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada ou não existente com ID: "+ id));

        categoryRepository.delete(category);
    }

}
