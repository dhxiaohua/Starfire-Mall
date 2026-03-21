package com.starfire.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {
    
    private final NotificationHandler notificationHandler;
    private final WebSocketAuthInterceptor webSocketAuthInterceptor;
    
    public WebSocketConfig(NotificationHandler notificationHandler, 
                           WebSocketAuthInterceptor webSocketAuthInterceptor) {
        this.notificationHandler = notificationHandler;
        this.webSocketAuthInterceptor = webSocketAuthInterceptor;
    }
    
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(notificationHandler, "/ws/notifications")
                .addInterceptors(webSocketAuthInterceptor)
                .setAllowedOrigins("*");
    }
}