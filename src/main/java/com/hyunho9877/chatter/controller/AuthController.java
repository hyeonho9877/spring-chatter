package com.hyunho9877.chatter.controller;

import com.hyunho9877.chatter.dto.UserDto;
import com.hyunho9877.chatter.service.interfaces.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {
    private final UserService userService;

    @GetMapping("/registration.form")
    public String showRegistrationForm(Model model) {
        UserDto userDto = new UserDto();
        model.addAttribute("user", userDto);
        return "registration";
    }

    @GetMapping("/sign.in")
    public String showSignInForm(Model model) {
        UserDto userDto = new UserDto();
        model.addAttribute("user", userDto);
        return "sign-in";
    }
}
