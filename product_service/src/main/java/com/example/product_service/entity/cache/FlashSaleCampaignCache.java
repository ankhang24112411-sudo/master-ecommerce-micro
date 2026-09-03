package com.example.product_service.entity.cache;

import com.example.product_service.entity.FlashSaleCampaign;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FlashSaleCampaignCache {
    private Long version ;
    private FlashSaleCampaign flashSaleCampaign;
    private String productId;
    private String productName;
    private String categoryName;

    public FlashSaleCampaignCache withClone(FlashSaleCampaign flashSaleCampaign,String productName,String categoryName){
        this.flashSaleCampaign = flashSaleCampaign;
        this.productName = productName;
        this.categoryName = categoryName;
        return this;
    }
    public FlashSaleCampaignCache withVersion(Long version){
        this.version = version;
        return this;
    }

}
