package com.swetonyancelmo.monify.controller;

import com.swetonyancelmo.monify.config.JWTUserData;
import com.swetonyancelmo.monify.controller.docs.AccountControllerDocs;
import com.swetonyancelmo.monify.dto.request.CreateAccountDto;
import com.swetonyancelmo.monify.dto.request.UpdateAccountDto;
import com.swetonyancelmo.monify.dto.response.AccountResponseDto;
import com.swetonyancelmo.monify.service.AccountService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/account/v1")
@Tag(name = "Account", description = "Account Endpoints")
public class AccountController implements AccountControllerDocs {

    @Autowired
    private AccountService accountService;

    @PreAuthorize("isAuthenticated()")
    @GetMapping(
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Override
    public ResponseEntity<List<AccountResponseDto>> getAllAccounts() {
        return ResponseEntity.ok(accountService.findAllAccounts());
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Override
    public ResponseEntity<AccountResponseDto> createAccount(@Valid @RequestBody CreateAccountDto data, Authentication authentication) {
        JWTUserData userData = (JWTUserData) authentication.getPrincipal();
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
                                                            @PathVariable UUID id,
                                                            Authentication authentication) {
        JWTUserData userData = (JWTUserData) authentication.getPrincipal();
        return ResponseEntity.ok(accountService.updateAccount(data, id, userData.userId()));
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping(
            value = "/{id}"
    )
    @Override
    public ResponseEntity<AccountResponseDto> deleteAccount(@PathVariable UUID id, Authentication authentication) {
        JWTUserData userData = (JWTUserData) authentication.getPrincipal();
        accountService.deleteAccount(id, userData.userId());
        return ResponseEntity.noContent().build();
    }

}
