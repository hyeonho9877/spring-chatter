package com.hyunho9877.chatter.repo;

import com.hyunho9877.chatter.domain.ApplicationUser;
import com.hyunho9877.chatter.domain.Friends;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;

import java.util.Set;

public interface FriendsRepository extends JpaRepository<Friends, Long> {
    Set<Friends> findByUser1(@NonNull ApplicationUser user1);

}