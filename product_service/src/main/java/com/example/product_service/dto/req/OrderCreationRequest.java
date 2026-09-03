package com.example.product_service.dto.req;

import lombok.Data;

@Data
public class OrderCreationRequest {
    String userId;
    String productId;
    Integer quantity;
}
