package com.hyunho9877.chatter.service;

import com.hyunho9877.chatter.config.JwtConfig;
import com.hyunho9877.chatter.domain.Role;
import com.hyunho9877.chatter.domain.User;
import com.hyunho9877.chatter.dto.UserDto;
import com.hyunho9877.chatter.repo.UserRepository;
import com.hyunho9877.chatter.service.interfaces.AuthService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
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
    private final SecretKey secretKey;
    private final JwtConfig jwtConfig;

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
            JwtParser parser = Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build();
            Jws<Claims> jws = parser.parseClaimsJws(refreshToken);
            String username = jws.getBody().getSubject();
            User user = getUser(username).orElseThrow();
            String accessToken = Jwts.builder()
                    .setSubject(user.getEmail())
                    .setExpiration(new Date(System.currentTimeMillis() + 10 * 60 * 1000))
                    .setIssuer(issuer)
                    .claim(jwtConfig.getRoleHeader(), user.getRole())
                    .signWith(secretKey)
                    .compact();

            refreshToken = Jwts.builder()
                    .setSubject(user.getEmail())
                    .setExpiration(new Date(System.currentTimeMillis() + 30 * 60 * 1000))
                    .setIssuer(issuer)
                    .signWith(secretKey)
                    .compact();

            return Map.of(jwtConfig.getAccessTokenHeader(), accessToken, jwtConfig.getRefreshTokenHeader(), refreshToken);

        } catch (Exception e) {
            return Collections.emptyMap();
        }
    }
}
