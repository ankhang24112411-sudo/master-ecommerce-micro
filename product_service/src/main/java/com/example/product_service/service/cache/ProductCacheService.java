package com.example.product_service.service.cache;

import com.example.product_service.entity.Product;
import com.example.product_service.infra.cache.RedisInfraService;
import com.example.product_service.infra.redission.RedisDistributedService;
import com.example.product_service.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import java.util.concurrent.TimeUnit;

@Service
@Slf4j(topic = "PRODUCT-CACHE-SERVICE")
@RequiredArgsConstructor
public class ProductCacheService {
    private RedisDistributedService redisDistributedService;
    private RedisInfraService redisInfraService;
    private ProductService productService;
    private String genEventItemKey(String itemId) {
        return "PRO_TICKET:ITEM:" + itemId;
    }
    private final static Cache<String, Product> productLocalCache = CacheBuilder.newBuilder()
            .initialCapacity(10)
            .concurrencyLevel(12)
            .expireAfterWrite(100, TimeUnit.MINUTES)
            .build();

    //TODO First Level : Redis easy
    //Redis has it => response
    //if not => goes to DBS => response
    public Product getProductDefaultCacheNormal(String id , Long version) {
        Product product = redisInfraService.getObject(genEventItemKey(id), Product.class);
        if (product != null) {
            return product;
        }
        product = productService.getById(id);
        if (product != null) {
            redisInfraService.setObject(genEventItemKey(id), product);
        }

        return product;
    }
   //TODO Second level : Redis (Cache Aside) + distributed Lock
    public Product getProductDefaultCacheVip(String id ,Long version){
        Product product = redisInfraService.getObject()
    }

}
