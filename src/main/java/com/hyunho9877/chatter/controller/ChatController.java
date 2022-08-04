package com.hyunho9877.chatter.controller;

import com.hyunho9877.chatter.domain.ChatRoom;
import com.hyunho9877.chatter.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;


    @GetMapping
    public List<ChatRoom> chat() {
        return chatService.findAllRoom();
    }

    @PostMapping
    public ChatRoom createRoom(@RequestBody String name) {
        return chatService.createRoom(name);
    }
}
