package com.starfire.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.starfire.entity.*;
import com.starfire.mapper.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Service
public class OrderService {
    
    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;
    private final CartMapper cartMapper;
    private final ProductMapper productMapper;
    private final UserMapper userMapper;
    
    public OrderService(OrderMapper orderMapper, OrderItemMapper orderItemMapper,
                        CartMapper cartMapper, ProductMapper productMapper,
                        UserMapper userMapper) {
        this.orderMapper = orderMapper;
        this.orderItemMapper = orderItemMapper;
        this.cartMapper = cartMapper;
        this.productMapper = productMapper;
        this.userMapper = userMapper;
    }
    
    // 生成订单号
    private String generateOrderNo() {
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String random = String.format("%04d", new Random().nextInt(10000));
        return "ORDER" + date + random;
    }
    
    // 创建订单（私有 - 用户下单）
    @Transactional
    public Order createOrder(Long userId, String username, Long addressId,
                            String payMethod, String remark) {
        // 获取用户购物车
        QueryWrapper<Cart> cartWrapper = new QueryWrapper<>();
        cartWrapper.eq("user_id", userId);
        List<Cart> carts = cartMapper.selectList(cartWrapper);
        
        if (carts.isEmpty()) {
            return null;
        }
        
        // 获取收货地址
        AddressService addressService = new AddressService(null);
        // 这里需要通过controller注入，暂时简化处理
        
        // 创建订单
        Order order = new Order();
        order.setOrderNo(generateOrderNo());
        order.setUserId(userId);
        order.setUsername(username);
        order.setPayMethod(payMethod);
        order.setRemark(remark);
        order.setStatus("pending");
        order.setDiscountAmount(BigDecimal.ZERO);
        
        BigDecimal totalAmount = BigDecimal.ZERO;
        List<OrderItem> orderItems = new ArrayList<>();
        
        // 遍历购物车商品
        for (Cart cart : carts) {
            Product product = productMapper.selectById(cart.getProductId());
            if (product == null || product.getStatus() != 1) {
                continue;
            }
            
            // 检查库存
            if (product.getStock() < cart.getQuantity()) {
                continue;
            }
            
            // 创建订单明细
            OrderItem item = new OrderItem();
            item.setProductId(product.getId());
            item.setProductName(product.getName());
            item.setProductImage(product.getImage());
            item.setPrice(product.getPrice());
            item.setQuantity(cart.getQuantity());
            item.setSubtotal(product.getPrice().multiply(new BigDecimal(cart.getQuantity())));
            
            totalAmount = totalAmount.add(item.getSubtotal());
            orderItems.add(item);
            
            // 扣减库存
            product.setStock(product.getStock() - cart.getQuantity());
            // 增加销量
            product.setSales(product.getSales() + cart.getQuantity());
            productMapper.updateById(product);
        }
        
        if (orderItems.isEmpty()) {
            return null;
        }
        
        order.setTotalAmount(totalAmount);
        order.setPayAmount(totalAmount);
        
        // 保存订单
        orderMapper.insert(order);
        
        // 保存订单明细
        for (OrderItem item : orderItems) {
            item.setOrderId(order.getId());
            orderItemMapper.insert(item);
        }
        
        // 清空购物车
        cartMapper.delete(cartWrapper);
        
        // 设置订单收货信息（简化版本，需要传入）
        // 实际应该通过addressId获取
        
        return order;
    }
    
    // 简化版创建订单（直接传入商品列表）
    @Transactional
    public Order createOrderSimple(Long userId, String username, List<Cart> carts,
                                   String receiverName, String receiverPhone,
                                   String receiverAddress, String payMethod) {
        if (carts == null || carts.isEmpty()) {
            return null;
        }
        
        Order order = new Order();
        order.setOrderNo(generateOrderNo());
        order.setUserId(userId);
        order.setUsername(username);
        order.setReceiverName(receiverName);
        order.setReceiverPhone(receiverPhone);
        order.setReceiverAddress(receiverAddress);
        order.setPayMethod(payMethod);
        order.setStatus("pending");
        order.setDiscountAmount(BigDecimal.ZERO);
        
        BigDecimal totalAmount = BigDecimal.ZERO;
        
        for (Cart cart : carts) {
            Product product = productMapper.selectById(cart.getProductId());
            if (product == null || product.getStatus() != 1 || product.getStock() < cart.getQuantity()) {
                continue;
            }
            
            OrderItem item = new OrderItem();
            item.setProductId(product.getId());
            item.setProductName(product.getName());
            item.setProductImage(product.getImage());
            item.setPrice(product.getPrice());
            item.setQuantity(cart.getQuantity());
            item.setSubtotal(product.getPrice().multiply(new BigDecimal(cart.getQuantity())));
            
            totalAmount = totalAmount.add(item.getSubtotal());
            
            // 扣减库存
            product.setStock(product.getStock() - cart.getQuantity());
            product.setSales(product.getSales() + cart.getQuantity());
            productMapper.updateById(product);
            
            // 保存订单明细
            orderItemMapper.insert(item);
        }
        
        if (totalAmount.compareTo(BigDecimal.ZERO) <= 0) {
            return null;
        }
        
        order.setTotalAmount(totalAmount);
        order.setPayAmount(totalAmount);
        orderMapper.insert(order);
        
        // 更新订单明细的orderId
        QueryWrapper<OrderItem> itemWrapper = new QueryWrapper<>();
        itemWrapper.isNull("order_id");
        List<OrderItem> items = orderItemMapper.selectList(itemWrapper);
        for (OrderItem item : items) {
            item.setOrderId(order.getId());
            orderItemMapper.updateById(item);
        }
        
        // 清空购物车
        QueryWrapper<Cart> cartWrapper = new QueryWrapper<>();
        cartWrapper.eq("user_id", userId);
        cartMapper.delete(cartWrapper);
        
        return order;
    }
    
    // 获取用户订单列表（私有 - 仅用户自己可见）
    public List<Order> getUserOrders(Long userId) {
        QueryWrapper<Order> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        wrapper.orderByDesc("create_time");
        
        List<Order> orders = orderMapper.selectList(wrapper);
        
        // 填充订单明细
        for (Order order : orders) {
            QueryWrapper<OrderItem> itemWrapper = new QueryWrapper<>();
            itemWrapper.eq("order_id", order.getId());
            order.setItems(orderItemMapper.selectList(itemWrapper));
        }
        
        return orders;
    }
    
    // 获取订单详情（私有 - 用户和管理员可见）
    public Order getOrderById(Long orderId, Long userId, boolean isAdmin) {
        Order order = orderMapper.selectById(orderId);
        
        if (order == null) {
            return null;
        }
        
        // 权限检查
        if (!isAdmin && !order.getUserId().equals(userId)) {
            return null;
        }
        
        // 填充订单明细
        QueryWrapper<OrderItem> itemWrapper = new QueryWrapper<>();
        itemWrapper.eq("order_id", orderId);
        order.setItems(orderItemMapper.selectList(itemWrapper));
        
        return order;
    }
    
    // 支付订单
    public boolean payOrder(Long orderId, Long userId, boolean isAdmin) {
        Order order = getOrderById(orderId, userId, isAdmin);
        
        if (order == null) {
            return false;
        }
        
        if (!"pending".equals(order.getStatus())) {
            return false;
        }
        
        order.setStatus("paid");
        order.setPayTime(LocalDateTime.now());
        
        return orderMapper.updateById(order) > 0;
    }
    
    // 取消订单
    public boolean cancelOrder(Long orderId, Long userId, boolean isAdmin) {
        Order order = getOrderById(orderId, userId, isAdmin);
        
        if (order == null) {
            return false;
        }
        
        // 只有待支付和已支付的订单可以取消
        if (!"pending".equals(order.getStatus()) && !"paid".equals(order.getStatus())) {
            return false;
        }
        
        // 恢复库存
        if (order.getItems() != null) {
            for (OrderItem item : order.getItems()) {
                Product product = productMapper.selectById(item.getProductId());
                if (product != null) {
                    product.setStock(product.getStock() + item.getQuantity());
                    product.setSales(product.getSales() - item.getQuantity());
                    if (product.getSales() < 0) product.setSales(0);
                    productMapper.updateById(product);
                }
            }
        }
        
        order.setStatus("cancelled");
        return orderMapper.updateById(order) > 0;
    }
    
    // 确认收货
    public boolean confirmReceive(Long orderId, Long userId) {
        Order order = getOrderById(orderId, userId, false);
        
        if (order == null) {
            return false;
        }
        
        if (!"shipped".equals(order.getStatus())) {
            return false;
        }
        
        order.setStatus("completed");
        return orderMapper.updateById(order) > 0;
    }
    
    // ======================================
    // 管理员接口
    // ======================================
    
    // 获取所有订单（仅管理员可见）
    public List<Order> getAllOrders() {
        QueryWrapper<Order> wrapper = new QueryWrapper<>();
        wrapper.orderByDesc("create_time");
        
        List<Order> orders = orderMapper.selectList(wrapper);
        
        for (Order order : orders) {
            QueryWrapper<OrderItem> itemWrapper = new QueryWrapper<>();
            itemWrapper.eq("order_id", order.getId());
            order.setItems(orderItemMapper.selectList(itemWrapper));
        }
        
        return orders;
    }
    
    // 分页获取订单（仅管理员可见）
    public Page<Order> getOrdersPage(int page, int size, String status) {
        Page<Order> pageParam = new Page<>(page, size);
        QueryWrapper<Order> wrapper = new QueryWrapper<>();
        
        if (status != null && !status.isEmpty()) {
            wrapper.eq("status", status);
        }
        
        wrapper.orderByDesc("create_time");
        
        Page<Order> orderPage = orderMapper.selectPage(pageParam, wrapper);
        
        // 填充订单明细
        for (Order order : orderPage.getRecords()) {
            QueryWrapper<OrderItem> itemWrapper = new QueryWrapper<>();
            itemWrapper.eq("order_id", order.getId());
            order.setItems(orderItemMapper.selectList(itemWrapper));
        }
        
        return orderPage;
    }
    
    // 发货
    public boolean shipOrder(Long orderId) {
        Order order = orderMapper.selectById(orderId);
        
        if (order == null || !"paid".equals(order.getStatus())) {
            return false;
        }
        
        order.setStatus("shipped");
        return orderMapper.updateById(order) > 0;
    }
    
    // 获取销售统计（仅管理员可见）
    public Map<String, Object> getSalesStats() {
        return null; // 暂时使用ProductService的实现
    }
    
    // 获取今日销售统计
    public Map<String, Object> getTodayStats() {
        return null;
    }
}
