package com.hyunho9877.chatter.repo;

import com.hyunho9877.chatter.domain.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    @Query("select c from ChatMessage c where c.sender=:username or c.receiver=:username order by c.timestamp")
    Collection<ChatMessage> findBySenderOrReceiverOrderByTimestamp(String username);
    Collection<ChatMessage> findBySenderAndReceiverAndConfirmedFalse(String sender, String receiver);

}