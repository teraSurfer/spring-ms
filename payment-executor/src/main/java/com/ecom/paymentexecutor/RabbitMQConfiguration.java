package com.ecom.paymentexecutor;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfiguration {
    public static final String PAYMENT_QUEUE = "payment_queue";

    // https://spring.io/guides/gs/messaging-rabbitmq
    public static final String EXCHANGE_NAME = "processed_payment_exchange";
    public static final String PAYMENT_EXCHANGE_NAME = "payment_exchange";
    public static final String QUEUE_NAME = "processed_payment_queue";
    public static final String DLQ_NAME = PAYMENT_QUEUE + ".dlq";
    public static final String DLX_EXCHANGE_NAME = PAYMENT_EXCHANGE_NAME + ".dlx";
    public static final String ROUTING_KEY = "processed_payment";
    public static final String PAYMENT_ROUTING_KEY = "payment";
    public static final String DLX_ROUTING_KEY = PAYMENT_ROUTING_KEY + ".failures";

    @Bean("payment_queue")
    Queue paymentQueue() {
        return QueueBuilder.durable(PAYMENT_QUEUE)
                .deadLetterExchange(DLX_EXCHANGE_NAME)
                .deadLetterRoutingKey(DLX_ROUTING_KEY)
                .build();
    }

    @Bean("queue")
    Queue queue() {
        return QueueBuilder.durable(QUEUE_NAME)
                .build();
    }

    @Bean
    FanoutExchange deadLetterExchange() {
        return new FanoutExchange(DLX_EXCHANGE_NAME);
    }

    @Bean("deadLetterQueue")
    Queue deadLetterQueue() {
        return QueueBuilder.durable(DLQ_NAME).build();
    }

    @Bean
    TopicExchange exchange() {
        return new TopicExchange(EXCHANGE_NAME);
    }

    @Bean
    Binding binding(@Qualifier("queue") Queue queue, TopicExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(ROUTING_KEY);
    }

    @Bean
    Binding deadLetterBinding(@Qualifier("deadLetterQueue") Queue deadLetterQueue, FanoutExchange deadLetterExchange) {
        return BindingBuilder.bind(deadLetterQueue).to(deadLetterExchange);
    }
}
