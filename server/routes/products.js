/**
 * 商品管理相关API路由
 */
const express = require('express');
const mysql = require('mysql2/promise');
const router = express.Router();

// 引入统一配置
const config = require('../config');

// 使用配置中的数据库配置
const dbConfig = {
  host: config.database.host,
  user: config.database.user,
  password: config.database.password,
  database: config.database.database
};

// 创建数据库连接池
const pool = mysql.createPool(dbConfig);

// 初始化商品表（如果不存在）
const initProductsTable = async () => {
  try {
    await pool.query(`
      CREATE TABLE IF NOT EXISTS products (
        id INT PRIMARY KEY AUTO_INCREMENT,
        name VARCHAR(255) NOT NULL,
        category VARCHAR(50) NOT NULL,
        description TEXT,
        price DECIMAL(10, 2) NOT NULL,
        stock INT DEFAULT 0,
        image VARCHAR(500),
        features VARCHAR(500),
        sales INT DEFAULT 0,
        status VARCHAR(20) DEFAULT 'active',
        create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
        update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
      )
    `);
    
    // 检查是否有初始数据
    const [rows] = await pool.query('SELECT COUNT(*) as count FROM products');
    if (rows[0].count === 0) {
      // 插入初始商品数据
      const initialProducts = [
        // 鼠标
        ['星火RGB电竞鼠标', 'mouse', '16000DPI光学传感器，RGB炫彩灯效，轻量化设计', 299, 50, '/images/mouse/1.jpg', '16000 DPI,RGB灯效,58克'],
        ['星火无线游戏鼠标', 'mouse', '低延迟无线连接，超长续航72小时', 399, 30, '/images/mouse/2.jpg', '无线连接,72小时续航'],
        ['星火轻量化鼠标', 'mouse', '58克超轻设计，FPS神器', 349, 40, '/images/mouse/3.jpg', '58克,PMW3389'],
        ['星火有线电竞鼠标', 'mouse', '1ms响应时间，职业选手选择', 199, 60, '/images/mouse/5.jpg', '1ms响应,欧姆龙微动'],
        // 键盘
        ['星火青轴机械键盘', 'keyboard', '经典青轴，段落感强，RGB背光', 399, 35, '/images/keyboard/1.jpg', '青轴,RGB背光'],
        ['星火红轴机械键盘', 'keyboard', '线性手感，快速触发，热插拔', 449, 25, '/images/keyboard/2.jpg', '红轴,热插拔'],
        ['星火87键机械键盘', 'keyboard', '紧凑布局，节省桌面空间', 349, 30, '/images/keyboard/4.jpg', '87键,紧凑'],
        // 耳机
        ['星火7.1游戏耳机', 'headset', '虚拟7.1环绕声，听声辨位，降噪麦克风', 299, 45, '/images/headset/1.jpg', '7.1声道,降噪麦'],
        ['星火无线游戏耳机', 'headset', '低延迟无线，震撼低音', 499, 20, '/images/headset/2.jpg', '无线,2.4G'],
        ['星火专业游戏耳机', 'headset', '50mm大单元，舒适佩戴', 399, 30, '/images/headset/3.jpg', '50mm单元,舒适佩戴'],
        // 手柄
        ['星火无线游戏手柄', 'controller', '多平台兼容，震动反馈', 299, 40, '/images/controller/1.jpg', '无线,双震动'],
        ['星火有线游戏手柄', 'controller', '0延迟，竞技首选', 199, 50, '/images/controller/2.jpg', '有线,0延迟'],
        ['星火精英游戏手柄', 'controller', '自定义宏，4背键', 499, 15, '/images/controller/3.jpg', '4背键,自定义']
      ];
      
      for (const p of initialProducts) {
        await pool.query(
          'INSERT INTO products (name, category, description, price, stock, image, features) VALUES (?, ?, ?, ?, ?, ?, ?)',
          p
        );
      }
    }
  } catch (err) {
    console.error('初始化商品表失败:', err);
  }
};

// 初始化调用
initProductsTable();

// 获取商品列表
router.get('/products', async (req, res) => {
  try {
    const { category, status, keyword } = req.query;
    let sql = 'SELECT * FROM products WHERE 1=1';
    const params = [];
    
    if (category && category !== 'all') {
      sql += ' AND category = ?';
      params.push(category);
    }
    if (status) {
      sql += ' AND status = ?';
      params.push(status);
    }
    if (keyword) {
      sql += ' AND (name LIKE ? OR description LIKE ?)';
      params.push(`%${keyword}%`, `%${keyword}%`);
    }
    
    sql += ' ORDER BY create_time DESC';
    
    const [rows] = await pool.query(sql, params);
    return res.json({ success: true, products: rows });
  } catch (err) {
    return res.json({ success: false, message: '获取商品列表失败' });
  }
});

// 获取单个商品
router.get('/products/:id', async (req, res) => {
  try {
    const { id } = req.params;
    const [rows] = await pool.query('SELECT * FROM products WHERE id = ?', [id]);
    
    if (rows.length === 0) {
      return res.json({ success: false, message: '商品不存在' });
    }
    
    return res.json({ success: true, product: rows[0] });
  } catch (err) {
    return res.json({ success: false, message: '获取商品信息失败' });
  }
});

// 添加商品
router.post('/products', async (req, res) => {
  const { name, category, description, price, stock, image, features } = req.body;
  
  if (!name || !category || !price) {
    return res.json({ success: false, message: '请填写必要的商品信息' });
  }
  
  try {
    const [result] = await pool.query(
      'INSERT INTO products (name, category, description, price, stock, image, features) VALUES (?, ?, ?, ?, ?, ?, ?)',
      [name, category, description || '', price, stock || 0, image || '', features || '']
    );
    
    return res.json({ success: true, message: '商品添加成功', productId: result.insertId });
  } catch (err) {
    return res.json({ success: false, message: '添加商品失败' });
  }
});

// 更新商品
router.put('/products/:id', async (req, res) => {
  const { id } = req.params;
  const { name, category, description, price, stock, image, features, status } = req.body;
  
  try {
    await pool.query(
      'UPDATE products SET name = ?, category = ?, description = ?, price = ?, stock = ?, image = ?, features = ?, status = ? WHERE id = ?',
      [name, category, description, price, stock, image, features, status, id]
    );
    
    return res.json({ success: true, message: '商品更新成功' });
  } catch (err) {
    return res.json({ success: false, message: '更新商品失败' });
  }
});

// 删除商品
router.delete('/products/:id', async (req, res) => {
  const { id } = req.params;
  
  try {
    await pool.query('DELETE FROM products WHERE id = ?', [id]);
    return res.json({ success: true, message: '商品删除成功' });
  } catch (err) {
    return res.json({ success: false, message: '删除商品失败' });
  }
});

// 获取销售统计
router.get('/sales/stats', async (req, res) => {
  try {
    // 总销售额（模拟）
    const [products] = await pool.query('SELECT SUM(price * sales) as totalSales, SUM(sales) as totalOrders FROM products');
    const [categoryStats] = await pool.query('SELECT category, SUM(sales) as sales, COUNT(*) as count FROM products GROUP BY category');
    const [lowStock] = await pool.query('SELECT * FROM products WHERE stock < 10 ORDER BY stock ASC LIMIT 10');
    
    // 模拟订单数据
    const todayOrders = Math.floor(Math.random() * 50) + 10;
    const todaySales = Math.floor(Math.random() * 5000) + 1000;
    
    return res.json({
      success: true,
      stats: {
        totalSales: products[0].totalSales || 0,
        totalOrders: products[0].totalOrders || 0,
        todayOrders,
        todaySales,
        categoryStats,
        lowStockProducts: lowStock
      }
    });
  } catch (err) {
    return res.json({ success: false, message: '获取销售统计失败' });
  }
});

// 更新库存
router.put('/products/:id/stock', async (req, res) => {
  const { id } = req.params;
  const { stock } = req.body;
  
  if (stock === undefined) {
    return res.json({ success: false, message: '请提供库存数量' });
  }
  
  try {
    await pool.query('UPDATE products SET stock = ? WHERE id = ?', [stock, id]);
    return res.json({ success: true, message: '库存更新成功' });
  } catch (err) {
    return res.json({ success: false, message: '更新库存失败' });
  }
});

// 更新销量
router.put('/products/:id/sales', async (req, res) => {
  const { id } = req.params;
  const { quantity } = req.body;
  
  if (!quantity) {
    return res.json({ success: false, message: '请提供销量数量' });
  }
  
  try {
    await pool.query('UPDATE products SET sales = sales + ? WHERE id = ?', [quantity, id]);
    return res.json({ success: true, message: '销量更新成功' });
  } catch (err) {
    return res.json({ success: false, message: '更新销量失败' });
  }
});

module.exports = router;
