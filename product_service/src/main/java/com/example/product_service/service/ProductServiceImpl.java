package com.example.product_service.service;

import com.example.product_service.dto.clients.ProductValid;
import com.example.product_service.dto.req.CreateProductReq;
import com.example.product_service.dto.clients.ProductDTO;
import com.example.product_service.dto.clients.ProductFilter;
import com.example.product_service.entity.Product;
import com.example.product_service.exception.ApplicationErrors;
import com.example.product_service.mapper.ProductMapper;
import com.example.product_service.repository.CategoryRepository;
import com.example.product_service.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductMapper productMapper;
    private final ProductRepository productRepo;
    private final CategoryRepository categoryRepository;
    @Override
    public Product create(CreateProductReq createProductReq) {

        var existedCategoryOptional =
                categoryRepository.findById(createProductReq.getCategoryId());

        if (existedCategoryOptional.isEmpty()) {
            throw ApplicationErrors.PRODUCT_NOT_FOUND;
        }

        Product creatingProduct =
                productMapper.fromCreateRequest(createProductReq);
       return productRepo.save(creatingProduct);
    }

    @Override
    public List<ProductDTO> search(ProductFilter productFilter) {
        return productRepo.findAllByIdn(productFilter.getIds())
                .stream()
                .map(productMapper::toProductDTO)
                .toList();
    }

    @Override
    public List<ProductDTO> decreaseQuantityByIds(List<ProductDTO> productsDTO) {
//product valid from user
        List<ProductValid> groupProductById = productsDTO.stream()
                .collect(Collectors.groupingBy(ProductDTO::getId,
                        Collectors.summingInt(ProductDTO::getStock)
                ))
                .entrySet()
                .stream()
                .map(e -> new ProductValid(e.getKey(), e.getValue()))
                .toList();

        List<String> ids = groupProductById.stream()
                .map(ProductValid::getId)
                .toList();
//list product in repo
        List<Product> products =
                productRepo.findByIdInAndIsDeleted(ids, false);

        Map<String, Product> productDtoMap = new HashMap<>();

        for (Product product : products) {
            productDtoMap.putIfAbsent(product.getId(), product);
        }

        List<Product> productHadChecked = groupProductById.stream()
                .map(productValid -> {
                    Product product = productDtoMap.get(productValid.getId());
                            if (product == null) {
                              throw ApplicationErrors.PRODUCT_NOT_FOUND;
                            }
                    if(product.getStock() < productValid.getQuantity()) {
                        throw ApplicationErrors.INVENTORY_NOT_ENOUGH;
                        }

         int quantity = product.getStock() - productValid.getQuantity();

          product.setStock(quantity);
                            return product;
                }).toList();




//        for (ProductValid productValid : groupProductById) {
//
//            if (!productDtoMap.containsKey(productValid.getId())) {
//                throw new BusinessException("Product is not exist");
//            }
//
//            Product product = productDtoMap.get(productValid.getId());
//
//            if (product.getQuantity() < productValid.getQuantity()) {
//                throw new BusinessException("Stock not enough product");
//            }
//
//            int quantity =
//                    product.getQuantity() - productValid.getQuantity();
//
//            product.setQuantity(quantity);
//
//            productHadChecked.add(product);
//        }

        productRepo.saveAll(productHadChecked);

        return productHadChecked.stream()
                .map(product -> {
                    ProductDTO dto = new ProductDTO();
                    dto.setId(product.getId());
                    dto.setStock(product.getStock());
                    return dto;
                })
                .toList();
        }
    }

