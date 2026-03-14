-- 星火游戏商城数据库初始化脚本

-- 创建数据库
CREATE DATABASE IF NOT EXISTS starfire_mall DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE starfire_mall;

-- 用户表
CREATE TABLE IF NOT EXISTS users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    nickname VARCHAR(50) DEFAULT NULL,
    email VARCHAR(100) DEFAULT NULL,
    phone VARCHAR(20) DEFAULT NULL,
    avatar TEXT DEFAULT NULL,
    status TINYINT DEFAULT 1 COMMENT '1:正常 0:禁用',
    role VARCHAR(20) DEFAULT 'user' COMMENT 'user:普通用户 admin:管理员',
    admin_status VARCHAR(20) DEFAULT 'none' COMMENT 'none:未申请 pending:待审批 approved:已批准 rejected:已拒绝',
    admin_request_time DATETIME DEFAULT NULL,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_username (username),
    INDEX idx_status (status),
    INDEX idx_role (role),
    INDEX idx_admin_status (admin_status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 商品表
CREATE TABLE IF NOT EXISTS products (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(200) NOT NULL,
    category VARCHAR(50) NOT NULL COMMENT 'mouse:鼠标 keyboard:键盘 headset:耳机 controller:手柄',
    description TEXT,
    price DECIMAL(10, 2) NOT NULL,
    stock INT DEFAULT 0,
    image TEXT,
    features TEXT COMMENT '特点，多个用逗号分隔',
    sales INT DEFAULT 0 COMMENT '销量',
    status TINYINT DEFAULT 1 COMMENT '1:上架 0:下架',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_category (category),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 客服消息表
CREATE TABLE IF NOT EXISTS customer_messages (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL,
    content TEXT NOT NULL,
    is_from_admin TINYINT DEFAULT 0 COMMENT '0:用户发送 1:管理员回复',
    is_read TINYINT DEFAULT 0 COMMENT '0:未读 1:已读',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_username (username),
    INDEX idx_is_read (is_read)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 产品评论表
CREATE TABLE IF NOT EXISTS product_reviews (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    product_id BIGINT NOT NULL COMMENT '商品ID',
    username VARCHAR(50) NOT NULL COMMENT '评论用户',
    rating INT DEFAULT 5 COMMENT '评分1-5',
    content TEXT COMMENT '评论内容',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_product_id (product_id),
    INDEX idx_username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 订单表
CREATE TABLE IF NOT EXISTS orders (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_no VARCHAR(50) NOT NULL UNIQUE COMMENT '订单号',
    username VARCHAR(50) NOT NULL COMMENT '用户名',
    total_amount DECIMAL(10, 2) NOT NULL DEFAULT 0 COMMENT '订单总金额',
    status VARCHAR(20) DEFAULT 'pending' COMMENT '订单状态: pending-待支付, paid-已支付, shipped-已发货, completed-已完成, cancelled-已取消',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_username (username),
    INDEX idx_order_no (order_no),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 订单明细表
CREATE TABLE IF NOT EXISTS order_items (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_id BIGINT NOT NULL COMMENT '订单ID',
    product_id BIGINT NOT NULL COMMENT '商品ID',
    product_name VARCHAR(200) NOT NULL COMMENT '商品名称',
    price DECIMAL(10, 2) NOT NULL COMMENT '购买单价',
    quantity INT NOT NULL DEFAULT 1 COMMENT '购买数量',
    subtotal DECIMAL(10, 2) NOT NULL COMMENT '小计',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_order_id (order_id),
    INDEX idx_product_id (product_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 清理重复产品（保留ID较小的）
DELETE FROM products WHERE id IN (
    SELECT id FROM (
        SELECT id FROM products 
        WHERE (name, category) IN (
            SELECT name, category FROM products GROUP BY name, category HAVING COUNT(*) > 1
        ) AND id NOT IN (
            SELECT MIN(id) FROM products GROUP BY name, category HAVING COUNT(*) > 1
        )
    ) AS duplicates
);

-- 插入默认管理员账号 (密码请在 application.yml 中配置)
INSERT INTO users (username, password, role, admin_status) 
VALUES ('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt.RNxu', 'admin', 'approved')
ON DUPLICATE KEY UPDATE role = 'admin', admin_status = 'approved';

-- 插入示例商品数据
INSERT INTO products (name, category, description, price, stock, image, features, sales, status) VALUES
('雷蛇蝰蛇V3专业版', 'mouse', '高性能无线游戏鼠标，支持30000 DPI传感器', 899.00, 100, '/images/mouse/1.jpg', '无线连接,30000 DPI,轻量设计', 520, 1),
('罗技G Pro X Superlight', 'mouse', '轻量化无线鼠标，仅重63克', 799.00, 150, '/images/mouse/2.jpg', '无线连接,63克轻量,HERO 25K传感器', 888, 1),
('赛睿Rival 3', 'mouse', '精准电竞鼠标，8500 DPI传感器', 299.00, 200, '/images/mouse/3.jpg', '有线连接,8500 DPI,RGB灯效', 320, 1),
('雷蛇黑寡妇蜘蛛V4', 'keyboard', '机械键盘，支持客制化轴体', 1299.00, 80, '/images/keyboard/1.jpg', '机械轴,RGB灯效,客制化', 256, 1),
('罗技G913 TKL', 'keyboard', '无线机械键盘，矮轴设计', 1599.00, 60, '/images/keyboard/2.jpg', '无线连接,矮轴,RGB灯效', 198, 1),
('樱桃MX8.2 TKL', 'keyboard', '德国原厂轴体，品质保证', 1099.00, 100, '/images/keyboard/3.jpg', '机械轴,德国樱桃,RGB', 145, 1),
('索尼WH-1000XM5', 'headset', '顶级降噪耳机，30小时续航', 2799.00, 50, '/images/headset/1.jpg', '主动降噪,30小时续航,Hi-Res认证', 368, 1),
('罗技G Pro X', 'headset', '专业游戏耳机，支持Blue VO!CE', 899.00, 120, '/images/headset/2.jpg', '游戏耳机,Blue VO!CE,7.1环绕声', 456, 1),
('雷蛇影鲛终极版', 'headset', '无线游戏耳机，THX空间音效', 1499.00, 70, '/images/headset/3.jpg', '无线连接,THX音效,触觉反馈', 189, 1),
('Xbox Series X手柄', 'controller', '下一代游戏手柄，精准控制', 499.00, 200, '/images/controller/1.jpg', '无线连接,自适应扳机,分享按钮', 678, 1),
('PlayStation DualSense', 'controller', 'PS5手柄，沉浸式触觉反馈', 529.00, 150, '/images/controller/2.jpg', '触觉反馈,自适应扳机,内置麦克风', 523, 1),
('任天堂Switch Pro手柄', 'controller', 'Switch专用手柄，舒适握感', 380.00, 100, '/images/controller/3.jpg', '无线连接,体感控制,NFC', 234, 1);
