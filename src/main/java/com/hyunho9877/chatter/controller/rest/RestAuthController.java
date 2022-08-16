package com.hyunho9877.chatter.controller.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import com.hyunho9877.chatter.domain.ApplicationUser;
import com.hyunho9877.chatter.dto.RoleToUser;
import com.hyunho9877.chatter.dto.UserDto;
import com.hyunho9877.chatter.service.auth.AuthService;
import com.hyunho9877.chatter.utils.cookie.CookieParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Map;

import static javax.servlet.http.HttpServletResponse.SC_BAD_REQUEST;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/auth")
@Slf4j
public class RestAuthController {

    private final AuthService authService;
    private final CookieParser cookieParser;

    @PostMapping("/registration.do")
    public ResponseEntity<String> registration(@Valid UserDto userDto) {
        log.info("received {}", userDto);
        try {
            if (authService.registerNewUserAccount(authService.buildUser(userDto)).isPresent()) {
                URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/v1/auth/registration.do").toUriString());
                return ResponseEntity.created(uri).body(userDto.getEmail());
            } else return ResponseEntity.badRequest().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/validate")
    public boolean validate(String email) {
        System.out.println("email = " + email);
        return !authService.isDuplicated(email);
    }

    @PostMapping("/role/add-to-user")
    public ResponseEntity<?> addRoleToUser(RoleToUser form) {
        authService.addRoleToUser(form.getEmail(), form.getRole());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/token/refresh")
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String token = cookieParser.parseRefreshCookie(request.getCookies());
        if (!Strings.isNullOrEmpty(token)) {
            Map<String, String> tokens = authService.refresh(token, request.getRequestURL().toString());
            new ObjectMapper().writeValue(response.getOutputStream(), tokens);
        } else {
            response.setStatus(SC_BAD_REQUEST);
            new ObjectMapper().writeValue(response.getOutputStream(), "Refresh Token Missing!");
        }
    }

    @GetMapping("/all-users")
    public ResponseEntity<List<ApplicationUser>> allUsers() {
        return ResponseEntity.ok(authService.getAllUsers());
    }

    @PostMapping("/withdrawal")
    public ResponseEntity<String> withdrawal(HttpServletRequest request, String username) {
        log.info("email received {}", username);
        String accessToken = cookieParser.parseAccessCookie(request.getCookies());
        String removed = authService.remove(username, accessToken);
        return ResponseEntity.ok(removed);
    }
}