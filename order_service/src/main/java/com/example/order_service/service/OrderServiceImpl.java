package com.example.order_service.service;

import com.example.order_service.dtos.clientDTO.ProductDTO;
import com.example.order_service.dtos.clientDTO.ProductFilter;
import com.example.order_service.dtos.request.OrderItemRequest;
import com.example.order_service.dtos.request.OrderRequest;
import com.example.order_service.entity.OrderEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService{
    @Override
    public OrderEntity createOrder(OrderRequest request) {
        List<String> productIds = request.getOrderItems().stream()
                .map(OrderItemRequest::getProductId)
                .distinct()
                .toList();

        List<ProductDTO> products =
                productClient.getProductsByIds(new ProductFilter(productIds));

        Map<String, ProductDTO> productPriceMap = new HashMap<>();

        products.forEach(productDTO -> {
            productPriceMap.put(productDTO.getId(), productDTO);
        });

        OrderEntity order = new OrderEntity();
        order.setCustomerId(OrderRequest.getCustomerId());
        order.setStatus(OrderStatus.NEW.name());
        order.setTotalAmount(0);

        OrderEntity savedOrder = orderRepository.save(order);
        return null;
    }
}
