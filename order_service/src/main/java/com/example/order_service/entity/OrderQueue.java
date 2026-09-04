package com.example.order_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "order_queue")
public class OrderQueue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;

    @Column(unique = true, nullable = false, length = 64)
    private String token;

    private String productId;
    private String flashSaleId;
    private int quantity;
    private int userId;

    // 0=PENDING, 1=SUCCESS, 2=FAILED
    private int status;

    private String orderNumber;
    private String message;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
