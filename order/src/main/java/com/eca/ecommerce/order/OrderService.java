package com.eca.ecommerce.order;

import com.eca.ecommerce.customer.CustomerClient;
import com.eca.ecommerce.exception.BusinessException;
import com.eca.ecommerce.kafka.OrderConfirmation;
import com.eca.ecommerce.kafka.OrderProducer;
import com.eca.ecommerce.orderline.OrderLineRequest;
import com.eca.ecommerce.orderline.OrderLineService;
import com.eca.ecommerce.payment.PaymentClient;
import com.eca.ecommerce.payment.PaymentRequest;
import com.eca.ecommerce.product.ProductClient;
import com.eca.ecommerce.product.PurchaseRequest;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository repository;
    private final OrderMapper mapper;
    private final CustomerClient customerClient;
    private final PaymentClient paymentClient;
    private final ProductClient productClient;
    private final OrderLineService orderLineService;
    private final OrderProducer orderProducer;

    @Transactional
    public Integer createOrder(OrderRequest orderRequest) {
        ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor();
        // future chaining
        CompletableFuture<Integer> future = CompletableFuture
                .supplyAsync(() -> getContext(orderRequest), executorService)
                // Step 1 : customerClient.findCustomerById(request.customerId()) --> returns CustomerResponse
                .thenApplyAsync(this::findCustomer)
                // Step 2 : productClient.purchaseProducts(request.products()) --> returns List<PurchaseResponse>
                .thenApplyAsync(this::makePurchaseRequest)
                // Step 3 : OrderRepository repository.save the order --> returns order
                .thenApplyAsync((context -> saveOrder(context.getOrderRequest(), context)))
                // step 4 : save order line request for each purchased products
                .thenApplyAsync((this::saveOrderLines))
                // step 5 : paymentClient.requestOrderPayment(paymentRequest)
                .thenApplyAsync((this::makePayment))
                // step 6 : send order confirmation by messaging service
                .thenApplyAsync(this::sendOrderNotification)
                // step 7 : return order id integer
                .thenApplyAsync(OrderContext::getOrder)
                .thenApplyAsync(Order::getId);

        try {
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }

    }

    private static OrderContext getContext(OrderRequest orderRequest) {
        return OrderContext.builder().orderRequest(orderRequest).build();
    }

    private OrderContext sendOrderNotification(OrderContext context) {
        orderProducer.sendOrderConfirmation(
                new OrderConfirmation(
                        context.getOrderRequest().reference(),
                        context.getOrderRequest().amount(),
                        context.getOrderRequest().paymentMethod(),
                        context.getCustomerResponse(),
                        context.getPurchasedProducts()
                ));
        return context;
    }

    private OrderContext makePayment(OrderContext context) {
        var paymentRequest = new PaymentRequest(
                context.getOrderRequest().amount(),
                context.getOrderRequest().paymentMethod(),
                context.getOrder().getId(),
                context.getOrder().getReference(),
                context.getCustomerResponse()
        );
        Integer paymentId = paymentClient.requestOrderPayment(paymentRequest);
        return OrderContext.builder()
                .orderRequest(context.getOrderRequest())
                .customerResponse(context.getCustomerResponse())
                .purchasedProducts(context.getPurchasedProducts())
                .order(context.getOrder())
                .paymentId(paymentId)
                .build();
    }

    private OrderContext saveOrderLines(OrderContext context) {
        for (PurchaseRequest purchaseRequest : context.getOrderRequest().products()) {
            orderLineService.saveOrderLine(
                    new OrderLineRequest(
                            null,
                            context.getOrder().getId(),
                            purchaseRequest.productId(),
                            purchaseRequest.quantity()
                    )
            );
        }
        return context;
    }

    private OrderContext saveOrder(OrderRequest orderRequest, OrderContext context) {
        Order order = this.repository.save(mapper.toOrder(context.getOrderRequest()));
        log.info("order created details {}", orderRequest);

        return OrderContext.builder()
                .orderRequest(context.getOrderRequest())
                .customerResponse(context.getCustomerResponse())
                .purchasedProducts(context.getPurchasedProducts())
                .order(order)
                .build();
    }

    private OrderContext makePurchaseRequest(OrderContext context) {
        var purchasedProducts = productClient.purchaseProducts(context.getOrderRequest().products());
        return OrderContext.builder()
                .orderRequest(context.getOrderRequest())
                .customerResponse(context.getCustomerResponse())
                .purchasedProducts(purchasedProducts)
                .build();
    }

    private OrderContext findCustomer(OrderContext context) {
        var customer = this.customerClient
                .findCustomerById(context.getOrderRequest().customerId())
                .orElseThrow(() -> new BusinessException("Cannot create order:: No customer exists with the provided ID"));
        return OrderContext.builder()
                .orderRequest(context.getOrderRequest())
                .customerResponse(customer).build();
    }

    public List<OrderResponse> findAllOrders() {
        log.info("find all orders");
        return this.repository.findAll()
                .stream()
                .map(this.mapper::fromOrder)
                .collect(Collectors.toList());
    }

    public OrderResponse findById(Integer id) {
        return this.repository.findById(id)
                .map(this.mapper::fromOrder)
                .orElseThrow(() -> new EntityNotFoundException(String.format("No order found with the provided ID: %d", id)));
    }
}
