package com.ecom.orders;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    @Autowired
    OrderRepository orderRepository;

    @PostMapping("")
    public ResponseEntity<Order> createOrder(@RequestBody Order order) {
        try {
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
}
