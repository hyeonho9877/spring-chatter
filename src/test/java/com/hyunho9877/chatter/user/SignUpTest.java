package com.hyunho9877.chatter.user;

import com.hyunho9877.chatter.domain.User;
import com.hyunho9877.chatter.dto.UserDto;
import com.hyunho9877.chatter.service.interfaces.UserServiceProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
public class SignUpTest {

    @Autowired
    private UserServiceProvider userService;


    @Test
    @DisplayName("정상 입력")
    void signUpWithAllValid() {
        UserDto userDto = new UserDto();
        userDto.setEmail("valid@gmail.com");
        userDto.setPassword("password");
        userDto.setMatchingPassword("password");
        userDto.setFirstName("first");
        userDto.setLastName("last");
        userDto.setAge(15);
        userDto.setGender(User.Gender.MALE);

        User user = userService.registerNewUserAccount(userDto).orElseThrow();
        assertThat(user.getName()).isEqualTo(userDto.getFirstName() + userDto.getLastName());
        assertThat(user.getEmail()).isEqualTo(userDto.getEmail());
        assertThat(user.getAge()).isSameAs(userDto.getAge());
        assertThat(user.getGender()).isSameAs(userDto.getGender());
    }

    @Test
    @DisplayName("중복 이메일")
    void signUpWithDuplicatedEmail() {
        UserDto userDto1 = new UserDto();
        userDto1.setEmail("invalidMail");
        userDto1.setPassword("password");
        userDto1.setMatchingPassword("password");
        userDto1.setFirstName("first");
        userDto1.setLastName("last");
        userDto1.setAge(15);
        userDto1.setGender(User.Gender.MALE);
        userService.registerNewUserAccount(userDto1).orElseThrow();

        UserDto userDto2 = new UserDto();
        userDto2.setEmail("invalidMail");
        userDto2.setPassword("password");
        userDto2.setMatchingPassword("password");
        userDto2.setFirstName("first");
        userDto2.setLastName("last");
        userDto2.setAge(15);
        userDto2.setGender(User.Gender.MALE);

        assertThat(userService.registerNewUserAccount(userDto2)).isEqualTo(Optional.empty());
    }
}
