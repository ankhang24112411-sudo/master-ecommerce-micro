package com.example.product_service.service;

import com.example.product_service.entity.OrderQueue;
import com.example.product_service.entity.cache.FlashSaleCampaignCache;

public interface FlashSaleService {
     FlashSaleCampaignCache findById(String flashSaleId);

     OrderQueue placeOrderMQ(String userId, String productId, int quantity) ;

    }
