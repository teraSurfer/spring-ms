package com.ecom.payments;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    private static final Logger log = LoggerFactory.getLogger(PaymentController.class);
    @Autowired
    PaymentRepository repository;

    @Autowired
    RabbitTemplate rabbitTemplate;



    @PostMapping("")
    public ResponseEntity<Payment> makePayment(@RequestBody Payment payment) {
        // Payment logic
        try {
//            docker run -it --rm --name rabbitmq -p 5672:5672 -p 15672:15672 rabbitmq:4.0-management
            ObjectMapper mapper = new ObjectMapper();
            payment.setStatus("ACCEPTED");
            repository.save(payment);
            String paymentJson = mapper.writeValueAsString(payment);
            rabbitTemplate.convertAndSend(RabbitMQConfiguration.EXCHANGE_NAME, RabbitMQConfiguration.ROUTING_KEY, paymentJson);
            return ResponseEntity.ok(payment);
        } catch (Exception e) {
            // Log error
            log.info("Error while making payment", e);
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{paymentId}")
    public ResponseEntity<Payment> getPayment(@PathVariable Long paymentId) {
        // Get payment logic
        try {
            Payment payment = repository.findById(paymentId).orElseThrow();
            return ResponseEntity.ok(payment);
        } catch (Exception e) {
            // Log error
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<Payment> getPaymentByOrder(@PathVariable Long orderId) {
        // Get payment by order logic
        try {
            Payment payment = repository.findByOrderId(orderId).orElseThrow();
            return ResponseEntity.ok(payment);
        } catch (Exception e) {
            // Log error
            return ResponseEntity.notFound().build();
        }
    }
}
