package com.blackrock.challenge.api.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record FilterRequestDTO(
        @NotNull @Valid List<QPeriodDTO> q,
        @NotNull @Valid List<PPeriodDTO> p,
        @NotNull @Valid List<KPeriodDTO> k,
        @NotNull @Valid List<TransactionDTO> transactions
) {
}