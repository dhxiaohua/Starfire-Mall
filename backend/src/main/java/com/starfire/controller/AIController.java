package com.starfire.controller;

import com.starfire.dto.AIChatRequest;
import com.starfire.dto.AIChatResponse;
import com.starfire.dto.ApiResponse;
import com.starfire.service.AIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * AI 客服控制器
 */
@RestController
@RequestMapping("/api/ai")
public class AIController {

    @Autowired
    private AIService aiService;

    /**
     * AI 聊天接口
     *
     * @param request 聊天请求
     * @return AI 回复
     */
    @PostMapping("/chat")
    public ApiResponse<AIChatResponse> chat(@RequestBody AIChatRequest request) {
        try {
            AIChatResponse response = aiService.chat(request);
            return ApiResponse.success(response);
        } catch (Exception e) {
            return ApiResponse.error("AI 聊天失败: " + e.getMessage());
        }
    }

    /**
     * 测试 AI 服务连接
     *
     * @return 连接状态
     */
    @GetMapping("/test")
    public ApiResponse<Boolean> testConnection() {
        try {
            boolean connected = aiService.testConnection();
            return ApiResponse.success(connected);
        } catch (Exception e) {
            return ApiResponse.error("测试连接失败: " + e.getMessage());
        }
    }

    /**
     * 清除会话上下文
     *
     * @param username 用户名
     * @return 新的会话 ID
     */
    @PostMapping("/clear-session")
    public ApiResponse<String> clearSession(@RequestBody Map<String, String> request) {
        try {
            String username = request.get("username");
            if (username == null || username.isEmpty()) {
                return ApiResponse.error("用户名不能为空");
            }
            String newSessionId = aiService.clearSession(username);
            return ApiResponse.success(newSessionId);
        } catch (Exception e) {
            return ApiResponse.error("清除会话失败: " + e.getMessage());
        }
    }
}