package com.hyunho9877.chatter.controller.rest;

import com.google.common.hash.HashFunction;
import com.hyunho9877.chatter.domain.ChatMessage;
import com.hyunho9877.chatter.service.interfaces.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/v1/chat")
@RequiredArgsConstructor
@Slf4j
public class RestChatController {

    private final SimpMessageSendingOperations messageSendingOperations;

    @MessageMapping("/hello")
    public void newUser(@Payload ChatMessage message, SimpMessageHeaderAccessor headerAccessor) {
        System.out.println("RestChatController.newUser");
        System.out.println("message = " + message);
        headerAccessor.getSessionAttributes().put("username", message.getSender());
        messageSendingOperations.convertAndSend("/topic/"+message.getReceiver(), message);
    }
}
