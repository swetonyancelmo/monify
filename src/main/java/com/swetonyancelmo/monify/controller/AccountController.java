package com.swetonyancelmo.monify.controller;

import com.swetonyancelmo.monify.config.JWTUserData;
import com.swetonyancelmo.monify.controller.docs.AccountControllerDocs;
import com.swetonyancelmo.monify.dto.request.CreateAccountDto;
import com.swetonyancelmo.monify.dto.request.UpdateAccountDto;
import com.swetonyancelmo.monify.dto.response.AccountResponseDto;
import com.swetonyancelmo.monify.service.AccountService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/account/v1")
@Tag(name = "Account", description = "Account Endpoints")
@RequiredArgsConstructor
public class AccountController implements AccountControllerDocs {

    private final AccountService accountService;

    @PreAuthorize("isAuthenticated()")
    @GetMapping(
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Override
    public ResponseEntity<Page<AccountResponseDto>> getAllAccounts(
            @RequestParam(value = "page", defaultValue = "0") @Min(0) Integer page,
            @RequestParam(value = "size", defaultValue = "12") @Positive Integer size,
            @RequestParam(value = "direction", defaultValue = "asc") String direction
    ) {
        var sortDirection = "desc".equalsIgnoreCase(direction) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, "name"));
        return ResponseEntity.ok(accountService.findAllAccounts(pageable));
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Override
    public ResponseEntity<AccountResponseDto> createAccount(@Valid @RequestBody CreateAccountDto data) {
        JWTUserData userData = (JWTUserData) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.status(HttpStatus.CREATED).body(accountService.createAccount(data, userData.email()));
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping(
            value = "/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Override
    public ResponseEntity<AccountResponseDto> updateAccount(@Valid @RequestBody UpdateAccountDto data,
                                                            @PathVariable UUID id) {
        JWTUserData userData = (JWTUserData) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok(accountService.updateAccount(data, id, userData.userId()));
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping(
            value = "/{id}"
    )
    @Override
    public ResponseEntity<Void> deleteAccount(@PathVariable UUID id) {
        JWTUserData userData = (JWTUserData) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        accountService.deleteAccount(id, userData.userId());
        return ResponseEntity.noContent().build();
    }

}