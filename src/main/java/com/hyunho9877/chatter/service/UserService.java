package com.hyunho9877.chatter.service;

import com.hyunho9877.chatter.domain.User;
import com.hyunho9877.chatter.dto.UserDto;
import com.hyunho9877.chatter.repo.UserRepository;
import com.hyunho9877.chatter.service.interfaces.UserServiceProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService implements UserServiceProvider {
    private final UserRepository userRepository;
    private final PasswordEncoder encoder;

    public Optional<User> registerNewUserAccount(UserDto userDto) {
        if (isDuplicated(userDto.getEmail())) return Optional.empty();
        User user = User.builder()
                .email(userDto.getEmail())
                .password(encoder.encode(userDto.getPassword()))
                .name(userDto.getFirstName() + userDto.getLastName())
                .age(userDto.getAge())
                .gender(userDto.getGender())
                .build();
        return Optional.of(userRepository.save(user));
    }

    public boolean isDuplicated(String email) throws NoSuchElementException {
        return userRepository.findById(email).isPresent();
    }
}
