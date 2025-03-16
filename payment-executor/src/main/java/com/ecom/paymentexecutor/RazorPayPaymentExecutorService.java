package com.ecom.paymentexecutor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

@Service
public class RazorPayPaymentExecutorService implements PaymentExecutorService {

    private static final Logger log = LoggerFactory.getLogger(RazorPayPaymentExecutorService.class);

    @Override
    public Payment processPayment(Payment payment) throws PaymentExecutorServiceException {
        log.info("attempting to process payment id:{} amount:{}", payment.getId(), payment.getAmount());
        // this is an example exception, in a real application you would typically
        // see this condition to handle a 5xx error from the payment gateway.
        if (payment.getAmount() > 1000) {
            throw new PaymentExecutorServiceException("Payment amount is too high");
        }
        payment.setStatus("SUCCESS");
        return payment;
    }
}
