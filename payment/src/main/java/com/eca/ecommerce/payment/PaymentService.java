package com.eca.ecommerce.payment;

import com.eca.ecommerce.notification.NotificationProducer;
import com.eca.ecommerce.notification.PaymentNotificationRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

  private final PaymentRepository repository;
  private final PaymentMapper mapper;
  private final NotificationProducer notificationProducer;

  public Integer createPayment(PaymentRequest request) {
    log.info("Payment request received from order service");
    var payment = this.repository.save(this.mapper.toPayment(request));
    log.info("Payment of order successfully completed");
    this.notificationProducer.sendNotification(
            new PaymentNotificationRequest(
                    request.orderReference(),
                    request.amount(),
                    request.paymentMethod(),
                    request.customer().firstname(),
                    request.customer().lastname(),
                    request.customer().email()
            )
    );
    return payment.getId();
  }
}
