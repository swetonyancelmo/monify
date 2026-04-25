package com.swetonyancelmo.monify.domain.categories;

import com.swetonyancelmo.monify.domain.categories.enums.CategoryType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

public record UpdateCategoryDto(
        @NotBlank(message = "O nome é obrigatório")
        @Length(min = 3, max = 100)
        String name,

        @Enumerated(EnumType.STRING)
        CategoryType type
) {
}
