package com.example.product_service.kafka.topic;

import com.google.common.annotations.Beta;
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
