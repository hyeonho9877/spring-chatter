package com.hyunho9877.chatter.service.interfaces;

import com.hyunho9877.chatter.domain.ApplicationUser;
import com.hyunho9877.chatter.domain.Friends;

import java.util.Set;

public interface SocialService {
    ApplicationUser getUser(String email);
    String registerNewFriend(String email, String friendEmail);
    Set<Friends> getFriends(String email);
    String removeFriend(String email, String friendEmail);
}
