package com.starfire.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.starfire.dto.UserVO;
import com.starfire.entity.User;
import com.starfire.mapper.UserMapper;
import com.starfire.service.RedisCacheService;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UserService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final RedisCacheService redisCacheService;
    private final FileUploadService fileUploadService;

    public UserService(UserMapper userMapper, PasswordEncoder passwordEncoder, RedisCacheService redisCacheService, FileUploadService fileUploadService) {
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.redisCacheService = redisCacheService;
        this.fileUploadService = fileUploadService;
    }
    
    public User findByUsername(String username) {
        // 尝试从缓存获取
        String key = "user:username:" + username;
        User user = redisCacheService.get(key);
        if (user != null) {
            return user;
        }
        
        // 缓存未命中，从数据库查询
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("username", username);
        user = userMapper.selectOne(wrapper);
        
        // 存入缓存
        if (user != null) {
            redisCacheService.set(key, user, 3600);
        }
        
        return user;
    }
    
    public User findByRole(String role) {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("role", role);
        return userMapper.selectOne(wrapper);
    }
    
    public User findById(Long id) {
        // 尝试从缓存获取
        String key = "user:id:" + id;
        User user = redisCacheService.get(key);
        if (user != null) {
            return user;
        }
        
        // 缓存未命中，从数据库查询
        user = userMapper.selectById(id);
        
        // 存入缓存
        if (user != null) {
            redisCacheService.set(key, user, 3600);
        }
        
        return user;
    }
    
    public boolean register(String username, String nickname, String password) {
        if (username == null || username.trim().isEmpty()) {
            System.out.println("注册失败：用户名为空");
            return false;
        }
        
        if (nickname == null || nickname.trim().isEmpty()) {
            System.out.println("注册失败：昵称为空");
            return false;
        }
        
        if (password == null || password.trim().isEmpty()) {
            System.out.println("注册失败：密码为空");
            return false;
        }
        
        try {
            // 检查用户名是否已存在
            if (findByUsername(username) != null) {
                System.out.println("注册失败：用户名已存在 - " + username);
                return false;
            }
            
            User user = new User();
            user.setUsername(username);
            user.setNickname(nickname);
            user.setPassword(passwordEncoder.encode(password));
            user.setStatus(1);
            user.setRole("user");
            user.setAdminStatus("none");
            
            boolean success = userMapper.insert(user) > 0;
            if (success) {
                System.out.println("注册成功 - " + username);
                // 清除用户统计缓存
                redisCacheService.delete("user:count:total");
                redisCacheService.delete("user:count:active");
            } else {
                System.out.println("注册失败：数据库插入失败 - " + username);
            }
            return success;
        } catch (Exception e) {
            System.out.println("注册异常 - " + username + ": " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    public User login(String username, String password) {
        if (username == null || username.trim().isEmpty()) {
            System.out.println("登录失败：用户名为空");
            return null;
        }
        
        if (password == null || password.trim().isEmpty()) {
            System.out.println("登录失败：密码为空");
            return null;
        }
        
        try {
            User user = findByUsername(username);
            if (user == null) {
                System.out.println("登录失败：用户不存在 - " + username);
                return null;
            }
            
            // 检查用户状态
            if (user.getStatus() == null || user.getStatus() != 1) {
                System.out.println("登录失败：用户已被禁用 - " + username);
                return null;
            }
            
            // 验证密码
            if (!passwordEncoder.matches(password, user.getPassword())) {
                System.out.println("登录失败：密码错误 - " + username);
                return null;
            }
            
            System.out.println("登录成功 - " + username);
            return user;
        } catch (Exception e) {
            System.out.println("登录异常 - " + username + ": " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    public boolean changePassword(String username, String newPassword) {
        if (username == null || username.trim().isEmpty()) {
            System.out.println("修改密码失败：用户名为空");
            return false;
        }
        
        if (newPassword == null || newPassword.trim().isEmpty()) {
            System.out.println("修改密码失败：新密码为空");
            return false;
        }
        
        try {
            User user = findByUsername(username);
            if (user == null) {
                System.out.println("修改密码失败：用户不存在 - " + username);
                return false;
            }
            
            user.setPassword(passwordEncoder.encode(newPassword));
            boolean success = userMapper.updateById(user) > 0;
            if (success) {
                System.out.println("修改密码成功 - " + username);
                // 清除相关缓存
                String idKey = "user:id:" + user.getId();
                String usernameKey = "user:username:" + user.getUsername();
                redisCacheService.delete(idKey);
                redisCacheService.delete(usernameKey);
            } else {
                System.out.println("修改密码失败：数据库更新失败 - " + username);
            }
            return success;
        } catch (Exception e) {
            System.out.println("修改密码异常 - " + username + ": " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean updateUserInfo(User user) {
        // 如果 avatar 是 base64 数据，则上传到文件服务器
        if (user.getAvatar() != null && user.getAvatar().startsWith("data:image/")) {
            try {
                // 获取旧头像路径用于删除
                User oldUser = userMapper.selectById(user.getId());
                String oldAvatar = oldUser != null ? oldUser.getAvatar() : null;

                // 上传新头像（按用户ID分目录）
                String avatarPath = fileUploadService.uploadBase64Image(user.getAvatar(), user.getId());
                user.setAvatar(avatarPath);

                // 删除旧头像文件
                if (oldAvatar != null && oldAvatar.startsWith("/images/avatars/")) {
                    fileUploadService.deleteFile(oldAvatar);
                }
            } catch (Exception e) {
                System.err.println("头像上传失败: " + e.getMessage());
                e.printStackTrace();
                return false;
            }
        }

        boolean success = userMapper.updateById(user) > 0;
        if (success) {
            // 清除相关缓存
            String idKey = "user:id:" + user.getId();
            String usernameKey = "user:username:" + user.getUsername();
            redisCacheService.delete(idKey);
            redisCacheService.delete(usernameKey);
            // 清除用户统计缓存
            redisCacheService.delete("user:count:total");
            redisCacheService.delete("user:count:active");
            redisCacheService.delete("user:count:admin");
        }
        return success;
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
        
        boolean success = userMapper.updateById(user) > 0;
        if (success) {
            // 清除相关缓存
            String idKey = "user:id:" + user.getId();
            String usernameKey = "user:username:" + user.getUsername();
            redisCacheService.delete(idKey);
            redisCacheService.delete(usernameKey);
            // 清除用户统计缓存
            redisCacheService.delete("user:count:admin");
            redisCacheService.delete("user:count:pending");
        }
        return success;
    }
    
    public boolean applyAdmin(String username) {
        User user = findByUsername(username);
        if (user == null || "admin".equals(user.getRole())) {
            return false;
        }
        
        user.setAdminStatus("pending");
        user.setAdminRequestTime(LocalDateTime.now());
        boolean success = userMapper.updateById(user) > 0;
        if (success) {
            // 清除相关缓存
            String idKey = "user:id:" + user.getId();
            String usernameKey = "user:username:" + user.getUsername();
            redisCacheService.delete(idKey);
            redisCacheService.delete(usernameKey);
            // 清除用户统计缓存
            redisCacheService.delete("user:count:pending");
        }
        return success;
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
        boolean success = userMapper.updateById(user) > 0;
        if (success) {
            // 清除相关缓存
            String idKey = "user:id:" + user.getId();
            String usernameKey = "user:username:" + user.getUsername();
            redisCacheService.delete(idKey);
            redisCacheService.delete(usernameKey);
            // 清除用户统计缓存
            redisCacheService.delete("user:count:pending");
        }
        return success;
    }
    
    public boolean revokeAdmin(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            return false;
        }
        
        user.setRole("user");
        user.setAdminStatus("none");
        boolean success = userMapper.updateById(user) > 0;
        if (success) {
            // 清除相关缓存
            String idKey = "user:id:" + user.getId();
            String usernameKey = "user:username:" + user.getUsername();
            redisCacheService.delete(idKey);
            redisCacheService.delete(usernameKey);
            // 清除用户统计缓存
            redisCacheService.delete("user:count:admin");
        }
        return success;
    }
    
    public boolean updateUserStatus(Long userId, Integer status) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            return false;
        }
        
        user.setStatus(status);
        boolean success = userMapper.updateById(user) > 0;
        if (success) {
            // 清除相关缓存
            String idKey = "user:id:" + user.getId();
            String usernameKey = "user:username:" + user.getUsername();
            redisCacheService.delete(idKey);
            redisCacheService.delete(usernameKey);
            // 清除用户统计缓存
            redisCacheService.delete("user:count:active");
        }
        return success;
    }
    
    public boolean deleteUser(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null || "admin".equals(user.getRole())) {
            return false;
        }

        boolean success = userMapper.deleteById(userId) > 0;
        if (success) {
            // 删除用户的所有上传文件
            fileUploadService.deleteUserFiles(userId);

            // 清除相关缓存
            String idKey = "user:id:" + user.getId();
            String usernameKey = "user:username:" + user.getUsername();
            redisCacheService.delete(idKey);
            redisCacheService.delete(usernameKey);
            // 清除用户统计缓存
            redisCacheService.delete("user:count:total");
            redisCacheService.delete("user:count:active");
            if ("admin".equals(user.getRole())) {
                redisCacheService.delete("user:count:admin");
            }
        }
        return success;
    }
    
    public long getTotalUsers() {
        // 尝试从缓存获取
        String key = "user:count:total";
        Object cached = redisCacheService.get(key);
        if (cached != null) {
            return cached instanceof Integer ? ((Integer) cached).longValue() : (Long) cached;
        }
        
        // 缓存未命中，从数据库查询
        Long count = userMapper.selectCount(new QueryWrapper<>());
        
        // 存入缓存
        redisCacheService.set(key, count, 3600);
        
        return count;
    }
    
    public long getAdminUsers() {
        // 尝试从缓存获取
        String key = "user:count:admin";
        Object cached = redisCacheService.get(key);
        if (cached != null) {
            return cached instanceof Integer ? ((Integer) cached).longValue() : (Long) cached;
        }
        
        // 缓存未命中，从数据库查询
        Long count = userMapper.selectCount(new QueryWrapper<User>().eq("role", "admin"));
        
        // 存入缓存
        redisCacheService.set(key, count, 3600);
        
        return count;
    }
    
    public long getPendingRequestsCount() {
        // 尝试从缓存获取
        String key = "user:count:pending";
        Object cached = redisCacheService.get(key);
        if (cached != null) {
            return cached instanceof Integer ? ((Integer) cached).longValue() : (Long) cached;
        }
        
        // 缓存未命中，从数据库查询
        Long count = userMapper.selectCount(new QueryWrapper<User>().eq("admin_status", "pending"));
        
        // 存入缓存
        redisCacheService.set(key, count, 3600);
        
        return count;
    }
    
    public long getActiveUsers() {
        // 尝试从缓存获取
        String key = "user:count:active";
        Object cached = redisCacheService.get(key);
        if (cached != null) {
            return cached instanceof Integer ? ((Integer) cached).longValue() : (Long) cached;
        }
        
        // 缓存未命中，从数据库查询
        Long count = userMapper.selectCount(new QueryWrapper<User>().eq("status", 1));
        
        // 存入缓存
        redisCacheService.set(key, count, 3600);
        
        return count;
    }
    
    public UserVO toVO(User user) {
        UserVO vo = new UserVO();
        BeanUtils.copyProperties(user, vo);
        return vo;
    }
}
