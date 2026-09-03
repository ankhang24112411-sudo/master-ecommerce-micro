package com.example.product_service.kafka;

import com.example.product_service.dto.res.PlaceOrderMQMessage;
import com.example.product_service.kafka.consumers.KafkaTopicConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaOrderProducer {

    private KafkaTemplate<String, PlaceOrderMQMessage> kafkaTemplate;

    public void sendAndAwaitAck(PlaceOrderMQMessage message) throws Exception{
     kafkaTemplate.send(KafkaTopicConfig.ORDER_PLACE_TOPIC, message.getToken(), message);
    }
    public CompletableFuture<SendResult<String, PlaceOrderMQMessage>> sendAsync(PlaceOrderMQMessage message) {
        return kafkaTemplate.send(KafkaTopicConfig.ORDER_PLACE_TOPIC, message.getToken(), message);
    }
}
