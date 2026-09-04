package com.example.order_service.clients;

import com.example.order_service.dtos.BaseResponse;
import com.example.order_service.dtos.clientDTO.ProductDTO;
import com.example.order_service.dtos.clientDTO.ProductFilter;
import com.example.order_service.dtos.request.PlaceOrderFlashSaleRequest;
import com.example.order_service.exception.ApplicationErrors;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ProductClientImpl implements ProductClient{
    private final WebClient.Builder webClientBuilder;
    @Override
    @CircuitBreaker(name = "productService", fallbackMethod = "productServiceFallback")
    public List<ProductDTO> getProductByIds(ProductFilter productFilter) {
        BaseResponse<List<ProductDTO>> response = webClientBuilder.build()
                .post()
                .uri("http://product-service/v1/products/search")
                .bodyValue(productFilter)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<BaseResponse<List<ProductDTO>>>() {})
                .block();

        if (response == null || response.getData() == null) {
            throw ApplicationErrors.INVALID_SERVICE_RESPONSE;
        }

        return response.getData();
    }
    public ProductDTO productServiceFallback(String productId, Throwable throwable) {
        throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Product Service not response ", throwable);
    }
    @Override
    public void decreaseQuantityByIds(List<ProductDTO> productsDto) {
        BaseResponse<List<ProductDTO>> response = webClientBuilder.build()
                .put()
                .uri("http://product-service/v1/products/ids")
                .bodyValue(productsDto)
                .retrieve()
                .bodyToMono(
                        new ParameterizedTypeReference<BaseResponse<List<ProductDTO>>>() {
                        }
                )
                .block();

        if (response == null || response.getData() == null) {
            throw ApplicationErrors.INVALID_SERVICE_RESPONSE;
        }
    }

    @Override
    @CircuitBreaker(name = "productService", fallbackMethod = "productServiceFallback")
    public FlashSaleOrderResponse getFlashSaleResponse(PlaceOrderFlashSaleRequest request) {
        BaseResponse<FlashSaleOrderResponse> response = webClientBuilder.build()
                .put()
                .uri("http://product-service/v1/products/ids")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(
                        new ParameterizedTypeReference<BaseResponse<FlashSaleOrderResponse>>() {
                        }
                )
                .block();

        if (response == null || response.getData() == null) {
            throw ApplicationErrors.INVALID_SERVICE_RESPONSE;
        }
        return response.getData();
    }


}
