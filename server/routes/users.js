/**
 * 用户相关API路由
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

// 检查账号是否已存在
router.post('/check-username', async (req, res) => {
  const { username } = req.body;
  
  if (!username) {
    return res.json({ success: false, message: '请输入账号' });
  }
  
  const usernameRegex = /^[a-zA-Z0-9]{4,16}$/;
  if (!usernameRegex.test(username)) {
    return res.json({ success: false, message: '账号只能包含字母和数字，4-16位' });
  }
  
  try {
    const [rows] = await pool.query('SELECT id FROM users WHERE username = ?', [username]);
    
    if (rows.length > 0) {
      return res.json({ success: false, message: '该账号已被注册' });
    }
    
    return res.json({ success: true, message: '账号可用', available: true });
  } catch (err) {
    return res.json({ success: false, message: '服务器错误' });
  }
});

// 用户注册
router.post('/register', async (req, res) => {
  const { username, password, captcha, captchaCode } = req.body;
  
  if (!username || !password || !captcha) {
    return res.json({ success: false, message: '请填写完整信息' });
  }
  
  const usernameRegex = /^[a-zA-Z0-9]{4,16}$/;
  if (!usernameRegex.test(username)) {
    return res.json({ success: false, message: '账号只能包含字母和数字，4-16位' });
  }
  
  if (password.length < 6 || password.length > 20) {
    return res.json({ success: false, message: '密码长度应为6-20位' });
  }
  
  if (captcha.toUpperCase() !== captchaCode) {
    return res.json({ success: false, message: '验证码错误' });
  }
  
  try {
    const [rows] = await pool.query('SELECT id FROM users WHERE username = ?', [username]);
    
    if (rows.length > 0) {
      return res.json({ success: false, message: '该账号已被注册' });
    }
    
    const [result] = await pool.query('INSERT INTO users (username, password) VALUES (?, ?)', [username, password]);
    
    return res.json({ success: true, message: '注册成功' });
  } catch (err) {
    return res.json({ success: false, message: '注册失败' });
  }
});

// 用户登录
router.post('/login', async (req, res) => {
  const { username, password, captcha, captchaCode } = req.body;
  
  if (!username || !password) {
    return res.json({ success: false, message: '请输入账号和密码' });
  }
  
  if (captcha.toUpperCase() !== captchaCode) {
    return res.json({ success: false, message: '验证码错误' });
  }
  
  try {
    const [rows] = await pool.query('SELECT * FROM users WHERE username = ? AND password = ?', [username, password]);
    
    if (rows.length === 0) {
      return res.json({ success: false, message: '账号或密码错误' });
    }
    
    const user = rows[0];
    return res.json({ 
      success: true, 
      message: '登录成功',
      user: {
        username: user.username,
        id: user.id,
        nickname: user.nickname || '',
        avatar: user.avatar || '',
        isAdmin: user.role === 'admin',
        adminStatus: user.admin_status || 'none'
      }
    });
  } catch (err) {
    return res.json({ success: false, message: '服务器错误' });
  }
});

// 修改密码
router.post('/change-password', async (req, res) => {
  const { username, newPassword, confirmPassword } = req.body;
  
  if (!username || !newPassword) {
    return res.json({ success: false, message: '请填写完整信息' });
  }
  
  if (newPassword !== confirmPassword) {
    return res.json({ success: false, message: '两次密码输入不一致' });
  }
  
  if (newPassword.length < 6 || newPassword.length > 20) {
    return res.json({ success: false, message: '密码长度应为6-20位' });
  }
  
  try {
    const [rows] = await pool.query('SELECT id FROM users WHERE username = ?', [username]);
    
    if (rows.length === 0) {
      return res.json({ success: false, message: '账号不存在' });
    }
    
    await pool.query('UPDATE users SET password = ? WHERE username = ?', [newPassword, username]);
    
    return res.json({ success: true, message: '密码修改成功' });
  } catch (err) {
    return res.json({ success: false, message: '修改密码失败' });
  }
});

// 获取用户信息
router.get('/user-info', async (req, res) => {
  const { username } = req.query;
  
  if (!username) {
    return res.json({ success: false, message: '请提供用户名' });
  }
  
  try {
    const [rows] = await pool.query('SELECT id, username, nickname, email, phone, avatar, status, create_time FROM users WHERE username = ?', [username]);
    
    if (rows.length === 0) {
      return res.json({ success: false, message: '用户不存在' });
    }
    
    const user = rows[0];
    return res.json({ 
      success: true, 
      user: {
        id: user.id,
        username: user.username,
        nickname: user.nickname || user.username,
        email: user.email || '',
        phone: user.phone || '',
        avatar: user.avatar || '',
        status: user.status,
        role: user.role || 'user',
        adminStatus: user.admin_status || 'none',
        createTime: user.create_time
      }
    });
  } catch (err) {
    return res.json({ success: false, message: '服务器错误' });
  }
});

// 更新用户信息
router.post('/update-user-info', async (req, res) => {
  const { username, nickname, email, phone, avatar } = req.body;
  
  if (!username) {
    return res.json({ success: false, message: '请提供用户名' });
  }
  
  try {
    const [result] = await pool.query('UPDATE users SET nickname = ?, email = ?, phone = ?, avatar = ? WHERE username = ?', [nickname || null, email || null, phone || null, avatar || null, username]);
    
    if (result.affectedRows > 0) {
      return res.json({ success: true, message: '信息更新成功' });
    } else {
      return res.json({ success: false, message: '用户不存在' });
    }
  } catch (err) {
    console.error('更新用户信息失败:', err.message);
    return res.json({ success: false, message: '更新失败: ' + err.message });
  }
});

// 修改密码（需要验证旧密码）
router.post('/change-password-with-old', async (req, res) => {
  const { username, oldPassword, newPassword } = req.body;
  
  if (!username || !oldPassword || !newPassword) {
    return res.json({ success: false, message: '请填写完整信息' });
  }
  
  if (newPassword.length < 6 || newPassword.length > 20) {
    return res.json({ success: false, message: '密码长度应为6-20位' });
  }
  
  try {
    const [rows] = await pool.query('SELECT id FROM users WHERE username = ? AND password = ?', [username, oldPassword]);
    
    if (rows.length === 0) {
      return res.json({ success: false, message: '当前密码错误' });
    }
    
    await pool.query('UPDATE users SET password = ? WHERE username = ?', [newPassword, username]);
    
    return res.json({ success: true, message: '密码修改成功' });
  } catch (err) {
    return res.json({ success: false, message: '修改密码失败' });
  }
});

module.exports = router;
