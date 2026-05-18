package com.swetonyancelmo.monify.controller;

import com.swetonyancelmo.monify.controller.docs.TransactionControllerDocs;
import com.swetonyancelmo.monify.dto.request.CreateTransactionDto;
import com.swetonyancelmo.monify.dto.request.UpdateTransactionDto;
import com.swetonyancelmo.monify.dto.response.TransactionResponseDto;
import com.swetonyancelmo.monify.service.TransactionService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/transaction/v1")
@Tag(name = "Transaction", description = "Transaction Endpoints")
@RequiredArgsConstructor
public class TransactionController implements TransactionControllerDocs {

    private final TransactionService transactionService;

    @PreAuthorize("isAuthenticated()")
    @GetMapping(
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Override
    public ResponseEntity<Page<TransactionResponseDto>> getAllTransactions(
            @RequestParam(value = "page", defaultValue = "0") @Min(0) Integer page,
            @RequestParam(value = "size", defaultValue = "12") @Positive Integer size,
            @RequestParam(value = "direction", defaultValue = "asc") String direction
    ) {
        var sortDirection = "desc".equalsIgnoreCase(direction) ? Direction.DESC : Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, "date"));
        return ResponseEntity.ok(transactionService.findAllTransactions(pageable));
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
