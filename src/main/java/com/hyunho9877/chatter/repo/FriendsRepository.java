package com.hyunho9877.chatter.repo;

import com.hyunho9877.chatter.domain.ApplicationUser;
import com.hyunho9877.chatter.domain.Friends;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface FriendsRepository extends JpaRepository<Friends, Long> {
    List<Friends> findByUser1OrderByIdAsc(ApplicationUser user1);
    @Query(value = "select f from Friends f where (f.user1.email=:sender and f.user2.email=:receiver) or (f.user1.email=:receiver and f.user2.email=:sender)")
    List<Friends> findFriendsBySenderAndReceiver(String sender, String receiver);
}