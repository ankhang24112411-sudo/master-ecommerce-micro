package com.example.product_service.service.cache;

import com.example.product_service.entity.Product;
import com.example.product_service.entity.ProductCache;
import com.example.product_service.infra.cache.RedisInfraService;
import com.example.product_service.infra.redission.RedisDistributedLocker;
import com.example.product_service.infra.redission.RedisDistributedService;
import com.example.product_service.service.ProductService;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductCacheServiceRefactor {
    private final RedisDistributedService redisDistributedService;
    private final RedisInfraService redisInfraService;
    private final ProductService productService;

    private final static Cache<String, ProductCache> productLocalCache = CacheBuilder.newBuilder()
            .initialCapacity(10)
            .concurrencyLevel(12)
            .expireAfterWrite(100, TimeUnit.MINUTES)
            .build();

    public ProductCache getProductDetail(String productId, Long version){
        if(productId == null){
            return null;
        }
        ProductCache productCache = getProductLocalCache(productId);

        if(productCache != null){
            if(version == null){
                log.info("01: GET FROM LOCAL CACHE: versionUser:{}, versionLOcal: {}", version, productCache.getVersion());
                return productCache;
            }
            if(version.equals(productCache.getVersion())){
//                Hai version giống nhau → local đúng phiên bản → trả local.
                log.info("02: GET FROM LOCAL CACHE: versionUser:{}, versionLOcal: {}", version, productCache.getVersion());
                return productCache;
            }
            if(version < productCache.getVersion()){
//                Version phía gọi nhỏ hơn → local mới hơn → trả local.
                log.info("02: GET FROM LOCAL CACHE: versionUser:{}, versionLOcal: {}", version, productCache.getVersion());
                return productCache;
            }
            if(version > productCache.getVersion()){
//                Version phía gọi lớn hơn → local đang cũ → bỏ local, xuống Redis:
                return getProductDistributedCache(productId);
            }
        }
        return getProductDistributedCache(productId);

    }

    private ProductCache getProductDistributedCache(String productId) {
        ProductCache productCache = redisInfraService.getObject(genEventItemKey(productId), ProductCache.class);
        if(productCache == null){
            log.info("GET PRODUCT FROM DISTRIBUTED LOCK");
            productCache = getProductDatabase(productId);
        }
        productLocalCache.put(productId,productCache);
        log.info("GET PRODUCT FROM DISTRIBUTED CACHE | {} ", productCache.getProduct().getStock());
        return productCache;
    }

    private ProductCache getProductDatabase(String productId) {
        RedisDistributedLocker locker = redisDistributedService.getDistributedLock(genEventItemKeyLock(productId));

        try{
            boolean isLock = locker.tryLock(1 , 5 , TimeUnit.SECONDS);
        if(!isLock){
            return null;
        }
        ProductCache productCache = redisInfraService.getObject(genEventItemKey(productId), ProductCache.class);
        if(productCache != null){
            return productCache;
        }
        Product product = productService.getById(productId);

        if(product == null){
            return null;
        }
        productCache = new ProductCache().withClone(product).withVersion(System.currentTimeMillis());
        redisInfraService.setObject(genEventItemKey(productId),productCache);
        return productCache;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }finally {
            locker.unlock();
        }
    }

    private String genEventItemKey(String productId) {
        return "PRODUCT:" + productId;
    }

    private String genEventItemKeyLock(String productId) {
        return "PRODUCT_LOCK" + productId;
    }
    private ProductCache getProductLocalCache(String productId) {
        return productLocalCache.getIfPresent(productId);
    }
    public void invalidateProductCache(String productId){
        productLocalCache.invalidate(productId);
        redisInfraService.delete(genEventItemKey(productId));
    }
}
