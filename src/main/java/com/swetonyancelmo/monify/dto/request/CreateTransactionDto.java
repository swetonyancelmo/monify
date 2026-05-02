package com.swetonyancelmo.monify.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.swetonyancelmo.monify.domain.enums.TransactionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.hibernate.validator.constraints.Length;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CreateTransactionDto(
        @NotNull(message = "O tipo da transação é obrigatório")
        TransactionType type,

        @NotNull(message = "A valor da transação é obrigatório")
        @Positive(message = "A transação deve ser maior que 0")
        BigDecimal amount,

        @NotBlank(message = "A descrição da transação é obrigatória")
        @Length(min = 3, max = 100, message = "A descrição deve ter entre 3 a 100 caracteres")
        String description,

        @NotNull(message = "A data é obrigatória")
        @JsonFormat(pattern = "dd/MM/yyyy")
        LocalDate date
) {
}
