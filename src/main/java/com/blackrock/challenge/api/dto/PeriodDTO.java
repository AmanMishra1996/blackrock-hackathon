package com.blackrock.challenge.api.dto;

import jakarta.validation.constraints.NotBlank;

public record PeriodDTO(@NotBlank String start, @NotBlank String end) {
}