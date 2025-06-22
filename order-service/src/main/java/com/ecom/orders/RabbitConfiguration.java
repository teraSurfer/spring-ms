package com.ecom.orders;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfiguration {
    public static final String ORDER_UPDATES_QUEUE = "order_updates_queue";
    public static final String ORDER_UPDATES_EXCHANGE = "order_updates_exchange";
    public static final String ORDER_UPDATES_ROUTING_KEY = "order.updates";

    @Bean("queue")
    Queue queue() {
        return QueueBuilder.durable(ORDER_UPDATES_QUEUE)
                .build();
    }

    @Bean("exchange")
    TopicExchange exchange() {
        return new TopicExchange(ORDER_UPDATES_EXCHANGE);
    }

    @Bean
    Binding binding(Queue queue, TopicExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(ORDER_UPDATES_ROUTING_KEY);
    }

}
