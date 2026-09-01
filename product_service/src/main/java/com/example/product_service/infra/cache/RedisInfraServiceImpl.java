package com.example.product_service.infra.cache;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.Map;
import java.util.Optional;

@Component
@Slf4j
public class RedisInfraServiceImpl implements RedisInfraService {
    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public void setString(String key, String value) {
        if(StringUtils.hasLength(key)){ // null or ''
            return;
        }
        redisTemplate.opsForValue().set(key,value);
    }

    @Override
    public String getString(String key) {
//        Object result = redisTemplate.opsForValue().get(key);
//        if(result == null){
//            return null;
//        }
//        return String.valueOf(result);
        return Optional.ofNullable(redisTemplate.opsForValue().get(key))
                .map(String::valueOf)
                .orElse(null);
    }

    @Override
    public void setObject(String key, Object value) {
      if(!StringUtils.hasLength(key)){
          return ;
      }
      try{
          redisTemplate.opsForValue().set(key, value);
      }catch (Exception e){
          log.error("setObj error : key={}, error={}", key, e.getMessage(), e);
      }
      Object result = redisTemplate.opsForValue().get(key);
      log.info("Set redis::{}", result != null && result.equals(value));
    }

    @Override
    public <T> T getObject(String key, Class<T> targetClass) {
        Object result = redisTemplate.opsForValue().get(key);
//        log.info("get Cache::{}", result);
        if (result == null) {
            return null;
        }
//        try {
//            log.info("get Cache::1{}", JSON.parseObject((String) result, targetClass));
//            return JSON.parseObject((String) result, targetClass);
//        } catch (Exception e) {
//            log.error("error Cache::{}", e);
//            return null;
//        }
        // Nếu kết quả là một LinkedHashMap
        if (result instanceof Map) {
            try {
                // Chuyển đổi LinkedHashMap thành đối tượng mục tiêu
                com.fasterxml.jackson.databind.ObjectMapper objectMapper = new com.fasterxml.jackson.databind.ObjectMapper();
                return objectMapper.convertValue(result, targetClass);
            } catch (IllegalArgumentException e) {
//                log.error("Error converting LinkedHashMap to object: {}", e.getMessage());
                return null;
            }
        }

        // Nếu result là String, thực hiện chuyển đổi bình thường
        if (result instanceof String) {
            try {
                com.fasterxml.jackson.databind.ObjectMapper objectMapper = new ObjectMapper();
                return objectMapper.readValue((String) result, targetClass);
            } catch (JsonProcessingException e) {
                log.error("Error deserializing JSON to object: {}", e.getMessage());
                return null;
            }
        }

        return null; // hoặc ném ra một ngoại lệ tùy ý
    }


    @Override
    public void delete(String key) {
        redisTemplate.delete(key);

    }

    @Override
    public RedisTemplate<String, Object> getRedisTemplate() {
        return redisTemplate;
    }

    @Override
    public void setInt(String key, int value) {
        redisTemplate.opsForValue().set(key, value);

    }

    @Override
    public int getInt(String key) {
        return (int) redisTemplate.opsForValue().get(key);
    }
}
