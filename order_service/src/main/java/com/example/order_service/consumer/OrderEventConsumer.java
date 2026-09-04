package com.example.order_service.consumer;

import com.example.order_service.config.utils.OrderStatus;
import com.example.order_service.consumer.event.InventoryReservedEvent;
import com.example.order_service.consumer.event.PaymentEvent;
import com.example.order_service.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.BackOff;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.stereotype.Component;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

@Component
@Slf4j(topic = "Order-Event-Consumer")
@RequiredArgsConstructor
public class OrderEventConsumer {
    private final OrderService orderService;
    private final ObjectMapper objectMapper;
    @KafkaListener(topics = "payment")
    @RetryableTopic(attempts = "4", backOff = @BackOff(delay = 2_000, multiplier = 2.0),
            exclude = {NullPointerException.class, IllegalArgumentException.class}
    )
    public void handlePaymentEvent(String orderString) throws JacksonException{
        PaymentEvent paymentEvent = objectMapper.readValue(orderString, PaymentEvent.class);
        log.info("Payment completed event {}", paymentEvent.getOrderId());

        orderService.handlePaymentEvent(paymentEvent);
    }

    @KafkaListener(topics = "inventory-reserved")
    @RetryableTopic(attempts = "4", backOff = @BackOff(delay = 2_000, multiplier = 2.0),
            exclude = {NullPointerException.class, IllegalArgumentException.class}
    )
    public void handleInventoryReservedEvent(String orderString) throws JacksonException{
        InventoryReservedEvent inventoryReservedEvent = objectMapper.readValue(orderString, InventoryReservedEvent.class);
        log.info("Payment completed event {}", inventoryReservedEvent.getOrderId());
        orderService.handleInventoryReservedEvent(inventoryReservedEvent);
    }

}
