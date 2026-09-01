package com.example.product_service.infra.redis;

public interface RedisInfraService {
    void setObject(String key, Object value);
    <T> T getObject(String key, Class<T> targetClass);

}
