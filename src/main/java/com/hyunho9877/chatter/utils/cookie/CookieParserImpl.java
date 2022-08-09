package com.hyunho9877.chatter.utils.cookie;

import com.hyunho9877.chatter.config.JwtConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;
import java.util.Arrays;

@Component
@RequiredArgsConstructor
public class CookieParserImpl implements CookieParser{

    private final JwtConfig jwtConfig;

    @Override
    public String parseAccessCookie(Cookie[] cookies) {
        if(cookies == null) return "";
        return Arrays.stream(cookies)
                .filter(cookie -> cookie.getName().equals(jwtConfig.getAccessTokenHeader()))
                .map(Cookie::getValue)
                .findFirst()
                .orElse("");
    }

    @Override
    public String parseRefreshCookie(Cookie[] cookies) {
        if(cookies == null) return "";
        return Arrays.stream(cookies)
                .filter(cookie -> cookie.getName().equals(jwtConfig.getRefreshTokenHeader()))
                .map(Cookie::getValue)
                .findFirst()
                .orElse("");
    }
}
