package com.hyunho9877.chatter.config;

import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import com.hyunho9877.chatter.filter.CustomAuthenticationFilter;
import com.hyunho9877.chatter.filter.CustomAuthorizationFilter;
import com.hyunho9877.chatter.filter.FilterChainValidator;
import com.hyunho9877.chatter.utils.cookie.CookieParser;
import com.hyunho9877.chatter.utils.jwt.implementation.SimpleJwtGenerator;
import com.hyunho9877.chatter.utils.jwt.ApplicationJwtGenerator;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

import static com.hyunho9877.chatter.domain.Role.ADMIN;
import static com.hyunho9877.chatter.domain.Role.MANAGER;

@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtConfig config;
    private final AuthenticationConfiguration configuration;
    private final FilterChainValidator filterChainValidator;
    private final CookieParser cookieParser;

    @Bean
    public AuthenticationManager authenticationManager() throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        CustomAuthenticationFilter authenticationFilter = new CustomAuthenticationFilter(config, authenticationManager(), jwtGenerator());
        authenticationFilter.setFilterProcessesUrl("/v1/auth/do");
        return http
                .httpBasic().disable()
                .csrf().disable()
                .cors().and()
                .headers().frameOptions().disable().and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                .addFilter(authenticationFilter)
                .addFilterBefore(new CustomAuthorizationFilter(secretKey(), config, filterChainValidator, cookieParser), UsernamePasswordAuthenticationFilter.class)
                .authorizeRequests()
                .antMatchers("/auth/login").anonymous()
                .antMatchers("/v1/auth/do", "/v1/auth/token/refresh", "/v1/auth/registration").permitAll()
                .anyRequest().authenticated().and()
                .build();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().requestMatchers(PathRequest.toStaticResources().atCommonLocations());
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecretKey secretKey() {
        return Keys.hmacShaKeyFor(config.getSecretKey().getBytes(StandardCharsets.UTF_8));
    }

    @Bean
    public ApplicationJwtGenerator jwtGenerator() {
        return new SimpleJwtGenerator(config, secretKey());
    }

    @Bean
    public HashFunction hashFunction() {
        return Hashing.sha256();
    }
}
