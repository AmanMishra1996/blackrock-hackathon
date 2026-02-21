package com.blackrock.challenge.api.dto;

public record KReturnDTO(
        String start,
        String end,
        double amount,
        double profits,
        double taxBenefit
) {
}