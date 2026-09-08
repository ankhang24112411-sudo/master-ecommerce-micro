package com.example.product_service.cronjob;

import com.example.product_service.service.cache.prod.StockOrderCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j(topic = "WARM-UP-SERVICE")
@RequiredArgsConstructor
public class WarmUpDataBeforeEvent {
    private StockOrderCacheService stockOrderCacheService;

}
