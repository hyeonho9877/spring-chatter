package com.hyunho9877.chatter.service.chat.implementation;

import com.hyunho9877.chatter.domain.ApplicationUser;
import com.hyunho9877.chatter.domain.ChatMessage;
import com.hyunho9877.chatter.domain.Exchange;
import com.hyunho9877.chatter.domain.Friends;
import com.hyunho9877.chatter.dto.MessageType;
import com.hyunho9877.chatter.dto.ServerMessage;
import com.hyunho9877.chatter.dto.UserMessage;
import com.hyunho9877.chatter.repo.ChatMessageRepository;
import com.hyunho9877.chatter.repo.FriendsRepository;
import com.hyunho9877.chatter.repo.UserRepository;
import com.hyunho9877.chatter.service.chat.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static com.hyunho9877.chatter.dto.MessageType.*;

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
            message.setTimestamp(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm").format(LocalDateTime.now()));
            rabbitTemplate.convertAndSend(exchange, message.getReceiver(), message);
            messageRepository.save(message);
        } catch (AmqpException e) {
            e.printStackTrace();
            log.error("message send failed : {}", message);
        }
    }

    @Override
    public void notifyOnline(String username) {
        Collection<Friends> friends = friendsRepository.findByUser1OrderByIdAsc(ApplicationUser.builder().email(username).build());
        friends.stream().map(Friends::getUser2).forEach(friend ->
            rabbitTemplate.convertAndSend(
                    exchange,
                    friend.getEmail(),
                    new ServerMessage(ONLINE, username))
        );
    }

    @Override
    public void notifyOffline(String username) {
        Collection<Friends> friends = friendsRepository.findByUser1OrderByIdAsc(ApplicationUser.builder().email(username).build());
        friends.stream().map(Friends::getUser2).forEach(friend ->
            rabbitTemplate.convertAndSend(
                    exchange,
                    friend.getEmail(),
                    new ServerMessage(OFFLINE, friend.getEmail()))
        );
    }

    @Override
    @Transactional
    public void confirmMessage(String username, String receiver) {
        Collection<ChatMessage> messages = messageRepository.findBySenderAndReceiverAndConfirmedFalse(username, receiver);
        messages.forEach(m -> m.setConfirmed(true));
    }

    @Override
    public void getMessages(String username) {
        HashMap<String, List<UserMessage>> messagesByUser = new HashMap<>();
        Collection<ChatMessage> messages = messageRepository.findBySenderOrReceiverOrderByTimestamp(username);
        messages.forEach(m -> {
            String receiver = m.getReceiver();
            String sender = m.getSender();

            if(!sender.equals(username)) messagesByUser.putIfAbsent(sender, new ArrayList<>());
            if(!receiver.equals(username)) messagesByUser.putIfAbsent(receiver, new ArrayList<>());

            UserMessage userMessage = new UserMessage(sender.equals(username) ? SEND : RECEIVE, m.getMessage(), m.getTimestamp());

            if(userMessage.getType() == SEND) messagesByUser.get(receiver).add(userMessage);
            else messagesByUser.get(sender).add(userMessage);
        });

    }
}
