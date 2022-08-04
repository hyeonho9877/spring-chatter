package com.hyunho9877.chatter.domain;

import com.hyunho9877.chatter.utils.converter.GenderConverter;
import lombok.*;

import javax.persistence.Column;
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
    @Column
    private String email;
    @Column
    private String password;
    @Column
    private String name;
    @Column
    private int age;
    @Column
    @Convert(converter = GenderConverter.class)
    private Gender gender;

    public enum Gender {
        MALE, FEMALE
    }
}
