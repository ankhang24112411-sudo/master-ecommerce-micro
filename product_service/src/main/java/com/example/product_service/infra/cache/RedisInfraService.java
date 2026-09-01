package com.example.product_service.infra.cache;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.data.redis.core.RedisTemplate;

public interface RedisInfraService {
    void setString(String key, String value);
    String getString(String key);
    void setObject(String key, Object value);
    <T> T getObject(String key, Class<T> targetClass) ;
    void delete(String key);

    RedisTemplate<String , Object> getRedisTemplate();

    void setInt(String key, int value);

    int getInt(String key);
}
