package com.example.product_service.consumers.dto;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Table;
import lombok.*;
import org.springframework.data.annotation.Id;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderEntity {


    private String id;

    private String customerId;

    private String status;

    private Integer totalAmount;

}

