package com.example.order_service.service;

import com.example.order_service.clients.ProductClient;
import com.example.order_service.config.utils.OrderStatus;
import com.example.order_service.dtos.resp.OrderDTO;
import com.example.order_service.dtos.resp.OrderItemDTO;
import com.example.order_service.dtos.resp.PagedOrdersDTO;
import com.example.order_service.kafka.event.InventoryReservedEvent;
import com.example.order_service.kafka.event.PaymentEvent;
import com.example.order_service.dtos.clientDTO.ProductDTO;
import com.example.order_service.dtos.clientDTO.ProductFilter;
import com.example.order_service.kafka.event.OrderCreatedEvent;
import com.example.order_service.kafka.event.OrderItemEvent;
import com.example.order_service.dtos.request.OrderItemRequest;
import com.example.order_service.dtos.request.OrderRequest;
import com.example.order_service.dtos.request.PlaceOrderFlashSaleRequest;
import com.example.order_service.dtos.resp.FlashSaleOrderResponse;
import com.example.order_service.entity.OrderEntity;
import com.example.order_service.entity.OrderItemEntity;
import com.example.order_service.exception.ApplicationErrors;
import com.example.order_service.repository.OrderItemRepository;
import com.example.order_service.repository.OrderRepository;
import com.example.order_service.repository.orderdeduction.OrderDeductionDomainService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService{
    private final ProductClient productClient;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepo;
    private final OrderDeductionDomainService orderDeductionDomainService;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    @Override
    public OrderEntity createOrder(OrderRequest request) {
        List<String> productIds = request.getOrderItems().stream()
                .map(OrderItemRequest::getProductId)
                .distinct()
                .toList();

        List<ProductDTO> products = productClient.getProductByIds(new ProductFilter(productIds));

        Map<String, ProductDTO> productPriceMap = new HashMap<>();

        products.forEach(productDTO -> {
            productPriceMap.put(productDTO.getId(), productDTO);
        });

        OrderEntity order = new OrderEntity();
        order.setCustomerId(request.getCustomerId());
        order.setStatus(OrderStatus.PENDING.name());
        order.setTotalAmount(BigDecimal.valueOf(0));

        OrderEntity savedOrder = orderRepository.save(order);

        BigDecimal totalAmount = BigDecimal.ZERO;
        List<OrderItemEntity> orderItems = new ArrayList<>();

        for (var itemDTO : request.getOrderItems()) {

            ProductDTO productDTO =
                    productPriceMap.get(itemDTO.getProductId());

            if (productDTO == null) {
                throw ApplicationErrors.PRODUCT_NOT_FOUND;
            }

            if (itemDTO.getQuantity() > productDTO.getStock()) {
                throw ApplicationErrors.PRODUCT_NOT_FOUND;
            }

            BigDecimal price = productDTO.getPrice();

            OrderItemEntity item = new OrderItemEntity();
            item.setOrderId(savedOrder.getId());
            item.setProductId(itemDTO.getProductId());
            item.setPrice(price);
            item.setQuantity(itemDTO.getQuantity());

            orderItems.add(item);

            totalAmount = price.multiply(BigDecimal.valueOf(itemDTO.getQuantity()));
        }

        orderItemRepo.saveAll(orderItems);
        savedOrder.setTotalAmount(totalAmount);
        OrderEntity createdOrder = orderRepository.save(savedOrder);

        List<OrderItemEvent> eventItems = orderItems.stream()
                .map(item -> OrderItemEvent.builder()
                        .productId(item.getProductId())
                        .quantity(item.getQuantity())
                        .price(item.getPrice()).build()).toList();

        OrderCreatedEvent event = OrderCreatedEvent.builder()
                .orderId(createdOrder.getId())
                .customerId(createdOrder.getCustomerId())
                .orderItems(eventItems)
                .build();
        kafkaTemplate.send("order_created", event);
//        Order Service gửi object, JsonSerializer biến object thành JSON;
//        Product Service nhận JSON dưới dạng String rồi tự map lại thành object.
        log.info("Publish new order success to order_created");
        return createdOrder;
    }

    @Override
    public void updateOrderStatus(String orderId, OrderStatus status) {
        orderRepository.findById(orderId).ifPresent(order -> {
            order.setStatus(status.name());

        });
    }

    @Override
    public void handleInventoryReservedEvent(InventoryReservedEvent inventoryReservedEvent) {
      orderRepository.findById(inventoryReservedEvent.getOrderId()).ifPresent(orderEntity -> {
            if(!orderEntity.getStatus().equals(OrderStatus.PENDING.name())){
                throw ApplicationErrors.INVALID_ORDER_STATUS;
            }
            if(inventoryReservedEvent.getStatus().equals("SUCCESS")){
                orderEntity.setStatus(OrderStatus.STOCK_RESERVED.name());
            }else {
                orderEntity.setStatus(OrderStatus.FAILED.name());
            }
        });
    }

    @Override
    public void handlePaymentEvent(PaymentEvent paymentEvent) {
        orderRepository.findById(paymentEvent.getOrderId()).ifPresent(orderEntity -> {
            if(!orderEntity.getStatus().equals(OrderStatus.STOCK_RESERVED.name())){
                throw ApplicationErrors.INVALID_ORDER_STATUS;
            }
            if(paymentEvent.getStatus().equals("SUCCESS")){
                orderEntity.setStatus(OrderStatus.PAYMENT_RECEIVED.name());
            }else {
                orderEntity.setStatus(OrderStatus.FAILED.name());
            }
        });
    }
   public FlashSaleOrderResponse flashSaleOrderMQ(PlaceOrderFlashSaleRequest request){
    if(request.getFlashSaleId() == null || request.getUserId() == null || request.getQuantity() <= 0){
        throw ApplicationErrors.INVALID_REQUEST;
    }
    return productClient.getFlashSaleResponse(request);
   }

//    @Override
//    public void placeOrderMQ(String productId, int quantity) {
//        int redisResult = stockOrderCacheService.decreaseStockCacheByLUA(productId,quantity );
//    }

    private String extractYearMonthFromOrderNumber(String orderNumber){

        try{
            String[] parts = orderNumber.split("-");
            if(parts.length < 2){
                throw new IllegalStateException("Invalid order number format");
            }
            long timestamp = Long.parseLong(parts[parts.length - 1]);
            LocalDateTime dateTime = Instant.ofEpochMilli(timestamp)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime();

            return dateTime.format(DateTimeFormatter.ofPattern("yyyyMM"));

        }catch (Exception e){
            throw new RuntimeException("Failed to extract yearMonth from orderNumber: " + orderNumber, e);

        }
    }
    public PagedOrdersDTO findPage(String yearMonth, Integer lastId, int limit ){
        validateYearMonth(yearMonth);
        if(limit < 0){
            throw new IllegalArgumentException("limit must be greater than 0");
        }
        List<Object[]> results = orderDeductionDomainService.findPage(yearMonth, lastId,limit);
        List<OrderDTO> items = results.stream().map(this::toOrderDTO).toList();

        boolean hasMore = results.size() == limit ;
        Integer nextCursor = hasMore ? items.get(items.size() - 1).getId() : null;
        return new PagedOrdersDTO(items, nextCursor,hasMore);
    }
    private OrderDTO toOrderDTO(Object[] row){
        requireColumnsName(row,"orders_yyyyMM");
        return new OrderDTO(
                (Integer) row[0],
                (String) row[1],
                (String) row[2],
                (BigDecimal) row[3],
                (String) row[4],
                toBoolean(row[5]),
                toLocalDateTime(row[6]),     // created_date
                (String) row[7],              // created_by
                toLocalDateTime(row[8]),     // last_modified_date
                (String) row[9]               // last_modified_by
        );
    }
    private OrderItemDTO toOrderItemDTO(Object[] row){
        requireColumnsName(row,"orders_items_yyyyMM");
        return new OrderItemDTO(
                (Integer) row[0],
                (Integer) row[1],
                (String) row[2],
                (BigDecimal ) row[3],
                row[4] == null ? null : ((Number) row[4]).intValue(),
                toBoolean(row[5]),
                toLocalDateTime(row[6]),     // created_date
                (String) row[7],              // created_by
                toLocalDateTime(row[8]),     // last_modified_date
                (String) row[9]               // last_modified_by
                );
    }
   private Boolean toBoolean(Object val){

        if (val == null){
            return null;
        }
        if(val instanceof Boolean bool){
            return bool;
        }
        if(val instanceof Number number){
            return number.intValue() != 0;
        }
        throw new IllegalArgumentException("Unsupported is_deleted type: " +val.getClass().getName());
   }
   private LocalDateTime toLocalDateTime(Object value){
        if(value == null){
            return null;
        }
        if(value instanceof LocalDateTime dateTime){
            return dateTime;
        }
        if(value instanceof Timestamp timestamp){
            return timestamp.toLocalDateTime();
        }
       throw new IllegalArgumentException("Unsupported time type: " + value.getClass().getName());

   }
   private void validateYearMonth(String yearMonth){
        if(yearMonth == null || !yearMonth.matches("[0-9]{4}(0[1-9]|1[0-1])")){
            throw new IllegalArgumentException("yearMonth should match formats");
        }
   }
    private void requireColumnsName(Object[] row, String table) {
        if (row == null || row.length != 10) {
            throw new IllegalArgumentException("Expected 10 columns for " + table
                    + "; check the existing table against the repository DDL");
        }
    }
}
