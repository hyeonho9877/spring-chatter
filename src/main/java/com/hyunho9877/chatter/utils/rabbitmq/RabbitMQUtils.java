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
        String hashed = hash(username);
        Queue queue = new Queue(hashed, durable);
        amqpAdmin.declareQueue(queue);
        String routingKey = Exchange.EXCHANGE.getExchange() + "." + hashed;
        amqpAdmin.declareBinding(binding(queue, topicExchange(), routingKey));
    }

    private String hash(String string) {
        return hashFunction.hashString(string, StandardCharsets.UTF_8).toString();
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
