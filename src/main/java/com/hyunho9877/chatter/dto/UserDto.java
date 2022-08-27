package com.hyunho9877.chatter.dto;

import com.hyunho9877.chatter.domain.Gender;
import com.hyunho9877.chatter.utils.validators.PasswordMatches;
import com.hyunho9877.chatter.utils.validators.ValidEmail;
import com.sun.istack.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotEmpty;

@Getter
@Setter
@ToString
@PasswordMatches
public class UserDto {
    @NotNull
    @NotEmpty
    private String name;

    @NotNull
    @NotEmpty
    private String password;
    @NotNull
    @NotEmpty
    private String matchingPassword;

    @ValidEmail
    @NotNull
    @NotEmpty
    private String email;

    @NotNull
    private int age;

    @NotNull
    private Gender gender;
}