package com.hyunho9877.chatter.service.chat;

import com.hyunho9877.chatter.domain.ChatMessage;
import com.hyunho9877.chatter.dto.UserMessage;
import org.springframework.amqp.core.Message;

import java.util.List;
import java.util.Map;

public interface ChatService {

    void send(ChatMessage message);
    void notifyOnline(String username);
    void notifyOffline(String username);
    void confirmMessage(String username, String receiver);
    Map<String, List<UserMessage>> getMessages(String username);
}
