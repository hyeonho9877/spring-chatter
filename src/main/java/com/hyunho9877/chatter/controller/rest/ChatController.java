package com.hyunho9877.chatter.controller.rest;

import com.hyunho9877.chatter.domain.ChatMessage;
import com.hyunho9877.chatter.service.ChatServiceImpl;
import com.hyunho9877.chatter.service.interfaces.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @GetMapping
    public String chat(String receiver, String message, Authentication authentication) {
        String username = (String) authentication.getPrincipal();
        chatService.send(new ChatMessage(username, receiver, message));
        return "message sent!";
    }
}
