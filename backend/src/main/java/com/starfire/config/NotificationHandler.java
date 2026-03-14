package com.starfire.config;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class NotificationHandler extends TextWebSocketHandler {
    
    // 存储所有连接的会话 - 用户名 -> WebSocketSession
    private static final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();
    
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        // 从session属性获取用户名
        String username = (String) session.getAttributes().get("username");
        if (username != null) {
            sessions.put(username, session);
            System.out.println("WebSocket连接建立: " + username);
        }
    }
    
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String username = (String) session.getAttributes().get("username");
        if (username != null) {
            sessions.remove(username);
            System.out.println("WebSocket连接关闭: " + username);
        }
    }
    
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        // 处理客户端发送的消息
        String payload = message.getPayload();
        System.out.println("收到WebSocket消息: " + payload);
    }
    
    // 广播消息给所有管理员
    public static void broadcastToAdmins(String message) {
        sessions.forEach((username, session) -> {
            if (username.startsWith("admin_") || "admin".equals(username)) {
                sendMessage(username, message);
            }
        });
    }
    
    // 发送消息给指定用户
    public static void sendMessage(String username, String message) {
        WebSocketSession session = sessions.get(username);
        if (session != null && session.isOpen()) {
            try {
                session.sendMessage(new TextMessage(message));
            } catch (IOException e) {
                System.err.println("发送WebSocket消息失败: " + e.getMessage());
            }
        }
    }
    
    // 获取在线用户数
    public static int getOnlineCount() {
        return sessions.size();
    }
}
