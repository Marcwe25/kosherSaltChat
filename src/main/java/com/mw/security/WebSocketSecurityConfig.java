package com.mw.security;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.messaging.MessageSecurityMetadataSourceRegistry;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
//@ComponentScan("com.alibou.security")
public class WebSocketSecurityConfig implements WebMvcConfigurer {


    protected boolean sameOriginDisabled() {
        return true;
    }


    protected void configureInbound(MessageSecurityMetadataSourceRegistry messages) {
        messages
                .simpDestMatchers("/chat-room-websocket")
                .authenticated()
                .anyMessage().authenticated();
    }


}
