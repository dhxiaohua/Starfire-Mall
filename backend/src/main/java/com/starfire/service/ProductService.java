package com.starfire.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.starfire.entity.Order;
import com.starfire.entity.OrderItem;
import com.starfire.entity.Product;
import com.starfire.mapper.OrderItemMapper;
import com.starfire.mapper.OrderMapper;
import com.starfire.mapper.ProductMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class ProductService {
    
    private final ProductMapper productMapper;
    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;
    
    public ProductService(ProductMapper productMapper, OrderMapper orderMapper, OrderItemMapper orderItemMapper) {
        this.productMapper = productMapper;
        this.orderMapper = orderMapper;
        this.orderItemMapper = orderItemMapper;
    }
    
    public Page<Product> getProductList(String category, String keyword, int page, int size) {
        Page<Product> pageParam = new Page<>(page, size);
        QueryWrapper<Product> wrapper = new QueryWrapper<>();
        
        // 根据分类ID或slug查询
        if (category != null && !category.equals("all")) {
            try {
                Long categoryId = Long.parseLong(category);
                wrapper.eq("category_id", categoryId);
            } catch (NumberFormatException e) {
                // 如果不是数字，可能是slug
                wrapper.eq("category_id", Long.parseLong(category));
            }
        }
        
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.and(w -> w.like("name", keyword).or().like("description", keyword));
        }
        
        wrapper.eq("status", 1);
        wrapper.orderByDesc("sales", "create_time");
        
        return productMapper.selectPage(pageParam, wrapper);
    }
    
    public Product getProductById(Long id) {
        return productMapper.selectById(id);
    }
    
    public boolean addProduct(Product product) {
        product.setStatus(1);
        product.setSales(0);
        return productMapper.insert(product) > 0;
    }
    
    public boolean updateProduct(Product product) {
        return productMapper.updateById(product) > 0;
    }
    
    public boolean deleteProduct(Long id) {
        return productMapper.deleteById(id) > 0;
    }
    
    public boolean updateStock(Long id, Integer stock) {
        Product product = productMapper.selectById(id);
        if (product == null) {
            return false;
        }
        
        product.setStock(stock);
        return productMapper.updateById(product) > 0;
    }
    
    public boolean updateSales(Long id, Integer sales) {
        Product product = productMapper.selectById(id);
        if (product == null) {
            return false;
        }
        
        product.setSales(sales);
        return productMapper.updateById(product) > 0;
    }
    
    public List<String> getCategories() {
        QueryWrapper<Product> wrapper = new QueryWrapper<>();
        wrapper.select("DISTINCT category");
        wrapper.eq("status", 1);
        List<Product> products = productMapper.selectList(wrapper);
        return products.stream().map(Product::getCategory).distinct().toList();
    }
    
    public long getTotalSales() {
        // 从订单明细表计算实际销量
        QueryWrapper<OrderItem> wrapper = new QueryWrapper<>();
        wrapper.select("SUM(quantity) as total");
        Map<String, Object> result = orderItemMapper.selectMaps(wrapper).stream().findFirst().orElse(null);
        if (result != null && result.get("total") != null) {
            return ((Number) result.get("total")).longValue();
        }
        return 0;
    }
    
    public Map<String, Object> getSalesStats() {
        Map<String, Object> stats = new HashMap<>();
        
        // 总销售额 - 从已完成的订单获取
        QueryWrapper<Order> salesWrapper = new QueryWrapper<>();
        salesWrapper.select("COALESCE(SUM(pay_amount), 0) as total");
        salesWrapper.in("status", Arrays.asList("completed", "shipped", "paid"));
        Map<String, Object> salesResultMap = orderMapper.selectMaps(salesWrapper).stream().findFirst().orElse(null);
        BigDecimal totalSales = BigDecimal.ZERO;
        if (salesResultMap != null && salesResultMap.get("total") != null) {
            totalSales = new BigDecimal(salesResultMap.get("total").toString());
        }
        stats.put("totalSales", totalSales);
        
        // 总订单数
        QueryWrapper<Order> ordersWrapper = new QueryWrapper<>();
        Long totalOrders = orderMapper.selectCount(ordersWrapper);
        stats.put("totalOrders", totalOrders != null ? totalOrders : 0);
        
        // 今日销售额
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        QueryWrapper<Order> todaySalesWrapper = new QueryWrapper<>();
        todaySalesWrapper.select("COALESCE(SUM(pay_amount), 0) as total");
        todaySalesWrapper.in("status", Arrays.asList("completed", "shipped", "paid"));
        todaySalesWrapper.ge("create_time", todayStart);
        Map<String, Object> todaySalesMap = orderMapper.selectMaps(todaySalesWrapper).stream().findFirst().orElse(null);
        BigDecimal todaySales = BigDecimal.ZERO;
        if (todaySalesMap != null && todaySalesMap.get("total") != null) {
            todaySales = new BigDecimal(todaySalesMap.get("total").toString());
        }
        stats.put("todaySales", todaySales);
        
        // 今日订单数
        QueryWrapper<Order> todayOrdersWrapper = new QueryWrapper<>();
        todayOrdersWrapper.ge("create_time", todayStart);
        Long todayOrders = orderMapper.selectCount(todayOrdersWrapper);
        stats.put("todayOrders", todayOrders != null ? todayOrders : 0);
        
        // 分类统计
        List<Map<String, Object>> categoryStats = new ArrayList<>();
        QueryWrapper<Product> catWrapper = new QueryWrapper<>();
        catWrapper.select("category_id, SUM(sales) as sales, COUNT(*) as count");
        catWrapper.eq("status", 1);
        catWrapper.groupBy("category_id");
        List<Product> categories = productMapper.selectList(catWrapper);
        for (Product p : categories) {
            Map<String, Object> cat = new HashMap<>();
            cat.put("categoryId", p.getCategoryId());
            cat.put("sales", p.getSales());
            categoryStats.add(cat);
        }
        stats.put("categoryStats", categoryStats);
        
        // 低库存产品
        QueryWrapper<Product> lowStockWrapper = new QueryWrapper<>();
        lowStockWrapper.eq("status", 1);
        lowStockWrapper.lt("stock", 10);
        lowStockWrapper.orderByAsc("stock");
        lowStockWrapper.last("LIMIT 5");
        List<Product> lowStockProducts = productMapper.selectList(lowStockWrapper);
        stats.put("lowStockProducts", lowStockProducts);
        
        return stats;
    }
}
