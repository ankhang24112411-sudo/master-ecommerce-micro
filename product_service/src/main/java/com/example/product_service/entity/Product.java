package com.example.product_service.entity;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Table(name = "products")
public class Product extends BaseEntity{
    @Id
    @GeneratedValue(generator = "uuid")
    private String id;

    private String name;

    private Integer price;

    private Integer stock;

    @Column(name = "category_id")
    private String categoryId;
}
