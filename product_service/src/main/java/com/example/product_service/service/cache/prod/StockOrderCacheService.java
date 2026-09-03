package com.example.product_service.service.cache.prod;

import com.example.product_service.entity.ProductCache;
import com.example.product_service.infra.cache.RedisInfraService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j(topic = "PRODUCT_STOCK_CACHE_SERVICE")
@RequiredArgsConstructor
public class StockOrderCacheService {
    // Lua script: Trừ stock trong Redis một cách ATOMIC
    private static final String LUA_STOCK_DEDUCT =
            // Lấy stock hiện tại từ Redis
            "local stock = redis.call('GET', KEYS[1]); " +

                    // Không có key stock trong Redis -> cache miss
                    //TODO return -1 để Java biết cần warm up lại từ DB
                    "if stock == false then return -1 end; " +

                    // Redis lưu string -> convert sang number
                    "stock = tonumber(stock); " +

                    // Nếu stock >= số lượng cần mua
                    "if (stock >= tonumber(ARGV[1])) then " +

                    // Trừ stock và lưu lại Redis
                    "   redis.call('SET', KEYS[1], stock - tonumber(ARGV[1])); " +

                    // TODO Trừ thành công
                    "   return 1; " +

                    //TODO Không đủ stock
                    "end; " +
                    "return 0; ";
    private static final DefaultRedisScript<Long> SCRIPT_DEDUCT = new DefaultRedisScript<>(LUA_STOCK_DEDUCT, Long.class);

    private ProductCacheServiceRefactor productCacheServiceRefactor;
    private RedisInfraService redisInfraService;

    public int decreaseStockCacheByLUA(String productId , Integer quantity){
        String key = getKeyStockCache(productId);
        Long result = redisInfraService.getRedisTemplate().execute(SCRIPT_DEDUCT, List.of(key), quantity);
        return result != null ? result.intValue() : -1;
    }

    public boolean addStockAvailableToCache(String productId) {
        if(productId == null){
            return false;
        }
        ProductCache productCache = productCacheServiceRefactor.getProductDetail(productId, null);
        if(productCache == null || productCache.getProduct() == null){
            return false;
        }
        String keyStockCache  = getKeyStockCache(productId);
        log.info("get-> getKeyStockCache | {} , {}, {}" ,productId, keyStockCache , productCache.getProduct().getStock());
        redisInfraService.setInt(keyStockCache, productCache.getProduct().getStock());
        return  true;
    }
    private String getKeyStockCache(String productId) {
        return "PRODUCT:"+ productId + ":STOCK";
    }

}
