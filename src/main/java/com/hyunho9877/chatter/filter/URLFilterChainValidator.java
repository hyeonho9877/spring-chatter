package com.hyunho9877.chatter.filter;

import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class URLFilterChainValidator implements FilterChainValidator{

    private final Set<String> noAuthorizationNeededURL;

    public URLFilterChainValidator() {
        this.noAuthorizationNeededURL = new HashSet<>();
        noAuthorizationNeededURL.add("/ws");
        noAuthorizationNeededURL.add("/v1/auth/registration.do");
        noAuthorizationNeededURL.add("/auth/registration.form");
        noAuthorizationNeededURL.add("/v1/auth/do");
        noAuthorizationNeededURL.add("/v1/auth/token/refresh");
        noAuthorizationNeededURL.add("/auth/sign.in");
        noAuthorizationNeededURL.add("/favicon.ico");
    }

    @Override
    public boolean validate(String url) {
        return noAuthorizationNeededURL.contains(url);
    }
}
