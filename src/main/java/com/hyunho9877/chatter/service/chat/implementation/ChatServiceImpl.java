package com.hyunho9877.chatter.service.chat.implementation;

import com.hyunho9877.chatter.domain.ChatMessage;
import com.hyunho9877.chatter.domain.Exchange;
import com.hyunho9877.chatter.repo.ChatMessageRepository;
import com.hyunho9877.chatter.service.chat.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private static final String exchange = Exchange.EXCHANGE.getExchange();
    private final RabbitTemplate rabbitTemplate;
    private final ChatMessageRepository messageRepository;

    @Override
    public void send(ChatMessage message) {
        log.info(exchange);
        log.info(exchange + "." + message.getReceiver());
        log.info(message.toString());
        try {
            rabbitTemplate.convertAndSend(exchange, exchange + "." + message.getReceiver(), message);
            messageRepository.save(message);
        } catch (AmqpException e) {
            log.error("message send failed : {}", message);
        }
    }
}
