package com.starfire.service;

import com.starfire.dto.AIChatRequest;
import com.starfire.dto.AIChatResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * AI 客服服务类
 * 用于集成 AnythingLLM API
 */
@Service
public class AIService {

    private static final Logger logger = LoggerFactory.getLogger(AIService.class);

    @Value("${anythingllm.api-url}")
    private String apiUrl;

    @Value("${anythingllm.api-key}")
    private String apiKey;

    @Value("${anythingllm.workspace}")
    private String workspace;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @Autowired
    private AIChatService aiChatService;

    /**
     * 发送消息给 AI 客服
     *
     * @param request 聊天请求
     * @return AI 回复
     */
    public AIChatResponse chat(AIChatRequest request) {
        AIChatResponse response = new AIChatResponse();

        try {
            // 生成或使用现有的会话 ID
            String sessionId = request.getSessionId();
            if (sessionId == null || sessionId.isEmpty()) {
                sessionId = generateSessionId(request.getUsername());
            }
            response.setSessionId(sessionId);

            // 保存用户消息到数据库
            aiChatService.saveMessage(request.getUsername(), sessionId, request.getMessage(), true);

            // 构建 AnythingLLM API 请求体（根据图片中的格式）
            String mode = "query";  // query 模式：查询或知识库；chat 模式：聊天（支持上下文）
            Map<String, Object> body = new HashMap<>();
            body.put("message", request.getMessage());
            body.put("mode", mode);
            body.put("max_token", 512);  // 限制返回的 token 数量

            // 构建请求头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + apiKey);
            headers.set("accept", "application/json");

            HttpEntity<Map<String, Object>> httpEntity = new HttpEntity<>(body, headers);

            // 调用 AnythingLLM API（使用正确的端点格式）
            String fullUrl = apiUrl + "/api/v1/workspace/" + workspace + "/chat";
            logger.info("调用 AnythingLLM API: {}", fullUrl);
            logger.info("请求参数: message={}, mode={}, max_token={}", request.getMessage(), mode, 512);

            // 直接使用 String URL，避免 RestTemplate 解析路径变量
            java.net.URI uri = java.net.URI.create(fullUrl);
            ResponseEntity<String> apiResponse = restTemplate.exchange(
                    uri,
                    HttpMethod.POST,
                    httpEntity,
                    String.class
            );

            if (apiResponse.getStatusCode() == HttpStatus.OK) {
                String responseBody = apiResponse.getBody();
                logger.info("AnythingLLM 响应: {}", responseBody);

                JsonNode jsonNode = objectMapper.readTree(responseBody);

                // 解析响应
                if (jsonNode.has("textResponse")) {
                    String reply = jsonNode.get("textResponse").asText();
                    response.setReply(reply);
                    response.setSuccess(true);
                    logger.info("AI 回复成功");
                    
                    // 保存 AI 回复到数据库
                    aiChatService.saveMessage(request.getUsername(), sessionId, reply, false);
                } else {
                    response.setSuccess(false);
                    response.setError("无法解析 AI 响应，缺少 textResponse 字段");
                    logger.error("AI 响应格式错误: {}", responseBody);
                }
            } else {
                response.setSuccess(false);
                response.setError("API 调用失败: " + apiResponse.getStatusCode());
                logger.error("API 调用失败: {}", apiResponse.getStatusCode());
            }

        } catch (Exception e) {
            response.setSuccess(false);
            response.setError("系统错误: " + e.getMessage());
            logger.error("AI 聊天异常", e);
        }

        return response;
    }

    /**
     * 生成会话 ID
     */
    private String generateSessionId(String username) {
        if (username != null && !username.isEmpty()) {
            return username + "-" + System.currentTimeMillis();
        }
        return UUID.randomUUID().toString();
    }

    /**
     * 测试 AI 服务连接
     */
    public boolean testConnection() {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + apiKey);
            headers.set("accept", "application/json");

            HttpEntity<Void> httpEntity = new HttpEntity<>(headers);

            // 测试获取工作区列表来验证连接
            String fullUrl = apiUrl + "/api/v1/workspaces";
            ResponseEntity<String> response = restTemplate.exchange(
                    fullUrl,
                    HttpMethod.GET,
                    httpEntity,
                    String.class
            );

            boolean connected = response.getStatusCode() == HttpStatus.OK;
            logger.info("AI 服务连接测试: {}", connected ? "成功" : "失败");
            return connected;
        } catch (Exception e) {
            logger.error("测试 AI 服务连接失败", e);
            return false;
        }
    }

    /**
     * 清除会话上下文
     * 通过生成新的 sessionId 来重置对话
     *
     * @param username 用户名
     * @return 新的会话 ID
     */
    public String clearSession(String username) {
        String newSessionId = generateSessionId(username);
        logger.info("清除会话上下文，生成新会话 ID: {}", newSessionId);
        return newSessionId;
    }
}