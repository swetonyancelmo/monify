package com.swetonyancelmo.monify.controller.docs;

import com.swetonyancelmo.monify.domain.categories.CategoryResponseDto;
import com.swetonyancelmo.monify.domain.categories.CreateCategoryDto;
import com.swetonyancelmo.monify.domain.categories.UpdateCategoryDto;
import com.swetonyancelmo.monify.domain.dto.ErrorResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

public interface CategoryControllerDocs {

    @Operation(summary = "Get all Categories", description = "Get all Categories")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Categories Found",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = CategoryResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "Not authorized",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    ResponseEntity<List<CategoryResponseDto>> getAllCategories();

    @Operation(summary = "Create a Category", description = "Create a Category")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Category Created",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = CategoryResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Error in creating Category",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "Not authorized",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    ResponseEntity<CategoryResponseDto> createCategory(@Valid @RequestBody CreateCategoryDto data, Authentication authentication);

    @Operation(summary = "Update a Category", description = "Update a Category")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Category Updated",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = CategoryResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Error in updating Category",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "Not authorized",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    ResponseEntity<CategoryResponseDto> updateCategory(@Valid @RequestBody UpdateCategoryDto data, @PathVariable UUID uuid);

    @Operation(summary = "Delete a Category", description = "Delete a Category")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Category Deleted",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = CategoryResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Error deleting Category",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "Not authorized",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    ResponseEntity<Void> deleteCategory(@PathVariable UUID uuid);
}
