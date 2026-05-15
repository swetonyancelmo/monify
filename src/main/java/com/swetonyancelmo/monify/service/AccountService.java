package com.swetonyancelmo.monify.service;

import com.swetonyancelmo.monify.config.JWTUserData;
import com.swetonyancelmo.monify.domain.Account;
import com.swetonyancelmo.monify.domain.User;
import com.swetonyancelmo.monify.dto.request.CreateAccountDto;
import com.swetonyancelmo.monify.dto.request.UpdateAccountDto;
import com.swetonyancelmo.monify.dto.response.AccountResponseDto;
import com.swetonyancelmo.monify.exception.BusinessException;
import com.swetonyancelmo.monify.exception.ResourceNotFoundException;
import com.swetonyancelmo.monify.repository.AccountRepository;
import com.swetonyancelmo.monify.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AccountService {

    @Autowired private AccountRepository accountRepository;
    @Autowired private UserRepository userRepository;

    /**
     * Retorna TODAS as contas do usuário autenticado.
     * Garante que apenas contas pertencentes ao usuário sejam retornadas.
     *
     * @return {@link List} de {@link AccountResponseDto} com as contas do usuário
     */
    @Transactional(readOnly = true)
    public Page<AccountResponseDto> findAllAccounts(Pageable pageable) {
        JWTUserData userData = (JWTUserData) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return accountRepository.findByUserId(userData.userId(), pageable)
                .map(a -> new AccountResponseDto(a.getId(), a.getName(), a.getBalance(), a.getUser().getId()));
    }

    /**
     * Cria uma nova conta validando propriedade do usuário e unicidade do nome.
     *
     * <p><b>Processo:</b></p>
     * <ol>
     *   <li>Busca o usuário pelo email fornecido</li>
     *   <li>Verifica se já existe conta com mesmo nome para este usuário</li>
     *   <li>Cria e persiste a conta com o saldo inicial fornecido</li>
     * </ol>
     *
     * @param dto Dados da conta (nome, saldo inicial)
     * @param userEmail Email do usuário autenticado
     * @return {@link AccountResponseDto} com dados da conta criada
     * @throws ResourceNotFoundException se usuário não existir
     * @throws BusinessException se conta com mesmo nome já existe para este usuário
     */
    @Transactional
    public AccountResponseDto createAccount(CreateAccountDto dto, String userEmail) {
        User userData = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário com o email " + userEmail + " não encontrado"));

        if (accountRepository.existsByNameAndUserId(dto.name(), userData.getId())) {
            throw new BusinessException("Você já possui uma conta com o nome '" + dto.name() + "'");
        }

        Account account = new Account();
        account.setName(dto.name());
        account.setBalance(dto.balance());
        account.setUser(userData);

        Account accountSaved = accountRepository.save(account);

        return new AccountResponseDto(
                accountSaved.getId(),
                accountSaved.getName(),
                accountSaved.getBalance(),
                accountSaved.getUser().getId()
        );
    }

    /**
     * Atualiza uma conta verificando propriedade do usuário.
     *
     * <p><b>Processo:</b></p>
     * <ol>
     *   <li>Busca a conta pelo ID e ID do usuário (garante propriedade)</li>
     *   <li>Valida novo nome se fornecido (verifica duplicação)</li>
     *   <li>Atualiza nome se fornecido e válido</li>
     *   <li>Atualiza saldo se fornecido</li>
     *   <li>Persiste as alterações</li>
     * </ol>
     *
     * @param dto Novos dados da conta (nome e/ou saldo)
     * @param id ID da conta a atualizar
     * @param userId ID do usuário autenticado
     * @return {@link AccountResponseDto} com dados atualizados
     * @throws ResourceNotFoundException se conta não pertencer ao usuário
     * @throws BusinessException se novo nome já existe para este usuário
     */
    @Transactional
    public AccountResponseDto updateAccount(UpdateAccountDto dto, UUID id, UUID userId) {
        Account account = accountRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Conta não encontrada ou não existente com ID: "+ id));

        if (dto.name() != null && !dto.name().isEmpty()) {
            if (!account.getName().equals(dto.name()) && accountRepository.existsByNameAndUserId(dto.name(), userId)) {
                throw new BusinessException("Você já possui uma conta com o nome '" + dto.name() + "'");
            }
            account.setName(dto.name());
        }

        if (dto.balance() != null) {
            account.setBalance(dto.balance());
        }

        Account accountUpdated = accountRepository.save(account);

        return new AccountResponseDto(
                accountUpdated.getId(),
                accountUpdated.getName(),
                accountUpdated.getBalance(),
                accountUpdated.getUser().getId()
        );
    }

    /**
     * Deleta uma conta do usuário autenticado.
     *
     * <p><b>Processo:</b></p>
     * <ol>
     *   <li>Busca a conta pelo ID e ID do usuário (garante propriedade)</li>
     *   <li>Remove a conta do banco de dados</li>
     * </ol>
     *
     * @param id ID da conta a deletar
     * @param userId ID do usuário autenticado
     * @throws ResourceNotFoundException se conta não pertencer ao usuário
     */
    @Transactional
    public void deleteAccount(UUID id, UUID userId) {
        Account account = accountRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Conta não encontrada ou não existente com ID: "+ id));

        accountRepository.delete(account);
    }

}
