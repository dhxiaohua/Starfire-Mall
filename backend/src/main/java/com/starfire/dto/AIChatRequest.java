package com.starfire.dto;

/**
 * AI 聊天请求 DTO
 */
public class AIChatRequest {
    /**
     * 用户消息
     */
    private String message;

    /**
     * 会话 ID（用于保持上下文）
     */
    private String sessionId;

    /**
     * 用户名
     */
    private String username;

    // Getter 和 Setter 方法
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}