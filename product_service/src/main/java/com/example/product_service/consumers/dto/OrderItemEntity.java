package com.example.product_service.consumers.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemEntity {

    private String id;

    private String orderId;

    private String productId;

    private Integer price;

    private Integer quantity;
}
