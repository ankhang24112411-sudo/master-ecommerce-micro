package com.example.order_service.kafka;

import com.example.order_service.config.utils.OrderStatus;
import com.example.order_service.kafka.event.InventoryReservedEvent;
import com.example.order_service.kafka.event.PaymentEvent;
import com.example.order_service.kafka.event.OrderStockReserveEvent;
import com.example.order_service.entity.OrderEntity;
import com.example.order_service.entity.OrderItemEntity;
import com.example.order_service.repository.OrderItemRepository;
import com.example.order_service.repository.OrderRepository;
import com.example.order_service.repository.orderdeduction.OrderDeductionDomainService;
import com.example.order_service.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.BackOff;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.retrytopic.DltStrategy;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
@Slf4j(topic = "Order-Event-Consumer")
@RequiredArgsConstructor
public class OrderEventConsumer {
    private final OrderService orderService;
    private final ObjectMapper objectMapper;
    private final OrderRepository orderRepo;
    private final OrderItemRepository orderItemRepo;
    private final OrderDeductionDomainService orderDeductionDomainService;
    @KafkaListener(topics = "payment")
    @RetryableTopic(attempts = "4", backOff = @BackOff(delay = 2_000, multiplier = 2.0),
            exclude = {NullPointerException.class, IllegalArgumentException.class}
    )
    public void handlePaymentEvent(String orderString) throws JacksonException {
        PaymentEvent paymentEvent = objectMapper.readValue(orderString, PaymentEvent.class);
        log.info("Payment completed event {}", paymentEvent.getOrderId());

        orderService.handlePaymentEvent(paymentEvent);
    }

    @KafkaListener(topics = "inventory-reserved")
    @RetryableTopic(attempts = "4", backOff = @BackOff(delay = 2_000, multiplier = 2.0),
            exclude = {NullPointerException.class, IllegalArgumentException.class}
    )
    public void handleInventoryReservedEvent(String orderString) throws JacksonException {
        InventoryReservedEvent inventoryReservedEvent = objectMapper.readValue(orderString, InventoryReservedEvent.class);
        log.info("Payment completed event {}", inventoryReservedEvent.getOrderId());
        orderService.handleInventoryReservedEvent(inventoryReservedEvent);
    }

//    @Transactional(rollbackFor = Exception.class)
//    @KafkaListener(topics = "order-place-topic", concurrency = "3")
//    @RetryableTopic(attempts = "4",
//            backOff = @BackOff(delay = 2_000, multiplier = 2.0),
//            exclude = {NullPointerException.class, IllegalArgumentException.class}
//    )
//    public void processOrderStockAndFlashSalePurchase(String orderString) throws JacksonException {
//
//        PlaceOrderMQMessage message = objectMapper.readValue(orderString, PlaceOrderMQMessage.class);

    /// /TODO add to DBS
//        Optional<OrderQueue> orderQueue = orderQueueRepo.findByToken(message.getToken());
//        if(orderQueue.isPresent()){
//            if(orderQueue.get().getStatus() == 2){
//                throw ApplicationErrors.ORDER_ALREADY_CANCELLED;
//            }
//            if(orderQueue.get().getStatus() == 1){
//                throw ApplicationErrors.ORDER_ALREADY_COMPLETED;
//            }
//        }
//
//        String orderNumber = "MQ-" + message.getUserId() + "-" + System.currentTimeMillis();
//        String nTable = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMM"));
//
//        OrderEntity order = OrderEntity.builder()
//                .customerId(message.getUserId())
//                .status(OrderStatus.PENDING.name())
//                .orderNumber(orderNumber)
//                .totalAmount(message.getUnitPrice().multiply(BigDecimal.valueOf(message.getQuantity())))
//                .build();
//
//        OrderItemEntity oItem = OrderItemEntity.builder()
//                .orderId(order.getId())
//                .productId(message.getProductId())
//                .price(message.getUnitPrice())
//                .quantity(message.getQuantity()).build();
//
//        orderRepo.save(order);
//        orderItemRepo.save(oItem);
//    }
    @Transactional(rollbackFor = Exception.class)
    @KafkaListener(topics = "order-stock-reserve-topic", concurrency = "10")
    @RetryableTopic(attempts = "4",
            backOff = @BackOff(delay = 2_000, multiplier = 2.0),
            dltStrategy = DltStrategy.FAIL_ON_ERROR,
            exclude = {NullPointerException.class, IllegalArgumentException.class}
    )
    public void processOrderStockReservedEvent(String orderString) throws JacksonException {
        OrderStockReserveEvent message = objectMapper.readValue(orderString, OrderStockReserveEvent.class);
        String orderNumber = "MQ-" + message.getUserId() + "-" + System.currentTimeMillis();
        String nTable = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMM"));

        OrderEntity order = OrderEntity.builder()
                .customerId(message.getUserId())
                .status(OrderStatus.STOCK_RESERVED.name())
                .orderNumber(orderNumber)
                .totalAmount(message.getUnitPrice().multiply(BigDecimal.valueOf(message.getQuantity())))
                .build();

        OrderItemEntity oItem = OrderItemEntity.builder()
                .orderId(order.getId())
                .productId(message.getProductId())
                .price(message.getUnitPrice())
                .quantity(message.getQuantity()).build();

        orderDeductionDomainService.insertOrder(nTable, order);
        orderDeductionDomainService.insertOrderItem(nTable,oItem);
//        orderRepo.save(order);
//        orderItemRepo.save(oItem);
    }

    @Transactional(rollbackFor = Exception.class)
    @KafkaListener(topics = "order-cancel-topic", concurrency = "10")
    @RetryableTopic(attempts = "4",
            backOff = @BackOff(delay = 2_000, multiplier = 2.0),
            dltStrategy = DltStrategy.FAIL_ON_ERROR,
            exclude = {NullPointerException.class, IllegalArgumentException.class}
    )
    public void processOrderStockCancelEvent(String orderString) throws JacksonException {
        OrderStockReserveEvent message = objectMapper.readValue(orderString, OrderStockReserveEvent.class);
        String orderNumber = "MQ-" + message.getUserId() + "-" + System.currentTimeMillis();
        String nTable = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMM"));

        OrderEntity order = OrderEntity.builder()
                .customerId(message.getUserId())
                .status(OrderStatus.CANCELLED.name())
                .orderNumber(orderNumber)
                .totalAmount(message.getUnitPrice().multiply(BigDecimal.valueOf(message.getQuantity())))
                .build();
        orderDeductionDomainService.insertOrder(nTable, order);

    }
}
