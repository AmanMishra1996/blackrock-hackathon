package com.blackrock.challenge.api.dto;

import java.util.List;

public record ValidationResponseDTO<T>(
        List<T> valid,
        List<InvalidItemDTO<T>> invalid
) {
}