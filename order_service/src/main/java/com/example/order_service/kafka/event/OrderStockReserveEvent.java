package com.example.order_service.kafka.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class OrderStockReserveEvent {
    String userId;
    String token;
    BigDecimal unitPrice;
    String productId;
    Integer quantity;

}