package com.example.product_service.exception;

public record FieldViolation(
        String field,
        String message) {

}
