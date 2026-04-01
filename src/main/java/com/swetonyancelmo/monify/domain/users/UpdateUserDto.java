package com.swetonyancelmo.monify.domain.users;

import jakarta.validation.constraints.Size;

public record UpdateUserDto(
        @Size(max = 255)
        String name,

        @Size(min = 6, max = 20, message = "A senha deve ter entre 6 a 20 caracteres")
        String password
) {
}
