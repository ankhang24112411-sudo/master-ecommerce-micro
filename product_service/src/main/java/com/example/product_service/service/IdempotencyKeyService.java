package com.example.product_service.service;

import java.time.LocalDateTime;


public interface IdempotencyKeyService {
    public boolean tryInsert(String token, LocalDateTime expiresAt) ;

    }
