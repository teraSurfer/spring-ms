package com.ecom.paymentexecutor;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;


@Controller
public class PaymentExecutorController {

    private static final Logger log = LoggerFactory.getLogger(PaymentExecutorController.class);
    @Autowired
    RabbitTemplate rabbitTemplate;
    ObjectMapper mapper = new ObjectMapper();


    @RabbitListener(queues = "payment_queue")
    public void processMessage(String message) {
        try {
            log.info("Received message: " + message);
            Payment payment = mapper.readValue(message, Payment.class);
            processPayment(payment);
        } catch (Exception e) {
            log.error("Error while processing message", e);
        }
    }

    public void processPayment(Payment payment) {
        try {
            log.info("Processing payment: " + payment);
            if (payment.getAmount() > 1000) {
                throw new Exception("Payment amount is too high");
            }
            payment.setStatus("SUCCESS");
            String paymentJson = mapper.writeValueAsString(payment);
            rabbitTemplate.convertAndSend(PaymentExecutorApplication.EXCHANGE_NAME, "processed_payment", paymentJson);
        } catch (Exception e) {
            payment.setStatus("FAILED");
            try {
                String paymentJson = mapper.writeValueAsString(payment);
                rabbitTemplate.convertAndSend(PaymentExecutorApplication.EXCHANGE_NAME, "processed_payment", paymentJson);
            } catch (Exception ex) {
                log.error("Error while processing payment", ex);
            }

        }
    }
}
