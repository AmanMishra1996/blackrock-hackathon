package com.blackrock.challenge.api.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.List;

public record ReturnsRequestDTO(
        @Positive int age,
        @Positive double wage,
        @NotNull double inflation,
        @NotNull @Valid List<QPeriodDTO> q,
        @NotNull @Valid List<PPeriodDTO> p,
        @NotNull @Valid List<KPeriodDTO> k,
        @NotNull @Valid List<TransactionDTO> transactions
) {
}