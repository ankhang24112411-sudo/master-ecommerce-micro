package com.example.order_service.controller;

import com.example.order_service.dtos.BaseResponse;
import com.example.order_service.dtos.request.OrderRequest;
import com.example.order_service.dtos.request.PlaceOrderFlashSaleRequest;
import com.example.order_service.entity.OrderEntity;
import com.example.order_service.service.OrderService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/orders")
@RequiredArgsConstructor
@Validated
@Slf4j
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/place-order")
    @RateLimiter(name = "backendA", fallbackMethod = "fallbackCreateOrder")
    ResponseEntity<BaseResponse<OrderEntity>> createOrder(
            @Valid @RequestBody OrderRequest orderDTO) {

        return ResponseEntity.ok().body(
                new BaseResponse<>(orderService.createOrder(orderDTO),  null));
    }
    public String fallbackCreateOrder(Throwable throwable){
        return "Too much request";
    }
    @PostMapping("/place-order-flashsake")
    @RateLimiter(name = "backendB", fallbackMethod = "fallbackCreateOrder")
    ResponseEntity<BaseResponse<OrderEntity>> flashSaleOrderMQ(@Valid @RequestBody PlaceOrderFlashSaleRequest request) {
        return ResponseEntity.ok().body(new BaseResponse<>(orderService.flashSaleOrderMQ(request),  null));
    }
}
