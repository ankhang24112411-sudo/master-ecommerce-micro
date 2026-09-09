package com.example.product_service.service;

import com.example.product_service.entity.OutboxEvent;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

public interface OutboxEventService {
    List<OutboxEvent> findPendingBatch(int limit);
    void markPublished(String id, Instant publishedAt);
    void markPublishedBatch(List<String> ids, Instant publishedAt);
}
