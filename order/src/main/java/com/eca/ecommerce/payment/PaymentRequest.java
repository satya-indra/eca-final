package com.eca.ecommerce.payment;

import com.eca.ecommerce.customer.CustomerResponse;
import com.eca.ecommerce.order.PaymentMethod;

import java.math.BigDecimal;

public record PaymentRequest(
    BigDecimal amount,
    PaymentMethod paymentMethod,
    Integer orderId,
    String orderReference,
    CustomerResponse customer
) {
}
