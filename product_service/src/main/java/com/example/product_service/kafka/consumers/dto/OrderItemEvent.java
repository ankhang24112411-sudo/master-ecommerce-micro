package com.example.product_service.kafka.consumers.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemEvent {
    private String productId;

    private Integer quantity;
}
