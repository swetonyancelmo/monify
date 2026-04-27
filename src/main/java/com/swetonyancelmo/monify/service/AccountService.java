package com.swetonyancelmo.monify.service;

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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AccountService {

    @Autowired private AccountRepository accountRepository;
    @Autowired private UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<AccountResponseDto> findAllAccounts() {
        return accountRepository.findAll().stream()
                .map(a -> new AccountResponseDto(a.getId(), a.getName(), a.getBalance(), a.getUser().getId()))
                .collect(Collectors.toList());
    }

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

    @Transactional
    public AccountResponseDto updateAccount(UpdateAccountDto dto, UUID id, UUID userId) {
        Account account = accountRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Conta não encontrada ou não existente com ID: "+ id));

        if (dto.name() != null && !dto.name().isEmpty()) {
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

    @Transactional
    public void deleteAccount(UUID id, UUID userId) {
        Account account = accountRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Conta não encontrada ou não existente com ID: "+ id));

        accountRepository.delete(account);
    }

}
