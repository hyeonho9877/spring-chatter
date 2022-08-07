package com.hyunho9877.chatter.controller.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import com.hyunho9877.chatter.domain.Role;
import com.hyunho9877.chatter.dto.UserDto;
import com.hyunho9877.chatter.service.interfaces.AuthService;
import lombok.Data;
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
import java.util.Map;

import static com.google.common.net.HttpHeaders.AUTHORIZATION;
import static javax.servlet.http.HttpServletResponse.SC_BAD_REQUEST;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/auth")
@Slf4j
public class RestAuthController {

    private final AuthService authService;

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

    @PostMapping("/role/new")
    public ResponseEntity<Role> saveRole(Role role) {
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/v1/auth/role/new").toUriString());
        return ResponseEntity.created(uri).body(authService.registerNewRole(role));
    }

    @PostMapping("/role/add-to-user")
    public ResponseEntity<?> addRoleToUser(RoleToUserForm form) {
        authService.addRoleToUser(form.getEmail(), form.getRoleName());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/token/refresh")
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String token = request.getHeader(AUTHORIZATION);
        if(!Strings.isNullOrEmpty(token)){
            Map<String, String> tokens = authService.refresh(token, request.getRequestURL().toString());
            new ObjectMapper().writeValue(response.getOutputStream(), tokens);
        } else {
            response.setStatus(SC_BAD_REQUEST);
            new ObjectMapper().writeValue(response.getOutputStream(), "Refresh Token Missing!");
        }
    }
}

@Data
class RoleToUserForm {
    private String email;
    private String roleName;
}