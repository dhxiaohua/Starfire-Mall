-- 初始化商品数据
USE starfire_mall;

-- 检查是否已有商品数据
SELECT COUNT(*) INTO @product_count FROM products;

SET @product_count = 0;

-- 如果没有商品数据，则插入测试数据
INSERT INTO products (name, category_id, description, price, original_price, stock, image, features, sales, rating, review_count, status)
SELECT * FROM (
    SELECT
    -- 鼠标 (category_id = 1)
    ('雷蛇毒蝰V3专业版', 1, '高性能无线游戏鼠标，支持20000 DPI传感器', 899.00, 1299.00, 100, '/images/mouse/1.jpg', '无线连接,20000 DPI,轻量化设计', 520, 4.8, 128, 1) AS row_data
    UNION ALL
    SELECT ('罗技G Pro X Superlight', 1, '超轻量化无线鼠标，仅重63克', 799.00, 999.00, 150, '/images/mouse/2.jpg', '无线连接,63克轻便,HERO 25K传感器', 888, 4.9, 256, 1)
    UNION ALL
    SELECT ('赛睿Rival 3', 1, '精准电竞鼠标，8500 DPI传感器', 299.00, 399.00, 200, '/images/mouse/3.jpg', '有线连接,8500 DPI,RGB灯效', 320, 4.5, 89, 1)
    UNION ALL
    SELECT ('雷蛇曼巴眼镜蛇3', 1, '专业FPS游戏鼠标', 599.00, 799.00, 80, '/images/mouse/4.jpg', '有线连接,26000 DPI,人体工学', 156, 4.7, 45, 1)
    SELECT ('华硕ROG夜魔', 1, '轻量化游戏鼠标', 449.00, 599.00, 120, '/images/mouse/5.jpg', '无线连接,58克,RGB灯效', 203, 4.6, 67, 1)

    UNION ALL
    -- 键盘 (category_id = 2)
    SELECT ('雷蛇黑寡妇蜘蛛4', 2, '机械键盘，支持幻彩灯光', 1299.00, 1599.00, 80, '/images/keyboard/1.jpg', '机械轴,RGB灯效,多媒体键', 256, 4.7, 78, 1)
    SELECT ('罗技G913 TKL', 2, '无线机械键盘，紧凑设计', 1599.00, 1999.00, 60, '/images/keyboard/2.jpg', '无线连接,紧凑布局,RGB灯效', 198, 4.8, 56, 1)
    SELECT ('美商海盗船K70 RGB', 2, 'RGB机械键盘，Cherry MX轴', 1099.00, 1399.00, 90, '/images/keyboard/3.jpg', 'Cherry MX,RGB灯光,铝合金', 312, 4.6, 92, 1)
    SELECT ('樱桃MX Board 3.0', 2, '原厂机械键盘', 699.00, 899.00, 150, '/images/keyboard/4.jpg', 'Cherry MX,简约设计,耐摔', 189, 4.5, 67, 1)
    SELECT ('Keychron K2', 2, '无线机械键盘，适配Mac', 499.00, 699.00, 200, '/images/keyboard/5.jpg', '蓝牙/有线,Mac/Win兼容,RGB', 278, 4.7, 145, 1)

    UNION ALL
    -- 耳机 (category_id = 3)
    SELECT ('雷蛇北海巨妖V3', 3, '游戏耳机，7.1环绕声', 799.00, 999.00, 100, '/images/headset/1.jpg', '7.1声道,降噪麦克风,舒适佩戴', 345, 4.6, 89, 1)
    SELECT ('罗技G Pro X', 3, '专业电竞耳机', 999.00, 1299.00, 80, '/images/headset/2.jpg', 'Blue麦克风,50mm驱动,可换耳罩', 456, 4.8, 123, 1)
    SELECT ('赛睿Arctis 7', 3, '无线游戏耳机', 899.00, 1199.00, 70, '/images/headset/3.jpg', '无线连接,30小时续航,降噪', 267, 4.7, 98, 1)
    SELECT ('金士顿HyperX Cloud II', 3, '经典游戏耳机', 599.00, 799.00, 150, '/images/headset/4.jpg', '7.1声道,记忆海绵,耐用', 423, 4.5, 156, 1)
    SELECT ('森海塞尔GSP 600', 3, '高端游戏耳机', 1299.00, 1599.00, 50, '/images/headset/5.jpg', '开放式设计,HiFi音质,轻量化', 189, 4.9, 67, 1)

    UNION ALL
    -- 手柄 (category_id = 4)
    SELECT ('Xbox Series X手柄', 4, '微软官方手柄', 499.00, 599.00, 200, '/images/controller/1.jpg', '无线连接,触觉反馈,3.5mm接口', 678, 4.8, 234, 1)
    SELECT ('PlayStation 5 DualSense', 4, '索尼PS5手柄', 599.00, 699.00, 180, '/images/controller/2.jpg', '触觉反馈,自适应扳机,内置麦克风', 567, 4.9, 189, 1)
    SELECT ('任天堂Switch Pro', 4, 'Switch专业手柄', 449.00, 549.00, 150, '/images/controller/3.jpg', '无线连接,HD震动,长续航', 456, 4.7, 145, 1)
    SELECT ('八位堂8BitDo Pro 2', 4, '通用游戏手柄', 299.00, 399.00, 250, '/images/controller/4.jpg', '多平台兼容,可编程按键,RGB灯', 345, 4.6, 123, 1)
    SELECT ('飞智VX2 Quest', 4, 'VR游戏手柄', 399.00, 499.00, 100, '/images/controller/5.jpg', 'VR专用,体感控制,低延迟', 234, 4.5, 89, 1)
) AS data
WHERE (SELECT COUNT(*) FROM products) = 0;