package com.swetonyancelmo.monify.dto.response;

import com.swetonyancelmo.monify.domain.enums.CategoryType;

import java.util.UUID;

public record CategoryResponseDto(
        UUID id,
        String name,
        CategoryType type,
        UUID user_id
) {
}
