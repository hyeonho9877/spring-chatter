package com.hyunho9877.chatter.service;

import com.hyunho9877.chatter.domain.ChatMessage;
import com.hyunho9877.chatter.service.interfaces.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private static final String EXCHANGE_NAME = "sample.exchange";
    private final AmqpAdmin amqpAdmin;
    private final RabbitTemplate rabbitTemplate;

    @Override
    public void send(ChatMessage message) {
        rabbitTemplate.convertAndSend(EXCHANGE_NAME, "sample.springmq."+message.getReceiver(), message);
    }

    @Override
    public Message receive() {
        return null;
    }
}
