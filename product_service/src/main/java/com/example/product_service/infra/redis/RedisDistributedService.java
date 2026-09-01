package com.example.product_service.infra.redis;

public interface RedisDistributedService {
    RedisDistributedLocker getDistributedLock(String lockKey);
}
