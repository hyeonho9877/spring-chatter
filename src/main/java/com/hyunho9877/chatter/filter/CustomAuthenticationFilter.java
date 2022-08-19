package com.hyunho9877.chatter.filter;

import com.google.common.base.Strings;
import com.hyunho9877.chatter.config.JwtConfig;
import com.hyunho9877.chatter.utils.jwt.ApplicationJwtGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class CustomAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final JwtConfig jwtConfig;
    private final AuthenticationManager authenticationManager;
    private final ApplicationJwtGenerator jwtGenerator;

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        log.info("username is {} and pwd is {}", username, password);
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, password);
        return authenticationManager.authenticate(authenticationToken);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) throws IOException, ServletException {
        User user = (User) authentication.getPrincipal();
        log.info("authenticated user : {}, authorities : {}", user.getUsername(), user.getAuthorities());
        String accessToken = jwtGenerator.generateAccessToken(user.getUsername(), request.getRequestURL().toString(), user.getAuthorities());
        String refreshToken = jwtGenerator.generateRefreshToken(user.getUsername(), request.getRequestURL().toString());
//        response.setHeader(config.getAccessTokenHeader(), accessToken);
//        response.setHeader(config.getRefreshTokenHeader(), refreshToken);
        Cookie accessCookie = new Cookie(jwtConfig.getAccessTokenHeader(), accessToken);
        accessCookie.setHttpOnly(true);
        accessCookie.setPath("/");
        accessCookie.setMaxAge(jwtGenerator.getAccessTokenExpiration() / 1000);

        Cookie refreshCookie = new Cookie(jwtConfig.getRefreshTokenHeader(), refreshToken);
        refreshCookie.setHttpOnly(true);
        refreshCookie.setPath("/");
        refreshCookie.setMaxAge(jwtGenerator.getRefreshTokenExpiration() / 1000);

        log.info("cookie domain {}", request.getServerName());

        response.addCookie(accessCookie);
        response.addCookie(refreshCookie);

        String go = (String) request.getAttribute("go");
        if (!Strings.isNullOrEmpty(go)) response.setHeader("Referer", go);
        else response.setHeader("Referer", "/");
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }
}
