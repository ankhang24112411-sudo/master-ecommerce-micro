package com.example.product_service.entity;



import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Table(name = "categories")
public class Category extends BaseEntity {
    @Id
    @GeneratedValue(generator = "uuid")
    private String id;

    private String name;

    @Column(name = "parent_id")
    private String parentId;
}