package com.starfire.entity;

import com.baomidou.mybatisplus.annotation.*;
import java.time.LocalDateTime;

/**
 * AI 聊天消息实体
 */
@TableName("ai_chat_messages")
public class AIChatMessage {

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 会话ID
     */
    private String sessionId;

    /**
     * 消息内容
     */
    private String message;

    /**
     * 是否为用户消息（1-用户，0-AI）
     */
    private Boolean isUser;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    @TableLogic
    private Integer deleted;

    // Getter 和 Setter 方法
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Boolean getIsUser() {
        return isUser;
    }

    public void setIsUser(Boolean isUser) {
        this.isUser = isUser;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Integer getDeleted() {
        return deleted;
    }

    public void setDeleted(Integer deleted) {
        this.deleted = deleted;
    }
}