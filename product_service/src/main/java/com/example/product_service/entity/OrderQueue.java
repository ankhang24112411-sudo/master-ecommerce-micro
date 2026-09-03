package com.example.product_service.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.Instant;
import java.time.LocalDateTime;

    @Data
    @Accessors(chain = true)
    @NoArgsConstructor
    public class OrderQueue {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Column(unique = true, nullable = false, length = 64)
        private String token;

        private String productId;
        private int quantity;
        private String userId;

        // 0=PENDING, 1=SUCCESS, 2=FAILED
        private int status;

        private String orderNumber;
        private String message;

        private Instant createdAt;
}
