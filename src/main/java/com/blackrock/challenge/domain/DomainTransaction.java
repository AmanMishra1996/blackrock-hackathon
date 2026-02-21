package com.blackrock.challenge.domain;

public record DomainTransaction(
        long epochSec,
        String date,
        double amount,
        double ceiling,
        double remanent
) {
}