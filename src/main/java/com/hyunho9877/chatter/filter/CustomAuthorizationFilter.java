package com.hyunho9877.chatter.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.net.HttpHeaders.AUTHORIZATION;
import static javax.servlet.http.HttpServletResponse.SC_FORBIDDEN;

@RequiredArgsConstructor
@Slf4j
public class CustomAuthorizationFilter extends OncePerRequestFilter {

    private final SecretKey secretKey;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (request.getServletPath().equals("/v1/auth/do") || request.getServletPath().equals("v1/auth/token/refresh")) filterChain.doFilter(request, response);
        else {
            String token = request.getHeader(AUTHORIZATION);
            if(!Strings.isNullOrEmpty(token)){
                try {
                    JwtParser parser = Jwts.parserBuilder()
                            .setSigningKey(secretKey)
                            .build();
                    Jws<Claims> jws = parser.parseClaimsJws(token);
                    String username = jws.getBody().getSubject();
                    List<LinkedHashMap<String, ?>> roles = (List<LinkedHashMap<String, ?>>) jws.getBody().get("roles");
                    List<SimpleGrantedAuthority> authorities = roles.stream().map(role -> role.get("authority")).map(role->new SimpleGrantedAuthority(role.toString())).toList();
                    log.info("authorities : {}", authorities);
                    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, null, authorities);
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                    filterChain.doFilter(request, response);
                } catch (Exception e) {
                    log.error("jwt authorization failed with {}", token);
                    response.setStatus(SC_FORBIDDEN);
                    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                    Map<String, String> error_message = Map.of("error_message", e.getMessage());
                    new ObjectMapper().writeValue(response.getOutputStream(), error_message);
                }
            } else filterChain.doFilter(request, response);
        }
    }
}
