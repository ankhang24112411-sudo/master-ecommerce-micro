package com.example.product_service.service.cache.flashsale;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;


public interface IdempotencyKeyService {
    public boolean tryInsert(String token, LocalDateTime expiresAt) ;

    }
