package com.hyunho9877.chatter.filter;

import com.hyunho9877.chatter.config.JwtConfig;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.crypto.SecretKey;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;

@Slf4j
@RequiredArgsConstructor
public class CustomAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final JwtConfig jwtConfig;
    private final SecretKey secretKey;
    private final AuthenticationManager authenticationManager;

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
        Date accessTokenExp = new Date(System.currentTimeMillis() + 10 * 60 * 1000);
        String accessToken = Jwts.builder()
                .setSubject(user.getUsername())
                .setExpiration(accessTokenExp)
                .setIssuer(request.getRequestURL().toString())
                .claim(jwtConfig.getRoleHeader(), user.getAuthorities())
                .signWith(secretKey)
                .compact();

        Date refreshTokenExp = new Date(System.currentTimeMillis() + 30 * 60 * 1000);
        String refreshToken = Jwts.builder()
                .setSubject(user.getUsername())
                .setExpiration(refreshTokenExp)
                .setIssuer(request.getRequestURL().toString())
                .signWith(secretKey)
                .compact();

//        response.setHeader(config.getAccessTokenHeader(), accessToken);
//        response.setHeader(config.getRefreshTokenHeader(), refreshToken);
        Cookie accessCookie = new Cookie(jwtConfig.getAccessTokenHeader(), accessToken);
        accessCookie.setHttpOnly(true);
        accessCookie.setMaxAge(10 * 60 * 1000);

        Cookie refreshCookie = new Cookie(jwtConfig.getRefreshTokenHeader(), refreshToken);
        refreshCookie.setHttpOnly(true);
        refreshCookie.setMaxAge(30 * 60 * 1000);

        log.info("cookie domain {}", request.getServerName());
        response.addCookie(accessCookie);
        response.addCookie(refreshCookie);
    }
}
