package com.example.product_service.dto.req;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateProductReq {

    @NotEmpty
    private String name;

    private Integer price;

    private Integer stock;

    private String categoryId;
}