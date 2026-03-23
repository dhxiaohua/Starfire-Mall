package com.starfire.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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
    public IPage<Order> getUserOrdersPage(String username, int page, int size, String status) {
        Page<Order> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<Order>()
            .eq(Order::getUsername, username)
            .orderByDesc(Order::getCreateTime);

        if (status != null && !status.isEmpty()) {
            wrapper.eq(Order::getStatus, status);
        }

        IPage<Order> orderPage = page(pageParam, wrapper);

        // 为每个订单加载订单项数据
        for (Order order : orderPage.getRecords()) {
            List<OrderItem> items = getOrderItemsByOrderId(order.getId());
            order.setItems(items);
        }

        return orderPage;
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
    
    @Override
    @Transactional
    public Order createOrder(Order order, List<OrderItem> items) {
        System.out.println("开始创建订单");
        System.out.println("订单基本信息：" + order);
        System.out.println("订单项数量：" + (items != null ? items.size() : 0));
        
        // 生成订单号：时间戳 + 随机数
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String random = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        order.setOrderNo(timestamp + random);
        
        // 设置订单状态为待支付
        order.setStatus("pending");
        
        // 保存订单
        System.out.println("保存订单到数据库...");
        boolean saved = save(order);
        System.out.println("订单保存结果：" + saved);
        
        if (!saved) {
            throw new RuntimeException("订单保存失败");
        }
        
        System.out.println("订单ID：" + order.getId());
        
        // 保存订单项
        if (items != null && !items.isEmpty()) {
            System.out.println("开始保存订单项...");
            for (OrderItem item : items) {
                item.setOrderId(order.getId());
                int result = orderItemMapper.insert(item);
                System.out.println("订单项保存结果：" + result + ", 数据：" + item);
            }
        }
        
        System.out.println("订单创建完成，订单号：" + order.getOrderNo() + "，状态：" + order.getStatus());
        return order;
    }
    
    @Override
    @Transactional
    public boolean completePayment(Long id) {
        Order order = getById(id);
        if (order == null) {
            System.out.println("订单不存在：" + id);
            return false;
        }
        
        if (!"pending".equals(order.getStatus())) {
            System.out.println("订单状态错误，无法完成支付：" + order.getStatus());
            return false;
        }
        
        order.setStatus("paid");
        order.setPayTime(LocalDateTime.now());
        boolean result = updateById(order);
        System.out.println("订单支付完成：" + order.getOrderNo() + "，结果：" + result);
        return result;
    }
    
    @Override
    @Transactional
    public boolean cancelOrder(Long id) {
        Order order = getById(id);
        if (order == null) {
            System.out.println("订单不存在：" + id);
            return false;
        }
        
        if (!"pending".equals(order.getStatus())) {
            System.out.println("订单状态错误，无法取消：" + order.getStatus());
            return false;
        }
        
        boolean result = removeById(id);
        System.out.println("订单已取消并删除：" + order.getOrderNo() + "，结果：" + result);
        return result;
    }
}