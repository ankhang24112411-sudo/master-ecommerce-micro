package com.example.product_service.service;

import com.example.product_service.dto.CreateProductReq;
import com.example.product_service.dto.clients.ProductFilter;
import com.example.product_service.entity.Product;
import com.example.product_service.mapper.ProductMapper;
import com.example.product_service.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductMapper productMapper;
    private final CategoryRepository categoryRepository;
    @Override
    public Product create(CreateProductReq createProductReq) {

        var existedCategoryOptional =
                categoryRepository.findById(createProductReq.getCategoryId());

        if (existedCategoryOptional.isEmpty()) {
            throw new ApplicationException("category not found");
        }

        Product creatingProduct =
                productMapper.fromCreateRequest(createProductReq);

    }

    @Override
    public List<Product> search(ProductFilter productFilter) {
        return List.of();
    }

}