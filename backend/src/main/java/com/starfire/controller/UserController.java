package com.starfire.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.starfire.dto.ApiResponse;
import com.starfire.dto.UserVO;
import com.starfire.entity.User;
import com.starfire.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {
    
    private final UserService userService;
    
    public UserController(UserService userService) {
        this.userService = userService;
    }
    
    // 获取用户信息
    @GetMapping("/info")
    public ApiResponse<UserVO> getUserInfo(@RequestParam String username) {
        User user = userService.findByUsername(username);
        
        if (user == null) {
            return ApiResponse.error("用户不存在");
        }
        
        return ApiResponse.success(userService.toVO(user));
    }
    
    // 更新用户信息
    @PutMapping("/update")
    public ApiResponse<String> updateUserInfo(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        
        if (username == null || username.isEmpty()) {
            return ApiResponse.error("用户名不能为空");
        }
        
        User user = userService.findByUsername(username);
        if (user == null) {
            return ApiResponse.error("用户不存在");
        }
        
        if (request.containsKey("nickname")) {
            user.setNickname(request.get("nickname"));
        }
        if (request.containsKey("email")) {
            user.setEmail(request.get("email"));
        }
        if (request.containsKey("phone")) {
            user.setPhone(request.get("phone"));
        }
        if (request.containsKey("avatar")) {
            user.setAvatar(request.get("avatar"));
        }
        
        boolean success = userService.updateUserInfo(user);
        
        if (success) {
            return ApiResponse.success("更新成功", null);
        } else {
            return ApiResponse.error("更新失败");
        }
    }
    
    // 修改密码（验证旧密码）
    @PutMapping("/password")
    public ApiResponse<String> changePassword(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        String oldPassword = request.get("oldPassword");
        String newPassword = request.get("newPassword");
        
        if (username == null || oldPassword == null || newPassword == null) {
            return ApiResponse.error("请填写完整信息");
        }
        
        if (newPassword.length() < 6 || newPassword.length() > 20) {
            return ApiResponse.error("密码长度应为6-20位");
        }
        
        User user = userService.findByUsername(username);
        if (user == null) {
            return ApiResponse.error("用户不存在");
        }
        
        // 验证旧密码
        User loginUser = userService.login(username, oldPassword);
        if (loginUser == null) {
            return ApiResponse.error("原密码错误");
        }
        
        boolean success = userService.changePassword(username, newPassword);
        
        if (success) {
            return ApiResponse.success("密码修改成功", null);
        } else {
            return ApiResponse.error("密码修改失败");
        }
    }
}
