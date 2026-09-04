package com.example.order_service.clients;

import com.example.order_service.dtos.clientDTO.ProductDTO;
import com.example.order_service.dtos.clientDTO.ProductFilter;
import com.example.order_service.dtos.request.PlaceOrderFlashSaleRequest;
import com.example.order_service.dtos.resp.FlashSaleOrderResponse;

import java.util.List;

public interface ProductClient {
    List<ProductDTO> getProductByIds(ProductFilter productFilter);
   void  decreaseQuantityByIds(List<ProductDTO> productsDto);
   FlashSaleOrderResponse getFlashSaleResponse(PlaceOrderFlashSaleRequest request);
}
