package com.example.order_service.service.cache;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

@Service
@Slf4j(topic = "STOCK - ORDER - CACHE - SERVICE")
public class StockOrderCacheService {
    private static final String LUA_STOCK_DEDUCT =  // Lấy stock hiện tại từ Redis
            "local stock = redis.call('GET', KEYS[1]); " +

                    // Không có key stock trong Redis -> cache miss
                    // return -1 để Java biết cần warm up lại từ DB
                    "if stock == false then return -1 end; " +

                    // Redis lưu string -> convert sang number
                    "stock = tonumber(stock); " +

                    // Nếu stock >= số lượng cần mua
                    "if (stock >= tonumber(ARGV[1])) then " +

                    // Trừ stock và lưu lại Redis
                    "   redis.call('SET', KEYS[1], stock - tonumber(ARGV[1])); " +

                    // Trừ thành công
                    "   return 1; " +

                    // Không đủ stock
                    "end; " +
                    "return 0; ";
    private static final DefaultRedisScript<Long> SCRIPT_DEDUCT = new DefaultRedisScript<>(LUA_STOCK_DEDUCT, Long.class);
}
