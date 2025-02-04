package com.eca.ecommerce.kafka;

import com.eca.ecommerce.product.PurchaseResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.weaver.ast.Or;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.springframework.kafka.support.KafkaHeaders.TOPIC;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventProducer {

    private final KafkaTemplate<String, DefaultMessage> kafkaTemplate;

    public void sendOrderConfirmation(OrderConfirmationMessage orderConfirmation) {
        log.info("Sending order confirmation");
        Message<OrderConfirmationMessage> message = MessageBuilder
                .withPayload(orderConfirmation)
                .setHeader(TOPIC, "order-topic")
                .build();

        kafkaTemplate.send(message);
    }

    public void sendInventoryRollbackEvent(List<PurchaseResponse> productToRollback) {
        log.info("Sending order confirmation");
        Message<InventoryRollbackMessage> message = MessageBuilder
                .withPayload(InventoryRollbackMessage.builder()
                        .productToRollback(productToRollback)
                        .build())
                .setHeader(TOPIC, "inventory-rollback-topic")
                .build();

        kafkaTemplate.send(message);
    }
}
