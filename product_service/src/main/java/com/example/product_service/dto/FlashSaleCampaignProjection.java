package com.example.product_service.dto;

import com.example.product_service.entity.FlashSaleCampaign;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;


public  interface FlashSaleCampaignProjection  {

    FlashSaleCampaign getFlashSaleCampaign();


    String getProductName();


    String getCategoryName();
}
