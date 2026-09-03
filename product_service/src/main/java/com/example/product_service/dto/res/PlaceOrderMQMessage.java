package com.example.product_service.dto.res;

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
    private String userId;

    private int quantity;
    private BigDecimal unitPrice;
    private long timestamp;
}
