package com.example.product_service.controller;


import com.example.product_service.dto.BaseResponse;
import com.example.product_service.dto.clients.ProductDTO;
import com.example.product_service.dto.clients.ProductFilter;
import com.example.product_service.entity.Product;
import com.example.product_service.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/products")
public class ProductController {
 private final ProductService productService;
    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<Product>> getDetail(@PathVariable String id, @RequestParam (name= "version",required = false)Long version) {
     return ResponseEntity.ok(new BaseResponse<>(productService.getProductById(id, version), "success"));
    }
@GetMapping("/search")
public ResponseEntity<BaseResponse<List<ProductDTO>>> search(@RequestBody ProductFilter productFilter) {

    List<ProductDTO> products = productService.search(productFilter);

    return ResponseEntity.ok(new BaseResponse<>(products, "success"));
}
    @PostMapping("/ids")

    public BaseResponse<List<ProductDTO>> decreaseQuantityByIds(@RequestBody @Valid List<ProductDTO> request) {

        productService.decreaseQuantityByIds(request);

        return new BaseResponse<>(productService.decreaseQuantityByIds(request), "ok");
    }
}