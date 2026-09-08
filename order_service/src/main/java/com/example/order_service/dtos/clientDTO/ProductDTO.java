package com.example.order_service.dtos.clientDTO;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {
    private String id;
    private String name;
    private BigDecimal price;
    private Integer stock;
    private String categoryId;
}
