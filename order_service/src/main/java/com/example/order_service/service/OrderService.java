package com.example.order_service.service;

import com.example.order_service.config.utils.OrderStatus;
import com.example.order_service.consumer.event.InventoryReservedEvent;
import com.example.order_service.consumer.event.PaymentEvent;
import com.example.order_service.dtos.request.OrderRequest;
import com.example.order_service.entity.OrderEntity;

public interface OrderService {
    OrderEntity createOrder(OrderRequest request);

    void updateOrderStatus(String orderId , OrderStatus status);

  void  handleInventoryReservedEvent(InventoryReservedEvent inventoryReservedEvent);

    void handlePaymentEvent(com.example.order_service.consumer.event.PaymentEvent paymentEvent);
}
