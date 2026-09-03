package com.example.product_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "outbox_event", indexes = {
        @Index(name = "idx_status_created", columnList = "status, created_at")
})
public class OutboxEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private Long id;

    @Column(nullable = false, length = 64)
    private String aggregateId;

    @Column(nullable = false, length = 64)
    private String eventType;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String payload;

    private int status;

    private LocalDateTime createdAt;
    private LocalDateTime publishedAt;
}