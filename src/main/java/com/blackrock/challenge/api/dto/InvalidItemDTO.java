package com.blackrock.challenge.api.dto;

public record InvalidItemDTO<T>(T item, String message) {
}