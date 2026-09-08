package com.example.order_service.repository.orderdeduction;


import com.example.order_service.entity.OrderEntity;
import com.example.order_service.entity.OrderItemEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.util.List;

@Service
public class OrderDeductionDomainService {

    @Autowired
    private OrderDeductionRepository orderDeductionRepository;

    public void insertOrder(String yearMonth, OrderEntity order) {
        orderDeductionRepository.insertOrder(yearMonth, order);
    }

    public void insertOrderItem(String yearMonth, OrderItemEntity orderItem) {
        orderDeductionRepository.insertOrderItem(yearMonth, orderItem);
    }

    public List<Object[]> findAll(String yearMonth) {
        return orderDeductionRepository.findAll(yearMonth);
    }

    public Object[] findByOrderNumber(String yearMonth, String orderNumber) {
        return orderDeductionRepository.findByOrderNumber(yearMonth, orderNumber);
    }

    public List<Object[]> findByDateRange(String yearMonth, Instant startDate, Instant endDate) {
        return orderDeductionRepository.findByDateRange(yearMonth, startDate, endDate);
    }

    public boolean updateOrderStatus(String yearMonth, String orderNumber, String status) {
        return orderDeductionRepository.updateOrderStatus(yearMonth, orderNumber, status);
    }

    public List<Object[]> findPage(String yearMonth, Integer lastId, int limit) {
        return orderDeductionRepository.findPage(yearMonth, lastId, limit);
    }

    public List<Object[]> findItemsByOrderId(String yearMonth, String orderId) {
        return orderDeductionRepository.findItemsByOrderId(yearMonth, orderId);
    }
}

