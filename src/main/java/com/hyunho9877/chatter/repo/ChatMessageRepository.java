package com.hyunho9877.chatter.repo;

import com.hyunho9877.chatter.domain.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
}