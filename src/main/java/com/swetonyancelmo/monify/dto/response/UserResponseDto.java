package com.swetonyancelmo.monify.dto.response;

import java.util.UUID;

public record UserResponseDto(
        UUID id,
        String name,
        String email
) {
}
