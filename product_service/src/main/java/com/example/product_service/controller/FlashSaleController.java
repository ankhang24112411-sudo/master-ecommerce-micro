package com.example.product_service.controller;

import com.example.product_service.dto.BaseResponse;
import com.example.product_service.dto.req.OrderCreationRequest;
import com.example.product_service.entity.OrderQueue;
import com.example.product_service.entity.Product;
import com.example.product_service.service.FlashSaleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/flashsale-engine")
public class FlashSaleController {
    private final FlashSaleService flashSaleService;
    @GetMapping("/")
    public ResponseEntity<BaseResponse<?>> flashSaleOrderMQ(@RequestBody OrderCreationRequest request) {
        return ResponseEntity.ok(new BaseResponse<>(flashSaleService.placeOrderMQv2(request.getUserId(), request.getProductId(), request.getQuantity()), "ok"));
    }
}
