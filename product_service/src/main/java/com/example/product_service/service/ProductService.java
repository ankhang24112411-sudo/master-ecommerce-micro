package com.example.product_service.service;

import com.example.product_service.dto.CreateProductReq;
import com.example.product_service.dto.clients.ProductFilter;
import com.example.product_service.entity.Product;

import java.util.List;

public interface ProductService {
 Product create(CreateProductReq createProductReq);
 List<Product> search(ProductFilter productFilter);
}
