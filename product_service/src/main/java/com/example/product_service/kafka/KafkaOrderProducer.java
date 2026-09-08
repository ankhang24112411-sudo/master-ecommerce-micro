package com.example.product_service.kafka;

import com.example.product_service.dto.res.PlaceOrderMQMessage;
import com.example.product_service.kafka.topic.OrderCancelEvent;
import com.example.product_service.kafka.topic.OrderStockReserveEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaOrderProducer {

    private KafkaTemplate<String, Object> kafkaTemplate;
    /**
     * Outbox Publisher — gửi từng event theo chế độ blocking.
     *
     * Gửi message lên Kafka và chờ Broker ACK tối đa 5 giây.
     * Thread hiện tại sẽ bị block tại phương thức get().
     *
     * Nếu Broker trả ACK thành công:
     * - Phương thức kết thúc bình thường.
     * - Caller có thể gọi markPublished() để cập nhật event thành PUBLISHED.
     *
     * Nếu Kafka không phản hồi hoặc gửi thất bại do broker chết,
     * lỗi mạng, broker quá tải hoặc replication chưa hoàn thành:
     * - get() ném TimeoutException hoặc ExecutionException.
     * - Exception được đẩy về OutboxPublisherJob.
     * - Không gọi markPublished().
     * - Event vẫn giữ trạng thái PENDING.
     * - Event sẽ được lấy lại và retry trong lần cron tiếp theo.
     */
    public void sendAndAwaitAck(PlaceOrderMQMessage message) throws Exception{
     kafkaTemplate.send(KafkaTopicConfig.ORDER_PLACE_TOPIC, message.getToken(), message)
             .get(5, TimeUnit.SECONDS);
    }
    public CompletableFuture<SendResult<String, Object>> sendAsync(PlaceOrderMQMessage message)  {
        return kafkaTemplate.send(KafkaTopicConfig.ORDER_PLACE_TOPIC, message.getToken(), message);
    }
    public void sendOrderCancelLowStock(OrderCancelEvent message) throws ExecutionException, InterruptedException, TimeoutException {
        kafkaTemplate.send(KafkaTopicConfig.ORDER_CANCEL_TOPIC, message.getToken(), message)
                .get(5, TimeUnit.SECONDS);
    }
    public void sendOrderStockReserved(OrderStockReserveEvent message) throws ExecutionException, InterruptedException, TimeoutException {
        kafkaTemplate.send(KafkaTopicConfig.ORDER_STOCK_RESERVE_TOPIC, message.getToken(), message)
                .get(5, TimeUnit.SECONDS);
    }
}
