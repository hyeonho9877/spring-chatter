package com.hyunho9877.chatter.utils.handler;

import com.hyunho9877.chatter.service.chat.ChatService;
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

    @EventListener
    public void webSocketConnectedListener(SessionConnectedEvent event) {
        log.info("session connected : {}", event.getSource());
        UsernamePasswordAuthenticationToken authentication = (UsernamePasswordAuthenticationToken) event.getUser();
        String username = authentication.getName();
        chatService.notifyOnline(username);
    }

    @EventListener
    public void webSocketDisconnectedListener(SessionDisconnectEvent event) {
        log.info("session disconnected : {}", event.getSource());
        UsernamePasswordAuthenticationToken authentication = (UsernamePasswordAuthenticationToken) event.getUser();
        String username = authentication.getName();
        chatService.notifyOffline(username);
    }
}
