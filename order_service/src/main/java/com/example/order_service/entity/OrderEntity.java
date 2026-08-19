package com.example.order_service.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Table;
import lombok.*;
import org.springframework.data.annotation.Id;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderEntity extends BaseEntity{

    @Id
    @GeneratedValue(generator = "uuid")

    @Column(name = "id", length = 36)
    private String id;

    @Column(name = "customer_id", length = 36)
    private String customerId;

    @Column(name = "status")
    private String status;

    @Column(name = "total_amount")
    private Integer totalAmount;

}

