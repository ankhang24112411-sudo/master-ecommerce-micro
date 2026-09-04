package com.example.order_service.dtos.request;

import lombok.Data;

@Data
public class PlaceOrderFlashSaleRequest {
    String flashSaleId;
    int quantity;
    String userId;
}
