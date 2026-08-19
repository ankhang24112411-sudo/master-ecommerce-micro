package com.example.order_service.clients;

import com.example.order_service.dtos.BaseResponse;
import com.example.order_service.dtos.clientDTO.ProductDTO;
import com.example.order_service.dtos.clientDTO.ProductFilter;
import com.example.order_service.exception.ApplicationErrors;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ProductClientImpl implements ProductClient{
    private final WebClient.Builder webClientBuilder;
    @Override
    public List<ProductDTO> getProductByIds(ProductFilter productFilter) {
        BaseResponse<List<ProductDTO>> response = webClientBuilder.build()
                .post()
                .uri("http://localhost:8088/v1/products/search")
                .bodyValue(productFilter)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<BaseResponse<List<ProductDTO>>>() {})
                .block();

        if (response == null || response.getData() == null) {
            throw ApplicationErrors.INVALID_SERVICE_RESPONSE;
        }

        return response.getData();
    }

    @Override
    public void decreaseQuantityByIds(List<ProductDTO> productsDto) {
        BaseResponse<List<ProductDTO>> response = webClientBuilder.build()
                .put()
                .uri("http://localhost:8088/v1/products/ids")
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
}
