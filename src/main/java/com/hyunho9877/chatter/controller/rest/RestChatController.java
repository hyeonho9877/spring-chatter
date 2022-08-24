package com.hyunho9877.chatter.controller.rest;

import com.hyunho9877.chatter.domain.ChatMessage;
import com.hyunho9877.chatter.dto.UserMessage;
import com.hyunho9877.chatter.service.chat.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/v1/chat")
@RequiredArgsConstructor
@Slf4j
public class RestChatController {

    private final ChatService chatService;

    @MessageMapping("/hello")
    public void newUser(@Payload ChatMessage message, SimpMessageHeaderAccessor headerAccessor) {
        headerAccessor.getSessionAttributes().put("username", message.getSender());
        log.info("{}", message);
        chatService.send(message);
    }

    @PostMapping("/confirm")
    public ResponseEntity<?> confirm(Authentication authentication, String targetUser) {
        String username = (String) authentication.getPrincipal();
        chatService.confirmMessage(username, targetUser);
        return ResponseEntity.ok(targetUser);
    }

    @PostMapping("/get")
    public ResponseEntity<Map<String, List<UserMessage>>> getMessages(Authentication authentication) {
        String username = (String) authentication.getPrincipal();
        Map<String, List<UserMessage>> messages = chatService.getMessages(username);
        return ResponseEntity.ok(messages);
    }
}
