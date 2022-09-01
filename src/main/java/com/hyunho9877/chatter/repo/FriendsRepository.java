package com.hyunho9877.chatter.repo;

import com.hyunho9877.chatter.domain.Friends;
import com.hyunho9877.chatter.domain.FriendsKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Set;

public interface FriendsRepository extends JpaRepository<Friends, FriendsKey> {
    List<Friends> findByUser(String user);
    @Query(value = "select f from Friends f where f.user=:user")
    Set<Friends> findByUserAsSet(String user);
    @Query(value = "select f from Friends f where (f.user=:sender and f.friend=:receiver) or (f.user=:receiver and f.friend=:sender)")
    List<Friends> findFriendsBySenderAndReceiver(String sender, String receiver);
}