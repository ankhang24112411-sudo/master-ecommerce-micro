package com.example.product_service.kafka.topic;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)

public class OrderCreatedEvent {
    private String orderId;
    private List<OrderItemEvent> orderItems;
}
