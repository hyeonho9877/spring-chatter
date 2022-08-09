package com.hyunho9877.chatter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class ChatterApplication {
    public static void main(String[] args) {
        SpringApplication.run(ChatterApplication.class, args);
    }

    /*@Bean
    CommandLineRunner run(UserService userService) {
        return args -> {
            userService.registerNewUserAccount(new User("test1@gmail.com", "1234", "john", 24, MALE, USER));
            userService.registerNewUserAccount(new User("test2@gmail.com", "1234", "will", 24, MALE, USER));
            userService.registerNewUserAccount(new User("test3@gmail.com", "1234", "catalina", 24, FEMALE, MANAGER));
            userService.registerNewUserAccount(new User("test4@gmail.com", "1234", "jim", 24, MALE, ADMIN));
        };
    }*/
}

