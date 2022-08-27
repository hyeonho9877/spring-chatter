package com.hyunho9877.chatter.service.chat.implementation;

import com.hyunho9877.chatter.domain.ApplicationUser;
import com.hyunho9877.chatter.domain.ChatMessage;
import com.hyunho9877.chatter.domain.Exchange;
import com.hyunho9877.chatter.domain.Friends;
import com.hyunho9877.chatter.dto.ServerMessage;
import com.hyunho9877.chatter.dto.UserMessage;
import com.hyunho9877.chatter.dto.UserStatus;
import com.hyunho9877.chatter.repo.ChatMessageRepository;
import com.hyunho9877.chatter.repo.FriendsRepository;
import com.hyunho9877.chatter.service.chat.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.xml.crypto.dsig.keyinfo.PGPData;
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
    @Transactional
    public void send(ChatMessage message) {
        try {
            message.setTimestamp(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm").format(LocalDateTime.now()));
            rabbitTemplate.convertAndSend(exchange, message.getReceiver(), message);
            messageRepository.save(message);
            String receiver = message.getReceiver();
            String sender = message.getSender();
            String timestamp = message.getTimestamp();
            List<Friends> friendRelation = friendsRepository.findFriendsBySenderAndReceiver(sender, receiver);
            assert friendRelation.size() == 2;

            Friends bySender;
            Friends byReceiver;
            if(friendRelation.get(0).getUser1().getEmail().equals(message.getSender())) {
                bySender = friendRelation.get(0);
                byReceiver = friendRelation.get(1);
            } else {
                bySender = friendRelation.get(1);
                byReceiver = friendRelation.get(0);
            }

            // update time
            updateLastTime(bySender, timestamp);
            updateLastTime(byReceiver, timestamp);

            // update message
            updateLastMessage(bySender, message.getMessage());
            updateLastMessage(byReceiver, message.getMessage());

            // update unconfirmed Count
            updateUnconfirmed(byReceiver);
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
                    new ServerMessage(UserStatus.ONLINE, username))
        );
    }

    @Override
    public void notifyOffline(String username) {
        Collection<Friends> friends = friendsRepository.findByUser1OrderByIdAsc(ApplicationUser.builder().email(username).build());
        friends.stream().map(Friends::getUser2).forEach(friend ->
            rabbitTemplate.convertAndSend(
                    exchange,
                    friend.getEmail(),
                    new ServerMessage(UserStatus.OFFLINE, username))
        );
    }

    @Override
    @Transactional
    public void confirmMessage(String username, String sender) {
        Collection<ChatMessage> messages = messageRepository.findBySenderAndReceiverAndConfirmedFalse(sender, username);
        updateConfirmed(username, sender);
        messages.forEach(m -> m.setConfirmed(true));
    }

    @Override
    public Map<String, List<UserMessage>> getMessages(String username) {
        HashMap<String, List<UserMessage>> messagesByUser = new HashMap<>();
        Collection<ChatMessage> messages = messageRepository.findBySenderOrReceiverOrderByTimestamp(username);
        messages.forEach(m -> {
            String receiver = m.getReceiver();
            String sender = m.getSender();

            if (!sender.equals(username)) messagesByUser.putIfAbsent(sender, new ArrayList<>());
            if (!receiver.equals(username)) messagesByUser.putIfAbsent(receiver, new ArrayList<>());

            UserMessage userMessage = new UserMessage(sender.equals(username) ? SEND : RECEIVE, m.getMessage(), m.getTimestamp());

            if (userMessage.getType() == SEND) messagesByUser.get(receiver).add(userMessage);
            else messagesByUser.get(sender).add(userMessage);
        });
        return messagesByUser;
    }

    private void updateLastTime(Friends friends, String timestamp) {
        friends.setLastChattedTime(timestamp);
    }

    private void updateLastMessage(Friends friends, String message) {
        friends.setLastMessage(message);
    }

    private void updateUnconfirmed(Friends byReceiver){
        byReceiver.setUnconfirmed(byReceiver.getUnconfirmed() + 1);
    }

    private void updateConfirmed(String username, String sender){
        List<Friends> friendRelation = friendsRepository.findFriendsBySenderAndReceiver(sender, username);
        Friends relation = friendRelation.get(0);
        if(relation.getUser1().getEmail().equals(username)) relation.setUnconfirmed(0);
        else {
            relation = friendRelation.get(1);
            relation.setUnconfirmed(0);
        }
    }
}
