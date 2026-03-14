package com.starfire.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.starfire.dto.ApiResponse;
import com.starfire.entity.CustomerMessage;
import com.starfire.service.CustomerMessageService;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/customer")
public class CustomerController {
    
    private final CustomerMessageService messageService;
    
    public CustomerController(CustomerMessageService messageService) {
        this.messageService = messageService;
    }
    
    // 获取用户客服消息
    @GetMapping("/messages")
    public ApiResponse<Map<String, Object>> getMessages(
            @RequestParam String username,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Page<CustomerMessage> pageResult = messageService.getMessagesByUsername(username, page, size);
        
        Map<String, Object> data = new HashMap<>();
        data.put("messages", pageResult.getRecords());
        data.put("total", pageResult.getTotal());
        
        return ApiResponse.success(data);
    }
    
    // 发送客服消息
    @PostMapping("/send")
    public ApiResponse<String> sendMessage(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        String content = request.get("content");
        
        if (username == null || content == null || content.isEmpty()) {
            return ApiResponse.error("请填写消息内容");
        }
        
        boolean success = messageService.sendMessage(username, content, false);
        
        if (success) {
            return ApiResponse.success("消息发送成功", null);
        } else {
            return ApiResponse.error("消息发送失败");
        }
    }
    
    // 获取所有客服消息（管理员用）- 返回会话列表格式
    @GetMapping("/all-messages")
    public ApiResponse<Map<String, Object>> getAllMessages(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "100") int size) {
        
        Page<CustomerMessage> pageResult = messageService.getAllMessages(page, size);
        List<CustomerMessage> allMessages = pageResult.getRecords();
        
        // 按用户名分组，构建会话列表
        Map<String, List<CustomerMessage>> grouped = allMessages.stream()
            .collect(Collectors.groupingBy(CustomerMessage::getUsername));
        
        List<Map<String, Object>> conversations = new ArrayList<>();
        for (Map.Entry<String, List<CustomerMessage>> entry : grouped.entrySet()) {
            List<CustomerMessage> msgs = entry.getValue();
            // 按时间排序
            msgs.sort(Comparator.comparing(CustomerMessage::getCreateTime));
            
            CustomerMessage lastMsg = msgs.get(msgs.size() - 1);
            long unreadCount = msgs.stream()
                .filter(m -> !Boolean.TRUE.equals(m.getIsRead()) && !Boolean.TRUE.equals(m.getIsFromAdmin()))
                .count();
            
            Map<String, Object> conv = new HashMap<>();
            conv.put("username", entry.getKey());
            conv.put("lastMessage", lastMsg.getContent());
            conv.put("lastTime", lastMsg.getCreateTime());
            conv.put("unreadCount", unreadCount);
            conv.put("messages", msgs);
            conversations.add(conv);
        }
        
        // 按最后消息时间排序
        conversations.sort((a, b) -> {
            LocalDateTime timeA = (LocalDateTime) a.get("lastTime");
            LocalDateTime timeB = (LocalDateTime) b.get("lastTime");
            return timeB.compareTo(timeA);
        });
        
        Map<String, Object> data = new HashMap<>();
        data.put("conversations", conversations);
        data.put("total", conversations.size());
        
        return ApiResponse.success(data);
    }
    
    // 获取指定用户的对话
    @GetMapping("/conversation/{username}")
    public ApiResponse<Map<String, Object>> getConversation(@PathVariable String username) {
        Page<CustomerMessage> pageResult = messageService.getMessagesByUsername(username, 1, 100);
        List<CustomerMessage> messages = pageResult.getRecords();
        
        // 标记为已读
        messageService.markAsRead(username);
        
        Map<String, Object> data = new HashMap<>();
        data.put("messages", messages);
        data.put("total", messages.size());
        
        return ApiResponse.success(data);
    }
    
    // 标记消息为已读
    @PostMapping("/mark-read")
    public ApiResponse<String> markAsRead(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        
        boolean success = messageService.markAsRead(username);
        
        if (success) {
            return ApiResponse.success("消息已标记为已读", null);
        } else {
            return ApiResponse.error("操作失败");
        }
    }
    
    // 发送客服回复（管理员用）
    @PostMapping("/reply")
    public ApiResponse<String> sendReply(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        String content = request.get("content");
        
        if (username == null || content == null || content.isEmpty()) {
            return ApiResponse.error("请填写回复内容");
        }
        
        boolean success = messageService.sendMessage(username, content, true);
        
        if (success) {
            return ApiResponse.success("回复发送成功", null);
        } else {
            return ApiResponse.error("回复发送失败");
        }
    }
}
