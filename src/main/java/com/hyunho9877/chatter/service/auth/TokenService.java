package com.hyunho9877.chatter.service.auth;

import java.util.Map;

public interface TokenService {
    Map<String, String> refresh(String refreshToken, String issuer);
}
