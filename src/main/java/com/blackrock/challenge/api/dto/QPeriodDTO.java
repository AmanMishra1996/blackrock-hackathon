package com.blackrock.challenge.api.dto;

import jakarta.validation.constraints.PositiveOrZero;

public record QPeriodDTO(String start, String end, @PositiveOrZero long fixed) {
}