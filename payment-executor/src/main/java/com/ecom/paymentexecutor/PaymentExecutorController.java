package com.ecom.paymentexecutor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.AMQP;
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
    @Autowired
    PaymentExecutorService paymentExecutorService;

    ObjectMapper mapper = new ObjectMapper();


    @RabbitListener(queues = RabbitMQConfiguration.PAYMENT_QUEUE)
    public void processMessage(String message) throws PaymentExecutorServiceException {
        try {
            log.info("Received message: {}", message);
            Payment payment = mapper.readValue(message, Payment.class);
            processPayment(payment);
        } catch (JsonProcessingException e) {
            log.error("Error converting to json", e);
        }
    }

    public void processPayment(Payment payment) throws PaymentExecutorServiceException {
        try {
            Payment processedPayment = paymentExecutorService.processPayment(payment);
            log.info("Processed payment: {}", processedPayment);
            rabbitTemplate.convertAndSend(RabbitMQConfiguration.EXCHANGE_NAME, RabbitMQConfiguration.ROUTING_KEY, mapper.writeValueAsString(processedPayment));
        } catch (JsonProcessingException e) {
            log.error("Error while processing payment json", e);
        }
    }
}
