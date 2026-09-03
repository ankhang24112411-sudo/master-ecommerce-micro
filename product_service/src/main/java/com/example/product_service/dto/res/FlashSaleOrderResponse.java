package com.example.product_service.dto.res;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FlashSaleOrderResponse {
    private String token;
    private String productId;
    private String userId;
    private Integer quantity;
    // 0=PENDING, 2=FAILED

    private Integer status;
    private String code;
    private String message;
    private Instant createdAt;

    public static FlashSaleOrderResponse success(String token, String productId, String userId, int quantity) {
        return FlashSaleOrderResponse.builder()
                .token(token)
                .productId(productId)
                .userId(userId)
                .quantity(quantity)
                .status(0)
                .createdAt(Instant.now())
                .build();
    }

    public static FlashSaleOrderResponse fail(String code, String message) {
        return FlashSaleOrderResponse.builder()
                .status(2)
                .code(code)
                .message(message)
                .createdAt(Instant.now())
                .build();
    }
}