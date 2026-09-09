package com.example.product_service.set_training;

import com.example.product_service.exception.ApplicationErrors;
import com.example.product_service.service.cache.flashsale.bloom.FlashSaleBloomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j(topic= "REDIS-SET")
public class RedisTraining {
    private final StringRedisTemplate redisTemplate;
    private final FlashSaleBloomService flashSaleBloomService;
    private static final String FLASH_SALE_STOCK = "flashsale-stock-test:{%s}:buyers";
    private static final String SOLD_OUT_FLAG = "flashsale:{%s}:soldout";
    public void initializeStock(String flashSaleId, long stock){
        String key = FLASH_SALE_STOCK.formatted(flashSaleId);
        redisTemplate.opsForValue().set(key, String.valueOf(stock));
    }
    public long getStock(String flashSaleId){
        String key = FLASH_SALE_STOCK.formatted(flashSaleId);
        return Long.parseLong(redisTemplate.opsForValue().get(key));
    }

    public void markSoldOut (String flashSaleId){
        String key = SOLD_OUT_FLAG.formatted(flashSaleId);
        redisTemplate.opsForValue().set(key, "true");
    }
    public boolean isSoldOut(String flashSaleId){
        String key = SOLD_OUT_FLAG.formatted(flashSaleId);
        String result = redisTemplate.opsForValue().get(key);
        return Boolean.parseBoolean(result);
    }
    public boolean buyCommand(String flashSaleId, String userId){
        if(flashSaleBloomService.hasPurchased(flashSaleId, userId)){
            throw ApplicationErrors.ACCESS_DENIED;
        }
        long stock = getStock(flashSaleId);

        if(stock < 0 ){
            return false;
        }
        return true;
    }
}

