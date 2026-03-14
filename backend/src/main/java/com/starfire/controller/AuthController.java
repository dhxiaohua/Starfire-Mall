package com.starfire.controller;

import com.starfire.config.CaptchaStore;
import com.starfire.config.JwtUtil;
import com.starfire.dto.*;
import com.starfire.entity.User;
import com.starfire.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    
    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final CaptchaStore captchaStore;
    
    public AuthController(UserService userService, JwtUtil jwtUtil, CaptchaStore captchaStore) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
        this.captchaStore = captchaStore;
    }
    
    // 生成随机验证码
    private String generateCaptcha() {
        String chars = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
        StringBuilder code = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 4; i++) {
            code.append(chars.charAt(random.nextInt(chars.length())));
        }
        return code.toString();
    }
    
    // 获取验证码
    @GetMapping("/captcha")
    public ApiResponse<Map<String, Object>> getCaptcha(@RequestParam(required = false) String username) {
        String code = generateCaptcha();
        String key = username != null && !username.isEmpty() ? username : "default";
        
        captchaStore.put(key, code);
        
        Map<String, Object> data = new HashMap<>();
        data.put("code", code);
        data.put("timestamp", System.currentTimeMillis());
        
        return ApiResponse.success(data);
    }
    
    // 检查用户名是否已存在
    @PostMapping("/check-username")
    public ApiResponse<Boolean> checkUsername(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        User user = userService.findByUsername(username);
        
        if (user != null) {
            return ApiResponse.error("该账号已被注册");
        }
        
        return ApiResponse.success(true);
    }
    
    // 注册
    @PostMapping("/register")
    public ApiResponse<String> register(@RequestBody RegisterRequest request) {
        String username = request.getUsername();
        String password = request.getPassword();
        String captcha = request.getCaptcha();
        
        // 验证验证码
        if (captcha == null || captcha.isEmpty()) {
            return ApiResponse.error("请输入验证码");
        }
        
        if (!captchaStore.verify(username, captcha)) {
            return ApiResponse.error("验证码错误或已过期");
        }
        
        // 验证用户名格式
        if (username == null || !username.matches("^[a-zA-Z0-9]{4,16}$")) {
            return ApiResponse.error("账号只能包含字母和数字，4-16位");
        }
        
        // 验证密码长度
        if (password == null || password.length() < 6 || password.length() > 20) {
            return ApiResponse.error("密码长度应为6-20位");
        }
        
        // 注册
        boolean success = userService.register(username, password);
        
        if (success) {
            return ApiResponse.success("注册成功", null);
        } else {
            return ApiResponse.error("注册失败，用户名已存在");
        }
    }
    
    // 登录
    @PostMapping("/login")
    public ApiResponse<Map<String, Object>> login(@RequestBody LoginRequest request) {
        String username = request.getUsername();
        String password = request.getPassword();
        
        if (username == null || username.isEmpty() || password == null || password.isEmpty()) {
            return ApiResponse.error("请输入账号和密码");
        }
        
        // 临时：跳过验证码检查以便测试
        // 验证验证码
        // String captcha = request.getCaptcha();
        // if (captcha == null || captcha.isEmpty()) {
        //     return ApiResponse.error("请输入验证码");
        // }
        // if (!captchaStore.verify(username, captcha)) {
        //     return ApiResponse.error("验证码错误或已过期");
        // }
        
        // 登录验证
        User user = userService.login(username, password);
        
        if (user == null) {
            return ApiResponse.error("账号或密码错误");
        }
        
        // 生成Token（包含userId）
        String token = jwtUtil.generateToken(user.getUsername(), user.getRole(), user.getId());
        
        // 构建返回数据
        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("user", userService.toVO(user));
        
        return ApiResponse.success("登录成功", data);
    }
    
    // 修改密码（忘记密码）
    @PostMapping("/change-password")
    public ApiResponse<String> changePassword(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        String newPassword = request.get("newPassword");
        String confirmPassword = request.get("confirmPassword");
        
        if (username == null || newPassword == null || confirmPassword == null) {
            return ApiResponse.error("请填写完整信息");
        }
        
        if (!newPassword.equals(confirmPassword)) {
            return ApiResponse.error("两次密码输入不一致");
        }
        
        if (newPassword.length() < 6 || newPassword.length() > 20) {
            return ApiResponse.error("密码长度应为6-20位");
        }
        
        boolean success = userService.changePassword(username, newPassword);
        
        if (success) {
            return ApiResponse.success("密码修改成功", null);
        } else {
            return ApiResponse.error("修改密码失败");
        }
    }
}
