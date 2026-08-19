package com.example.product_service.dto.clients;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ProductValid {

    private String id;
    private Integer quantity;
}