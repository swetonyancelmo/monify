package com.swetonyancelmo.monify.dto.request;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import org.hibernate.validator.constraints.Length;

import java.math.BigDecimal;

public record CreateAccountDto(

        @NotBlank(message = "O nome é obrigatório")
        @Length(min = 3, max = 100)
        String name,

        @NotNull(message = "O saldo inicial é obrigatório")
        @PositiveOrZero(message = "O saldo inicial não pode ser negativo")
        @Digits(integer = 10, fraction = 2, message = "O saldo inicial deve ter entre 10 dígitos inteiros e 2 casas decimais")
        BigDecimal balance
) {
}
