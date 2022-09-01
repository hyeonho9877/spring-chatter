package com.hyunho9877.chatter.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ServerMessage implements Serializable {
    private ServerMessageType type;
    private String user;
}
