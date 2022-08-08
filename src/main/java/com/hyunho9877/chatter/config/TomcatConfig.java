package com.hyunho9877.chatter.config;

import org.apache.catalina.Context;
import org.apache.tomcat.util.http.Rfc6265CookieProcessor;
import org.apache.tomcat.util.http.SameSiteCookies;
import org.springframework.boot.web.embedded.tomcat.TomcatContextCustomizer;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TomcatConfig implements TomcatContextCustomizer {
    @Override
    public void customize(Context context) {
        Rfc6265CookieProcessor cookieProcessor = new Rfc6265CookieProcessor();
        cookieProcessor.setSameSiteCookies(SameSiteCookies.LAX.getValue());
        context.setCookieProcessor(cookieProcessor);
    }
}
