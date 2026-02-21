package com.blackrock.challenge.api.dto;

import java.util.List;

public record ReturnsResponseDTO(
        double transactionsTotalAmount,
        double transactionsTotalCeiling,
        List<KReturnDTO> savingsByDates
) {
}