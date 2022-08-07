package com.hyunho9877.chatter.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "application.jwt")
@Data
public class JwtConfig {
    private String secretKey;
    private String tokenPrefix;
    private Integer tokenExpirationAfterDays;
    private String issuerHeader;
    private String issuer;

    public String getAccessTokenHeader() {
        return "accessToken";
    }

    public String getRefreshTokenHeader(){
        return "refreshToken";
    }
}
