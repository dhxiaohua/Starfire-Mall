package com.starfire.controller;

import com.starfire.dto.ApiResponse;
import com.starfire.dto.AIChatMessageDTO;
import com.starfire.service.AIChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * AI 聊天控制器
 * 用于管理员查看AI客服聊天记录
 */
@RestController
@RequestMapping("/api/ai/chat")
public class AIChatController {

    @Autowired
    private AIChatService aiChatService;

    /**
     * 获取用户的所有聊天记录
     */
    @GetMapping("/messages/{username}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<List<AIChatMessageDTO>> getUserMessages(@PathVariable String username) {
        try {
            List<AIChatMessageDTO> messages = aiChatService.getUserMessages(username);
            return ApiResponse.success(messages);
        } catch (Exception e) {
            return ApiResponse.error("获取聊天记录失败: " + e.getMessage());
        }
    }

    /**
     * 获取指定会话的聊天记录
     */
    @GetMapping("/session/{sessionId}/messages")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<List<AIChatMessageDTO>> getSessionMessages(@PathVariable String sessionId) {
        try {
            List<AIChatMessageDTO> messages = aiChatService.getSessionMessages(sessionId);
            return ApiResponse.success(messages);
        } catch (Exception e) {
            return ApiResponse.error("获取会话记录失败: " + e.getMessage());
        }
    }

    /**
     * 获取所有活跃的AI会话列表
     */
    @GetMapping("/sessions")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<List<Map<String, Object>>> getAllSessions() {
        try {
            List<Map<String, Object>> sessions = aiChatService.getAllActiveSessions();
            return ApiResponse.success(sessions);
        } catch (Exception e) {
            return ApiResponse.error("获取会话列表失败: " + e.getMessage());
        }
    }
}