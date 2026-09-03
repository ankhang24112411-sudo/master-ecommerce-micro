package com.example.product_service.service.cache.flashsale;

import com.example.product_service.entity.cache.FlashSaleCampaignCache;
import com.example.product_service.infra.cache.RedisInfraService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

@Service
@Slf4j(topic = "STOCK_CACHE_SERVICE")
@RequiredArgsConstructor
public class StockFlashSaleCache {
    // Lua script: Trừ stock trong Redis một cách ATOMIC
    private static final String LUA_FLASHSALE_STOCK_DEDUCT =
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
    // Lua script: Hoàn trả stock khi order fail/cancel
    private static final String LUA_RESTORE =
            // Lấy stock hiện tại
            "local stock = redis.call('GET', KEYS[1]); " +

                    // Nếu key tồn tại
                    "if (stock) then " +

                    // Cộng trả lại stock
                    "   redis.call('SET', KEYS[1], tonumber(stock) + tonumber(ARGV[1])); " +

                    // Restore thành công
                    "   return 1; " +

                    // Không tìm thấy key
                    "end; " +
                    "return 0;";
    private static final DefaultRedisScript<Long> SCRIPT_DEDUCT = new DefaultRedisScript<>(LUA_FLASHSALE_STOCK_DEDUCT, Long.class);
    private static final DefaultRedisScript<Long> SCRIPT_RESTORE = new DefaultRedisScript<>(LUA_RESTORE, Long.class);

    private final FlashSaleCacheServiceRefactor flashSaleCacheServiceRefactor;
    private final RedisInfraService redisInfraService;

    public int decreaseFSStockCacheByLUA(String flashSaleId , Integer quantity){
        String key = getKeyFSStockCache(flashSaleId);
        Long result = redisInfraService.getRedisTemplate().execute(SCRIPT_DEDUCT, List.of(key), quantity);
        return result != null ? result.intValue() : -1;
    }

    public boolean addFSStockAvailableToCache(String flashSaleId) {
        if(flashSaleId == null){
            return false;
        }
        //hold from DBS
        FlashSaleCampaignCache flashSaleCampaignCache = flashSaleCacheServiceRefactor.getFlashSaleDetail(flashSaleId, null);
        if(flashSaleCampaignCache == null || flashSaleCampaignCache.getFlashSaleCampaign() == null){
            return false;
        }
        String keyFSStockCache  = getKeyFSStockCache(flashSaleId);
        log.info("get-> getKeyStockCache | {} , {}, {}" ,flashSaleId, keyFSStockCache , flashSaleCampaignCache.getFlashSaleCampaign().getStock());
        redisInfraService.setInt(keyFSStockCache, flashSaleCampaignCache.getFlashSaleCampaign().getStock());
        return  true;
    }

    private String getKeyFSStockCache(String flashSaleId) {
        return "FLASHSALE:"+ flashSaleId + ":STOCK";
    }
    public boolean increaseStockCache(String flashSaleId, Integer quantity) {
        String key = getKeyFSStockCache(flashSaleId);
        Long result = redisInfraService.getRedisTemplate().execute(SCRIPT_RESTORE, Collections.singletonList(key), quantity);
        return result != null && result == 1;
    }
}
