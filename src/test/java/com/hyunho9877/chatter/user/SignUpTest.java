package com.hyunho9877.chatter.user;

import com.hyunho9877.chatter.domain.ApplicationUser;
import com.hyunho9877.chatter.domain.Gender;
import com.hyunho9877.chatter.dto.UserDto;
import com.hyunho9877.chatter.service.interfaces.UserService;
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
    private UserService userService;


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
        userDto.setGender(Gender.MALE);

        ApplicationUser applicationUser = userService.registerNewUserAccount(userService.buildUser(userDto)).orElseThrow();
        assertThat(applicationUser.getName()).isEqualTo(userDto.getFirstName() + userDto.getLastName());
        assertThat(applicationUser.getEmail()).isEqualTo(userDto.getEmail());
        assertThat(applicationUser.getAge()).isSameAs(userDto.getAge());
        assertThat(applicationUser.getGender()).isSameAs(userDto.getGender());
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
        userDto1.setGender(Gender.MALE);
        userService.registerNewUserAccount(userService.buildUser(userDto1)).orElseThrow();

        UserDto userDto2 = new UserDto();
        userDto2.setEmail("invalidMail");
        userDto2.setPassword("password");
        userDto2.setMatchingPassword("password");
        userDto2.setFirstName("first");
        userDto2.setLastName("last");
        userDto2.setAge(15);
        userDto2.setGender(Gender.MALE);

        assertThat(userService.registerNewUserAccount(userService.buildUser(userDto2))).isEqualTo(Optional.empty());
    }
}
