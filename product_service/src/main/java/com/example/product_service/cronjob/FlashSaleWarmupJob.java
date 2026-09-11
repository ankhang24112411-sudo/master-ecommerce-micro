package com.example.product_service.cronjob;


import com.example.product_service.entity.cache.FlashSaleCampaignCache;
import com.example.product_service.infra.cache.RedisInfraService;
import com.example.product_service.repository.FlashSaleCampaignRepository;
import com.example.product_service.service.cache.flashsale.FlashSaleCacheServiceRefactor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j(topic = "FLASH-SALE-WARM-UP")
public class FlashSaleWarmupJob {
    //before flash sale 10mins will warm up
    private static final long WARMUP_BEFORE_FLASH_SALE = 10 * 60;
    private final FlashSaleCampaignRepository warmupRepository;
    private final FlashSaleCacheServiceRefactor flashSaleCacheServiceRefactor;
    private final RedisInfraService redisInfraService;

    //wait 30000 milis = 30s
    @Scheduled(fixedDelay = 30_000)
    public void warmUpcomingCampaigns() {
        Instant now = Instant.now();
        List<String> ids = warmupRepository.findIdsForWarmup(now, now.plusSeconds(WARMUP_BEFORE_FLASH_SALE));
        for (String flashSaleId : ids) {
            try {
                warmUpOne(flashSaleId);
            } catch (Exception e) {
                log.error("WARMUP FAILED: id={}", flashSaleId, e);
            }
        }
    }

    private void warmUpOne(String flashSaleId) {
        FlashSaleCampaignCache getInDBS = flashSaleCacheServiceRefactor.getFlashSaleDetail(flashSaleId, null);
        if (getInDBS == null || getInDBS.getFlashSaleCampaign() == null) {
            log.warn("WARUMP SKIPPED : Can not get campaign id ={}", flashSaleId);
            return;
        }
        var campaign = getInDBS.getFlashSaleCampaign();
        if (!Boolean.TRUE.equals(campaign.getIsDeleted())) {
            return;
        }
        if (campaign.getStartAt() == null || campaign.getEndAt() == null || !campaign.getEndAt().isAfter(campaign.getStartAt()) || campaign.getStock() == null || campaign.getStock() < 0) {
            throw new IllegalStateException("Campaign không đủ dữ liệu để warm up: " + flashSaleId);

        }
        String stockKey = "FLASHSALE:" + flashSaleId + ":STOCK";
        var redisTemplate = redisInfraService.getRedisTemplate();
        redisTemplate.opsForValue().setIfAbsent(stockKey,campaign.getStock() );
    }
}
