package com.blackrock.challenge.service;

import com.blackrock.challenge.api.dto.InvalidItemDTO;
import com.blackrock.challenge.api.dto.ValidationResponseDTO;

import java.util.ArrayList;
import java.util.List;

final class ValidationAccumulator<T> {
    private final List<T> valid = new ArrayList<>();
    private final List<InvalidItemDTO<T>> invalid = new ArrayList<>();

    void addValid(T item) {
        valid.add(item);
    }

    void addInvalid(T item, String message) {
        invalid.add(new InvalidItemDTO<>(item, message));
    }

    ValidationResponseDTO<T> build() {
        return new ValidationResponseDTO<>(valid, invalid);
    }
}