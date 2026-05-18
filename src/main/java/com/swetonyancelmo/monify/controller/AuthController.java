package com.swetonyancelmo.monify.controller;

import com.swetonyancelmo.monify.controller.docs.AuthControllerDocs;
import com.swetonyancelmo.monify.dto.response.LoginResponseDto;
import com.swetonyancelmo.monify.dto.request.CreateUserDto;
import com.swetonyancelmo.monify.dto.request.LoginRequestDto;
import com.swetonyancelmo.monify.dto.response.UserResponseDto;
import com.swetonyancelmo.monify.service.AuthService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth/v1")
@Tag(name = "Auth", description = "Auth Endpoints")
@RequiredArgsConstructor
public class AuthController implements AuthControllerDocs {

    private final AuthService authService;

    @PostMapping(
            value = "/register",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Override
    public ResponseEntity<UserResponseDto> register(@RequestBody @Valid CreateUserDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.create(dto));
    }

    @PostMapping(
            value = "/login",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Override
    public ResponseEntity<LoginResponseDto> login(@RequestBody @Valid LoginRequestDto dto) {
        return ResponseEntity.status(HttpStatus.OK).body(authService.login(dto));
    }

}