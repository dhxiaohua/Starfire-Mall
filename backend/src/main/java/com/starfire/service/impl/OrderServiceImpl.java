package com.starfire.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.starfire.entity.Order;
import com.starfire.entity.OrderItem;
import com.starfire.mapper.OrderMapper;
import com.starfire.mapper.OrderItemMapper;
import com.starfire.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {
    
    @Autowired
    private OrderMapper orderMapper;
    
    @Autowired
    private OrderItemMapper orderItemMapper;
    
    @Override
    public Map<String, Object> getStats() {
        Map<String, Object> stats = new HashMap<>();
        
        stats.put("totalSales", orderMapper.getTotalSales());
        stats.put("totalOrders", orderMapper.getTotalOrders());
        stats.put("todaySales", orderMapper.getTodaySales());
        stats.put("todayOrders", orderMapper.getTodayOrders());
        stats.put("pendingOrders", orderMapper.getPendingOrders());
        stats.put("shippingOrders", orderMapper.getShippingOrders());
        stats.put("completedOrders", orderMapper.getCompletedOrders());
        
        return stats;
    }
    
    @Override
    public IPage<Order> getOrdersPage(int page, int size, String keyword, String status) {
        Page<Order> pageParam = new Page<>(page, size);
        return orderMapper.selectOrdersWithDetails(pageParam, keyword, status);
    }
    
    @Override
    public List<OrderItem> getOrderItemsByOrderId(Long orderId) {
        return orderItemMapper.selectByOrderId(orderId);
    }
    
    @Override
    public Order getOrderByOrderNo(String orderNo) {
        return orderMapper.selectOne(
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<Order>()
                .eq(Order::getOrderNo, orderNo)
        );
    }
    
    @Override
    @Transactional
    public boolean updateOrderStatus(Long id, String status) {
        Order order = new Order();
        order.setId(id);
        order.setStatus(status);
        return updateById(order);
    }
}