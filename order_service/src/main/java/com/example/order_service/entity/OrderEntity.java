package com.example.order_service.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
@Entity
@Table(name = "orders")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class OrderEntity extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", length = 36, nullable = false, updatable = false)
    private String id;

    @Column(name = "customer_id", length = 36)
    private String customerId;

    @Column(name = "status")
    private String status;

    @Column(name = "total_amount")
    private Integer totalAmount;

}

