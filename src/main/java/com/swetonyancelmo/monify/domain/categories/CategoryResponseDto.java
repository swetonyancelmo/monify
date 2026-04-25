package com.swetonyancelmo.monify.domain.categories;

import com.swetonyancelmo.monify.domain.categories.enums.CategoryType;

import java.util.UUID;

public record CategoryResponseDto(
        UUID id,
        String name,
        CategoryType type,
        UUID user_id
) {
}
