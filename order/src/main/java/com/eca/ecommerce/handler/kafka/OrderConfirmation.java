package com.eca.ecommerce.handler.kafka;

import com.eca.ecommerce.handler.customer.CustomerResponse;
import com.eca.ecommerce.handler.order.PaymentMethod;
import com.eca.ecommerce.handler.product.PurchaseResponse;

import java.math.BigDecimal;
import java.util.List;

public record OrderConfirmation (
        String orderReference,
        BigDecimal totalAmount,
        PaymentMethod paymentMethod,
        CustomerResponse customer,
        List<PurchaseResponse> products

) {
}
