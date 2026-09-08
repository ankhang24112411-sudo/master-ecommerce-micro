package com.example.order_service.kafka.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class PlaceOrderMQMessage {

    private String token;
    private String flashSaleId;
    private String productId;
    private String userId;

    private int quantity;
    private BigDecimal unitPrice;
    private long timestamp;
}
