package com.hyunho9877.chatter.domain;

import com.hyunho9877.chatter.dto.UserStatus;
import com.hyunho9877.chatter.utils.converter.GenderConverter;
import com.hyunho9877.chatter.utils.converter.RoleConverter;
import com.hyunho9877.chatter.utils.ws.WebSocketSessionManager;
import lombok.*;
import javax.persistence.*;

import static com.hyunho9877.chatter.dto.UserStatus.*;
import static com.hyunho9877.chatter.dto.UserStatus.OFFLINE;

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
    @org.springframework.data.annotation.Transient
    private UserStatus onlineStatus;

    public static ApplicationUser getPublicProfile(ApplicationUser user) {
        return new ApplicationUser(user.getEmail(), null, user.getName(), user.getAge(), user.getGender(), null, WebSocketSessionManager.isSessionExists(user.getEmail()) ? ONLINE : OFFLINE);
    }
}
