const express = require('express');
const router = express.Router();
const mysql = require('mysql2');

// 创建共享的数据库连接池 - 只创建一次
let sharedPool = null;

const getDbPool = () => {
  if (sharedPool) return sharedPool;
  
  const config = require('../config');
  sharedPool = mysql.createPool({
    host: config.database.host,
    user: config.database.user,
    password: config.database.password,
    database: config.database.database,
    waitForConnections: true,
    connectionLimit: 10,
    queueLimit: 0
  }).promise();
  
  return sharedPool;
};

// 确保客服消息表存在
const ensureTableExists = async (pool) => {
  const createTableSQL = `
    CREATE TABLE IF NOT EXISTS customer_messages (
      id INT AUTO_INCREMENT PRIMARY KEY,
      username VARCHAR(50) NOT NULL,
      content TEXT NOT NULL,
      is_from_admin TINYINT DEFAULT 0,
      is_read TINYINT DEFAULT 0,
      create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
      INDEX idx_username (username),
      INDEX idx_create_time (create_time),
      INDEX idx_is_read (is_read)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
  `;
  
  await pool.query(createTableSQL);
};

// 获取用户客服消息
router.get('/customer/messages', async (req, res) => {
  const { username } = req.query;
  
  if (!username) {
    return res.json({ success: false, message: '缺少用户名' });
  }
  
  const pool = getDbPool();
  
  try {
    await ensureTableExists(pool);
    
    const sql = 'SELECT * FROM customer_messages WHERE username = ? ORDER BY create_time ASC';
    const [rows] = await pool.query(sql, [username]);
    
    const messages = rows.map(row => ({
      id: row.id,
      content: row.content,
      time: new Date(row.create_time).toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' }),
      isMine: !row.is_from_admin
    }));
    
    res.json({ success: true, messages: messages || [] });
  } catch (err) {
    console.error('获取消息失败:', err.message);
    res.json({ success: false, message: err.message, messages: [] });
  }
});

// 发送客服消息
router.post('/customer/send', async (req, res) => {
  const { username, content } = req.body;
  
  if (!username || !content) {
    return res.json({ success: false, message: '缺少必要参数' });
  }
  
  const pool = getDbPool();
  
  try {
    await ensureTableExists(pool);
    
    const sql = 'INSERT INTO customer_messages (username, content, is_from_admin) VALUES (?, ?, 0)';
    await pool.query(sql, [username, content]);
    
    res.json({ success: true, message: '发送成功' });
  } catch (err) {
    console.error('发送消息失败:', err.message);
    res.json({ success: false, message: err.message });
  }
});

// 获取所有客服消息（管理员用）- 按用户分组
router.get('/customer/all-messages', async (req, res) => {
  const pool = getDbPool();
  
  try {
    await ensureTableExists(pool);
    
    // 获取每个用户的最新消息和未读数
    const sql = `
      SELECT 
        username,
        MAX(create_time) as last_time,
        SUM(CASE WHEN is_read = 0 AND is_from_admin = 0 THEN 1 ELSE 0 END) as unread_count,
        (SELECT content FROM customer_messages WHERE username = m.username ORDER BY create_time DESC LIMIT 1) as last_message
      FROM customer_messages m
      GROUP BY username
      ORDER BY last_time DESC
    `;
    
    const [rows] = await pool.query(sql);
    
    const conversations = rows.map(row => ({
      username: row.username,
      lastMessage: row.last_message || '',
      lastTime: row.last_time,
      unreadCount: row.unread_count || 0
    }));
    
    res.json({ success: true, conversations: conversations || [] });
  } catch (err) {
    console.error('获取所有消息失败:', err.message);
    res.json({ success: false, message: err.message, conversations: [] });
  }
});

// 获取指定用户的详细消息
router.get('/customer/conversation/:username', async (req, res) => {
  const { username } = req.params;
  
  const pool = getDbPool();
  
  try {
    await ensureTableExists(pool);
    
    const sql = 'SELECT * FROM customer_messages WHERE username = ? ORDER BY create_time ASC';
    const [rows] = await pool.query(sql, [username]);
    
    const messages = rows.map(row => ({
      id: row.id,
      content: row.content,
      time: new Date(row.create_time).toLocaleString('zh-CN'),
      isMine: !row.is_from_admin,
      isFromAdmin: row.is_from_admin
    }));
    
    res.json({ success: true, messages: messages || [] });
  } catch (err) {
    console.error('获取对话失败:', err.message);
    res.json({ success: false, message: err.message, messages: [] });
  }
});

// 标记消息为已读
router.post('/customer/mark-read', async (req, res) => {
  const { username } = req.body;
  
  if (!username) {
    return res.json({ success: false, message: '缺少用户名' });
  }
  
  const pool = getDbPool();
  
  try {
    await ensureTableExists(pool);
    
    const sql = 'UPDATE customer_messages SET is_read = 1 WHERE username = ? AND is_from_admin = 0';
    await pool.query(sql, [username]);
    
    res.json({ success: true });
  } catch (err) {
    console.error('标记已读失败:', err.message);
    res.json({ success: false, message: err.message });
  }
});

// 发送客服回复（管理员用）
router.post('/customer/reply', async (req, res) => {
  const { username, content } = req.body;
  
  if (!username || !content) {
    return res.json({ success: false, message: '缺少必要参数' });
  }
  
  const pool = getDbPool();
  
  try {
    await ensureTableExists(pool);
    
    const sql = 'INSERT INTO customer_messages (username, content, is_from_admin, is_read) VALUES (?, ?, 1, 1)';
    await pool.query(sql, [username, content]);
    
    res.json({ success: true, message: '回复成功' });
  } catch (err) {
    console.error('发送回复失败:', err.message);
    res.json({ success: false, message: err.message });
  }
});

module.exports = router;
