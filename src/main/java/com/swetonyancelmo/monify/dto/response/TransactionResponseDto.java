package com.swetonyancelmo.monify.dto.response;

import com.swetonyancelmo.monify.domain.enums.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record TransactionResponseDto(
        UUID id,
        TransactionType type,
        BigDecimal amount,
        String description,
        LocalDate date,
        UUID account_id,
        UUID category_id
) {
}
