package com.example.product_service.mapper;

import com.example.product_service.dto.req.CreateProductReq;
import com.example.product_service.dto.clients.ProductDTO;
import com.example.product_service.dto.req.UpdateProductReq;
import com.example.product_service.entity.Product;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;


@Mapper(componentModel = "spring")
public interface ProductMapper {

    Product fromCreateRequest(CreateProductReq createProductReq);

    ProductDTO toProductDTO(Product product);
//    Product  fromCreateRequest(CreateProductReq createProductReq);
@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
Product fromUpdateRequest(UpdateProductReq request, @MappingTarget Product product);

}
