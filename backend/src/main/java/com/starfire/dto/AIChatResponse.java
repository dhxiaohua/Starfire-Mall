package com.starfire.dto;

/**
 * AI 聊天响应 DTO
 */
public class AIChatResponse {
    /**
     * AI 回复内容
     */
    private String reply;

    /**
     * 会话 ID
     */
    private String sessionId;

    /**
     * 是否成功
     */
    private boolean success;

    /**
     * 错误信息（如果失败）
     */
    private String error;

    // Getter 和 Setter 方法
    public String getReply() {
        return reply;
    }

    public void setReply(String reply) {
        this.reply = reply;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}