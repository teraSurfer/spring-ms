package com.ecom.payments;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class PaymentsApplication {

    public static final String EXCHANGE_NAME = "payment_exchange";
    public static final String QUEUE_NAME = "payment_queue";

    public static void main(String[] args) {
        SpringApplication.run(PaymentsApplication.class, args);
    }

    // https://spring.io/guides/gs/messaging-rabbitmq
    @Bean
    Queue queue() {
        return new Queue(QUEUE_NAME, false);
    }

    @Bean
    TopicExchange exchange() {
        return new TopicExchange(EXCHANGE_NAME);
    }

    @Bean
    Binding binding(Queue queue, TopicExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with("payment");
    }
}
