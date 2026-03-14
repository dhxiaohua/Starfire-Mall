package com.starfire.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.starfire.dto.ApiResponse;
import com.starfire.entity.Cart;
import com.starfire.entity.Order;
import com.starfire.entity.User;
import com.starfire.mapper.UserMapper;
import com.starfire.service.CartService;
import com.starfire.service.OrderService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    
    private final OrderService orderService;
    private final CartService cartService;
    private final UserMapper userMapper;
    
    public OrderController(OrderService orderService, CartService cartService, UserMapper userMapper) {
        this.orderService = orderService;
        this.cartService = cartService;
        this.userMapper = userMapper;
    }
    
    // 创建订单（私有 - 需登录）
    @PostMapping
    public ApiResponse<Order> createOrder(
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader("X-Username") String username,
            @RequestBody Map<String, Object> request) {
        
        // 获取收货地址信息
        String receiverName = request.get("receiverName").toString();
        String receiverPhone = request.get("receiverPhone").toString();
        String receiverAddress = request.get("receiverAddress").toString();
        String payMethod = request.getOrDefault("payMethod", "online").toString();
        
        // 获取购物车商品
        List<Cart> carts = cartService.getUserCart(userId);
        
        if (carts.isEmpty()) {
            return ApiResponse.error("购物车为空");
        }
        
        // 创建订单
        Order order = orderService.createOrderSimple(userId, username, carts,
                receiverName, receiverPhone, receiverAddress, payMethod);
        
        if (order != null) {
            return ApiResponse.success("订单创建成功", order);
        } else {
            return ApiResponse.error("订单创建失败");
        }
    }
    
    // 获取用户订单列表（私有 - 仅用户自己可见）
    @GetMapping
    public ApiResponse<List<Order>> getUserOrders(@RequestHeader("X-User-Id") Long userId) {
        List<Order> orders = orderService.getUserOrders(userId);
        return ApiResponse.success(orders);
    }
    
    // 获取订单详情（私有 - 用户和管理员可见）
    @GetMapping("/{orderId}")
    public ApiResponse<Order> getOrderDetail(
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader("X-User-Role") String userRole,
            @PathVariable Long orderId) {
        
        boolean isAdmin = "admin".equals(userRole);
        Order order = orderService.getOrderById(orderId, userId, isAdmin);
        
        if (order == null) {
            return ApiResponse.error("订单不存在");
        }
        
        return ApiResponse.success(order);
    }
    
    // 支付订单（私有 - 用户）
    @PostMapping("/{orderId}/pay")
    public ApiResponse<String> payOrder(
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader("X-User-Role") String userRole,
            @PathVariable Long orderId) {
        
        boolean isAdmin = "admin".equals(userRole);
        boolean success = orderService.payOrder(orderId, userId, isAdmin);
        
        if (success) {
            return ApiResponse.success("支付成功", null);
        } else {
            return ApiResponse.error("支付失败");
        }
    }
    
    // 取消订单（私有 - 用户和管理员）
    @PostMapping("/{orderId}/cancel")
    public ApiResponse<String> cancelOrder(
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader("X-User-Role") String userRole,
            @PathVariable Long orderId) {
        
        boolean isAdmin = "admin".equals(userRole);
        boolean success = orderService.cancelOrder(orderId, userId, isAdmin);
        
        if (success) {
            return ApiResponse.success("订单已取消", null);
        } else {
            return ApiResponse.error("取消失败");
        }
    }
    
    // 确认收货（私有 - 用户）
    @PostMapping("/{orderId}/confirm")
    public ApiResponse<String> confirmReceive(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long orderId) {
        
        boolean success = orderService.confirmReceive(orderId, userId);
        
        if (success) {
            return ApiResponse.success("确认收货成功", null);
        } else {
            return ApiResponse.error("确认收货失败");
        }
    }
    
    // ======================================
    // 管理员接口
    // ======================================
    
    // 获取所有订单（仅管理员可见）
    @GetMapping("/admin/all")
    public ApiResponse<Page<Order>> getAllOrders(
            @RequestHeader("X-User-Role") String userRole,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status) {
        
        if (!"admin".equals(userRole)) {
            return ApiResponse.error("无权限");
        }
        
        Page<Order> orders = orderService.getOrdersPage(page, size, status);
        return ApiResponse.success(orders);
    }
    
    // 发货（仅管理员可见）
    @PostMapping("/admin/{orderId}/ship")
    public ApiResponse<String> shipOrder(
            @RequestHeader("X-User-Role") String userRole,
            @PathVariable Long orderId) {
        
        if (!"admin".equals(userRole)) {
            return ApiResponse.error("无权限");
        }
        
        boolean success = orderService.shipOrder(orderId);
        
        if (success) {
            return ApiResponse.success("发货成功", null);
        } else {
            return ApiResponse.error("发货失败");
        }
    }
}
