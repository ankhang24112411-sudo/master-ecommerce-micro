package com.example.order_service.repository.orderdeduction;


import com.example.order_service.entity.OrderEntity;
import com.example.order_service.entity.OrderItemEntity;

import java.time.Instant;
import java.util.List;

public interface OrderDeductionRepository {
    void insertOrder(String yearMonth, OrderEntity order);
    void insertOrderItem(String yearMonth, OrderItemEntity orderItem);
    List<Object[]> findAll(String yearMonth);
    Object[] findByOrderNumber(String yearMonth, String orderNumber);
    List<Object[]> findByDateRange(String yearMonth, Instant startDate, Instant endDate);
    boolean updateOrderStatus(String yearMonth, String orderNumber, String status);
    List<Object[]> findPage(String yearMonth, Integer lastId, int limit);
    List<Object[]> findItemsByOrderId(String yearMonth, Integer orderId);
}