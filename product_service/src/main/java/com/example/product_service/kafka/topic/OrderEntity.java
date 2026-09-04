package com.example.product_service.kafka.topic;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrderEntity {


    private String id;

    private String customerId;

    private String status;

    private Integer totalAmount;

}

