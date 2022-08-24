package com.hyunho9877.chatter.service.social.implementation;

import com.hyunho9877.chatter.domain.ApplicationUser;
import com.hyunho9877.chatter.domain.Friends;
import com.hyunho9877.chatter.repo.FriendsRepository;
import com.hyunho9877.chatter.repo.UserRepository;
import com.hyunho9877.chatter.service.social.SocialService;
import com.hyunho9877.chatter.utils.ws.WebSocketSessionManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class SocialServiceImpl implements SocialService {

    private final UserRepository userRepository;
    private final FriendsRepository friendsRepository;
    private final WebSocketSessionManager sessionManager;

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
    public String registerNewFriend(String email, String following) throws EntityNotFoundException {
        ApplicationUser user = userRepository.findById(email).orElseThrow();
        ApplicationUser friend = userRepository.getReferenceById(following);
        friendsRepository.saveAndFlush(Friends.of(user, friend));
        friendsRepository.saveAndFlush(Friends.of(friend, user));
        return following;
    }

    @Override
    public List<ApplicationUser> getFriends(String email) {
        List<Friends> result = friendsRepository.findByUser1OrderByIdAsc(userRepository.getReferenceById(email));
        return result.stream().map(Friends::getUser2).map(ApplicationUser::getPublicProfile).toList();
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
        return sessionManager.isSessionExists(email);
    }
}
