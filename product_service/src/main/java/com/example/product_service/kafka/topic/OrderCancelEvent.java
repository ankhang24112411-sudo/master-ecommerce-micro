package com.example.product_service.kafka.topic;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderCancelEvent {
    private String token;
    private String flashSaleId;
    private String userId;
    private Integer quantity;
    private String reason;
}
