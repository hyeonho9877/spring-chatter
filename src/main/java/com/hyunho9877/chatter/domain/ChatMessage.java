package com.hyunho9877.chatter.domain;

import lombok.Data;

@Data
public class ChatMessage {
    public enum MessageType{
        ENTER, TALK
    }

    private MessageType type;
    private String roomId;
    private String sender;
    private String message;
}
