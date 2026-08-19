package com.example.product_service.controller;


import com.example.product_service.dto.BaseResponse;
import com.example.product_service.dto.CreateProductReq;
import com.example.product_service.entity.Product;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/products")
public class ProductController {

//    @PostMapping
//    public ResponseEntity<BaseResponse<Product>> create(@RequestBody @Valid CreateProductReq req) {
//
//    }
@PostMapping("/search")
public ResponseEntity<BaseResponse<List<Product>>> search(
        @RequestBody ProductFilter productFilter) {

    List<Product> products = productService.search(productFilter);

    return ResponseEntity.ok(
            new BaseResponse<>(
                    products,
                    "success",
                    null
            )
    );
}
}