package com.hyunho9877.chatter.utils.jwt.implementation;

import com.hyunho9877.chatter.config.JwtConfig;
import com.hyunho9877.chatter.domain.Role;
import com.hyunho9877.chatter.utils.jwt.ApplicationJwtGenerator;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

import javax.crypto.SecretKey;
import java.util.Collection;
import java.util.Date;

@RequiredArgsConstructor
public class SimpleJwtGenerator implements ApplicationJwtGenerator {

    private final JwtConfig jwtConfig;
    private final SecretKey secretKey;
    private final int DEFAULT_ACCESS_TOKEN_EXPIRATION = 10;
    private final int DEFAULT_REFRESH_TOKEN_EXPIRATION = 30;

    @Override
    public String generateAccessToken(String subject, String issuer, Collection<GrantedAuthority> authorities) {
        return Jwts.builder()
                .setSubject(subject)
                .setExpiration(new Date(System.currentTimeMillis() + DEFAULT_ACCESS_TOKEN_EXPIRATION * 60 * 1000))
                .setIssuer(issuer)
                .claim(jwtConfig.getRoleHeader(), authorities)
                .signWith(secretKey)
                .compact();
    }

    @Override
    public String generateAccessToken(String subject, String issuer, Role role) {
        return Jwts.builder()
                .setSubject(subject)
                .setExpiration(new Date(System.currentTimeMillis() + DEFAULT_ACCESS_TOKEN_EXPIRATION * 60 * 1000))
                .setIssuer(issuer)
                .claim(jwtConfig.getRoleHeader(), role)
                .signWith(secretKey)
                .compact();
    }

    /**
     *
     * @param subject Jwt Subject
     * @param expiration Expiration in minutes Max 15 minutes
     * @param issuer Jwt issuer, maybe server domain
     * @param role Jwt claimers role
     * @return Generated JWT Token
     */
    @Override
    public String generateAccessToken(String subject, int expiration, String issuer, Role role){
        return Jwts.builder()
                .setSubject(subject)
                .setExpiration(new Date(System.currentTimeMillis() + (long) Math.min(expiration, DEFAULT_ACCESS_TOKEN_EXPIRATION) * 60 * 1000))
                .setIssuer(issuer)
                .claim(jwtConfig.getRoleHeader(), role)
                .signWith(secretKey)
                .compact();
    }

    @Override
    public String generateRefreshToken(String subject, String issuer) {
        return Jwts.builder()
                .setSubject(subject)
                .setExpiration(new Date(System.currentTimeMillis() + DEFAULT_REFRESH_TOKEN_EXPIRATION * 60 * 1000))
                .setIssuer(issuer)
                .signWith(secretKey)
                .compact();
    }

    @Override
    public String generateRefreshToken(String subject, int expiration, String issuer){
        return Jwts.builder()
                .setSubject(subject)
                .setExpiration(new Date(System.currentTimeMillis() + (long) Math.min(expiration, DEFAULT_REFRESH_TOKEN_EXPIRATION) * 60 * 1000))
                .setIssuer(issuer)
                .signWith(secretKey)
                .compact();
    }

    @Override
    public int getAccessTokenExpiration() {
        return DEFAULT_ACCESS_TOKEN_EXPIRATION * 60 * 1000;
    }

    @Override
    public int getRefreshTokenExpiration() {
        return DEFAULT_REFRESH_TOKEN_EXPIRATION * 60 * 1000;
    }
}
