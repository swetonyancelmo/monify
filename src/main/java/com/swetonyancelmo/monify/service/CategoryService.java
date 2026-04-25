package com.swetonyancelmo.monify.service;

import com.swetonyancelmo.monify.domain.categories.Category;
import com.swetonyancelmo.monify.domain.categories.CategoryResponseDto;
import com.swetonyancelmo.monify.domain.categories.CreateCategoryDto;
import com.swetonyancelmo.monify.domain.categories.UpdateCategoryDto;
import com.swetonyancelmo.monify.domain.users.User;
import com.swetonyancelmo.monify.exception.BusinessException;
import com.swetonyancelmo.monify.exception.ResourceNotFoundException;
import com.swetonyancelmo.monify.repository.CategoryRepository;
import com.swetonyancelmo.monify.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CategoryService {

    @Autowired private CategoryRepository categoryRepository;
    @Autowired private UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<CategoryResponseDto> findAllCategories() {
       return categoryRepository.findAll().stream()
               .map(c -> new CategoryResponseDto(c.getId(), c.getName(), c.getType(), c.getUser().getId()))
               .collect(Collectors.toList());
    }

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

    @Transactional
    public void deleteCategory(UUID id, UUID userId) {
        Category category = categoryRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada ou não existente com ID: "+ id));

        categoryRepository.delete(category);
    }

}
