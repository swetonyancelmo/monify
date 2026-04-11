package com.swetonyancelmo.monify.controller.docs;

import com.swetonyancelmo.monify.domain.dto.ErrorResponseDto;
import com.swetonyancelmo.monify.domain.users.CreateUserDto;
import com.swetonyancelmo.monify.domain.users.UpdateUserDto;
import com.swetonyancelmo.monify.domain.users.UserResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

public interface UserControllerDocs {

    @Operation(summary = "Get a User", description = "Get a User By Id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User Found",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = UserResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "User not Found",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    ResponseEntity<UserResponseDto> getById(@PathVariable("uuid") UUID id);

    @Operation(summary = "Create a User", description = "Create a User")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User Created",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = UserResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Error creating User",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Email Already Exists",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    ResponseEntity<UserResponseDto> create(@RequestBody CreateUserDto dto);

    @Operation(summary = "Update a User", description = "Update a User By Id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User Updated",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = UserResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "User not Found",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Error updating User",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    ResponseEntity<UserResponseDto> update(@RequestBody UpdateUserDto dto, @PathVariable("uuid") UUID id);
}
