package com.eca.ecommerce.kafka;

import com.eca.ecommerce.customer.CustomerResponse;
import com.eca.ecommerce.order.PaymentMethod;
import com.eca.ecommerce.product.PurchaseResponse;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;

@Builder
public class OrderConfirmationMessage extends DefaultMessage {
    String orderReference;
    BigDecimal totalAmount;
    PaymentMethod paymentMethod;
    CustomerResponse customer;
    List<PurchaseResponse> products;
}
