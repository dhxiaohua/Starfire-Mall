package com.starfire.controller;

import com.starfire.dto.ApiResponse;
import com.starfire.entity.Cart;
import com.starfire.entity.Product;
import com.starfire.entity.User;
import com.starfire.mapper.UserMapper;
import com.starfire.service.CartService;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cart")
public class CartController {
    
    private final CartService cartService;
    private final UserMapper userMapper;
    
    public CartController(CartService cartService, UserMapper userMapper) {
        this.cartService = cartService;
        this.userMapper = userMapper;
    }
    
    // 获取购物车列表（私有 - 需登录）
    @GetMapping
    public ApiResponse<Map<String, Object>> getCart(@RequestHeader("X-User-Id") Long userId) {
        List<Cart> carts = cartService.getUserCart(userId);
        
        // 计算总价
        double totalPrice = 0;
        for (Cart cart : carts) {
            if (cart.getProduct() != null) {
                totalPrice += cart.getProduct().getPrice().doubleValue() * cart.getQuantity();
            }
        }
        
        Map<String, Object> data = new HashMap<>();
        data.put("carts", carts);
        data.put("totalPrice", totalPrice);
        data.put("count", carts.size());
        
        return ApiResponse.success(data);
    }
    
    // 添加到购物车（私有 - 需登录）
    @PostMapping
    public ApiResponse<String> addToCart(
            @RequestHeader("X-User-Id") Long userId,
            @RequestBody Map<String, Object> request) {
        
        Long productId = Long.parseLong(request.get("productId").toString());
        Integer quantity = Integer.parseInt(request.getOrDefault("quantity", 1).toString());
        
        boolean success = cartService.addToCart(userId, productId, quantity);
        
        if (success) {
            return ApiResponse.success("添加到购物车成功", null);
        } else {
            return ApiResponse.error("添加到购物车失败");
        }
    }
    
    // 更新购物车商品数量（私有 - 需登录）
    @PutMapping("/{cartId}")
    public ApiResponse<String> updateCart(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long cartId,
            @RequestBody Map<String, Integer> request) {
        
        Integer quantity = request.get("quantity");
        
        if (quantity == null || quantity <= 0) {
            return ApiResponse.error("数量无效");
        }
        
        boolean success = cartService.updateCartQuantity(cartId, userId, quantity);
        
        if (success) {
            return ApiResponse.success("更新成功", null);
        } else {
            return ApiResponse.error("更新失败");
        }
    }
    
    // 删除购物车项（私有 - 需登录）
    @DeleteMapping("/{cartId}")
    public ApiResponse<String> removeFromCart(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long cartId) {
        
        boolean success = cartService.removeFromCart(cartId, userId);
        
        if (success) {
            return ApiResponse.success("删除成功", null);
        } else {
            return ApiResponse.error("删除失败");
        }
    }
    
    // 清空购物车（私有 - 需登录）
    @DeleteMapping
    public ApiResponse<String> clearCart(@RequestHeader("X-User-Id") Long userId) {
        boolean success = cartService.clearCart(userId);
        
        if (success) {
            return ApiResponse.success("购物车已清空", null);
        } else {
            return ApiResponse.error("清空失败");
        }
    }
    
    // 获取购物车数量（私有 - 需登录）
    @GetMapping("/count")
    public ApiResponse<Integer> getCartCount(@RequestHeader("X-User-Id") Long userId) {
        int count = cartService.getCartCount(userId);
        return ApiResponse.success(count);
    }
}
