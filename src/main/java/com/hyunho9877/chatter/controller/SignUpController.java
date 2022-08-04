package com.hyunho9877.chatter.controller;

import com.hyunho9877.chatter.dto.UserDto;
import com.hyunho9877.chatter.service.interfaces.UserServiceProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.Valid;

@Controller
@RequiredArgsConstructor
public class SignUpController {
    private final UserServiceProvider userService;

    @GetMapping("/user/registration.form")
    public String showRegistrationForm(Model model) {
        UserDto userDto = new UserDto();
        model.addAttribute("user", userDto);
        return "registration";
    }

    @PostMapping("/user/registration.do")
    @ResponseBody
    public ResponseEntity<?> registration(@Valid UserDto userDto) {
        System.out.println("userDto = " + userDto);
        try {
            if(userService.registerNewUserAccount(userDto).isPresent()) return ResponseEntity.ok(userDto.getEmail());
            else return ResponseEntity.badRequest().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/user/registration.validate")
    @ResponseBody
    public boolean validate(String email) {
        System.out.println("email = " + email);
        return !userService.isDuplicated(email);
    }
}
