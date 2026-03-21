package com.starfire.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;

import com.starfire.dto.ApiResponse;
import com.starfire.entity.Order;
import com.starfire.entity.OrderItem;
import com.starfire.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    
    @Autowired
    private OrderService orderService;
    
    @GetMapping("/stats")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Map<String, Object>> getStats() {
        return ApiResponse.success(orderService.getStats());
    }
    
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<IPage<Order>> getOrders(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status) {
        return ApiResponse.success(orderService.getOrdersPage(page, size, keyword, status));
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Map<String, Object>> getOrderDetail(@PathVariable Long id) {
        Order order = orderService.getById(id);
        if (order == null) {
            return ApiResponse.error("订单不存在");
        }
        
        List<OrderItem> items = orderService.getOrderItemsByOrderId(id);
        
        Map<String, Object> result = new HashMap<>();
        result.put("order", order);
        result.put("items", items);
        
        return ApiResponse.success(result);
    }
    
    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> updateOrderStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {
        String status = request.get("status");
        if (status == null || status.isEmpty()) {
            return ApiResponse.error("状态不能为空");
        }
        
        boolean success = orderService.updateOrderStatus(id, status);
        if (success) {
            return ApiResponse.success(null);
        } else {
            return ApiResponse.error("更新失败");
        }
    }
}