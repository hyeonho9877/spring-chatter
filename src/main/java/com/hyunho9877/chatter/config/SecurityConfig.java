package com.hyunho9877.chatter.config;

import com.hyunho9877.chatter.filter.CustomAuthenticationFilter;
import com.hyunho9877.chatter.filter.CustomAuthorizationFilter;
import com.hyunho9877.chatter.filter.FilterChainValidator;
import com.hyunho9877.chatter.filter.URLFilterChainValidator;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

import static com.hyunho9877.chatter.domain.Role.*;

@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtConfig config;
    private final AuthenticationConfiguration configuration;

    @Bean
    public AuthenticationManager authenticationManager() throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        CustomAuthenticationFilter authenticationFilter = new CustomAuthenticationFilter(config, secretKey(), authenticationManager());
        authenticationFilter.setFilterProcessesUrl("/v1/auth/do");
        return http
                .httpBasic().disable()
                .csrf().disable()
                .cors().and()
                .headers().frameOptions().disable().and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                .authorizeRequests().antMatchers("/v1/auth/all-users").hasAnyAuthority(ADMIN.name(), MANAGER.name()).and()
                .addFilter(authenticationFilter)
                .addFilterBefore(new CustomAuthorizationFilter(secretKey(), config, filterChainValidator()), UsernamePasswordAuthenticationFilter.class)
                .authorizeRequests()
                .antMatchers("/auth/**").permitAll()
                .antMatchers("/v1/auth/do", "/v1/auth/token/refresh").permitAll()
                .antMatchers("/v1/**").authenticated()
                .anyRequest().authenticated().and()
                .build();
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
    public FilterChainValidator filterChainValidator() {
        return new URLFilterChainValidator();
    }
}
