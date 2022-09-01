package com.hyunho9877.chatter.service.social;

import com.hyunho9877.chatter.domain.ApplicationUser;

import java.util.List;

public interface SocialService {
    ApplicationUser getUser(String email);
    List<ApplicationUser> getUsers(String username, String keyword);
    String registerNewFollow(String email, String following);
    List<ApplicationUser> getFriends(String email);
    String removeFriend(String email, String friendEmail);
    boolean isOnline(String email);
}
