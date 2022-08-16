package com.hyunho9877.chatter.domain;

import com.hyunho9877.chatter.utils.converter.GenderConverter;
import com.hyunho9877.chatter.utils.converter.RoleConverter;
import lombok.*;
import org.springframework.data.repository.cdi.Eager;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

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

    public static ApplicationUser getPublicProfile(ApplicationUser user) {
        return new ApplicationUser(user.getEmail(), null, user.getName(), user.getAge(), user.getGender(), null);
    }
}
