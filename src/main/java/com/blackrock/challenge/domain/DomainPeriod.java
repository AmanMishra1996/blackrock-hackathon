package com.blackrock.challenge.domain;

public record DomainPeriod(long startEpoch, long endEpoch) {
    public boolean contains(long t) {
        return t >= startEpoch && t <= endEpoch;
    }
}