package com.hyunho9877.chatter.utils.jwt.interfaces;

public interface ApplicationJwtParser {
    String getSubject(String token);
}
