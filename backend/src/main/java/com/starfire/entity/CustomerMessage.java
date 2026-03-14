package com.starfire.entity;

import com.baomidou.mybatisplus.annotation.*;
import java.time.LocalDateTime;

@TableName("customer_messages")
public class CustomerMessage {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String username;
    
    private String content;
    
    private Boolean isFromAdmin;
    
    private Boolean isRead;
    
    private String avatar;  // 发送者头像
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    // Getters and Setters
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
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public Boolean getIsFromAdmin() {
        return isFromAdmin;
    }
    
    public void setIsFromAdmin(Boolean isFromAdmin) {
        this.isFromAdmin = isFromAdmin;
    }
    
    public Boolean getIsRead() {
        return isRead;
    }
    
    public void setIsRead(Boolean isRead) {
        this.isRead = isRead;
    }
    
    public String getAvatar() {
        return avatar;
    }
    
    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
    
    public LocalDateTime getCreateTime() {
        return createTime;
    }
    
    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }
}