package com.example.product_service.service;

import com.example.product_service.dto.clients.ProductValid;
import com.example.product_service.dto.req.CreateProductReq;
import com.example.product_service.dto.clients.ProductDTO;
import com.example.product_service.dto.clients.ProductFilter;
import com.example.product_service.dto.req.LockProductItem;
import com.example.product_service.dto.req.LockProductReq;
import com.example.product_service.entity.Product;
import com.example.product_service.exception.ApplicationErrors;
import com.example.product_service.mapper.ProductMapper;
import com.example.product_service.repository.CategoryRepository;
import com.example.product_service.repository.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "PRODUCT - SERVICE")
public class ProductServiceImpl implements ProductService {
    private final ProductMapper productMapper;
    private final ProductRepository productRepo;
    private final CategoryRepository categoryRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final RedissonClient redissonClient;

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
    @Override
    @Transactional
    @CacheEvict(value = {"product_search"}, allEntries = true)
    public void lock(LockProductReq lockProductReq) {
        List<LockProductItem> items = lockProductReq.getItems();

        List<String> sortedIds = items.stream()
                .map(LockProductItem::getId)
                .sorted()
                .toList();
        String lockKey = "lock:products:" + String.join(",", sortedIds);
        RLock lock = redissonClient.getLock(lockKey);

        try{
            if(lock.tryLock(10, 5 , TimeUnit.SECONDS)) {
                Thread.sleep(4000);
                log.info("Acquired Redis lock for: {}", lockKey);

                var productIdQuantityMap = items.stream()
                        .collect(Collectors.toMap(LockProductItem::getId, LockProductItem::getQuantity));

                List<Product> products = productRepo.findAllByIdn(new ArrayList<>(productIdQuantityMap.keySet()));

                if (products.isEmpty()) {
                    throw new RuntimeException("Product not found");
                }
                products.forEach(product -> {
                    int remainStock = product.getStock() - productIdQuantityMap.get(product.getId());
                    if (remainStock < 0) {
                        throw new RuntimeException("Product " + product.getId() + "is not enough stock");
                    }
                    product.setStock(remainStock);
                    productRepo.saveAll(products);
                });
            }
                else {
                    throw new RuntimeException("Server busy , please try again later");
                }

            }
         catch (InterruptedException e) {
              Thread.currentThread().interrupt();
              throw new RuntimeException("Process interrupted");
        }finally {
            log.info("Waiting for un;ock [{}]", lockKey);
            if(lock.isHeldByCurrentThread()){
                lock.unlock();
                log.info("Unlock success for [{}]", lockKey);
            }
        }

    }
    }

