package com.starfire.config;

import com.starfire.mapper.OrderMapper;
import com.starfire.mapper.ProductMapper;
import com.starfire.mapper.UserMapper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
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
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(JdbcTemplate jdbcTemplate, ProductMapper productMapper, 
                          UserMapper userMapper, OrderMapper orderMapper,
                          PasswordEncoder passwordEncoder) {
        this.jdbcTemplate = jdbcTemplate;
        this.productMapper = productMapper;
        this.userMapper = userMapper;
        this.orderMapper = orderMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        try {
            // 检查并添加缺失的列
            checkAndAddColumns();

            // 确保有管理员账号
            ensureAdminUser();

            // 确保有测试用户
            ensureTestUsers();

            // 初始化商品数据
            initializeProducts();

            // 清理重复产品
            cleanupDuplicateProducts();

            // 初始化订单数据
            initializeOrders();
        } catch (Exception e) {
            System.out.println("数据库初始化失败，应用将继续启动: " + e.getMessage());
        }
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
                
                // 使用 PasswordEncoder 加密密码
                String encodedPassword = passwordEncoder.encode(adminPassword);
                
                jdbcTemplate.update(
                    "INSERT INTO users (username, password, nickname, email, status, role, admin_status, create_time) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
                    "admin", encodedPassword, 
                    "管理员", "admin@starfire.com", 1, "admin", "approved", LocalDateTime.now()
                );
                System.out.println("已创建默认管理员账号: admin (密码: " + adminPassword + ")");
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
                // 创建测试用户 - 使用 PasswordEncoder 加密默认密码 test123
                String testPassword = "test123";
                String encodedPassword = passwordEncoder.encode(testPassword);
                
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
                System.out.println("已创建测试用户 (密码: " + testPassword + ")");
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

    private void initializeProducts() {
        try {
            // 检查是否已有商品数据
            Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM products", Integer.class);
            if (count != null && count > 0) {
                System.out.println("商品数据已存在，跳过初始化（当前商品数: " + count + "）");
                return;
            }
        } catch (Exception e) {
            System.out.println("商品表检查: " + e.getMessage());
            return;
        }

        System.out.println("开始初始化商品数据...");

        // 商品数据数组
        Object[][] products = {
            // 鼠标 (category_id = 1)
            {"雷蛇毒蝰V3专业版", 1L, "高性能无线游戏鼠标，支持20000 DPI传感器", 899.00, 1299.00, 100, "/images/mouse/1.jpg", "无线连接,20000 DPI,轻量化设计", 520, 4.8, 128, 1},
            {"罗技G Pro X Superlight", 1L, "超轻量化无线鼠标，仅重63克", 799.00, 999.00, 150, "/images/mouse/2.jpg", "无线连接,63克轻便,HERO 25K传感器", 888, 4.9, 256, 1},
            {"赛睿Rival 3", 1L, "精准电竞鼠标，8500 DPI传感器", 299.00, 399.00, 200, "/images/mouse/3.jpg", "有线连接,8500 DPI,RGB灯效", 320, 4.5, 89, 1},
            {"雷蛇曼巴眼镜蛇3", 1L, "专业FPS游戏鼠标", 599.00, 799.00, 80, "/images/mouse/4.jpg", "有线连接,26000 DPI,人体工学", 156, 4.7, 45, 1},
            {"华硕ROG夜魔", 1L, "轻量化游戏鼠标", 449.00, 599.00, 120, "/images/mouse/5.jpg", "无线连接,58克,RGB灯效", 203, 4.6, 67, 1},

            // 键盘 (category_id = 2)
            {"雷蛇黑寡妇蜘蛛4", 2L, "机械键盘，支持幻彩灯光", 1299.00, 1599.00, 80, "/images/keyboard/1.jpg", "机械轴,RGB灯效,多媒体键", 256, 4.7, 78, 1},
            {"罗技G913 TKL", 2L, "无线机械键盘，紧凑设计", 1599.00, 1999.00, 60, "/images/keyboard/2.jpg", "无线连接,紧凑布局,RGB灯效", 198, 4.8, 56, 1},
            {"美商海盗船K70 RGB", 2L, "RGB机械键盘，Cherry MX轴", 1099.00, 1399.00, 90, "/images/keyboard/3.jpg", "Cherry MX,RGB灯光,铝合金", 312, 4.6, 92, 1},
            {"樱桃MX Board 3.0", 2L, "原厂机械键盘", 699.00, 899.00, 150, "/images/keyboard/4.jpg", "Cherry MX,简约设计,耐摔", 189, 4.5, 67, 1},
            {"Keychron K2", 2L, "无线机械键盘，适配Mac", 499.00, 699.00, 200, "/images/keyboard/5.jpg", "蓝牙/有线,Mac/Win兼容,RGB", 278, 4.7, 145, 1},

            // 耳机 (category_id = 3)
            {"雷蛇北海巨妖V3", 3L, "游戏耳机，7.1环绕声", 799.00, 999.00, 100, "/images/headset/1.jpg", "7.1声道,降噪麦克风,舒适佩戴", 345, 4.6, 89, 1},
            {"罗技G Pro X", 3L, "专业电竞耳机", 999.00, 1299.00, 80, "/images/headset/2.jpg", "Blue麦克风,50mm驱动,可换耳罩", 456, 4.8, 123, 1},
            {"赛睿Arctis 7", 3L, "无线游戏耳机", 899.00, 1199.00, 70, "/images/headset/3.jpg", "无线连接,30小时续航,降噪", 267, 4.7, 98, 1},
            {"金士顿HyperX Cloud II", 3L, "经典游戏耳机", 599.00, 799.00, 150, "/images/headset/4.jpg", "7.1声道,记忆海绵,耐用", 423, 4.5, 156, 1},
            {"森海塞尔GSP 600", 3L, "高端游戏耳机", 1299.00, 1599.00, 50, "/images/headset/5.jpg", "开放式设计,HiFi音质,轻量化", 189, 4.9, 67, 1},

            // 手柄 (category_id = 4)
            {"Xbox Series X手柄", 4L, "微软官方手柄", 499.00, 599.00, 200, "/images/controller/1.jpg", "无线连接,触觉反馈,3.5mm接口", 678, 4.8, 234, 1},
            {"PlayStation 5 DualSense", 4L, "索尼PS5手柄", 599.00, 699.00, 180, "/images/controller/2.jpg", "触觉反馈,自适应扳机,内置麦克风", 567, 4.9, 189, 1},
            {"任天堂Switch Pro", 4L, "Switch专业手柄", 449.00, 549.00, 150, "/images/controller/3.jpg", "无线连接,HD震动,长续航", 456, 4.7, 145, 1},
            {"八位堂8BitDo Pro 2", 4L, "通用游戏手柄", 299.00, 399.00, 250, "/images/controller/4.jpg", "多平台兼容,可编程按键,RGB灯", 345, 4.6, 123, 1},
            {"飞智VX2 Quest", 4L, "VR游戏手柄", 399.00, 499.00, 100, "/images/controller/5.jpg", "VR专用,体感控制,低延迟", 234, 4.5, 89, 1}
        };

        int successCount = 0;
        for (Object[] product : products) {
            try {
                jdbcTemplate.update(
                    "INSERT INTO products (name, category_id, description, price, original_price, stock, image, features, sales, rating, review_count, status, create_time, update_time) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                    product[0], product[1], product[2], product[3], product[4], product[5], 
                    product[6], product[7], product[8], product[9], product[10], product[11],
                    LocalDateTime.now(), LocalDateTime.now()
                );
                successCount++;
            } catch (Exception e) {
                System.err.println("插入商品失败: " + product[0] + ", 错误: " + e.getMessage());
            }
        }

        System.out.println("成功初始化 " + successCount + " 条商品数据");
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