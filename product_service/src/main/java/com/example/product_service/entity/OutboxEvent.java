package com.example.product_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

import java.time.Instant;
import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@SuperBuilder
@Table(name = "outbox_event", indexes = {
        @Index(name = "idx_status_created", columnList = "eventType, created_at")
})
public class OutboxEvent extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false, length = 64)
    private String aggregateId;

    @Column(nullable = false, length = 64)
    private String eventType;

    @Column(name ="status")
    private Integer status;


    @Column(nullable = false, columnDefinition = "TEXT")
    private String payload;

    @Column(name ="published_at")

    private Instant publishedAt;
}