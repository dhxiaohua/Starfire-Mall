package com.starfire.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.starfire.dto.AIChatMessageDTO;
import com.starfire.entity.AIChatMessage;
import com.starfire.mapper.AIChatMessageMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * AI 聊天服务
 */
@Service
public class AIChatService {

    private static final Logger logger = LoggerFactory.getLogger(AIChatService.class);

    @Autowired
    private AIChatMessageMapper aiChatMessageMapper;
    
    /**
     * 保存聊天消息
     */
    public void saveMessage(String username, String sessionId, String message, boolean isUser) {
        AIChatMessage chatMessage = new AIChatMessage();
        chatMessage.setUsername(username);
        chatMessage.setSessionId(sessionId);
        chatMessage.setMessage(message);
        chatMessage.setIsUser(isUser);
        chatMessage.setCreatedAt(LocalDateTime.now());
        
        aiChatMessageMapper.insert(chatMessage);
        logger.info("保存AI聊天消息: username={}, sessionId={}, isUser={}", username, sessionId, isUser);
    }
    
    /**
     * 获取用户的所有聊天记录
     */
    public List<AIChatMessageDTO> getUserMessages(String username) {
        QueryWrapper<AIChatMessage> wrapper = new QueryWrapper<>();
        wrapper.eq("username", username)
               .orderByAsc("created_at");
        
        List<AIChatMessage> messages = aiChatMessageMapper.selectList(wrapper);
        return messages.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * 获取指定会话的聊天记录
     */
    public List<AIChatMessageDTO> getSessionMessages(String sessionId) {
        QueryWrapper<AIChatMessage> wrapper = new QueryWrapper<>();
        wrapper.eq("session_id", sessionId)
               .orderByAsc("created_at");
        
        List<AIChatMessage> messages = aiChatMessageMapper.selectList(wrapper);
        return messages.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * 获取所有活跃的AI会话列表（去重）
     */
    public List<Map<String, Object>> getAllActiveSessions() {
        // 查询所有有聊天记录的会话ID和用户名
        QueryWrapper<AIChatMessage> wrapper = new QueryWrapper<>();
        wrapper.select("DISTINCT session_id, username, MAX(created_at) as last_message_time")
               .groupBy("session_id, username")
               .orderByDesc("last_message_time");

        // 获取会话信息
        List<Map<String, Object>> result = aiChatMessageMapper.selectMaps(wrapper);

        // 为每个会话添加消息数量和最后一条消息
        for (Map<String, Object> session : result) {
            String sessionId = (String) session.get("session_id");

            // 获取该会话的消息数量
            QueryWrapper<AIChatMessage> countWrapper = new QueryWrapper<>();
            countWrapper.eq("session_id", sessionId);
            Integer messageCount = Math.toIntExact(aiChatMessageMapper.selectCount(countWrapper));
            session.put("message_count", messageCount);

            // 获取最后一条消息内容
            QueryWrapper<AIChatMessage> lastMsgWrapper = new QueryWrapper<>();
            lastMsgWrapper.eq("session_id", sessionId)
                        .orderByDesc("created_at")
                        .last("LIMIT 1");
            AIChatMessage lastMessage = aiChatMessageMapper.selectOne(lastMsgWrapper);
            if (lastMessage != null) {
                session.put("last_message", lastMessage.getMessage());
            }
        }

        return result;
    }
    
    /**
     * 删除用户的所有聊天记录
     */
    public boolean deleteUserMessages(String username) {
        QueryWrapper<AIChatMessage> wrapper = new QueryWrapper<>();
        wrapper.eq("username", username);
        
        int deleted = aiChatMessageMapper.delete(wrapper);
        logger.info("删除用户聊天记录: username={}, count={}", username, deleted);
        return deleted > 0;
    }
    
    /**
     * 转换为 DTO
     */
    private AIChatMessageDTO convertToDTO(AIChatMessage entity) {
        AIChatMessageDTO dto = new AIChatMessageDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }
}