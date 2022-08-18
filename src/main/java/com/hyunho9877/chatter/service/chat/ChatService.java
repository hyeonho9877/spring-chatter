package com.hyunho9877.chatter.service.chat;

import com.hyunho9877.chatter.domain.ChatMessage;
import org.springframework.amqp.core.Message;

public interface ChatService {

    void send(ChatMessage message);
    void notifyOnline(String username);
    void notifyOffline(String username);
}
