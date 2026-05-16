package com.swetonyancelmo.monify.service;

import com.swetonyancelmo.monify.config.JWTUserData;
import com.swetonyancelmo.monify.domain.Account;
import com.swetonyancelmo.monify.domain.Category;
import com.swetonyancelmo.monify.domain.Transaction;
import com.swetonyancelmo.monify.dto.request.CreateTransactionDto;
import com.swetonyancelmo.monify.dto.request.UpdateTransactionDto;
import com.swetonyancelmo.monify.dto.response.TransactionResponseDto;
import com.swetonyancelmo.monify.exception.BusinessException;
import com.swetonyancelmo.monify.exception.ResourceNotFoundException;
import com.swetonyancelmo.monify.repository.AccountRepository;
import com.swetonyancelmo.monify.repository.CategoryRepository;
import com.swetonyancelmo.monify.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final CategoryRepository categoryRepository;
    private final AccountRepository accountRepository;

    /**
     * Retorna TODAS as transações do usuário autenticado
     **/
    @Transactional(readOnly = true)
    public Page<TransactionResponseDto> findAllTransactions(Pageable pageable) {
        JWTUserData userData = (JWTUserData) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return transactionRepository.findByAccount_User_Id(userData.userId(), pageable)
                .map(t -> new TransactionResponseDto(
                        t.getId(),
                        t.getType(),
                        t.getAmount(),
                        t.getDescription(),
                        t.getDate(),
                        t.getAccount().getId(),
                        t.getCategory().getId()));
    }

    /**
     * Cria uma nova transação com validações de integridade e atualização de saldo.
     *
     * <p><b>Processo:</b></p>
     * <ol>
     *   <li>Valida existência da categoria e conta</li>
     *   <li>Verifica se categoria e conta pertencem ao mesmo usuário</li>
     *   <li>Valida compatibilidade entre tipo da categoria e tipo da transação</li>
     *   <li>Processa o impacto no saldo da conta:
     *       <ul>
     *           <li><b>DESPESA:</b> Verifica saldo suficiente e subtrai do saldo</li>
     *           <li><b>RENDA:</b> Adiciona o valor ao saldo</li>
     *       </ul>
     *   </li>
     *   <li>Persiste a transação no banco de dados</li>
     * </ol>
     *
     * @param dto Dados da transação (tipo, valor, descrição, data)
     * @param accountId ID da conta associada
     * @param categoryId ID da categoria associada
     * @return {@link TransactionResponseDto} com os dados da transação criada
     * @throws ResourceNotFoundException se categoria ou conta não existem
     * @throws BusinessException se validações falharem ou saldo for insuficiente
     */
    @Transactional
    public TransactionResponseDto createTransaction(CreateTransactionDto dto, UUID categoryId, UUID accountId) {
        JWTUserData userData = (JWTUserData) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada"));

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Conta não encontrada"));

        if (!category.getUser().getId().equals(userData.userId())) {
            throw new BusinessException("Categoria não pertence ao usuário autenticado");
        }

        if (!account.getUser().getId().equals(userData.userId())) {
            throw new BusinessException("Conta não pertence ao usuário autenticado");
        }

        if (!category.getUser().getId().equals(account.getUser().getId())) {
            throw new BusinessException("Categoria não pertence ao usuário da conta");
        }

        if (!category.getType().name().equals(dto.type().name())) {
            throw new BusinessException("Tipo da categoria incompatível com a transação");
        }

        switch (dto.type()) {
            case EXPENSE -> {
                if (dto.amount().compareTo(account.getBalance()) > 0) {
                    throw new BusinessException("Saldo insuficiente.");
                }
                account.setBalance(account.getBalance().subtract(dto.amount()));
            }
            case INCOME -> {
                account.setBalance(account.getBalance().add(dto.amount()));
            }
        }

        Transaction transaction = new Transaction();
        transaction.setType(dto.type());
        transaction.setAmount(dto.amount());
        transaction.setDescription(dto.description());
        transaction.setDate(dto.date());
        transaction.setAccount(account);
        transaction.setCategory(category);

        Transaction transactionSaved = transactionRepository.save(transaction);

        return new TransactionResponseDto(
                transactionSaved.getId(),
                transactionSaved.getType(),
                transactionSaved.getAmount(),
                transactionSaved.getDescription(),
                transactionSaved.getDate(),
                transactionSaved.getAccount().getId(),
                transactionSaved.getCategory().getId()
        );
    }

    /**
     * Atualiza uma transação existente com validações e reajuste de saldo.
     *
     * <p><b>Processo:</b></p>
     * <ol>
     *   <li>Valida existência da transação, categoria e conta</li>
     *   <li>Verifica se categoria e conta pertencem ao mesmo usuário</li>
     *   <li>Valida compatibilidade entre tipo da categoria e tipo da transação</li>
     *   <li>Reverte o impacto da transação anterior:
     *       <ul>
     *           <li><b>Era DESPESA:</b> Adiciona o valor de volta ao saldo</li>
     *           <li><b>Era RENDA:</b> Subtrai o valor do saldo</li>
     *       </ul>
     *   </li>
     *   <li>Aplica o novo impacto no saldo:
     *       <ul>
     *           <li><b>Nova DESPESA:</b> Verifica saldo e subtrai</li>
     *           <li><b>Nova RENDA:</b> Adiciona ao saldo</li>
     *       </ul>
     *   </li>
     *   <li>Persiste as alterações no banco de dados</li>
     * </ol>
     *
     * @param dto Novos dados da transação
     * @param transactionId ID da transação a ser atualizada
     * @param accountId ID da conta associada
     * @param categoryId ID da categoria associada
     * @return {@link TransactionResponseDto} com os dados atualizados
     * @throws ResourceNotFoundException se transação, categoria ou conta não existem
     * @throws BusinessException se validações falharem ou saldo for insuficiente
     */
    @Transactional
    public TransactionResponseDto updateTransaction(UpdateTransactionDto dto, UUID transactionId, UUID accountId, UUID categoryId) {
        JWTUserData userData = (JWTUserData) SecurityContextHolder.getContext().getAuthentication().getPrincipal();


        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new ResourceNotFoundException("Transação não encontrada"));

        if (!transaction.getAccount().getUser().getId().equals(userData.userId())) {
            throw new BusinessException("Transação não pertence ao usuário");
        }

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada"));

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Conta não encontrada"));

        if (!category.getUser().getId().equals(userData.userId())) {
            throw new BusinessException("Categoria não pertence ao usuário autenticado");
        }

        if (!account.getUser().getId().equals(userData.userId())) {
            throw new BusinessException("Conta não pertence ao usuário autenticado");
        }

        if (!category.getUser().getId().equals(account.getUser().getId())) {
            throw new BusinessException("Categoria não pertence ao usuário da conta");
        }

        if (!category.getType().name().equals(dto.type().name())) {
            throw new BusinessException("Tipo da categoria incompatível com a transação");
        }

        // Reverte impacto na conta antiga
        Account oldAccount = transaction.getAccount();
        switch (transaction.getType()) {
            case EXPENSE -> {
                oldAccount.setBalance(oldAccount.getBalance().add(transaction.getAmount()));
            }
            case INCOME -> {
                oldAccount.setBalance(oldAccount.getBalance().subtract(transaction.getAmount()));
            }
        }

        // Aplica novo impacto na conta nova
        switch (dto.type()) {
            case EXPENSE -> {
                if (dto.amount().compareTo(account.getBalance()) > 0) {
                    throw new BusinessException("Saldo insuficiente.");
                }
                account.setBalance(account.getBalance().subtract(dto.amount()));
            }
            case INCOME -> {
                account.setBalance(account.getBalance().add(dto.amount()));
            }
        }

        transaction.setType(dto.type());
        transaction.setAmount(dto.amount());
        transaction.setDescription(dto.description());
        transaction.setDate(dto.date());
        transaction.setCategory(category);
        transaction.setAccount(account);

        Transaction transactionUpdated = transactionRepository.save(transaction);

        return new TransactionResponseDto(
                transactionUpdated.getId(),
                transactionUpdated.getType(),
                transactionUpdated.getAmount(),
                transactionUpdated.getDescription(),
                transactionUpdated.getDate(),
                transactionUpdated.getAccount().getId(),
                transactionUpdated.getCategory().getId()
        );
    }

    /**
     * Deleta uma transação revertendo seu impacto no saldo da conta.
     *
     * <p><b>Processo:</b></p>
     * <ol>
     *   <li>Valida existência da transação</li>
     *   <li>Obtém a conta associada à transação</li>
     *   <li>Reverte o impacto no saldo:
     *       <ul>
     *           <li><b>Era DESPESA:</b> Adiciona o valor de volta ao saldo</li>
     *           <li><b>Era RENDA:</b> Subtrai o valor do saldo</li>
     *       </ul>
     *   </li>
     *   <li>Remove a transação do banco de dados</li>
     * </ol>
     *
     * @param transactionId ID da transação a ser deletada
     * @throws ResourceNotFoundException se a transação não existir
     */
    @Transactional
    public void deleteTransaction(UUID transactionId) {
        JWTUserData userData = (JWTUserData) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new ResourceNotFoundException("Transação não encontrada"));

        if (!transaction.getAccount().getUser().getId().equals(userData.userId())) {
            throw new BusinessException("Transação não pertence ao usuário");
        }

        Account account = transaction.getAccount();

        switch (transaction.getType()) {
            case EXPENSE -> {
                account.setBalance(account.getBalance().add(transaction.getAmount()));
            }
            case INCOME -> {
                account.setBalance(account.getBalance().subtract(transaction.getAmount()));
            }
        }

        transactionRepository.delete(transaction);
    }

}
