package com.hyunho9877.chatter.service;

import com.hyunho9877.chatter.domain.ApplicationUser;
import com.hyunho9877.chatter.domain.Friends;
import com.hyunho9877.chatter.repo.FriendsRepository;
import com.hyunho9877.chatter.repo.UserRepository;
import com.hyunho9877.chatter.service.interfaces.SocialService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
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
    public String registerNewFriend(String email, String following) throws EntityNotFoundException {
        ApplicationUser user = userRepository.findById(email).orElseThrow();
        ApplicationUser friend = userRepository.getReferenceById(following);
        friendsRepository.saveAndFlush(Friends.of(user, friend));
        friendsRepository.saveAndFlush(Friends.of(friend, user));
        return following;
    }

    @Override
    public Set<ApplicationUser> getFriends(String email) {
        Set<Friends> result = friendsRepository.findByUser1(userRepository.getReferenceById(email));
        return result.stream().map(Friends::getUser2).map(ApplicationUser::getPublicProfile).collect(Collectors.toSet());
    }

    @Override
    @Transactional
    public String removeFriend(String email, String friendEmail) throws EntityNotFoundException {
        ApplicationUser applicationUser = userRepository.findById(email).orElseThrow();
        ApplicationUser friend = userRepository.getReferenceById(friendEmail);
        return friendEmail;
    }
}
