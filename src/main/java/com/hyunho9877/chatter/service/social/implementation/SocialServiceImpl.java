package com.hyunho9877.chatter.service.social.implementation;

import com.hyunho9877.chatter.domain.ApplicationUser;
import com.hyunho9877.chatter.domain.Friends;
import com.hyunho9877.chatter.dto.UserStatus;
import com.hyunho9877.chatter.repo.FriendsRepository;
import com.hyunho9877.chatter.repo.UserRepository;
import com.hyunho9877.chatter.service.social.SocialService;
import com.hyunho9877.chatter.utils.ws.WebSocketSessionManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SocialServiceImpl implements SocialService {

    private final UserRepository userRepository;
    private final FriendsRepository friendsRepository;

    @Override
    public ApplicationUser getUser(String email) {
        ApplicationUser applicationUser = userRepository.findById(email).orElseThrow();
        return ApplicationUser.builder()
                .email(applicationUser.getEmail())
                .age(applicationUser.getAge())
                .name(applicationUser.getName())
                .gender(applicationUser.getGender())
                .build();
    }

    @Override
    public List<ApplicationUser> getUsers(String keyword) {
        return userRepository.findByNameContainsIgnoreCase(keyword);
    }

    @Override
    public String registerNewFriend(String email, String following) throws EntityNotFoundException {
        ApplicationUser user = userRepository.findById(email).orElseThrow();
        ApplicationUser friend = userRepository.findById(following).orElseThrow();
        try {
            friendsRepository.save(Friends.of(user, friend));
            friendsRepository.save(Friends.of(friend, user));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return following;
    }

    @Override
    public List<ApplicationUser> getFriends(String email) {
        List<Friends> result = friendsRepository.findByUser(email);
        return result.stream().map(ApplicationUser::getPublicProfile).toList();
    }

    @Override
    @Transactional
    public String removeFriend(String email, String friendEmail) throws EntityNotFoundException {
        ApplicationUser applicationUser = userRepository.findById(email).orElseThrow();
        ApplicationUser friend = userRepository.getReferenceById(friendEmail);
        return friendEmail;
    }

    @Override
    public boolean isOnline(String email) {
        return WebSocketSessionManager.isSessionExists(email) == UserStatus.ONLINE;
    }
}
