package com.hyunho9877.chatter.domain;

import com.google.common.base.Strings;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


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
    private String timestamp;
    private boolean confirmed = false;

    @PrePersist
    public void prePersist() {
        if(Strings.isNullOrEmpty(this.timestamp)) this.timestamp = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm").format(LocalDateTime.now());
        this.confirmed = false;
    }
}
