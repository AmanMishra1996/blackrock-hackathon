package com.blackrock.challenge.api.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.List;

public record ValidatorRequestDTO(
        @Positive double wage,
        @NotNull List<TransactionDTO> transactions
) {
}