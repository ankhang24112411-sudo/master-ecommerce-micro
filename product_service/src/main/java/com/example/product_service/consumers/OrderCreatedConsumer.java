package com.example.product_service.consumers;

import com.example.product_service.consumers.dto.OrderCreatedEvent;
import com.example.product_service.consumers.dto.OrderEntity;
import com.example.product_service.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.util.ArrayList;

@Component
@Slf4j(topic = "Order Created Consumer")
@RequiredArgsConstructor
public class OrderCreatedConsumer {
    private final ProductService productService;
    private final ObjectMapper objectMapper;
    @KafkaListener(topics = "order_created")
    public void handleOrderCreatedEvent(String orderString){
        OrderCreatedEvent orderCreatedEvent = objectMapper.readValue(orderString, OrderCreatedEvent.class);
        List<LockProductItem> lockProductItems = new ArrayList<>();
        log.info("");
    }
}
