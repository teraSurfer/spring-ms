package com.ecom.paymentexecutor;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class ProcessedPaymentsController {

    private static final Logger log = LoggerFactory.getLogger(ProcessedPaymentsController.class);
    @Autowired
    private PaymentRepository paymentRepository;
    private ObjectMapper objectMapper = new ObjectMapper();

    @RabbitListener(queues = RabbitMQConfiguration.PROCESSED_PAYMENTS_QUEUE_NAME)
    public void processMessage(String message) {
        // Process the message from the processed payments queue
        System.out.println("Received processed payment message: " + message);
        try {
            Payment payment = objectMapper.readValue(message, Payment.class);
            paymentRepository.save(payment);
            // Add your processing logic here
        } catch (Exception e) {
            // Handle the exception
            log.error("Error processing message: {}", e.getMessage());
        }
    }
}
