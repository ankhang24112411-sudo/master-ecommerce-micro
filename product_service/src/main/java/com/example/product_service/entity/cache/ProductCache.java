package com.example.product_service.entity.cache;

import com.example.product_service.entity.Product;
import lombok.Data;

@Data
public class ProductCache {
    private Long version ;
    private Product product;
    public ProductCache withClone(Product product){
        this.product = product;
        return this;
    }
    public ProductCache withVersion(Long version){
        this.version = version;
        return this;
    }

}
