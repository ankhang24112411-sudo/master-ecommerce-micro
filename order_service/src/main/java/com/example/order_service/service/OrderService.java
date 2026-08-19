package com.example.order_service.service;

import com.example.order_service.dtos.request.OrderRequest;
import com.example.order_service.entity.OrderEntity;

public interface OrderService {
    OrderEntity createOrder(OrderRequest request);
}
