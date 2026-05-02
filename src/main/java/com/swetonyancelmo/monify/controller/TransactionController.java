package com.swetonyancelmo.monify.controller;

import com.swetonyancelmo.monify.controller.docs.TransactionControllerDocs;
import com.swetonyancelmo.monify.dto.request.CreateTransactionDto;
import com.swetonyancelmo.monify.dto.request.UpdateTransactionDto;
import com.swetonyancelmo.monify.dto.response.TransactionResponseDto;
import com.swetonyancelmo.monify.service.TransactionService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/transaction/v1")
@Tag(name = "Transaction", description = "Transaction Endpoints")
public class TransactionController implements TransactionControllerDocs {

    @Autowired
    private TransactionService transactionService;

    @PreAuthorize("isAuthenticated()")
    @GetMapping(
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Override
    public ResponseEntity<List<TransactionResponseDto>> getAllTransactions() {
        return ResponseEntity.ok(transactionService.findAllTransactions());
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping(
            value = "/{categoryId}/{accountId}",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    @Override
    public ResponseEntity<TransactionResponseDto> createTransaction(
            @Valid @RequestBody CreateTransactionDto data,
            @PathVariable UUID categoryId,
            @PathVariable UUID accountId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(transactionService.createTransaction(data, categoryId, accountId));
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping(
            value = "/{transactionId}/{categoryId}/{accountId}",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    @Override
    public ResponseEntity<TransactionResponseDto> updateTransaction(
            @Valid @RequestBody UpdateTransactionDto data,
            @PathVariable UUID transactionId,
            @PathVariable UUID categoryId,
            @PathVariable UUID accountId) {
        return ResponseEntity.status(HttpStatus.OK).body(transactionService.updateTransaction(data, transactionId, accountId, categoryId));
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping(
            value = "/{transactionId}"
    )
    @Override
    public ResponseEntity<Void> deleteTransaction(@PathVariable UUID transactionId) {
        transactionService.deleteTransaction(transactionId);
        return ResponseEntity.noContent().build();
    }

}
