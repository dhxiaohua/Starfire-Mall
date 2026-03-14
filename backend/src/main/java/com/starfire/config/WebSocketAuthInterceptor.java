package com.starfire.config;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

@Component
public class WebSocketAuthInterceptor implements HandshakeInterceptor {
    
    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, 
                                    WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        // 从URL参数获取用户名
        String query = request.getURI().getQuery();
        if (query != null && query.contains("username=")) {
            String username = query.substring(query.indexOf("username=") + 9);
            if (username.contains("&")) {
                username = username.substring(0, username.indexOf("&"));
            }
            attributes.put("username", username);
            return true;
        }
        // 允许匿名连接
        return true;
    }
    
    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, 
                                WebSocketHandler wsHandler, Exception exception) {
    }
}
