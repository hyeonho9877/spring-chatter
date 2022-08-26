package com.hyunho9877.chatter.domain;

import com.hyunho9877.chatter.dto.UserStatus;
import com.hyunho9877.chatter.utils.converter.GenderConverter;
import com.hyunho9877.chatter.utils.converter.RoleConverter;
import com.hyunho9877.chatter.utils.ws.WebSocketSessionManager;
import lombok.*;
import javax.persistence.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ApplicationUser {
    @Id
    private String email;
    private String password;
    private String name;
    private Integer age;
    @Convert(converter = GenderConverter.class)
    private Gender gender;
    @Convert(converter = RoleConverter.class)
    private Role role;
    @Transient
    private UserStatus onlineStatus;
    @Transient
    private int unconfirmed;
    @Transient
    private String lastMessage;
    @Transient
    private String lastChatted;

    public static ApplicationUser getPublicProfile(Friends friend) {
        ApplicationUser user = friend.getUser2();
        return ApplicationUser.builder()
                .email(user.getEmail())
                .name(user.getName())
                .age(user.getAge())
                .onlineStatus(WebSocketSessionManager.isSessionExists(user.getEmail()))
                .gender(user.getGender())
                .unconfirmed(friend.getUnconfirmed())
                .lastMessage(friend.getLastMessage())
                .lastChatted(friend.getLastChattedTime())
                .build();
    }
}
