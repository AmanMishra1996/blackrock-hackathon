package com.blackrock.challenge.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;

public record ExpenseDTO(
        @NotBlank String date,
        @PositiveOrZero double amount
) {
}