package com.blackrock.challenge.api.dto;

import jakarta.validation.constraints.NotBlank;

public record TransactionDTO(
        @NotBlank String date,
        double amount,
        double ceiling,
        double remanent
) {
}