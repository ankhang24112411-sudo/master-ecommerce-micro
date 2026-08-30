package com.example.product_service.mapper;

import com.example.product_service.dto.req.CreateProductReq;
import com.example.product_service.dto.clients.ProductDTO;
import com.example.product_service.entity.Product;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface ProductMapper {

    Product fromCreateRequest(CreateProductReq createProductReq);

    ProductDTO toProductDTO(Product product);
//    Product  fromCreateRequest(CreateProductReq createProductReq);
}
