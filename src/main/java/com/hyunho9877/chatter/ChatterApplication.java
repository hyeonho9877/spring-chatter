package com.hyunho9877.chatter;

import com.hyunho9877.chatter.domain.Gender;
import com.hyunho9877.chatter.domain.Role;
import com.hyunho9877.chatter.domain.User;
import com.hyunho9877.chatter.service.interfaces.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Bean;

import java.util.ArrayList;

import static com.hyunho9877.chatter.domain.Gender.FEMALE;
import static com.hyunho9877.chatter.domain.Gender.MALE;
import static com.hyunho9877.chatter.domain.Role.*;

@SpringBootApplication
@ConfigurationPropertiesScan
public class ChatterApplication {
    public static void main(String[] args) {
        SpringApplication.run(ChatterApplication.class, args);
    }

    @Bean
    CommandLineRunner run(UserService userService) {
        return args -> {
            userService.registerNewUserAccount(new User("test1@gmail.com", "1234", "john", 24, MALE, USER));
            userService.registerNewUserAccount(new User("test2@gmail.com", "1234", "will", 24, MALE, USER));
            userService.registerNewUserAccount(new User("test3@gmail.com", "1234", "catalina", 24, FEMALE, MANAGER));
            userService.registerNewUserAccount(new User("test4@gmail.com", "1234", "jim", 24, MALE, ADMIN));
        };
    }
}
