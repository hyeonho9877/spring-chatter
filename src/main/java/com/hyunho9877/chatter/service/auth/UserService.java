package com.hyunho9877.chatter.service.auth;

import com.hyunho9877.chatter.domain.ApplicationUser;
import com.hyunho9877.chatter.domain.Role;
import com.hyunho9877.chatter.dto.UserDto;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;
import java.util.Optional;

public interface UserService extends UserDetailsService {
    Optional<ApplicationUser> registerNewUserAccount(ApplicationUser applicationUser);
    boolean isDuplicated(String email);
    void addRoleToUser(String email, Role role);
    Optional<ApplicationUser> getUser(String email);
    List<ApplicationUser> getAllUsers();
    ApplicationUser buildUser(UserDto userDto);
    String remove(String email, String subject) throws IllegalStateException;
}
