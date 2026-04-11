package com.swetonyancelmo.monify.controller;

import com.swetonyancelmo.monify.domain.users.CreateUserDto;
import com.swetonyancelmo.monify.domain.users.UpdateUserDto;
import com.swetonyancelmo.monify.domain.users.UserResponseDto;
import com.swetonyancelmo.monify.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/users/v1")
@Tag(name = "Users", description = "User Endpoints")
public class UserController implements com.swetonyancelmo.monify.controller.docs.UserControllerDocs {

    @Autowired
    private UserService userService;

    @GetMapping(
            path = "/{uuid}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Override
    public ResponseEntity<UserResponseDto> getById(@PathVariable("uuid") UUID id) {
        return ResponseEntity.ok(userService.findById(id));
    }

    @PostMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Override
    public ResponseEntity<UserResponseDto> create(@RequestBody @Valid CreateUserDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.create(dto));
    }

    @PutMapping(
            path = "/{uuid}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Override
    public ResponseEntity<UserResponseDto> update(@RequestBody @Valid UpdateUserDto dto, @PathVariable("uuid") UUID id) {
        return ResponseEntity.ok(userService.update(dto, id));
    }

}
