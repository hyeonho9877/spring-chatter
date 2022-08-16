package com.hyunho9877.chatter.utils.jwt;

public interface ApplicationJwtParser {
    String getSubject(String token);
}
