package com.hyunho9877.chatter.service.social.implementation;

import com.hyunho9877.chatter.domain.ApplicationUser;
import com.hyunho9877.chatter.domain.Exchange;
import com.hyunho9877.chatter.domain.Friends;
import com.hyunho9877.chatter.domain.FriendsKey;
import com.hyunho9877.chatter.dto.ServerMessage;
import com.hyunho9877.chatter.dto.ServerMessageType;
import com.hyunho9877.chatter.repo.FriendsRepository;
import com.hyunho9877.chatter.repo.UserRepository;
import com.hyunho9877.chatter.service.social.SocialService;
import com.hyunho9877.chatter.utils.ws.WebSocketSessionManager;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SocialServiceImpl implements SocialService {

    private final UserRepository userRepository;
    private final FriendsRepository friendsRepository;
    private final RabbitTemplate rabbitTemplate;

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
    public List<ApplicationUser> getUsers(String username, String keyword) {
        Set<String> friends = friendsRepository.findByUserAsSet(username).stream().map(Friends::getFriend).collect(Collectors.toSet());
        List<ApplicationUser> users = userRepository.findByNameContainsIgnoreCase(keyword);
        users.forEach(user -> {
            if (friends.contains(user.getEmail())) user.setFriend(true);
        });
        return users;
    }

    @Override
    public String registerNewFollow(String email, String following) throws EntityNotFoundException {
        ApplicationUser user = userRepository.findById(email).orElseThrow();
        ApplicationUser friend = userRepository.findById(following).orElseThrow();
        if(!friendsRepository.existsById(new FriendsKey(user.getEmail(), friend.getEmail()))){
            friendsRepository.save(Friends.of(user, friend));
            friendsRepository.save(Friends.of(friend, user));
            rabbitTemplate.convertAndSend(Exchange.EXCHANGE.getExchange(), following, new ServerMessage(ServerMessageType.NTF, email));
            return following;
        }
        return "";
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
        return WebSocketSessionManager.isSessionExists(email) == ServerMessageType.ONLINE;
    }
}
