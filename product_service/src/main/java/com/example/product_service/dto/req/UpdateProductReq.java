package com.example.product_service.dto.req;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class UpdateProductReq {

    private String name;
    private String description;
    private BigDecimal price;
    private Integer stock;
    private String categoryId;
}