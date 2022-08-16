package com.hyunho9877.chatter.utils.rabbitmq;

import com.google.common.hash.HashFunction;
import com.hyunho9877.chatter.domain.Exchange;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.*;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
public class RabbitMQUtils {

    private final HashFunction hashFunction;
    private final AmqpAdmin amqpAdmin;

    public void declareQueue(String username, boolean durable) {
        Queue queue = new Queue(username, durable);
        amqpAdmin.declareQueue(queue);
        String routingKey = Exchange.EXCHANGE.getExchange() + "." + username;
        amqpAdmin.declareBinding(binding(queue, topicExchange(), routingKey));
    }

    private TopicExchange topicExchange() {
        return new TopicExchange(Exchange.EXCHANGE.getExchange());
    }

    private Binding binding(Queue queue, TopicExchange topicExchange, String routingKey) {
        return BindingBuilder
                .bind(queue)
                .to(topicExchange)
                .with(routingKey);
    }
}