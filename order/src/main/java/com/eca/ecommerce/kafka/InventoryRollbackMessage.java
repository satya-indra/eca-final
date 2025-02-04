package com.eca.ecommerce.kafka;

import com.eca.ecommerce.product.PurchaseResponse;
import lombok.Builder;

import java.util.List;

@Builder
public class InventoryRollbackMessage extends DefaultMessage {
    List<PurchaseResponse> productToRollback;
}
