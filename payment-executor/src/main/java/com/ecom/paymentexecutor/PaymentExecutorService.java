package com.ecom.paymentexecutor;

public interface PaymentExecutorService {
    Payment processPayment(Payment payment) throws PaymentExecutorServiceException;
}
