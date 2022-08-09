package com.hyunho9877.chatter.domain;

import com.hyunho9877.chatter.utils.converter.GenderConverter;
import com.hyunho9877.chatter.utils.converter.RoleConverter;
import lombok.*;

import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    private String email;
    private String password;
    private String name;
    private Integer age;
    @Convert(converter = GenderConverter.class)
    private Gender gender;
    @Convert(converter = RoleConverter.class)
    private Role role;
}
