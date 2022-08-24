package com.hyunho9877.chatter.utils.ws;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Component
public class WebSocketSessionManager {
    private static Set<String> sessions = new HashSet<>();

    public void online(String username) {
        assert !sessions.contains(username);
        sessions.add(username);
        assert sessions.contains(username);
    }

    public void offline(String username){
        assert sessions.contains(username);
        sessions.remove(username);
        assert !sessions.contains(username);
    }

    public static boolean isSessionExists(String username) {
        return sessions.contains(username);
    }
}
