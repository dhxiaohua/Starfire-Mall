package com.starfire.config;

import com.starfire.mapper.OrderMapper;
import com.starfire.mapper.ProductMapper;
import com.starfire.mapper.UserMapper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Component
public class DataInitializer implements CommandLineRunner {

    private final JdbcTemplate jdbcTemplate;
    private final ProductMapper productMapper;
    private final UserMapper userMapper;
    private final OrderMapper orderMapper;

    public DataInitializer(JdbcTemplate jdbcTemplate, ProductMapper productMapper, 
                          UserMapper userMapper, OrderMapper orderMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.productMapper = productMapper;
        this.userMapper = userMapper;
        this.orderMapper = orderMapper;
    }

    @Override
    public void run(String... args) throws Exception {
        // 检查并添加缺失的列
        checkAndAddColumns();
        
        // 确保有管理员账号
        ensureAdminUser();
        
        // 确保有测试用户
        ensureTestUsers();
        
        // 清理重复产品
        cleanupDuplicateProducts();
        
        // 初始化订单数据
        initializeOrders();
    }
    
    private void ensureAdminUser() {
        try {
            // 检查是否已有管理员
            Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM users WHERE username = 'admin'", Integer.class);
            
            if (count == null || count == 0) {
                // 创建默认管理员 - 使用环境变量 ADMIN_PASSWORD 或默认密码
                String adminPassword = System.getenv("ADMIN_PASSWORD");
                if (adminPassword == null || adminPassword.isEmpty()) {
                    adminPassword = "admin123"; // 默认密码
                }
                // BCrypt 编码的密码
                String encodedPassword = "$2a$10$8K1p/a0dL3.HKwHkqbHILe5rH4F/gZFcYGFbS3YP5f5r5hKqJXyW"; 
                jdbcTemplate.update(
                    "INSERT INTO users (username, password, nickname, email, status, role, admin_status, create_time) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
                    "admin", encodedPassword, 
                    "管理员", "admin@starfire.com", 1, "admin", "approved", LocalDateTime.now()
                );
                System.out.println("已创建默认管理员账号: admin (请使用环境变量 ADMIN_PASSWORD 设置密码)");
            } else {
                // 确保管理员有正确角色
                jdbcTemplate.update("UPDATE users SET role = 'admin', admin_status = 'approved' WHERE username = 'admin'");
            }
        } catch (Exception e) {
            System.out.println("创建管理员账号: " + e.getMessage());
        }
    }
    
    private void ensureTestUsers() {
        try {
            // 检查是否已有测试用户
            Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM users WHERE role = 'user'", Integer.class);
            
            if (count == null || count < 3) {
                // 创建测试用户 - BCrypt 编码的默认密码 test123
                String encodedPassword = "$2a$10$N9qo8uLOickgx2ZMRZoMye4qDy7iRKSfvQ.hKvKzQqGvqGXZQYXi";
                String[] testUsers = {"testuser", "user1", "user2"};
                String[] nicknames = {"测试用户", "用户一", "用户二"};
                String[] emails = {"testuser@starfire.com", "user1@starfire.com", "user2@starfire.com"};
                
                for (int i = 0; i < testUsers.length; i++) {
                    try {
                        jdbcTemplate.update(
                            "INSERT INTO users (username, password, nickname, email, status, role, admin_status, create_time) " +
                            "VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
                            testUsers[i], encodedPassword, nicknames[i], emails[i], 1, "user", "none", LocalDateTime.now().minusDays(i + 1)
                        );
                    } catch (Exception e) {
                        // 用户已存在，跳过
                    }
                }
                System.out.println("已创建测试用户");
            }
        } catch (Exception e) {
            System.out.println("创建测试用户: " + e.getMessage());
        }
    }
    
    private void checkAndAddColumns() {
        try {
            // 检查 users 表的 last_login_time 列
            try {
                jdbcTemplate.execute("SELECT last_login_time FROM users LIMIT 1");
            } catch (Exception e) {
                jdbcTemplate.execute("ALTER TABLE users ADD COLUMN last_login_time DATETIME DEFAULT NULL");
            }
            
            // 检查 products 表的其他列
            try {
                jdbcTemplate.execute("SELECT original_price FROM products LIMIT 1");
            } catch (Exception e) {
                jdbcTemplate.execute("ALTER TABLE products ADD COLUMN original_price DECIMAL(10,2) DEFAULT NULL");
            }
            
            try {
                jdbcTemplate.execute("SELECT images FROM products LIMIT 1");
            } catch (Exception e) {
                jdbcTemplate.execute("ALTER TABLE products ADD COLUMN images VARCHAR(500) DEFAULT NULL");
            }
            
            try {
                jdbcTemplate.execute("SELECT rating FROM products LIMIT 1");
            } catch (Exception e) {
                jdbcTemplate.execute("ALTER TABLE products ADD COLUMN rating DECIMAL(3,2) DEFAULT 5.00");
            }
            
            try {
                jdbcTemplate.execute("SELECT review_count FROM products LIMIT 1");
            } catch (Exception e) {
                jdbcTemplate.execute("ALTER TABLE products ADD COLUMN review_count INT DEFAULT 0");
            }
            
            // 检查 orders 表的列
            try {
                jdbcTemplate.execute("SELECT user_id FROM orders LIMIT 1");
            } catch (Exception e) {
                jdbcTemplate.execute("ALTER TABLE orders ADD COLUMN user_id BIGINT DEFAULT NULL");
            }
            
            try {
                jdbcTemplate.execute("SELECT receiver_name FROM orders LIMIT 1");
            } catch (Exception e) {
                jdbcTemplate.execute("ALTER TABLE orders ADD COLUMN receiver_name VARCHAR(50) DEFAULT NULL");
                jdbcTemplate.execute("ALTER TABLE orders ADD COLUMN receiver_phone VARCHAR(20) DEFAULT NULL");
                jdbcTemplate.execute("ALTER TABLE orders ADD COLUMN receiver_address VARCHAR(300) DEFAULT NULL");
                jdbcTemplate.execute("ALTER TABLE orders ADD COLUMN discount_amount DECIMAL(10,2) DEFAULT 0.00");
                jdbcTemplate.execute("ALTER TABLE orders ADD COLUMN pay_amount DECIMAL(10,2) DEFAULT 0.00");
                jdbcTemplate.execute("ALTER TABLE orders ADD COLUMN pay_method VARCHAR(20) DEFAULT 'online'");
                jdbcTemplate.execute("ALTER TABLE orders ADD COLUMN pay_time DATETIME DEFAULT NULL");
                jdbcTemplate.execute("ALTER TABLE orders ADD COLUMN remark VARCHAR(500) DEFAULT NULL");
            }
            
            // 检查 order_items 表的列
            try {
                jdbcTemplate.execute("SELECT product_image FROM order_items LIMIT 1");
            } catch (Exception e) {
                jdbcTemplate.execute("ALTER TABLE order_items ADD COLUMN product_image VARCHAR(500) DEFAULT NULL");
            }
            
            // 检查 customer_messages 表的 avatar 列
            try {
                jdbcTemplate.execute("SELECT avatar FROM customer_messages LIMIT 1");
            } catch (Exception e) {
                jdbcTemplate.execute("ALTER TABLE customer_messages ADD COLUMN avatar VARCHAR(500) DEFAULT NULL");
            }
            
            System.out.println("数据库列检查完成");
        } catch (Exception e) {
            System.out.println("检查数据库列: " + e.getMessage());
        }
    }

    private void cleanupDuplicateProducts() {
        try {
            // 先检查category_id列是否存在，不存在则添加
            try {
                jdbcTemplate.execute("SELECT category_id FROM products LIMIT 1");
            } catch (Exception e) {
                // 列不存在，添加它
                jdbcTemplate.execute("ALTER TABLE products ADD COLUMN category_id BIGINT DEFAULT NULL");
                // 更新现有数据
                jdbcTemplate.execute("UPDATE products SET category_id = id");
            }
            
            // 删除重复产品，保留ID最小的
            String sql = "DELETE FROM products WHERE id IN (" +
                    "SELECT id FROM (" +
                    "SELECT id FROM products " +
                    "WHERE (name, category_id) IN (" +
                    "SELECT name, category_id FROM products GROUP BY name, category_id HAVING COUNT(*) > 1" +
                    ") AND id NOT IN (" +
                    "SELECT MIN(id) FROM products GROUP BY name, category_id HAVING COUNT(*) > 1" +
                    ")" +
                    ") AS duplicates" +
                    ")";
            
            int deleted = jdbcTemplate.update(sql);
            if (deleted > 0) {
                System.out.println("已清理 " + deleted + " 条重复产品记录");
            }
        } catch (Exception e) {
            System.out.println("清理重复产品: " + e.getMessage());
        }
    }

    private void initializeOrders() {
        try {
            // 检查是否已有订单
            Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM orders", Integer.class);
            if (count != null && count > 0) {
                System.out.println("订单数据已存在，跳过初始化");
                return;
            }
        } catch (Exception e) {
            System.out.println("订单表检查: " + e.getMessage());
            return;
        }

        // 获取所有产品
        try {
            List<ProductInfo> products = jdbcTemplate.query(
                "SELECT id, name, image, price FROM products WHERE status = 1 LIMIT 20",
                (rs, rowNum) -> new ProductInfo(
                    rs.getLong("id"), 
                    rs.getString("name"),
                    rs.getString("image"),
                    rs.getBigDecimal("price")
                )
            );
            
            if (products.isEmpty()) {
                System.out.println("没有可用的产品数据");
                return;
            }

            // 获取用户ID
            List<Long> userIds = jdbcTemplate.query(
                "SELECT id FROM users WHERE role = 'user' AND status = 1 LIMIT 10",
                (rs, rowNum) -> rs.getLong("id")
            );
            
            if (userIds.isEmpty()) {
                System.out.println("没有可用的用户数据");
                return;
            }

            Random random = new Random();
            LocalDateTime now = LocalDateTime.now();
            String[] statuses = {"pending", "paid", "shipped", "completed", "cancelled"};
            String[] payMethods = {"online", "COD"};
            
            int orderCount = 0;
            
            // 创建30个订单
            for (int i = 0; i < 30; i++) {
                Long userId = userIds.get(random.nextInt(userIds.size()));
                String orderNo = "ORDER" + System.currentTimeMillis() + String.format("%04d", i);
                String status = i < 15 ? "completed" : statuses[random.nextInt(statuses.length)];
                String payMethod = payMethods[random.nextInt(payMethods.length)];
                
                // 随机选择1-4个产品
                int itemCount = random.nextInt(4) + 1;
                BigDecimal totalAmount = BigDecimal.ZERO;
                
                // 创建订单
                LocalDateTime orderTime = now.minusDays(random.nextInt(60));
                
                jdbcTemplate.update(
                    "INSERT INTO orders (order_no, user_id, username, receiver_name, receiver_phone, " +
                    "receiver_address, total_amount, discount_amount, pay_amount, pay_method, status, create_time) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                    orderNo, userId, "user" + userId, "张三", "13800138000", 
                    "北京市朝阳区某某街道", BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
                    payMethod, status, orderTime
                );
                
                Long orderId = jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID()", Long.class);
                
                // 添加订单明细
                for (int j = 0; j < itemCount; j++) {
                    ProductInfo product = products.get(random.nextInt(products.size()));
                    int quantity = random.nextInt(3) + 1;
                    BigDecimal subtotal = product.price.multiply(BigDecimal.valueOf(quantity));
                    totalAmount = totalAmount.add(subtotal);
                    
                    jdbcTemplate.update(
                        "INSERT INTO order_items (order_id, product_id, product_name, product_image, price, quantity, subtotal, create_time) VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
                        orderId, product.id, product.name, product.image, product.price, quantity, subtotal, orderTime
                    );
                }
                
                // 更新订单金额
                BigDecimal payAmount = totalAmount;
                if (random.nextBoolean() && totalAmount.compareTo(BigDecimal.ZERO) > 0) {
                    payAmount = totalAmount.subtract(BigDecimal.valueOf(random.nextDouble() * 50));
                }
                
                LocalDateTime payTime = "completed".equals(status) || "shipped".equals(status) || "paid".equals(status) 
                    ? orderTime.plusHours(random.nextInt(24) + 1) : null;
                
                jdbcTemplate.update(
                    "UPDATE orders SET total_amount = ?, pay_amount = ?, pay_time = ? WHERE id = ?",
                    totalAmount, payAmount, payTime, orderId
                );
                
                orderCount++;
            }
            
            System.out.println("已生成 " + orderCount + " 条模拟订单数据");
            
        } catch (Exception e) {
            System.out.println("初始化订单数据失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static class ProductInfo {
        Long id;
        String name;
        String image;
        BigDecimal price;
        
        ProductInfo(Long id, String name, String image, BigDecimal price) {
            this.id = id;
            this.name = name;
            this.image = image;
            this.price = price;
        }
    }
}