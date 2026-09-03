package com.example.product_service.service;

import com.example.product_service.dto.OrderQueue;
import com.example.product_service.dto.req.CreateProductReq;
import com.example.product_service.dto.clients.ProductDTO;
import com.example.product_service.dto.clients.ProductFilter;
import com.example.product_service.dto.req.LockProductReq;
import com.example.product_service.dto.req.UpdateProductReq;
import com.example.product_service.entity.Product;
import jakarta.validation.Valid;

import java.util.List;

public interface ProductService {
 Product create(CreateProductReq createProductReq);
 List<ProductDTO> search(ProductFilter productFilter);

 List<ProductDTO> decreaseQuantityByIds(@Valid List<ProductDTO> request);

 Product getById(String id);
 Product update(String id, UpdateProductReq updateProductReq);
 void lock(LockProductReq lockProductReq);

 Product getProductById(String productid , Long version);


 OrderQueue placeOrderMQ(String userId, String productId, int quantity);

// OrderQueue placeOrderMQ(String userId, String productId, int quantity);
}
