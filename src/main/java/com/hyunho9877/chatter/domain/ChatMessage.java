package com.hyunho9877.chatter.domain;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;


@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter @ToString
public class ChatMessage implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long chatId;
    private String sender;
    private String receiver;
    private String message;
    private LocalDateTime timestamp;

    @PrePersist
    public void timestamp() {
        this.timestamp = LocalDateTime.now();
    }
}
