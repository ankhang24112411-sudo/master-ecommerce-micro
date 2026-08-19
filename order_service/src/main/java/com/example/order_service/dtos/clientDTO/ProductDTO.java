package com.example.order_service.dtos.clientDTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductDTO {
    private String id;
    private String name;
    private Integer price;
    private Integer stock;
    private String categoryId;
}
