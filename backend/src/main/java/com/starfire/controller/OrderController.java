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
    
    // 创建订单（待支付状态）
    @PostMapping
    public ApiResponse<Map<String, Object>> createOrder(@RequestBody Map<String, Object> request) {
        try {
            System.out.println("收到创建订单请求：" + request);
            
            // 创建订单对象
            Order order = new Order();
            
            // 从请求中获取订单基本信息
            if (request.containsKey("userId") && request.get("userId") != null) {
                try {
                    order.setUserId(Long.valueOf(request.get("userId").toString()));
                } catch (NumberFormatException e) {
                    System.out.println("用户ID转换失败：" + request.get("userId"));
                }
            }
            if (request.containsKey("username")) {
                order.setUsername((String) request.get("username"));
            }
            if (request.containsKey("receiverName")) {
                order.setReceiverName((String) request.get("receiverName"));
            }
            if (request.containsKey("receiverPhone")) {
                order.setReceiverPhone((String) request.get("receiverPhone"));
            }
            if (request.containsKey("receiverAddress")) {
                order.setReceiverAddress((String) request.get("receiverAddress"));
            }
            if (request.containsKey("totalAmount")) {
                order.setTotalAmount(new java.math.BigDecimal(request.get("totalAmount").toString()));
            }
            if (request.containsKey("discountAmount")) {
                order.setDiscountAmount(new java.math.BigDecimal(request.get("discountAmount").toString()));
            }
            if (request.containsKey("payAmount")) {
                order.setPayAmount(new java.math.BigDecimal(request.get("payAmount").toString()));
            }
            if (request.containsKey("payMethod")) {
                order.setPayMethod((String) request.get("payMethod"));
            }
            if (request.containsKey("remark")) {
                order.setRemark((String) request.get("remark"));
            }
            
            // 获取订单项并转换为OrderItem对象
            List<OrderItem> items = null;
            if (request.containsKey("items")) {
                Object itemsObj = request.get("items");
                if (itemsObj instanceof List) {
                    List<?> rawItems = (List<?>) itemsObj;
                    items = new java.util.ArrayList<>();
                    System.out.println("订单项数量：" + rawItems.size());
                    
                    for (Object rawItem : rawItems) {
                        if (rawItem instanceof Map) {
                            Map<String, Object> itemMap = (Map<String, Object>) rawItem;
                            OrderItem orderItem = new OrderItem();
                            
                            // 设置商品ID
                            if (itemMap.containsKey("productId")) {
                                Object productId = itemMap.get("productId");
                                if (productId instanceof Number) {
                                    orderItem.setProductId(((Number) productId).longValue());
                                } else {
                                    orderItem.setProductId(Long.valueOf(productId.toString()));
                                }
                            }
                            
                            // 设置商品名称
                            if (itemMap.containsKey("productName")) {
                                orderItem.setProductName((String) itemMap.get("productName"));
                            }
                            
                            // 设置商品图片
                            if (itemMap.containsKey("productImage")) {
                                orderItem.setProductImage((String) itemMap.get("productImage"));
                            }
                            
                            // 设置价格
                            if (itemMap.containsKey("price")) {
                                Object price = itemMap.get("price");
                                if (price instanceof Number) {
                                    orderItem.setPrice(new java.math.BigDecimal(price.toString()));
                                } else {
                                    orderItem.setPrice(new java.math.BigDecimal(price.toString()));
                                }
                            }
                            
                            // 设置数量
                            if (itemMap.containsKey("quantity")) {
                                Object quantity = itemMap.get("quantity");
                                if (quantity instanceof Number) {
                                    orderItem.setQuantity(((Number) quantity).intValue());
                                } else {
                                    orderItem.setQuantity(Integer.valueOf(quantity.toString()));
                                }
                            }
                            
                            // 设置小计
                            if (itemMap.containsKey("subtotal")) {
                                Object subtotal = itemMap.get("subtotal");
                                if (subtotal instanceof Number) {
                                    orderItem.setSubtotal(new java.math.BigDecimal(subtotal.toString()));
                                } else {
                                    orderItem.setSubtotal(new java.math.BigDecimal(subtotal.toString()));
                                }
                            }
                            
                            items.add(orderItem);
                            System.out.println("添加订单项：" + orderItem);
                        }
                    }
                }
            }
            
            // 创建订单（待支付状态）
            Order createdOrder = orderService.createOrder(order, items);
            System.out.println("订单创建成功，订单号：" + createdOrder.getOrderNo());
            
            Map<String, Object> result = new HashMap<>();
            result.put("order", createdOrder);
            result.put("items", items);
            
            return ApiResponse.success(result);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("创建订单失败：" + e.getMessage());
            return ApiResponse.error("创建订单失败：" + e.getMessage());
        }
    }
    
    // 完成支付
    @PostMapping("/{id}/pay")
    public ApiResponse<Void> completePayment(@PathVariable Long id) {
        try {
            boolean success = orderService.completePayment(id);
            if (success) {
                return ApiResponse.success(null);
            } else {
                return ApiResponse.error("支付失败，订单不存在或状态错误");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error("支付失败：" + e.getMessage());
        }
    }
    
    // 取消订单
    @DeleteMapping("/{id}")
    public ApiResponse<Void> cancelOrder(@PathVariable Long id) {
        try {
            boolean success = orderService.cancelOrder(id);
            if (success) {
                return ApiResponse.success(null);
            } else {
                return ApiResponse.error("取消订单失败，订单不存在或状态错误");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error("取消订单失败：" + e.getMessage());
        }
    }
    
    // 获取用户订单列表
    @GetMapping("/user/{username}")
    public ApiResponse<IPage<Order>> getUserOrders(
            @PathVariable String username,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status) {
        try {
            IPage<Order> orders = orderService.getUserOrdersPage(username, page, size, status);
            return ApiResponse.success(orders);
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error("获取订单失败：" + e.getMessage());
        }
    }
    
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