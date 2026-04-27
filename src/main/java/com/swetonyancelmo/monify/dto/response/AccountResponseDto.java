package com.swetonyancelmo.monify.dto.response;

import java.math.BigDecimal;
import java.util.UUID;

public record AccountResponseDto(
        UUID id,
        String name,
        BigDecimal balance,
        UUID user_id
) {
}
