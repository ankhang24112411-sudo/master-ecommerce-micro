package com.example.product_service.consumers;

import com.example.product_service.consumers.dto.OrderCreatedEvent;
import com.example.product_service.consumers.dto.OrderEntity;
import com.example.product_service.dto.req.LockProductItem;
import com.example.product_service.dto.req.LockProductReq;
import com.example.product_service.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.annotation.BackOff;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j(topic = "Order Created Consumer")
@RequiredArgsConstructor
public class OrderCreatedConsumer {
    private final ProductService productService;
    private final ObjectMapper objectMapper;
    @KafkaListener(topics = "order_created")
    @RetryableTopic(
            attempts = "4",
            backOff = @BackOff(
                    delay = 2_000,
                    multiplier = 2.0
            ),
            exclude = {
                    NullPointerException.class,
                    IllegalArgumentException.class
            }
    )
    public void handleOrderCreatedEvent(String orderString) throws JacksonException {

        OrderCreatedEvent orderCreatedEvent =
                objectMapper.readValue(
                        orderString,
                        OrderCreatedEvent.class
                );

        List<LockProductItem> lockProductItems = new ArrayList<>();
        orderCreatedEvent.getOrderItems().forEach(orderItem -> {
            LockProductItem lockProductItem = new LockProductItem() ;
            lockProductItem.setId(orderItem.getProductId());
            lockProductItem.setQuantity(orderItem.getQuantity());
            lockProductItems.add(lockProductItem);
        });
        LockProductReq lockProductReq = new LockProductReq();
        lockProductReq.setItems(lockProductItems);
        productService.lock(lockProductReq);
    }
}
