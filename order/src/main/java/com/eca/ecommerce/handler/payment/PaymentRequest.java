package com.eca.ecommerce.handler.payment;

import com.eca.ecommerce.handler.customer.CustomerResponse;
import com.eca.ecommerce.handler.order.PaymentMethod;

import java.math.BigDecimal;

public record PaymentRequest(
    BigDecimal amount,
    PaymentMethod paymentMethod,
    Integer orderId,
    String orderReference,
    CustomerResponse customer
) {
}
