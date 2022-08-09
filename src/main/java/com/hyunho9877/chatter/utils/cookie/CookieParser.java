package com.hyunho9877.chatter.utils.cookie;

import javax.servlet.http.Cookie;

public interface CookieParser {
    String parseAccessCookie(Cookie[] cookies);
    String parseRefreshCookie(Cookie[] cookies);
}
