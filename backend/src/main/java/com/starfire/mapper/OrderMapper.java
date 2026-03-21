package com.starfire.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.starfire.entity.Order;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@Mapper
public interface OrderMapper extends BaseMapper<Order> {
    
    @Select("SELECT COALESCE(SUM(pay_amount), 0) FROM orders WHERE status != 'cancelled' AND deleted = 0")
    BigDecimal getTotalSales();
    
    @Select("SELECT COUNT(*) FROM orders WHERE status != 'cancelled' AND deleted = 0")
    Integer getTotalOrders();
    
    @Select("SELECT COALESCE(SUM(pay_amount), 0) FROM orders WHERE DATE(create_time) = CURDATE() AND status != 'cancelled' AND deleted = 0")
    BigDecimal getTodaySales();
    
    @Select("SELECT COUNT(*) FROM orders WHERE DATE(create_time) = CURDATE() AND deleted = 0")
    Integer getTodayOrders();
    
    @Select("SELECT COUNT(*) FROM orders WHERE status = 'pending' AND deleted = 0")
    Integer getPendingOrders();
    
    @Select("SELECT COUNT(*) FROM orders WHERE status = 'shipping' AND deleted = 0")
    Integer getShippingOrders();
    
    @Select("SELECT COUNT(*) FROM orders WHERE status = 'completed' AND deleted = 0")
    Integer getCompletedOrders();
    
    IPage<Order> selectOrdersWithDetails(Page<Order> page, @Param("keyword") String keyword, @Param("status") String status);
}