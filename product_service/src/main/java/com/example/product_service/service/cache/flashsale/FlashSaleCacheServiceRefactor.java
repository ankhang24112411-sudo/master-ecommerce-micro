package com.example.product_service.service.cache.flashsale;

import com.example.product_service.entity.cache.FlashSaleCampaignCache;
import com.example.product_service.infra.cache.RedisInfraService;
import com.example.product_service.infra.redission.RedisDistributedLocker;
import com.example.product_service.infra.redission.RedisDistributedService;
import com.example.product_service.service.FlashSaleService;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@RequiredArgsConstructor
public class FlashSaleCacheServiceRefactor {
    private final RedisDistributedService redisDistributedService;
    private final RedisInfraService redisInfraService;
    private final FlashSaleService flashSaleService;

    private final static Cache<String, FlashSaleCampaignCache> flashSaleLocalCache = CacheBuilder.newBuilder()
            .initialCapacity(10)
            .concurrencyLevel(12)
            .expireAfterWrite(100, TimeUnit.MINUTES)
            .build();

    public FlashSaleCampaignCache getFlashSaleDetail(String flashSaleId, Long version){
        if(flashSaleId == null){
            return null;
        }
        FlashSaleCampaignCache flashSaleCampaignCache = getFlashSaleLocalCache(flashSaleId);

        if(flashSaleCampaignCache != null){
            if(version == null){
                log.info("01: GET FROM LOCAL CACHE: versionUser:{}, versionLOcal: {}", version, flashSaleCampaignCache.getVersion());
                return flashSaleCampaignCache;
            }
            if(version.equals(flashSaleCampaignCache.getVersion())){
//                Hai version giống nhau → local đúng phiên bản → trả local.
                log.info("02: GET FROM LOCAL CACHE: versionUser:{}, versionLOcal: {}", version, flashSaleCampaignCache.getVersion());
                return flashSaleCampaignCache;
            }
            if(version < flashSaleCampaignCache.getVersion()){
//                Version phía gọi nhỏ hơn → local mới hơn → trả local.
                log.info("02: GET FROM LOCAL CACHE: versionUser:{}, versionLOcal: {}", version, flashSaleCampaignCache.getVersion());
                return flashSaleCampaignCache;
            }
            if(version > flashSaleCampaignCache.getVersion()){
// NOTE: version nạp vào lớn hơn => tìm trong REDIS
                return getFlashSaleDistributedCache(flashSaleId);
            }
        }
        return getFlashSaleDistributedCache(flashSaleId);

    }
    //check trong REDIS
    private FlashSaleCampaignCache getFlashSaleDistributedCache(String flashSaleId) {
        FlashSaleCampaignCache flashSaleCampaignCache = redisInfraService.getObject(genEventItemKey(flashSaleId), FlashSaleCampaignCache.class);
        if(flashSaleCampaignCache == null){
            log.info("GET PRODUCT FROM DISTRIBUTED LOCK");
            flashSaleCampaignCache = getFlashSaleDatabase(flashSaleId);
        }
        flashSaleLocalCache.put(flashSaleId,flashSaleCampaignCache);
        log.info("GET PRODUCT FROM DISTRIBUTED CACHE | {} ", flashSaleCampaignCache.getFlashSaleCampaign().getStock());
        return flashSaleCampaignCache;
    }
    //    Cache Breakdown / Cache Stampede
    private FlashSaleCampaignCache getFlashSaleDatabase(String flashSaleId) {
        RedisDistributedLocker locker = redisDistributedService.getDistributedLock(genEventItemKeyLock(flashSaleId));

        try{
            boolean isLock = locker.tryLock(1 , 5 , TimeUnit.SECONDS);
            if(!isLock){
                return null;
            }
            FlashSaleCampaignCache flashSaleCampaignCache = redisInfraService.getObject(genEventItemKey(flashSaleId), FlashSaleCampaignCache.class);
            if(flashSaleCampaignCache != null){
                return flashSaleCampaignCache;
            }
            FlashSaleCampaignCache flashSaleCampaignCacheFromDBS = flashSaleService.findById(flashSaleId);

            if(flashSaleCampaignCacheFromDBS == null){
                return null;
            }
            flashSaleCampaignCache = new FlashSaleCampaignCache()
                    .withClone(flashSaleCampaignCacheFromDBS.getFlashSaleCampaign(), flashSaleCampaignCacheFromDBS.getProductName(), flashSaleCampaignCacheFromDBS.getCategoryName())
                    .withVersion(System.currentTimeMillis());
            redisInfraService.setObject(genEventItemKey(flashSaleId),flashSaleCampaignCache);
            return flashSaleCampaignCache;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }finally {
            locker.unlock();
        }
    }

    private String genEventItemKey(String flashSaleId) {
        return "FLASH_SALE:" + flashSaleId;
    }

    private String genEventItemKeyLock(String flashSaleId) {
        return "FLASH_SALE_LOCK" + flashSaleId;
    }
    private FlashSaleCampaignCache getFlashSaleLocalCache(String flashSaleId) {
        return flashSaleLocalCache.getIfPresent(flashSaleId);
    }
//    public void invalidateProductCache(String productId){
//        productLocalCache.invalidate(productId);
//        redisInfraService.delete(genEventItemKey(productId));
//    }
}
