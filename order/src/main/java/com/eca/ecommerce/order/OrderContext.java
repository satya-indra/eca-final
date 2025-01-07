package com.eca.ecommerce.order;

import com.eca.ecommerce.customer.CustomerResponse;
import com.eca.ecommerce.product.PurchaseResponse;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class OrderContext {
    private final OrderRequest orderRequest;
    private final CustomerResponse customerResponse;
    private final Order order;
    private final List<PurchaseResponse> purchasedProducts;
    Integer paymentId;
}
