package com.hyunho9877.chatter.service.social;

import com.hyunho9877.chatter.domain.ApplicationUser;
import com.hyunho9877.chatter.domain.Friends;

import java.util.Set;

public interface SocialService {
    ApplicationUser getUser(String email);
    String registerNewFriend(String email, String following);
    Set<ApplicationUser> getFriends(String email);
    String removeFriend(String email, String friendEmail);
}
