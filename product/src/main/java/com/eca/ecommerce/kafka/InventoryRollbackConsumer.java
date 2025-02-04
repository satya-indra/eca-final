package com.eca.ecommerce.kafka;

import com.eca.ecommerce.dto.response.ProductResponse;
import com.eca.ecommerce.product.PurchaseResponse;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Event Driven Saga Pattern using Choregrapher
 * 2 phase commit and 3 phase commit
 */
@Component
public class InventoryRollbackConsumer {
    @KafkaListener(topics = "product-rollback-topic", groupId = "product-service-group")
    public void consumeRollbackMessage(List<PurchaseResponse> productInfo) {
//        log.info("Received rollback message for product: {}", productInfo.getProductId());
        try {
            // Perform rollback logic
            rollbackInventory(productInfo);
//            log.info("Rollback successful for product: {}", productInfo.getProductId());
        } catch (Exception e) {
//            log.error("Failed to rollback for product: {}", productInfo.getProductId(), e);
            // Optionally, publish the message to a retry topic or take corrective actions
        }
    }

    private void rollbackInventory(List<PurchaseResponse> productInfo) {
        // Actual rollback logic (e.g., update database, reset inventory counts)
        // log.info("Rolling back inventory for product: {}", productInfo);
        // Database or API call to update inventory
        // Notify the owner that inventory is roll backed
    }
}
