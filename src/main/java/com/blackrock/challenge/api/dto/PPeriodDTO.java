package com.blackrock.challenge.api.dto;

import jakarta.validation.constraints.PositiveOrZero;

public record PPeriodDTO(String start, String end, @PositiveOrZero long extra) {
}