package com.example.product_service.service.impl;

import com.alibaba.fastjson.JSON;
import com.example.product_service.consumers.dto.FlashSaleOrderResponse;
import com.example.product_service.consumers.dto.PlaceOrderMQMessage;
import com.example.product_service.dto.FlashSaleCampaignProjection;
import com.example.product_service.entity.*;
import com.example.product_service.entity.cache.FlashSaleCampaignCache;
import com.example.product_service.exception.ApplicationErrors;
import com.example.product_service.repository.FlashSaleCampaignRepository;
import com.example.product_service.repository.IdempotencyKeyRepository;
import com.example.product_service.repository.OutboxEventRepository;
import com.example.product_service.service.FlashSaleService;
import com.example.product_service.service.cache.flashsale.IdempotencyKeyService;
import com.example.product_service.service.cache.flashsale.StockFlashSaleCache;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "FLASHSALE-SERVICE")
public class FlashSaleServiceImpl implements FlashSaleService {
    private final FlashSaleCampaignRepository flashSaleCampaignRepository;
    private final StockFlashSaleCache stockFlashSaleCache;
    private final IdempotencyKeyService idempotencyKeyService;
    private final TransactionTemplate transactionTemplate;
    private final OutboxEventRepository outboxEventRepo;
    @Override
    public FlashSaleCampaignCache findById(String flashSaleId) {
        FlashSaleCampaignProjection result = flashSaleCampaignRepository.findCacheById(flashSaleId);
        FlashSaleCampaignCache flashSaleCampaignCache = new FlashSaleCampaignCache().withClone(result.getFlashSaleCampaign(), result.getProductName(),result.getCategoryName());
        flashSaleCampaignCache.setProductName(result.getProductName());
        flashSaleCampaignCache.setCategoryName(result.getCategoryName());
        return flashSaleCampaignCache;
    }
    @Override
    public OrderQueue placeOrderMQ(String userId, String productId, int quantity) {
        //TODO bloom filter
        int redisResult = stockFlashSaleCache.decreaseFSStockCacheByLUA(productId, quantity);
        // -1 : Cannot find from REDIS
        if (redisResult == -1) {
            log.info("placeOrderMQ : cache miss for productId={}, warming up...", productId);
            boolean warmedUp = stockFlashSaleCache.addFSStockAvailableToCache(productId);

            if (!warmedUp) {
                return failedQueue("404", "PRODUCT_NOT_FOUND");
            }

            //decrease after data warm up

            redisResult = stockFlashSaleCache.decreaseFSStockCacheByLUA(productId, quantity);
        }
        if (redisResult == 0) {
            log.info("placeOrderMQ: Redis OOS for productId={}", productId);
            return failedQueue("409", "OUT_OF_STOCK");

        }
        BigDecimal unitPrice = stockFlashSaleCache.getEffectivePrice(productId);
        if (unitPrice.compareTo(BigDecimal.ZERO) <= 0) {
            stockFlashSaleCache.increaseStockCache(productId, quantity);
            return failedQueue("422", "PRICE_NOT_FOUND");
        }
        try {
            OrderQueue queue = transactionTemplate.execute(txStatus -> {
                String token = "MQ-" + UUID.randomUUID().toString().replace("-", "").substring(0, 16);
                boolean isNewOrder = idempotencyKeyService.tryInsert(token, LocalDateTime.now().plusHours(24));
                if (!isNewOrder) {
                    throw ApplicationErrors.INVALID_IDEMPOTENCY_KEY;
                }

                OrderQueue q = new OrderQueue()
                        .setToken(token)
                        .setProductId(productId)
                        .setQuantity(quantity)
                        .setUserId(userId)
                        .setCreatedAt(Instant.now());

                PlaceOrderMQMessage message = new PlaceOrderMQMessage(
                        token, productId, userId, quantity, unitPrice, System.currentTimeMillis()
                );
                OutboxEvent outboxEvent = new OutboxEvent()
                        .setAggregateId(token)
                        .setEventType("ORDER_PLACED")
                        .setPayload(JSON.toJSONString(message))
                        .setStatus(0)
                        .setCreatedAt(Instant.now());
                outboxEventRepo.save(outboxEvent);
                log.info("placeOrderMQ: queeued token ={}  {}", token, productId);

                return q;

            });
            return queue;

        } catch (Exception e) {
            stockFlashSaleCache.increaseStockCache(productId, quantity);
            log.error("placeOrderMQ: transaction failed, compensated Redis for ticketId={}", productId, e);
            return failedQueue("INTERNAL_ERROR", "Lỗi hệ thống, vui lòng thử lại");
        }
    }
    @Override
    public FlashSaleOrderResponse placeOrderMQv2(String userId, String productId, int quantity) {
        // 1. Trừ kho Redis Lua Script
        int redisResult = stockFlashSaleCache.decreaseFSStockCacheByLUA(productId, quantity);

        if (redisResult == -1) {
            log.info("placeOrderMQ: cache miss for productId={}, warming up...", productId);
            boolean warmedUp = stockFlashSaleCache.addFSStockAvailableToCache(productId);
            if (!warmedUp) {
                return FlashSaleOrderResponse.fail("404", "PRODUCT_NOT_FOUND");
            }
            redisResult = stockFlashSaleCache.decreaseFSStockCacheByLUA(productId, quantity);
        }

        if (redisResult == 0) {
            log.info("placeOrderMQ: Redis OOS for productId={}", productId);
            return FlashSaleOrderResponse.fail("409", "OUT_OF_STOCK");
        }

        BigDecimal unitPrice = stockFlashSaleCache.getEffectivePrice(productId);
        if (unitPrice.compareTo(BigDecimal.ZERO) <= 0) {
            stockFlashSaleCache.increaseStockCache(productId, quantity);
            return FlashSaleOrderResponse.fail("422", "PRICE_NOT_FOUND");
        }

        // 2. Lưu Outbox Event vào DB của Product Service
        try {
            return transactionTemplate.execute(txStatus -> {
                String token = "MQ-" + UUID.randomUUID().toString().replace("-", "").substring(0, 16);
                boolean isNewOrder = idempotencyKeyService.tryInsert(token, LocalDateTime.now().plusHours(24));
                if (!isNewOrder) {
                    throw ApplicationErrors.INVALID_IDEMPOTENCY_KEY;
                }

                PlaceOrderMQMessage message = new PlaceOrderMQMessage(
                        token, productId, userId, quantity, unitPrice, System.currentTimeMillis()
                );

                OutboxEvent outboxEvent = new OutboxEvent()
                        .setAggregateId(token)
                        .setEventType("ORDER_PLACED")
                        .setPayload(JSON.toJSONString(message))
                        .setStatus(0)
                        .setCreatedAt(Instant.now());

                outboxEventRepo.save(outboxEvent);
                log.info("placeOrderMQ: queued token={} productId={}", token, productId);

                return FlashSaleOrderResponse.success(token, productId, userId, quantity);
            });

        } catch (Exception e) {
            stockFlashSaleCache.increaseStockCache(productId, quantity);
            log.error("placeOrderMQ: transaction failed, compensated Redis for productId={}", productId, e);
            return FlashSaleOrderResponse.fail("INTERNAL_ERROR", "Lỗi hệ thống, vui lòng thử lại");
        }
    }
    private OrderQueue failedQueue(String code , String message){
        return new OrderQueue().setStatus(2).setMessage(code +": " + message);
    }
}
