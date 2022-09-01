package com.hyunho9877.chatter.domain;

import com.hyunho9877.chatter.dto.ServerMessageType;
import com.hyunho9877.chatter.utils.converter.GenderConverter;
import com.hyunho9877.chatter.utils.converter.RoleConverter;
import com.hyunho9877.chatter.utils.ws.WebSocketSessionManager;
import lombok.*;

import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;
import java.io.Serializable;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ApplicationUser implements Serializable {
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
    private ServerMessageType onlineStatus;
    @Transient
    private int unconfirmed;
    @Transient
    private String lastMessage;
    @Transient
    private String lastChatted;
    @Transient
    private boolean isFriend;

    public static ApplicationUser getPublicProfile(Friends friend) {
        ApplicationUser user = friend.getAppFriend();
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
