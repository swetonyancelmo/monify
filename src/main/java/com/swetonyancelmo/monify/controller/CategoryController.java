package com.swetonyancelmo.monify.controller;

import com.swetonyancelmo.monify.controller.docs.CategoryControllerDocs;
import com.swetonyancelmo.monify.dto.response.CategoryResponseDto;
import com.swetonyancelmo.monify.dto.request.CreateCategoryDto;
import com.swetonyancelmo.monify.dto.request.UpdateCategoryDto;
import com.swetonyancelmo.monify.config.JWTUserData;
import com.swetonyancelmo.monify.service.CategoryService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/category/v1")
@Tag(name = "Category", description = "Category Endpoints")
public class CategoryController implements CategoryControllerDocs {

    @Autowired
    private CategoryService categoryService;

    @PreAuthorize("isAuthenticated()")
    @GetMapping(
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Override
    public ResponseEntity<List<CategoryResponseDto>> getAllCategories() {
        return ResponseEntity.ok(categoryService.findAllCategories());
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Override
    public ResponseEntity<CategoryResponseDto> createCategory(@Valid @RequestBody CreateCategoryDto data) {
        JWTUserData userData = (JWTUserData) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.status(HttpStatus.CREATED).body(categoryService.createCategory(data, userData.email()));
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping(
            value = "/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Override
    public ResponseEntity<CategoryResponseDto> updateCategory(@Valid @RequestBody UpdateCategoryDto data, @PathVariable UUID id) {
        JWTUserData userData = (JWTUserData) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok(categoryService.updateCategory(data, id, userData.userId()));
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping(
            value = "/{id}"
    )
    @Override
    public ResponseEntity<Void> deleteCategory(@PathVariable UUID id) {
        JWTUserData userData = (JWTUserData) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        categoryService.deleteCategory(id, userData.userId());
        return ResponseEntity.noContent().build();
    }

}
