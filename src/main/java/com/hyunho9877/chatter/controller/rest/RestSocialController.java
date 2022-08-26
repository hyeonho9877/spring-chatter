package com.hyunho9877.chatter.controller.rest;

import com.hyunho9877.chatter.domain.ApplicationUser;
import com.hyunho9877.chatter.service.social.SocialService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Set;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/v1/social")
public class RestSocialController {

    private final SocialService socialService;

    @PostMapping("/user")
    public ResponseEntity<ApplicationUser> getUser(Authentication authentication, String username) {
        return ResponseEntity.ok(socialService.getUser(username));
    }

    @PostMapping("/following")
    public ResponseEntity<List<ApplicationUser>> getFriends(Authentication authentication) {
        String username = (String) authentication.getPrincipal();
        List<ApplicationUser> friends = socialService.getFriends(username);
        return ResponseEntity.ok(friends);
    }

    @PostMapping("/follow")
    public ResponseEntity<String> follow(Authentication authentication, String following) {
        String username = (String) authentication.getPrincipal();
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/v1/social/follow").toUriString());
        return ResponseEntity.created(uri).body(socialService.registerNewFriend(username, following));
    }

    @PostMapping("/unfollow")
    public ResponseEntity<String> unfollow(Authentication authentication, String userID) {
        String username = (String) authentication.getPrincipal();
        return ResponseEntity.ok(socialService.removeFriend(username, userID));
    }

    @PostMapping("/query")
    public ResponseEntity<Boolean> queryOnline(String username) {
        return ResponseEntity.ok(socialService.isOnline(username));
    }
}
