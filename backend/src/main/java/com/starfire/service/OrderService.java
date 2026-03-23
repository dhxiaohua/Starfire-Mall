package com.starfire.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.starfire.entity.Order;
import com.starfire.entity.OrderItem;

import java.util.List;
import java.util.Map;

public interface OrderService extends IService<Order> {
    
    Map<String, Object> getStats();
    
    IPage<Order> getOrdersPage(int page, int size, String keyword, String status);
    
    IPage<Order> getUserOrdersPage(String username, int page, int size, String status);
    
    List<OrderItem> getOrderItemsByOrderId(Long orderId);
    
    Order getOrderByOrderNo(String orderNo);
    
    boolean updateOrderStatus(Long id, String status);
    
    Order createOrder(Order order, List<OrderItem> items);
    
    boolean completePayment(Long id);
    
    boolean cancelOrder(Long id);
}