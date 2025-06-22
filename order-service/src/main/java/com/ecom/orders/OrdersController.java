package com.ecom.orders;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/orders")
public class OrdersController {

    private static final Logger log = LoggerFactory.getLogger(OrdersController.class);
    private ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    OrderRepository orderRepository;

    @PostMapping("")
    public ResponseEntity<Order> createOrder(@RequestBody Order order) {
        try {
            order.setStatus("PENDING");
            Order savedOrder = orderRepository.save(order);
            return ResponseEntity.ok(savedOrder);
        } catch (Exception e) {
            log.error("Error while saving order", e);
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<Order> getOrder(@PathVariable("orderId") long orderId) {
        try {
            Optional<Order> orderOptional = orderRepository.findById(orderId);
            return ResponseEntity.ok(orderOptional.orElseThrow());
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @RabbitListener(queues = RabbitConfiguration.ORDER_UPDATES_QUEUE)
    public void handleOrderUpdateMessage(String message){
        try {
            log.info("Received order update message: {}", message);
            OrderUpdateMessage orderUpdateMessage = objectMapper.readValue(message, OrderUpdateMessage.class);
            orderRepository.findById(orderUpdateMessage.getOrderId())
                    .ifPresent(order -> {
                        order.setStatus(orderUpdateMessage.getStatus());
                        order.setPaymentId(orderUpdateMessage.getPaymentId());
                        orderRepository.save(order);
                        log.info("Order {} updated to status: {}", order.getId(), order.getStatus());
                    });
        } catch (Exception e) {
            log.error("Error while processing order update message: {}", e.getMessage());
        }
    }


}
