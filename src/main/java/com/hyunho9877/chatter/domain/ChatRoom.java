package com.hyunho9877.chatter.domain;

import com.hyunho9877.chatter.service.ChatService;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.springframework.web.socket.WebSocketSession;

import java.util.HashSet;
import java.util.Set;

import static com.hyunho9877.chatter.domain.ChatMessage.MessageType.ENTER;

@Getter
@ToString
public class ChatRoom {
    private String roomId;
    private String name;
    private Set<WebSocketSession> sessions = new HashSet<>();

    @Builder
    public ChatRoom(String roomId, String name) {
        this.roomId = roomId;
        this.name = name;
    }

    public void handleActions(WebSocketSession session, ChatMessage message, ChatService chatService) {
        if(message.getType()== ENTER){
            sessions.add(session);
            message.setMessage(message.getSender()+"님이 입장하였습니다.");
        }
        sendMessage(message, chatService);
    }

    private <T> void sendMessage(T message, ChatService chatService) {
        (sessions).forEach(session->chatService.sendMessage(session,message));
    }
}
