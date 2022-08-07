package com.hyunho9877.chatter.domain;

import com.hyunho9877.chatter.utils.converter.GenderConverter;
import lombok.*;

import javax.persistence.*;
import java.util.Collection;

@Entity
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @Column
    private String email;
    @Column
    private String password;
    @Column
    private String name;
    @Column
    private Integer age;
    @Column
    @Convert(converter = GenderConverter.class)
    private Gender gender;
    @ManyToMany(fetch = FetchType.EAGER)
    private Collection<Role> roles;

    public enum Gender {
        MALE, FEMALE
    }
}
