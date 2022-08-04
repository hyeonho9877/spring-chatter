package com.hyunho9877.chatter.service.interfaces;

import com.hyunho9877.chatter.domain.User;
import com.hyunho9877.chatter.dto.UserDto;

import java.util.Optional;

public interface UserServiceProvider {
    Optional<User> registerNewUserAccount(UserDto userDto);
    boolean isDuplicated(String email);
}
