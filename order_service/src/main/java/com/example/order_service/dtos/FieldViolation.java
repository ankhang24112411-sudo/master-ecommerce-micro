package com.example.order_service.dtos;


public record FieldViolation(
        String field,
        String message) {

}
