package com.ecom.paymentexecutor;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class PaymentExecutorApplication {

    public static final String EXCHANGE_NAME = "processed_payments_exchange";
    public static final String QUEUE_NAME = "processed_payments_queue";

    public static void main(String[] args) {
        SpringApplication.run(PaymentExecutorApplication.class, args);
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
        return BindingBuilder.bind(queue).to(exchange).with("processed_payment");
    }
}
