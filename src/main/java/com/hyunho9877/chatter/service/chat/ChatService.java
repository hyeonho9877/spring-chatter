package com.hyunho9877.chatter.service.chat;

import com.hyunho9877.chatter.domain.ChatMessage;
import org.springframework.amqp.core.Message;

public interface ChatService {

    void send(ChatMessage message);
    void notifyOnline(String username);
    void notifyOffline(String username);
    void confirmMessage(String username, String receiver);
    void getMessages(String username);
}
