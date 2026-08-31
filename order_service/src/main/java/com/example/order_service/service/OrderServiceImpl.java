package com.example.order_service.service;

import com.example.order_service.clients.ProductClient;
import com.example.order_service.config.utils.OrderStatus;
import com.example.order_service.consumer.event.InventoryReservedEvent;
import com.example.order_service.consumer.event.PaymentEvent;
import com.example.order_service.dtos.clientDTO.ProductDTO;
import com.example.order_service.dtos.clientDTO.ProductFilter;
import com.example.order_service.dtos.events.OrderCreatedEvent;
import com.example.order_service.dtos.events.OrderItemEvent;
import com.example.order_service.dtos.request.OrderItemRequest;
import com.example.order_service.dtos.request.OrderRequest;
import com.example.order_service.entity.OrderEntity;
import com.example.order_service.entity.OrderItemEntity;
import com.example.order_service.exception.ApplicationErrors;
import com.example.order_service.repository.OrderItemRepository;
import com.example.order_service.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
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
        order.setStatus(OrderStatus.PENDING.name());
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

        orderItemRepo.saveAll(orderItems);
        savedOrder.setTotalAmount(totalAmount);
        OrderEntity createdOrder = orderRepository.save(savedOrder);

        List<OrderItemEvent> eventItems = orderItems.stream()
                .map(item -> OrderItemEvent.builder()
                        .productId(item.getProductId())
                        .quantity(item.getQuantity())
                        .price(item.getPrice()).build()).toList();

        OrderCreatedEvent event = OrderCreatedEvent.builder()
                .orderId(createdOrder.getId())
                .customerId(createdOrder.getCustomerId())
                .orderItems(eventItems)
                .build();
        kafkaTemplate.send("order_created", event);
//        Order Service gửi object, JsonSerializer biến object thành JSON;
//        Product Service nhận JSON dưới dạng String rồi tự map lại thành object.
        log.info("Publish new order success to order_created");
        return createdOrder;
    }

    @Override
    public void updateOrderStatus(String orderId, OrderStatus status) {
        orderRepository.findById(orderId).ifPresent(order -> {
            order.setStatus(status.name());

        });
    }

    @Override
    public void handleInventoryReservedEvent(InventoryReservedEvent inventoryReservedEvent) {
      orderRepository.findById(inventoryReservedEvent.getOrderId()).ifPresent(orderEntity -> {
            if(!orderEntity.getStatus().equals(OrderStatus.PENDING.name())){
                throw ApplicationErrors.INVALID_ORDER_STATUS;
            }
            if(inventoryReservedEvent.getStatus().equals("SUCCESS")){
                orderEntity.setStatus(OrderStatus.STOCK_RESERVED.name());
            }else {
                orderEntity.setStatus(OrderStatus.FAILED.name());
            }
        });
    }

    @Override
    public void handlePaymentEvent(PaymentEvent paymentEvent) {
        orderRepository.findById(paymentEvent.getOrderId()).ifPresent(orderEntity -> {
            if(!orderEntity.getStatus().equals(OrderStatus.STOCK_RESERVED.name())){
                throw ApplicationErrors.INVALID_ORDER_STATUS;
            }
            if(paymentEvent.getStatus().equals("SUCCESS")){
                orderEntity.setStatus(OrderStatus.PAYMENT_RECEIVED.name());
            }else {
                orderEntity.setStatus(OrderStatus.FAILED.name());
            }
        });
    }

}
