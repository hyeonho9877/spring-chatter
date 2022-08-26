package com.hyunho9877.chatter.utils.handler;

import com.hyunho9877.chatter.service.chat.ChatService;
import com.hyunho9877.chatter.utils.ws.WebSocketSessionManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.Message;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

import java.security.Principal;
import java.util.Arrays;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketEventHandler {

    private final ChatService chatService;
    private final WebSocketSessionManager sessionManager;

    @EventListener
    public void webSocketConnectedListener(SessionConnectedEvent event) {
        UsernamePasswordAuthenticationToken authentication = (UsernamePasswordAuthenticationToken) event.getUser();
        assert authentication != null;
        String username = authentication.getName();
        chatService.notifyOnline(username);
        sessionManager.online(username);
    }

    @EventListener
    public void webSocketDisconnectedListener(SessionDisconnectEvent event) {
        UsernamePasswordAuthenticationToken authentication = (UsernamePasswordAuthenticationToken) event.getUser();
        assert authentication != null;
        String username = authentication.getName();
        chatService.notifyOffline(username);
        sessionManager.offline(username);
    }
}
