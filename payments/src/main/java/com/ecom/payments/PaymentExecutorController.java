package com.ecom.payments;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Controller;

@Controller
public class PaymentExecutorController {

    @RabbitListener(queues = "payment_queue")
    public void processMessage(String message) {
        System.out.println("Received message: " + message);
    }
}
