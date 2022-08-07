package com.hyunho9877.chatter;

import com.hyunho9877.chatter.domain.Role;
import com.hyunho9877.chatter.domain.User;
import com.hyunho9877.chatter.service.interfaces.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Bean;

import java.util.ArrayList;

@SpringBootApplication
@ConfigurationPropertiesScan
public class ChatterApplication {
    public static void main(String[] args) {
        SpringApplication.run(ChatterApplication.class, args);
    }

    @Bean
    CommandLineRunner run(UserService userService) {
        return args -> {
            userService.registerNewRole(new Role("ROLE_USER"));
            userService.registerNewRole(new Role("ROLE_ADMIN"));
            userService.registerNewRole(new Role("ROLE_MANAGER"));
            userService.registerNewRole(new Role("ROLE_SUPER_ADMIN"));

            userService.registerNewUserAccount(new User("test1@gmail.com", "1234", "john", 24, User.Gender.MALE, new ArrayList<>()));
            userService.registerNewUserAccount(new User("test2@gmail.com", "1234", "will", 24, User.Gender.MALE, new ArrayList<>()));
            userService.registerNewUserAccount(new User("test3@gmail.com", "1234", "james", 24, User.Gender.MALE, new ArrayList<>()));
            userService.registerNewUserAccount(new User("test4@gmail.com", "1234", "jim", 24, User.Gender.MALE, new ArrayList<>()));

            userService.addRoleToUser("test1@gmail.com", "ROLE_USER");
            userService.addRoleToUser("test2@gmail.com", "ROLE_USER");
            userService.addRoleToUser("test3@gmail.com", "ROLE_USER");
            userService.addRoleToUser("test4@gmail.com", "ROLE_USER");
            userService.addRoleToUser("test4@gmail.com", "ROLE_ADMIN");
            userService.addRoleToUser("test4@gmail.com", "ROLE_SUPER_ADMIN");
        };
    }
}

