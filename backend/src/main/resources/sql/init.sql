-- ============================================
-- 星火商城 - 完整数据库初始化脚本
-- 版本: 1.0
-- 兼容性: MySQL 5.7+
-- ============================================

-- 设置客户端字符集为 utf8mb4，支持 emoji 表情符号
SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;
SET CHARACTER_SET_CLIENT = utf8mb4;
SET CHARACTER_SET_RESULTS = utf8mb4;
SET CHARACTER_SET_CONNECTION = utf8mb4;

-- 创建数据库
CREATE DATABASE IF NOT EXISTS starfire_mall DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE starfire_mall;

-- ============================================
-- 公共数据表（所有用户可见）
-- ============================================

-- 商品分类表
CREATE TABLE IF NOT EXISTS categories (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL COMMENT '分类名称',
    slug VARCHAR(50) NOT NULL UNIQUE COMMENT '分类标识',
    icon VARCHAR(100) DEFAULT NULL COMMENT '分类图标',
    sort_order INT DEFAULT 0 COMMENT '排序',
    status TINYINT DEFAULT 1 COMMENT '状态: 1启用 0禁用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_slug (slug),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 商品表（公开）
CREATE TABLE IF NOT EXISTS products (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(200) NOT NULL COMMENT '商品名称',
    category_id BIGINT NOT NULL COMMENT '分类ID',
    description TEXT COMMENT '商品描述',
    price DECIMAL(10,2) NOT NULL COMMENT '售价',
    original_price DECIMAL(10,2) DEFAULT NULL COMMENT '原价',
    stock INT DEFAULT 0 COMMENT '库存数量',
    image VARCHAR(500) NOT NULL COMMENT '主图片',
    images TEXT COMMENT '多图片，逗号分隔',
    features VARCHAR(500) DEFAULT NULL COMMENT '特点',
    sales INT DEFAULT 0 COMMENT '销量',
    rating DECIMAL(3,2) DEFAULT 5.00 COMMENT '评分',
    review_count INT DEFAULT 0 COMMENT '评论数',
    status TINYINT DEFAULT 1 COMMENT '状态: 1上架 0下架',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_category_id (category_id),
    INDEX idx_status (status),
    INDEX idx_price (price),
    INDEX idx_sales (sales)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 商品评论表（公开）
CREATE TABLE IF NOT EXISTS product_reviews (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    product_id BIGINT NOT NULL COMMENT '商品ID',
    username VARCHAR(50) NOT NULL COMMENT '评论用户',
    rating INT DEFAULT 5 COMMENT '评分1-5',
    content TEXT COMMENT '评论内容',
    images TEXT COMMENT '评论图片，逗号分隔',
    status TINYINT DEFAULT 1 COMMENT '状态: 1显示 0隐藏',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_product_id (product_id),
    INDEX idx_username (username),
    INDEX idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================
-- 用户相关表（私密）
-- ============================================

-- 用户表
CREATE TABLE IF NOT EXISTS users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(255) NOT NULL COMMENT '密码',
    nickname VARCHAR(50) DEFAULT NULL COMMENT '昵称',
    email VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
    phone VARCHAR(20) DEFAULT NULL COMMENT '手机',
    avatar LONGTEXT DEFAULT NULL COMMENT '头像（支持 Base64）',
    role VARCHAR(20) DEFAULT 'user' COMMENT '角色: user普通用户 admin管理员',
    admin_status VARCHAR(20) DEFAULT 'none' COMMENT 'admin申请状态: none未申请 pending待审批 approved已批准 rejected已拒绝',
    admin_request_time DATETIME DEFAULT NULL COMMENT '申请时间',
    status TINYINT DEFAULT 1 COMMENT '账号状态: 1正常 0禁用',
    last_login_time DATETIME DEFAULT NULL COMMENT '最后登录时间',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_username (username),
    INDEX idx_role (role),
    INDEX idx_status (status),
    INDEX idx_admin_status (admin_status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 用户收货地址表（私有）
CREATE TABLE IF NOT EXISTS addresses (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    receiver_name VARCHAR(50) NOT NULL COMMENT '收货人',
    phone VARCHAR(20) NOT NULL COMMENT '联系电话',
    province VARCHAR(50) NOT NULL COMMENT '省份',
    city VARCHAR(50) NOT NULL COMMENT '城市',
    district VARCHAR(50) NOT NULL COMMENT '区县',
    detail_address VARCHAR(200) NOT NULL COMMENT '详细地址',
    is_default TINYINT DEFAULT 0 COMMENT '是否默认: 1是 0否',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================
-- 交易相关表（私有+管理可见）
-- ============================================

-- 购物车表（私有）
CREATE TABLE IF NOT EXISTS carts (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    product_id BIGINT NOT NULL COMMENT '商品ID',
    quantity INT NOT NULL DEFAULT 1 COMMENT '数量',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_user_product (user_id, product_id),
    INDEX idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 订单表（包含逻辑删除字段）
CREATE TABLE IF NOT EXISTS orders (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_no VARCHAR(50) NOT NULL UNIQUE COMMENT '订单号',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    username VARCHAR(50) NOT NULL COMMENT '用户名',
    receiver_name VARCHAR(50) NOT NULL COMMENT '收货人',
    receiver_phone VARCHAR(20) NOT NULL COMMENT '联系电话',
    receiver_address VARCHAR(300) NOT NULL COMMENT '收货地址',
    total_amount DECIMAL(10,2) NOT NULL COMMENT '订单总额',
    discount_amount DECIMAL(10,2) DEFAULT 0.00 COMMENT '优惠金额',
    pay_amount DECIMAL(10,2) NOT NULL COMMENT '实付金额',
    pay_method VARCHAR(20) DEFAULT 'online' COMMENT '支付方式: online在线支付 COD货到付款',
    pay_time DATETIME DEFAULT NULL COMMENT '支付时间',
    status VARCHAR(20) DEFAULT 'pending' COMMENT '订单状态: pending待支付 paid已支付 shipping待发货 shipped已发货 completed已完成 cancelled已取消',
    remark VARCHAR(500) DEFAULT NULL COMMENT '订单备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted INT DEFAULT 0 COMMENT '逻辑删除: 0未删除 1已删除',
    INDEX idx_order_no (order_no),
    INDEX idx_user_id (user_id),
    INDEX idx_status (status),
    INDEX idx_create_time (create_time),
    INDEX idx_deleted (deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 订单明细表（包含逻辑删除字段）
CREATE TABLE IF NOT EXISTS order_items (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_id BIGINT NOT NULL COMMENT '订单ID',
    product_id BIGINT NOT NULL COMMENT '商品ID',
    product_name VARCHAR(200) NOT NULL COMMENT '商品名称',
    product_image VARCHAR(500) NOT NULL COMMENT '商品图片',
    price DECIMAL(10,2) NOT NULL COMMENT '单价',
    quantity INT NOT NULL DEFAULT 1 COMMENT '数量',
    subtotal DECIMAL(10,2) NOT NULL COMMENT '小计',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    deleted INT DEFAULT 0 COMMENT '逻辑删除: 0未删除 1已删除',
    INDEX idx_order_id (order_id),
    INDEX idx_product_id (product_id),
    INDEX idx_deleted (deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================
-- 客服相关表
-- ============================================

-- 客服消息表
CREATE TABLE IF NOT EXISTS customer_messages (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL COMMENT '用户',
    content TEXT NOT NULL COMMENT '消息内容',
    is_from_admin TINYINT DEFAULT 0 COMMENT '0用户发送 1管理员回复',
    is_read TINYINT DEFAULT 0 COMMENT '0未读 1已读',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_username (username),
    INDEX idx_is_read (is_read),
    INDEX idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 联系留言表
CREATE TABLE IF NOT EXISTS contact_messages (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    name VARCHAR(100) NOT NULL COMMENT '留言人姓名',
    email VARCHAR(255) COMMENT '电子邮箱',
    phone VARCHAR(20) COMMENT '联系电话',
    type VARCHAR(50) COMMENT '留言类型：product-产品咨询，order-订单问题，aftersale-售后服务，cooperation-商务合作，other-其他',
    message TEXT NOT NULL COMMENT '留言内容',
    reply TEXT COMMENT '回复内容',
    status TINYINT DEFAULT 0 COMMENT '状态：0-待处理，1-已处理',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    INDEX idx_status (status),
    INDEX idx_type (type),
    INDEX idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='联系留言表';

-- ============================================
-- 初始化数据
-- ============================================

-- 插入默认管理员 (密码: admin123)
INSERT INTO users (username, password, role, admin_status, status) 
VALUES ('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt.RNxu', 'admin', 'approved', 1)
ON DUPLICATE KEY UPDATE role = 'admin', admin_status = 'approved';

-- 插入分类
INSERT INTO categories (name, slug, icon, sort_order, status) VALUES
('电竞鼠标', 'mouse', '🐭', 1, 1),
('机械键盘', 'keyboard', '⌨️', 2, 1),
('游戏耳机', 'headset', '🎧', 3, 1),
('游戏手柄', 'controller', '🎮', 4, 1);

-- 插入商品（使用分类ID）
INSERT INTO products (name, category_id, description, price, original_price, stock, image, features, sales, rating, review_count, status) VALUES
-- 鼠标 (category_id = 1)
('雷蛇蝰蛇V3专业版', 1, '高性能无线游戏鼠标，支持30000 DPI传感器', 899.00, 1299.00, 100, '/images/mouse/1.jpg', '无线连接,30000 DPI,轻量设计', 520, 4.8, 128, 1),
('罗技G Pro X Superlight', 1, '轻量化无线鼠标，仅重63克', 799.00, 999.00, 150, '/images/mouse/2.jpg', '无线连接,63克轻量,HERO 25K传感器', 888, 4.9, 256, 1),
('赛睿Rival 3', 1, '精准电竞鼠标，8500 DPI传感器', 299.00, 399.00, 200, '/images/mouse/3.jpg', '有线连接,8500 DPI,RGB灯效', 320, 4.5, 89, 1),
('雷蛇巴塞利斯蛇V3', 1, '专业FPS游戏鼠标', 599.00, 799.00, 80, '/images/mouse/4.jpg', '有线连接,26000 DPI,人体工学', 156, 4.7, 45, 1),
('华硕ROG龙鳞', 1, '轻量化游戏鼠标', 449.00, 599.00, 120, '/images/mouse/5.jpg', '无线连接,58克,RGB灯效', 203, 4.6, 67, 1),

-- 键盘 (category_id = 2)
('雷蛇黑寡妇蜘蛛V4', 2, '机械键盘，支持客制化轴体', 1299.00, 1599.00, 80, '/images/keyboard/1.jpg', '机械轴,RGB灯效,客制化', 256, 4.7, 78, 1),
('罗技G913 TKL', 2, '无线机械键盘，矮轴设计', 1599.00, 1999.00, 60, '/images/keyboard/2.jpg', '无线连接,矮轴,RGB灯效', 198, 4.8, 56, 1),
('樱桃MX8.2 TKL', 2, '德国原厂轴体，品质保证', 1099.00, 1399.00, 100, '/images/keyboard/3.jpg', '机械轴,德国樱桃,RGB', 145, 4.9, 42, 1),
('海盗船K70 RGB', 2, '游戏机械键盘', 899.00, 1199.00, 90, '/images/keyboard/4.jpg', '机械轴,RGB,掌托', 178, 4.6, 51, 1),
('阿斯加特AK600', 2, '性价比机械键盘', 299.00, 399.00, 200, '/images/keyboard/5.jpg', '机械轴,有线,白色灯效', 234, 4.4, 89, 1),

-- 耳机 (category_id = 3)
('索尼WH-1000XM5', 3, '顶级降噪耳机，30小时续航', 2799.00, 3299.00, 50, '/images/headset/1.jpg', '主动降噪,30小时续航,Hi-Res认证', 368, 4.9, 124, 1),
('罗技G Pro X', 3, '专业游戏耳机，支持Blue VO!CE', 899.00, 1099.00, 120, '/images/headset/2.jpg', '游戏耳机,Blue VO!CE,7.1环绕声', 456, 4.7, 156, 1),
('雷蛇影鲛终极版', 3, '无线游戏耳机，THX空间音效', 1499.00, 1799.00, 70, '/images/headset/3.jpg', '无线连接,THX音效,触觉反馈', 189, 4.6, 67, 1),
('赛睿寒冰Nova', 3, '入门级游戏耳机', 299.00, 399.00, 180, '/images/headset/4.jpg', '游戏耳机,7.1环绕声,轻量化', 245, 4.5, 98, 1),
('森海塞尔GSP 600', 3, '专业游戏耳机', 1599.00, 1899.00, 40, '/images/headset/5.jpg', '游戏耳机,Hi-Fi,降噪麦克风', 87, 4.8, 34, 1),

-- 手柄 (category_id = 4)
('Xbox Series X手柄', 4, '下一代游戏手柄，精准控制', 499.00, 599.00, 200, '/images/controller/1.jpg', '无线连接,自适应扳机,分享按钮', 678, 4.8, 234, 1),
('PlayStation DualSense', 4, 'PS5手柄，沉浸式触觉反馈', 529.00, 629.00, 150, '/images/controller/2.jpg', '触觉反馈,自适应扳机,内置麦克风', 523, 4.9, 189, 1),
('任天堂Switch Pro手柄', 4, 'Switch专用手柄，舒适握感', 380.00, 450.00, 100, '/images/controller/3.jpg', '无线连接,体感控制,NFC', 234, 4.7, 87, 1),
('雷蛇幻影战狼', 4, '有线游戏手柄', 299.00, 399.00, 150, '/images/controller/4.jpg', '有线连接,宏按键,RGB', 156, 4.5, 56, 1),
('八位堂SN30 Pro', 4, '复古手柄，支持多平台', 198.00, 248.00, 120, '/images/controller/5.jpg', '蓝牙连接,复古设计,体感', 89, 4.4, 34, 1);

-- 插入示例评论
INSERT INTO product_reviews (product_id, username, rating, content, status) VALUES
(1, '游戏玩家A', 5, 'dpi很高，玩游戏特别顺手，电池续航也很给力！', 1),
(1, '电竞选手B', 4, '鼠标很好用，就是价格有点贵', 1),
(2, '科技爱好者C', 5, '罗技的品质没话说，轻量化设计太棒了', 1),
(3, '预算党D', 4, '性价比很高，这个价位很值', 1),
(6, '键盘发烧友E', 5, '青轴手感超级棒，RGB效果也很炫', 1),
(7, '无线党F', 5, '无线延迟几乎为零，矮轴手感很舒服', 1),
(11, '音乐达人G', 5, '降噪效果一流，音质也非常好', 1),
(12, '游戏玩家H', 4, '麦克风效果不错，7.1环绕声很棒', 1),
(16, '主机玩家I', 5, '手感完美，自适应扳机体验超棒', 1),
(17, '索粉J', 5, 'PS5必备，触觉反馈太真实了', 1);

-- 更新商品评论数
UPDATE products p SET review_count = (SELECT COUNT(*) FROM product_reviews WHERE product_id = p.id AND status = 1);

-- ============================================
-- 数据库初始化完成
-- ============================================
SELECT '数据库初始化完成！' AS message;
SELECT '默认管理员账号: admin / admin123' AS admin_info;
SELECT '数据库版本: 1.0' AS version;