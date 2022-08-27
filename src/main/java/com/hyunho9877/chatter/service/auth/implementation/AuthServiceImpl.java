package com.hyunho9877.chatter.service.auth.implementation;

import com.hyunho9877.chatter.config.JwtConfig;
import com.hyunho9877.chatter.domain.ApplicationUser;
import com.hyunho9877.chatter.domain.Role;
import com.hyunho9877.chatter.dto.UserDto;
import com.hyunho9877.chatter.repo.UserRepository;
import com.hyunho9877.chatter.service.auth.AuthService;
import com.hyunho9877.chatter.utils.jwt.ApplicationJwtGenerator;
import com.hyunho9877.chatter.utils.jwt.ApplicationJwtParser;
import com.hyunho9877.chatter.utils.rabbitmq.RabbitMQUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final RabbitMQUtils rabbitMQUtils;


    public Optional<ApplicationUser> registerNewUserAccount(ApplicationUser applicationUser) {
        if (isDuplicated(applicationUser.getEmail())) return Optional.empty();
        log.info("Saving new user {} to the database", applicationUser.getEmail());
        applicationUser.setPassword(encoder.encode(applicationUser.getPassword()));
        rabbitMQUtils.declareQueue(applicationUser.getEmail(), true);
        return Optional.of(userRepository.save(applicationUser));
    }

    public boolean isDuplicated(String email) throws NoSuchElementException {
        return userRepository.findById(email).isPresent();
    }

    @Override
    public void addRoleToUser(String email, Role role) {
        ApplicationUser applicationUser = userRepository.findById(email).orElseThrow();
        applicationUser.setRole(role);
    }

    @Override
    public Optional<ApplicationUser> getUser(String email) {
        return userRepository.findById(email);
    }

    @Override
    public List<ApplicationUser> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public ApplicationUser buildUser(UserDto userDto) {
        return ApplicationUser.builder()
                .email(userDto.getEmail())
                .password(userDto.getPassword())
                .name(userDto.getName())
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
        ApplicationUser applicationUser = userRepository.findById(email).orElseThrow(() -> {
            log.error("There is no user with " + email);
            throw new UsernameNotFoundException("There is no user with " + email);
        });
        return new org.springframework.security.core.userdetails.User(
                applicationUser.getEmail(),
                applicationUser.getPassword(),
                List.of(new SimpleGrantedAuthority(applicationUser.getRole().name())));
    }

    @Override
    public Map<String, String> refresh(String refreshToken, String issuer) {
        try {
            String username = parser.getSubject(refreshToken);
            ApplicationUser applicationUser = getUser(username).orElseThrow();
            String accessToken = jwtGenerator.generateAccessToken(username, issuer, applicationUser.getRole());
            return Map.of(jwtConfig.getAccessTokenHeader(), accessToken, jwtConfig.getRefreshTokenHeader(), refreshToken);
        } catch (Exception e) {
            return Collections.emptyMap();
        }
    }
}
