package com.example.product_service.service.impl;

import com.example.product_service.kafka.topic.InventoryReservedEvent;
import com.example.product_service.entity.OrderQueue;
import com.example.product_service.dto.clients.ProductValid;
import com.example.product_service.dto.req.CreateProductReq;
import com.example.product_service.dto.clients.ProductDTO;
import com.example.product_service.dto.clients.ProductFilter;
import com.example.product_service.dto.req.LockProductItem;
import com.example.product_service.dto.req.LockProductReq;
import com.example.product_service.dto.req.UpdateProductReq;
import com.example.product_service.entity.Product;
import com.example.product_service.exception.ApplicationErrors;
import com.example.product_service.mapper.ProductMapper;
import com.example.product_service.repository.CategoryRepository;
import com.example.product_service.repository.ProductRepository;
import com.example.product_service.service.ProductService;
import com.example.product_service.service.cache.prod.ProductCacheServiceRefactor;
import com.example.product_service.service.cache.prod.StockOrderCacheService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
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
    private final ProductCacheServiceRefactor productCacheServiceRefactor;
    private final StockOrderCacheService stockOrderCacheService;
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
    @Cacheable(value = "products", key = "#productFilter.ids", condition = "#productFilter.ids != null" )
    public List<ProductDTO> search(ProductFilter productFilter) {
        List<Product> entities = productRepo.findAllById(productFilter.getIds());

        List<ProductDTO> result = new ArrayList<>();

        for (Product product : entities) {
            try {
                log.info("Product to be founded : {} , {}", product.getName(), product.getId());
                result.add(productMapper.toProductDTO(product));
            } catch (Exception e) {
                log.error("Map Product sang ProductDTO lỗi, productId={}", product.getId(), e);
                throw e;
            }
        }
        log.info("Cache for product {}", result.get(0));
        return result;
    }
    @Override
    @CacheEvict(value = "products", allEntries = true)
    public Product update(String id, UpdateProductReq updateProductReq) {
        var existedCategoryOptional =
                categoryRepository.findById(updateProductReq.getCategoryId());

        if (existedCategoryOptional.isEmpty()) {
            throw ApplicationErrors.PRODUCT_NOT_FOUND;
        }

        var productOptional = productRepo.findById(id);

        if (productOptional.isEmpty()) {
            throw ApplicationErrors.PRODUCT_NOT_FOUND;
        }

        var existedProduct = productOptional.get();

        var updatingProduct =
                productMapper.fromUpdateRequest(updateProductReq, existedProduct);

        return productRepo.save(updatingProduct);
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
    public Product getById(String id) {
        return productRepo.findById(id).orElse(null);
    }
//    Request A
//
//    lấy Redis lock
//       |
//    v
//    đọc stock=100
//            |
//            v
//    update stock=95
//            |
//            v
//    unlock
//
//
//    Request B
//
//    chờ lock
//
//       |
//    v
//
//    đọc stock=95
//
//            |
//            v
//
//    update stock=85
//
//            |
//            v
//
//    unlock
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

                List<Product> products = productRepo.findAllById(new ArrayList<>(productIdQuantityMap.keySet()));

                if (products.isEmpty()) {
                    InventoryReservedEvent event = new InventoryReservedEvent();
                    event.setOrderId(lockProductReq.getOrderId());
                    event.setStatus("FAILED");
                    event.setMessage("Product not exist");
                    kafkaTemplate.send("inventory-reserved-failed", event);
                    return;
                }
                products.forEach(product -> {
                    int remainStock = product.getStock() - productIdQuantityMap.get(product.getId());
                    if (remainStock < 0) {
                        InventoryReservedEvent event = new InventoryReservedEvent();
                        event.setOrderId(lockProductReq.getOrderId());
                        event.setStatus("FAILED");
                        event.setMessage("Product not enough stock");
                        kafkaTemplate.send("inventory-reserved-failed", event);
                        return;
                    }
                    product.setStock(remainStock);
                    InventoryReservedEvent event = new InventoryReservedEvent();
                    event.setOrderId(lockProductReq.getOrderId());
                    event.setStatus("SUCCESS");
                    kafkaTemplate.send("inventory-reserved", event);
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

    @Override
    public Product getProductById(String productid, Long version) {
        return productCacheServiceRefactor.getProductDetail(productid,version).getProduct();
    }

    @Override
    public OrderQueue placeOrderMQ(String userId, String productId, int quantity) {
        //TODO bloom filter
        int redisResult = stockOrderCacheService.decreaseStockCacheByLUA(productId, quantity);
        if(redisResult == -1){
            log.info("placeOrderMQ : cache miss for productId={}, warming up..." ,productId);
            boolean warmedUp = stockOrderCacheService.addStockAvailableToCache(productId);

            if(!warmedUp){
                return failedQueue("PRODUCT_NOT_FOUND", "Cannot find product");
            }

         //decrease after data warm up

            redisResult = stockOrderCacheService.decreaseStockCacheByLUA(productId,quantity);
        }
        if(redisResult == 0){
            log.info("placeOrderMQ: Redis OOS for productId={}", productId);
            return failedQueue("OUT_OF_STOCK", "Product out of stock");

        }
//        long unitPrice = stockOrderCacheService.getEffectivePrice(productId);
        return null;
    }
    private OrderQueue failedQueue(String code , String message){
        return new OrderQueue().setStatus(2).setMessage(code +": " + message);
    }
}

