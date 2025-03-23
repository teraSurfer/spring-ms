package com.ecom.payments;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfiguration {
    public static final String EXCHANGE_NAME = "payment_exchange";
    public static final String QUEUE_NAME = "payment_queue";
    public static final String DLQ_NAME = QUEUE_NAME + ".dlq";
    public static final String DLX_EXCHANGE_NAME = EXCHANGE_NAME + ".dlx";
    public static final String ROUTING_KEY = "payment";
    public static final String DLX_ROUTING_KEY = ROUTING_KEY + ".failures";

    // https://spring.io/guides/gs/messaging-rabbitmq
    @Bean
    Queue queue() {
        return QueueBuilder.durable(QUEUE_NAME)
                .deadLetterExchange(DLX_EXCHANGE_NAME)
                .deadLetterRoutingKey(DLX_ROUTING_KEY)
                .build();
    }

    @Bean
    TopicExchange exchange() {
        return new TopicExchange(EXCHANGE_NAME);
    }

    @Bean
    Binding binding(Queue queue, TopicExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(ROUTING_KEY);
    }

}
