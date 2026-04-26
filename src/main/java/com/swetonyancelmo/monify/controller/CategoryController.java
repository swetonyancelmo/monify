package com.swetonyancelmo.monify.controller;

import com.swetonyancelmo.monify.controller.docs.CategoryControllerDocs;
import com.swetonyancelmo.monify.dto.response.CategoryResponseDto;
import com.swetonyancelmo.monify.dto.request.CreateCategoryDto;
import com.swetonyancelmo.monify.dto.request.UpdateCategoryDto;
import com.swetonyancelmo.monify.config.JWTUserData;
import com.swetonyancelmo.monify.domain.User;
import com.swetonyancelmo.monify.service.CategoryService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    public ResponseEntity<CategoryResponseDto> createCategory(@Valid @RequestBody CreateCategoryDto data, Authentication authentication) {
        JWTUserData userData = (JWTUserData) authentication.getPrincipal();
        return ResponseEntity.status(HttpStatus.CREATED).body(categoryService.createCategory(data, userData.email()));
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping(
            value = "/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Override
    public ResponseEntity<CategoryResponseDto> updateCategory(@Valid @RequestBody UpdateCategoryDto data, @PathVariable UUID id, @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(categoryService.updateCategory(data, id, user.getId()));
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping(
            value = "/{uuid}"
    )
    @Override
    public ResponseEntity<Void> deleteCategory(@PathVariable UUID uuid, @AuthenticationPrincipal User user) {
        categoryService.deleteCategory(uuid, user.getId());
        return ResponseEntity.noContent().build();
    }

}
