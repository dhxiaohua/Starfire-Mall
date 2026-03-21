package com.starfire.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.starfire.config.NotificationHandler;
import com.starfire.dto.ApiResponse;
import com.starfire.dto.UserVO;
import com.starfire.entity.User;
import com.starfire.service.ProductService;
import com.starfire.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    
    private final UserService userService;
    private final ProductService productService;
    private final com.starfire.service.OrderService orderService;
    
    public AdminController(UserService userService, ProductService productService, com.starfire.service.OrderService orderService) {
        this.userService = userService;
        this.productService = productService;
        this.orderService = orderService;
    }
    
    // 获取所有用户列表
    @GetMapping("/users")
    public ApiResponse<Map<String, Object>> getUsers(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Page<User> pageResult = userService.getUserList(page, size);
        
        Map<String, Object> data = new HashMap<>();
        data.put("users", pageResult.getRecords().stream()
                .map(userService::toVO)
                .collect(Collectors.toList()));
        data.put("total", pageResult.getTotal());
        data.put("pages", pageResult.getPages());
        
        return ApiResponse.success(data);
    }
    
    // 获取待审批的权限申请
    @GetMapping("/pending-requests")
    public ApiResponse<Map<String, Object>> getPendingRequests(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Page<User> pageResult = userService.getPendingRequests(page, size);
        
        Map<String, Object> data = new HashMap<>();
        data.put("requests", pageResult.getRecords().stream()
                .map(userService::toVO)
                .collect(Collectors.toList()));
        data.put("total", pageResult.getTotal());
        
        return ApiResponse.success(data);
    }
    
    // 审批权限申请
    @PostMapping("/approve-request")
    public ApiResponse<String> approveRequest(@RequestBody Map<String, Object> request) {
        Long userId = Long.valueOf(request.get("userId").toString());
        Boolean approved = (Boolean) request.get("approved");
        
        // 获取用户信息用于通知
        User user = userService.findById(userId);
        String username = user != null ? user.getUsername() : null;
        
        boolean success = userService.approveRequest(userId, approved);
        
        if (success) {
            String message = approved ? "已批准该用户的权限申请" : "已拒绝该用户的权限申请";
            
            // 发送WebSocket通知给用户
            if (username != null) {
                String notification = "{\"type\":\"" + (approved ? "request_approved" : "request_rejected") + "\",\"username\":\"" + username + "\",\"message\":\"" + (approved ? "您的管理员申请已通过！" : "您的管理员申请已被拒绝") + "\"}";
                NotificationHandler.sendMessage(username, notification);
            }
            
            return ApiResponse.success(message, null);
        } else {
            return ApiResponse.error("审批失败");
        }
    }
    
    // 申请成为管理员
    @PostMapping("/apply")
    public ApiResponse<String> applyAdmin(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        
        boolean success = userService.applyAdmin(username);
        
        if (success) {
            // 发送WebSocket通知给管理员
            String notification = "{\"type\":\"new_admin_request\",\"username\":\"" + username + "\",\"message\":\"用户 " + username + " 申请成为管理员\"}";
            NotificationHandler.broadcastToAdmins(notification);
            
            return ApiResponse.success("申请已提交，请等待管理员审批", null);
        } else {
            return ApiResponse.error("申请失败");
        }
    }
    
    // 撤销管理员申请
    @PostMapping("/cancel-application")
    public ApiResponse<String> cancelApplication(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        
        boolean success = userService.cancelApplication(username);
        
        if (success) {
            return ApiResponse.success("已撤销申请", null);
        } else {
            return ApiResponse.error("撤销申请失败");
        }
    }
    
    // 获取用户权限状态
    @GetMapping("/user-status")
    public ApiResponse<Map<String, Object>> getUserStatus(@RequestParam String username) {
        User user = userService.findByUsername(username);
        
        if (user == null) {
            return ApiResponse.error("用户不存在");
        }
        
        Map<String, Object> data = new HashMap<>();
        data.put("isAdmin", "admin".equals(user.getRole()));
        data.put("adminStatus", user.getAdminStatus());
        data.put("canApply", !"admin".equals(user.getRole()) && !"pending".equals(user.getAdminStatus()));
        
        return ApiResponse.success(data);
    }
    
    // 撤销管理员权限
    @PostMapping("/revoke")
    public ApiResponse<String> revokeAdmin(@RequestBody Map<String, Long> request) {
        Long userId = request.get("userId");
        
        boolean success = userService.revokeAdmin(userId);
        
        if (success) {
            return ApiResponse.success("已撤销该用户的管理员权限", null);
        } else {
            return ApiResponse.error("撤销权限失败");
        }
    }
    
    // 获取统计信息
    @GetMapping("/stats")
    public ApiResponse<Map<String, Object>> getStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalUsers", userService.getTotalUsers());
        stats.put("adminUsers", userService.getAdminUsers());
        stats.put("pendingRequests", userService.getPendingRequestsCount());
        stats.put("activeUsers", userService.getActiveUsers());
        
        // 添加订单销售统计
        Map<String, Object> orderStats = orderService.getStats();
        stats.put("totalSales", orderStats.get("totalSales"));
        stats.put("totalOrders", orderStats.get("totalOrders"));
        stats.put("todaySales", orderStats.get("todaySales"));
        stats.put("todayOrders", orderStats.get("todayOrders"));
        stats.put("pendingOrders", orderStats.get("pendingOrders"));
        stats.put("shippingOrders", orderStats.get("shippingOrders"));
        stats.put("completedOrders", orderStats.get("completedOrders"));
        
        return ApiResponse.success(stats);
    }
    
    // 更新用户状态
    @PostMapping("/update-user-status")
    public ApiResponse<String> updateUserStatus(@RequestBody Map<String, Object> request) {
        Long userId = Long.valueOf(request.get("userId").toString());
        Integer status = (Integer) request.get("status");
        
        boolean success = userService.updateUserStatus(userId, status);
        
        if (success) {
            String message = status == 1 ? "已启用用户" : "已禁用用户";
            return ApiResponse.success(message, null);
        } else {
            return ApiResponse.error("更新状态失败");
        }
    }
    
    // 删除用户
    @PostMapping("/delete-user")
    public ApiResponse<String> deleteUser(@RequestBody Map<String, Long> request) {
        Long userId = request.get("userId");
        
        boolean success = userService.deleteUser(userId);
        
        if (success) {
            return ApiResponse.success("用户已删除", null);
        } else {
            return ApiResponse.error("删除用户失败");
        }
    }
}
