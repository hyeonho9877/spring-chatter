package com.hyunho9877.chatter.service.chat.implementation;

import com.hyunho9877.chatter.domain.ApplicationUser;
import com.hyunho9877.chatter.domain.ChatMessage;
import com.hyunho9877.chatter.domain.Exchange;
import com.hyunho9877.chatter.domain.Friends;
import com.hyunho9877.chatter.dto.MessageType;
import com.hyunho9877.chatter.dto.ServerMessage;
import com.hyunho9877.chatter.repo.ChatMessageRepository;
import com.hyunho9877.chatter.repo.FriendsRepository;
import com.hyunho9877.chatter.service.chat.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

import java.util.Set;

import static com.hyunho9877.chatter.dto.MessageType.OFFLINE;
import static com.hyunho9877.chatter.dto.MessageType.ONLINE;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private static final String exchange = Exchange.EXCHANGE.getExchange();
    private final RabbitTemplate rabbitTemplate;
    private final ChatMessageRepository messageRepository;
    private final FriendsRepository friendsRepository;

    @Override
    public void send(ChatMessage message) {
        try {
            rabbitTemplate.convertAndSend(exchange, message.getReceiver(), message);
            messageRepository.save(message);
        } catch (AmqpException e) {
            log.error("message send failed : {}", message);
        }
    }

    @Override
    public void notifyOnline(String username) {
        log.info("notified online : {}", username);
        Set<Friends> friends = friendsRepository.findByUser1(ApplicationUser.builder().email(username).build());
        friends.stream().map(Friends::getUser2).forEach(friend ->
            rabbitTemplate.convertAndSend(
                    exchange,
                    friend.getEmail(),
                    new ServerMessage(ONLINE, friend.getEmail()))
        );
    }

    @Override
    public void notifyOffline(String username) {
        log.info("notified offline : {}", username);
        Set<Friends> friends = friendsRepository.findByUser1(ApplicationUser.builder().email(username).build());
        friends.stream().map(Friends::getUser2).forEach(friend ->
            rabbitTemplate.convertAndSend(
                    exchange,
                    friend.getEmail(),
                    new ServerMessage(OFFLINE, friend.getEmail()))
        );
    }
}
