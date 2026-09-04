package com.example.product_service.dto.req;

import lombok.Data;

@Data
public class PlaceOrderFlashSaleRequest {
    String userId;
    String flashSaleId;
    Integer quantity;
}
