package com.hyunho9877.chatter.service.interfaces;

import com.hyunho9877.chatter.domain.Role;
import com.hyunho9877.chatter.domain.User;
import com.hyunho9877.chatter.dto.UserDto;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;
import java.util.Optional;

public interface UserService extends UserDetailsService {
    Optional<User> registerNewUserAccount(User user);
    boolean isDuplicated(String email);
    void addRoleToUser(String email, Role role);
    Optional<User> getUser(String email);
    List<User> getAllUsers();
    User buildUser(UserDto userDto);
}
