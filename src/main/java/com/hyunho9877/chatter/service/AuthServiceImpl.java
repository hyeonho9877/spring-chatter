package com.hyunho9877.chatter.service;

import com.hyunho9877.chatter.config.JwtConfig;
import com.hyunho9877.chatter.domain.Role;
import com.hyunho9877.chatter.domain.User;
import com.hyunho9877.chatter.dto.UserDto;
import com.hyunho9877.chatter.repo.UserRepository;
import com.hyunho9877.chatter.service.interfaces.AuthService;
import com.hyunho9877.chatter.utils.jwt.SimpleJwtGenerator;
import com.hyunho9877.chatter.utils.jwt.SimpleApplicationJwtParser;
import com.hyunho9877.chatter.utils.jwt.interfaces.ApplicationJwtGenerator;
import com.hyunho9877.chatter.utils.jwt.interfaces.ApplicationJwtParser;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.SecretKey;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder encoder;
    private final ApplicationJwtParser parser;
    private final JwtConfig jwtConfig;
    private final ApplicationJwtParser jwtParser;
    private final ApplicationJwtGenerator jwtGenerator;

    public Optional<User> registerNewUserAccount(User user) {
        if (isDuplicated(user.getEmail())) return Optional.empty();
        log.info("Saving new user {} to the database", user.getEmail());
        user.setPassword(encoder.encode(user.getPassword()));
        return Optional.of(userRepository.save(user));
    }

    public boolean isDuplicated(String email) throws NoSuchElementException {
        return userRepository.findById(email).isPresent();
    }

    @Override
    public void addRoleToUser(String email, Role role) {
        User user = userRepository.findById(email).orElseThrow();
        user.setRole(role);
    }

    @Override
    public Optional<User> getUser(String email) {
        return userRepository.findById(email);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User buildUser(UserDto userDto) {
        return User.builder()
                .email(userDto.getEmail())
                .password(encoder.encode(userDto.getPassword()))
                .name(userDto.getFirstName() + userDto.getLastName())
                .age(userDto.getAge())
                .gender(userDto.getGender())
                .role(Role.USER)
                .build();
    }

    @Override
    public String remove(String email, String accessToken) throws IllegalStateException{
        String subject = jwtParser.getSubject(accessToken);
        if(email.equals(subject)) {
            userRepository.deleteById(email);
            return email;
        } else throw new IllegalStateException("JWT Subject is not same as withdrawal requester!!!");
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findById(email).orElseThrow(() -> {
            log.error("There is no user with " + email);
            throw new UsernameNotFoundException("There is no user with " + email);
        });
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                List.of(new SimpleGrantedAuthority(user.getRole().name())));
    }

    @Override
    public Map<String, String> refresh(String refreshToken, String issuer) {
        try {
            String username = parser.getSubject(refreshToken);
            User user = getUser(username).orElseThrow();
            String accessToken = jwtGenerator.generateAccessToken(username, issuer, user.getRole());
            return Map.of(jwtConfig.getAccessTokenHeader(), accessToken, jwtConfig.getRefreshTokenHeader(), refreshToken);
        } catch (Exception e) {
            return Collections.emptyMap();
        }
    }
}
