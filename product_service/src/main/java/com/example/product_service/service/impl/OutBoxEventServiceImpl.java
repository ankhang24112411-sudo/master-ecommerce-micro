package com.example.product_service.service.impl;

import com.example.product_service.entity.OutboxEvent;
import com.example.product_service.repository.OutboxEventRepository;
import com.example.product_service.service.OutboxEventService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OutBoxEventServiceImpl implements OutboxEventService {
    private final OutboxEventRepository outboxEventRepo;

    @Override
    public List<OutboxEvent> findPendingBatch(int limit) {
        return outboxEventRepo.findPending(PageRequest.of(0,limit)) ;
    }

    @Override
    @Transactional
    public void markPublished(String id, Instant publishedAt) {
        outboxEventRepo.markPublishedById(id, publishedAt);
    }

    @Override
    public void markPublishedBatch(List<String> ids, Instant publishedAt) {

    }
}
