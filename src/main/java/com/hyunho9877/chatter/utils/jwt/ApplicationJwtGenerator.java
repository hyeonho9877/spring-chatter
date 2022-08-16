package com.hyunho9877.chatter.utils.jwt;

import com.hyunho9877.chatter.domain.Role;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public interface ApplicationJwtGenerator {
    String generateAccessToken(String subject, String issuer, Collection<GrantedAuthority> authorities);
    String generateAccessToken(String subject, String issuer, Role role);
    String generateAccessToken(String subject, int expiration, String issuer, Role role);
    String generateRefreshToken(String subject, String issuer);
    String generateRefreshToken(String subject, int expiration, String issuer);
    int getAccessTokenExpiration();
    int getRefreshTokenExpiration();
}
