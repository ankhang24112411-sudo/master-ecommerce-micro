package com.example.order_service.service;

import com.example.order_service.clients.ProductClient;
import com.example.order_service.config.utils.OrderStatus;
import com.example.order_service.dtos.clientDTO.ProductDTO;
import com.example.order_service.dtos.clientDTO.ProductFilter;
import com.example.order_service.dtos.request.OrderItemRequest;
import com.example.order_service.dtos.request.OrderRequest;
import com.example.order_service.entity.OrderEntity;
import com.example.order_service.entity.OrderItemEntity;
import com.example.order_service.exception.ApplicationErrors;
import com.example.order_service.repository.OrderItemRepository;
import com.example.order_service.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService{
    private final ProductClient productClient;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepo;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    @Override
    public OrderEntity createOrder(OrderRequest request) {
        List<String> productIds = request.getOrderItems().stream()
                .map(OrderItemRequest::getProductId)
                .distinct()
                .toList();

        List<ProductDTO> products = productClient.getProductByIds(new ProductFilter(productIds));

        Map<String, ProductDTO> productPriceMap = new HashMap<>();

        products.forEach(productDTO -> {
            productPriceMap.put(productDTO.getId(), productDTO);
        });

        OrderEntity order = new OrderEntity();
        order.setCustomerId(request.getCustomerId());
        order.setStatus(OrderStatus.NEW.name());
        order.setTotalAmount(0);

        OrderEntity savedOrder = orderRepository.save(order);

        int totalAmount = 0;
        List<OrderItemEntity> orderItems = new ArrayList<>();

        for (var itemDTO : request.getOrderItems()) {

            ProductDTO productDTO =
                    productPriceMap.get(itemDTO.getProductId());

            if (productDTO == null) {
                throw ApplicationErrors.PRODUCT_NOT_FOUND;
            }

            if (itemDTO.getQuantity() > productDTO.getStock()) {
                throw ApplicationErrors.PRODUCT_NOT_FOUND;
            }

            Integer price = productDTO.getPrice();

            OrderItemEntity item = new OrderItemEntity();
            item.setOrderId(savedOrder.getId());
            item.setProductId(itemDTO.getProductId());
            item.setPrice(price);
            item.setQuantity(itemDTO.getQuantity());

            orderItems.add(item);

            totalAmount += price * itemDTO.getQuantity();
        }
        List<ProductDTO> buyProducts = orderItems.stream()
                        .map(orderItem -> ProductDTO
                                .builder()
                                .id(orderItem.getProductId())
                                .price(orderItem.getPrice())
                                .build()).toList();
        orderItemRepo.saveAll(orderItems);
        savedOrder.setTotalAmount(totalAmount);
        productClient.decreaseQuantityByIds(buyProducts);
        OrderEntity createdOrder = orderRepository.save(savedOrder);
        kafkaTemplate.send("order_created", createdOrder);
        return createdOrder;
    }
}
