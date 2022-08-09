package com.hyunho9877.chatter.utils.jwt;

import com.hyunho9877.chatter.utils.jwt.interfaces.ApplicationJwtParser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;

@Component
public class SimpleApplicationJwtParser implements ApplicationJwtParser {

    private final JwtParser parser;

    public SimpleApplicationJwtParser(SecretKey secretKey) {
        this.parser = Jwts.parserBuilder().setSigningKey(secretKey).build();
    }

    @Override
    public String getSubject(String token) {
        Jws<Claims> claimsJws = parser.parseClaimsJws(token);
        return claimsJws.getBody().getSubject();
    }
}
