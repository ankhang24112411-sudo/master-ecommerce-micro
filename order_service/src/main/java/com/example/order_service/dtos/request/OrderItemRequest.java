package com.example.order_service.dtos.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public class OrderItemRequest {

    @NotEmpty
    private String productId;

    @NotNull
    private Integer quantity;
}
