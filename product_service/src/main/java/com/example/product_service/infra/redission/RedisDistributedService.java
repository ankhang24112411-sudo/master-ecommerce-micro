package com.example.product_service.infra.redission;

public interface RedisDistributedService {
    RedisDistributedLocker getDistributedLock(String lockKey);
}
