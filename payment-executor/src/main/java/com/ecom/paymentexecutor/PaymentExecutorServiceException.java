package com.ecom.paymentexecutor;

public class PaymentExecutorServiceException extends RuntimeException {

    public PaymentExecutorServiceException(String message) {
        super(message);
    }

    public PaymentExecutorServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
