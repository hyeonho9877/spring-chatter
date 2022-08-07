package com.hyunho9877.chatter.service.interfaces;

import com.hyunho9877.chatter.domain.Role;
import com.hyunho9877.chatter.domain.User;
import com.hyunho9877.chatter.dto.UserDto;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Optional;

public interface UserService extends UserDetailsService {
    Optional<User> registerNewUserAccount(User user);
    boolean isDuplicated(String email);
    Role registerNewRole(Role role);
    void addRoleToUser(String email, String roleName);
    Optional<User> getUser(String email);
    User buildUser(UserDto userDto);
}
