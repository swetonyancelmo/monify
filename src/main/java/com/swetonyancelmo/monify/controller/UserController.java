package com.swetonyancelmo.monify.controller;

import com.swetonyancelmo.monify.config.JWTUserData;
import com.swetonyancelmo.monify.controller.docs.UserControllerDocs;
import com.swetonyancelmo.monify.dto.request.UpdateUserDto;
import com.swetonyancelmo.monify.dto.response.UserResponseDto;
import com.swetonyancelmo.monify.exception.BusinessException;
import com.swetonyancelmo.monify.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/users/v1")
@Tag(name = "Users", description = "User Endpoints")
@RequiredArgsConstructor
public class UserController implements UserControllerDocs {

    private final UserService userService;

    @PreAuthorize("isAuthenticated()")
    @GetMapping(
            path = "/{uuid}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Override
    public ResponseEntity<UserResponseDto> getById(@PathVariable("uuid") UUID id) {
        JWTUserData userData = (JWTUserData) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!userData.userId().equals(id)) {
            throw new BusinessException("Acesso negado: você não tem permissão para acessar este recurso");
        }
        return ResponseEntity.ok(userService.findById(id));
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping(
            path = "/{uuid}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Override
    public ResponseEntity<UserResponseDto> update(@RequestBody @Valid UpdateUserDto dto, @PathVariable("uuid") UUID id) {
        JWTUserData userData = (JWTUserData) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!userData.userId().equals(id)) {
            throw new BusinessException("Acesso negado: você não tem permissão para modificar este recurso");
        }
        return ResponseEntity.ok(userService.update(dto, id));
    }

}