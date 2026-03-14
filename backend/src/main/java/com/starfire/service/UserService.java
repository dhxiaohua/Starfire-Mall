package com.starfire.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.starfire.dto.UserVO;
import com.starfire.entity.User;
import com.starfire.mapper.UserMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UserService {
    
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    
    public UserService(UserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }
    
    public User findByUsername(String username) {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("username", username);
        return userMapper.selectOne(wrapper);
    }
    
    public User findByRole(String role) {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("role", role);
        return userMapper.selectOne(wrapper);
    }
    
    public User findById(Long id) {
        return userMapper.selectById(id);
    }
    
    public boolean register(String username, String password) {
        // 检查用户名是否已存在
        if (findByUsername(username) != null) {
            return false;
        }
        
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setStatus(1);
        user.setRole("user");
        user.setAdminStatus("none");
        
        return userMapper.insert(user) > 0;
    }
    
    public User login(String username, String password) {
        User user = findByUsername(username);
        if (user != null && passwordEncoder.matches(password, user.getPassword())) {
            return user;
        }
        return null;
    }
    
    public boolean changePassword(String username, String newPassword) {
        User user = findByUsername(username);
        if (user == null) {
            return false;
        }
        
        user.setPassword(passwordEncoder.encode(newPassword));
        return userMapper.updateById(user) > 0;
    }
    
    public boolean updateUserInfo(User user) {
        return userMapper.updateById(user) > 0;
    }
    
    public Page<User> getUserList(int page, int size) {
        Page<User> pageParam = new Page<>(page, size);
        return userMapper.selectPage(pageParam, new QueryWrapper<User>().orderByDesc("create_time"));
    }
    
    public Page<User> getPendingRequests(int page, int size) {
        Page<User> pageParam = new Page<>(page, size);
        return userMapper.selectPage(pageParam, 
            new QueryWrapper<User>().eq("admin_status", "pending").orderByDesc("admin_request_time"));
    }
    
    public boolean approveRequest(Long userId, boolean approved) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            return false;
        }
        
        if (approved) {
            user.setRole("admin");
            user.setAdminStatus("approved");
        } else {
            user.setAdminStatus("rejected");
        }
        
        return userMapper.updateById(user) > 0;
    }
    
    public boolean applyAdmin(String username) {
        User user = findByUsername(username);
        if (user == null || "admin".equals(user.getRole())) {
            return false;
        }
        
        user.setAdminStatus("pending");
        user.setAdminRequestTime(LocalDateTime.now());
        return userMapper.updateById(user) > 0;
    }
    
    public boolean cancelApplication(String username) {
        User user = findByUsername(username);
        if (user == null) {
            return false;
        }
        
        // 只有待审批状态可以撤销
        if (!"pending".equals(user.getAdminStatus())) {
            return false;
        }
        
        user.setAdminStatus("none");
        user.setAdminRequestTime(null);
        return userMapper.updateById(user) > 0;
    }
    
    public boolean revokeAdmin(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            return false;
        }
        
        user.setRole("user");
        user.setAdminStatus("none");
        return userMapper.updateById(user) > 0;
    }
    
    public boolean updateUserStatus(Long userId, Integer status) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            return false;
        }
        
        user.setStatus(status);
        return userMapper.updateById(user) > 0;
    }
    
    public boolean deleteUser(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null || "admin".equals(user.getRole())) {
            return false;
        }
        
        return userMapper.deleteById(userId) > 0;
    }
    
    public long getTotalUsers() {
        return userMapper.selectCount(new QueryWrapper<>());
    }
    
    public long getAdminUsers() {
        return userMapper.selectCount(new QueryWrapper<User>().eq("role", "admin"));
    }
    
    public long getPendingRequestsCount() {
        return userMapper.selectCount(new QueryWrapper<User>().eq("admin_status", "pending"));
    }
    
    public long getActiveUsers() {
        return userMapper.selectCount(new QueryWrapper<User>().eq("status", 1));
    }
    
    public UserVO toVO(User user) {
        UserVO vo = new UserVO();
        BeanUtils.copyProperties(user, vo);
        return vo;
    }
}
