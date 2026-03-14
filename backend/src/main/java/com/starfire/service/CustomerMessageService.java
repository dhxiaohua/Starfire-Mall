package com.starfire.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.starfire.entity.CustomerMessage;
import com.starfire.entity.User;
import com.starfire.mapper.CustomerMessageMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerMessageService {
    
    private final CustomerMessageMapper messageMapper;
    private final UserService userService;
    private final JdbcTemplate jdbcTemplate;
    
    @Autowired
    public CustomerMessageService(CustomerMessageMapper messageMapper, UserService userService, JdbcTemplate jdbcTemplate) {
        this.messageMapper = messageMapper;
        this.userService = userService;
        this.jdbcTemplate = jdbcTemplate;
        // 确保avatar列存在
        ensureAvatarColumn();
    }
    
    private void ensureAvatarColumn() {
        try {
            jdbcTemplate.execute("SELECT avatar FROM customer_messages LIMIT 1");
        } catch (Exception e) {
            jdbcTemplate.execute("ALTER TABLE customer_messages ADD COLUMN avatar VARCHAR(500) DEFAULT NULL");
        }
    }
    
    public Page<CustomerMessage> getMessagesByUsername(String username, int page, int size) {
        Page<CustomerMessage> pageParam = new Page<>(page, size);
        QueryWrapper<CustomerMessage> wrapper = new QueryWrapper<>();
        wrapper.eq("username", username);
        wrapper.orderByAsc("create_time");
        
        return messageMapper.selectPage(pageParam, wrapper);
    }
    
    // 获取所有消息（用于管理员查看）
    public Page<CustomerMessage> getAllMessages(int page, int size) {
        Page<CustomerMessage> pageParam = new Page<>(page, size);
        QueryWrapper<CustomerMessage> wrapper = new QueryWrapper<>();
        wrapper.orderByDesc("create_time");
        
        return messageMapper.selectPage(pageParam, wrapper);
    }
    
    public List<CustomerMessage> getAllConversations() {
        QueryWrapper<CustomerMessage> wrapper = new QueryWrapper<>();
        wrapper.orderByDesc("create_time");
        
        List<CustomerMessage> messages = messageMapper.selectList(wrapper);
        
        // 按用户名分组，返回每个用户的最后一条消息
        return messages;
    }
    
    public boolean sendMessage(String username, String content, boolean isFromAdmin) {
        CustomerMessage message = new CustomerMessage();
        message.setUsername(username);
        message.setContent(content);
        message.setIsFromAdmin(isFromAdmin);
        message.setIsRead(false);
        
        // TODO: 暂时注释掉头像逻辑以排查500错误
        // 获取发送者头像
        // if (isFromAdmin) {
        //     // 管理员头像：获取第一个管理员的头像
        //     User admin = userService.findByRole("admin");
        //     if (admin != null) {
        //         message.setAvatar(admin.getAvatar());
        //     }
        // } else {
        //     // 用户头像
        //     User user = userService.findByUsername(username);
        //     if (user != null) {
        //         message.setAvatar(user.getAvatar());
        //     }
        // }
        
        return messageMapper.insert(message) > 0;
    }
    
    public boolean markAsRead(String username) {
        List<CustomerMessage> messages = messageMapper.selectList(
            new QueryWrapper<CustomerMessage>().eq("username", username)
        );
        
        for (CustomerMessage message : messages) {
            message.setIsRead(true);
            messageMapper.updateById(message);
        }
        
        return true;
    }
    
    public long getUnreadCount(String username) {
        QueryWrapper<CustomerMessage> wrapper = new QueryWrapper<>();
        wrapper.eq("username", username);
        wrapper.eq("is_read", false);
        
        return messageMapper.selectCount(wrapper);
    }
}
