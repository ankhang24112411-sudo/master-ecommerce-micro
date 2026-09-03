package com.example.product_service.service.impl;

import com.example.product_service.repository.IdempotencyKeyRepository;
import com.example.product_service.service.cache.flashsale.IdempotencyKeyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class IdempotencyKeyServiceImpl implements IdempotencyKeyService {
    private final IdempotencyKeyRepository idempotencyKeyRepo;
    @Override
    public boolean tryInsert(String token, LocalDateTime expiresAt) {
        int affected = idempotencyKeyRepo.insertIgnore(token, LocalDateTime.now(), expiresAt);
        return affected == 1;    }
}
