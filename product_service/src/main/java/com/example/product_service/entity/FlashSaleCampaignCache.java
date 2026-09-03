package com.example.product_service.entity;

import lombok.Data;

@Data
public class FlashSaleCampaignCache {
    private Long version ;
    private FlashSaleCampaign flashSaleCampaign;
    private String productName;
    private String categoryName;

    public FlashSaleCampaignCache withClone(FlashSaleCampaign flashSaleCampaign){
        this.flashSaleCampaign = flashSaleCampaign;
        return this;
    }
    public FlashSaleCampaignCache withVersion(Long version){
        this.version = version;
        return this;
    }

}
