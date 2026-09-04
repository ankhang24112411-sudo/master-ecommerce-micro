package com.example.order_service.dtos.resp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FlashSaleOrderResponse {
    private String token;
    private String productId;
    private String productName;
    private BigDecimal price;
    private String userId;
    private Integer quantity;
    // 0=PENDING, 2=FAILED
    private Integer status;
    private String code;
    private String message;
    private Instant createdAt;
}
