package com.swetonyancelmo.monify.dto.request;

import com.swetonyancelmo.monify.domain.enums.CategoryType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

public record CreateCategoryDto(

        @NotBlank(message = "O nome é obrigatório")
        @Length(min = 3, max = 100)
        String name,

        @Enumerated(EnumType.STRING)
        CategoryType type
) {
}
