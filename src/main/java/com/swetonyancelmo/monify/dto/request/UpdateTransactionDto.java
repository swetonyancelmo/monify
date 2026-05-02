package com.swetonyancelmo.monify.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.swetonyancelmo.monify.domain.enums.TransactionType;
import jakarta.validation.constraints.Positive;
import org.hibernate.validator.constraints.Length;

import java.math.BigDecimal;
import java.time.LocalDate;

public record UpdateTransactionDto(

        TransactionType type,

        @Positive(message = "A transação deve ser maior que 0")
        BigDecimal amount,

        @Length(min = 3, max = 100, message = "A descrição deve ter entre 3 a 100 caracteres")
        String description,

        @JsonFormat(pattern = "dd/MM/yyyy")
        LocalDate date
) {
}
