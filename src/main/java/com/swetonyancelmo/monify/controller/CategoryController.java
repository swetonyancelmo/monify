package com.swetonyancelmo.monify.controller;

import com.swetonyancelmo.monify.controller.docs.CategoryControllerDocs;
import com.swetonyancelmo.monify.domain.categories.CategoryResponseDto;
import com.swetonyancelmo.monify.domain.categories.CreateCategoryDto;
import com.swetonyancelmo.monify.domain.categories.UpdateCategoryDto;
import com.swetonyancelmo.monify.config.JWTUserData;
import com.swetonyancelmo.monify.service.CategoryService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/category/v1")
@Tag(name = "Category", description = "Category Endpoints")
public class CategoryController implements CategoryControllerDocs {

    @Autowired
    private CategoryService categoryService;

    @GetMapping(
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Override
    public ResponseEntity<List<CategoryResponseDto>> getAllCategories() {
        return ResponseEntity.ok(categoryService.findAllCategories());
    }

    @PostMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Override
    public ResponseEntity<CategoryResponseDto> createCategory(@Valid @RequestBody CreateCategoryDto data, Authentication authentication) {
        JWTUserData userData = (JWTUserData) authentication.getPrincipal();
        return ResponseEntity.status(HttpStatus.CREATED).body(categoryService.createCategory(data, userData.email()));
    }

    @PutMapping(
            value = "/{uuid}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Override
    public ResponseEntity<CategoryResponseDto> updateCategory(@Valid @RequestBody UpdateCategoryDto data, @PathVariable UUID uuid) {
        return ResponseEntity.ok(categoryService.updateCategory(data, uuid));
    }

    @DeleteMapping(
            value = "/{uuid}"
    )
    @Override
    public ResponseEntity<Void> deleteCategory(@PathVariable UUID uuid) {
        categoryService.deleteCategory(uuid);
        return ResponseEntity.noContent().build();
    }

}
