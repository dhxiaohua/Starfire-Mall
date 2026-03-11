/**
 * 管理员相关API路由
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

// 获取所有用户列表（管理员）
router.get('/admin/users', async (req, res) => {
  try {
    const [rows] = await pool.query(
      'SELECT id, username, nickname, email, phone, status, role, admin_status, create_time FROM users ORDER BY create_time DESC'
    );
    return res.json({ success: true, users: rows });
  } catch (err) {
    return res.json({ success: false, message: '获取用户列表失败' });
  }
});

// 获取待审批的权限申请列表
router.get('/admin/pending-requests', async (req, res) => {
  try {
    const [rows] = await pool.query(
      'SELECT id, username, nickname, admin_status, admin_request_time FROM users WHERE admin_status = ? ORDER BY admin_request_time DESC',
      ['pending']
    );
    return res.json({ success: true, requests: rows });
  } catch (err) {
    return res.json({ success: false, message: '获取申请列表失败' });
  }
});

// 审批权限申请
router.post('/admin/approve-request', async (req, res) => {
  const { userId, approved } = req.body;
  
  if (!userId || approved === undefined) {
    return res.json({ success: false, message: '参数不完整' });
  }
  
  try {
    if (approved) {
      // 批准：设置为管理员
      await pool.query(
        'UPDATE users SET role = ?, admin_status = ? WHERE id = ?',
        ['admin', 'approved', userId]
      );
      return res.json({ success: true, message: '已批准该用户的权限申请' });
    } else {
      // 拒绝
      await pool.query(
        'UPDATE users SET admin_status = ? WHERE id = ?',
        ['rejected', userId]
      );
      return res.json({ success: true, message: '已拒绝该用户的权限申请' });
    }
  } catch (err) {
    return res.json({ success: false, message: '审批失败' });
  }
});

// 申请成为管理员
router.post('/admin/apply', async (req, res) => {
  const { username } = req.body;
  
  if (!username) {
    return res.json({ success: false, message: '请提供用户名' });
  }
  
  try {
    // 检查用户是否存在
    const [rows] = await pool.query('SELECT id, role, admin_status FROM users WHERE username = ?', [username]);
    
    if (rows.length === 0) {
      return res.json({ success: false, message: '用户不存在' });
    }
    
    const user = rows[0];
    
    // 检查是否已经是管理员
    if (user.role === 'admin') {
      return res.json({ success: false, message: '您已经是管理员' });
    }
    
    // 检查是否已有待处理的申请
    if (user.admin_status === 'pending') {
      return res.json({ success: false, message: '您已提交过申请，请等待审批' });
    }
    
    // 检查是否被拒绝过（允许再次申请）
    if (user.admin_status === 'rejected') {
      await pool.query(
        'UPDATE users SET admin_status = ?, admin_request_time = NOW() WHERE username = ?',
        ['pending', username]
      );
      return res.json({ success: true, message: '申请已重新提交' });
    }
    
    // 提交申请
    await pool.query(
      'UPDATE users SET admin_status = ?, admin_request_time = NOW() WHERE username = ?',
      ['pending', username]
    );
    
    return res.json({ success: true, message: '申请已提交，请等待管理员审批' });
  } catch (err) {
    return res.json({ success: false, message: '提交申请失败' });
  }
});

// 获取用户权限状态
router.get('/admin/user-status', async (req, res) => {
  const { username } = req.query;
  
  if (!username) {
    return res.json({ success: false, message: '请提供用户名' });
  }
  
  try {
    const [rows] = await pool.query(
      'SELECT id, username, role, admin_status FROM users WHERE username = ?',
      [username]
    );
    
    if (rows.length === 0) {
      return res.json({ success: false, message: '用户不存在' });
    }
    
    const user = rows[0];
    return res.json({
      success: true,
      isAdmin: user.role === 'admin',
      adminStatus: user.admin_status,
      canApply: user.role !== 'admin' && user.admin_status !== 'pending'
    });
  } catch (err) {
    return res.json({ success: false, message: '获取状态失败' });
  }
});

// 撤销管理员权限
router.post('/admin/revoke', async (req, res) => {
  const { userId } = req.body;
  
  if (!userId) {
    return res.json({ success: false, message: '参数不完整' });
  }
  
  try {
    // 检查是否是撤销自己
    const [checkRows] = await pool.query('SELECT username FROM users WHERE id = ?', [userId]);
    if (checkRows.length === 0) {
      return res.json({ success: false, message: '用户不存在' });
    }
    
    await pool.query(
      'UPDATE users SET role = ?, admin_status = ? WHERE id = ?',
      ['user', 'none', userId]
    );
    
    return res.json({ success: true, message: '已撤销该用户的管理员权限' });
  } catch (err) {
    return res.json({ success: false, message: '撤销权限失败' });
  }
});

// 获取统计信息
router.get('/admin/stats', async (req, res) => {
  try {
    const [totalUsers] = await pool.query('SELECT COUNT(*) as count FROM users');
    const [adminUsers] = await pool.query('SELECT COUNT(*) as count FROM users WHERE role = ?', ['admin']);
    const [pendingRequests] = await pool.query('SELECT COUNT(*) as count FROM users WHERE admin_status = ?', ['pending']);
    const [activeUsers] = await pool.query('SELECT COUNT(*) as count FROM users WHERE status = ?', [1]);
    
    return res.json({
      success: true,
      stats: {
        totalUsers: totalUsers[0].count,
        adminUsers: adminUsers[0].count,
        pendingRequests: pendingRequests[0].count,
        activeUsers: activeUsers[0].count
      }
    });
  } catch (err) {
    return res.json({ success: false, message: '获取统计信息失败' });
  }
});

// 更新用户状态（启用/禁用）
router.post('/admin/update-user-status', async (req, res) => {
  const { userId, status } = req.body;
  
  if (!userId || status === undefined) {
    return res.json({ success: false, message: '参数不完整' });
  }
  
  try {
    await pool.query('UPDATE users SET status = ? WHERE id = ?', [status, userId]);
    return res.json({ success: true, message: status === 1 ? '已启用用户' : '已禁用用户' });
  } catch (err) {
    return res.json({ success: false, message: '更新状态失败' });
  }
});

// 删除用户
router.post('/admin/delete-user', async (req, res) => {
  const { userId } = req.body;
  
  if (!userId) {
    return res.json({ success: false, message: '参数不完整' });
  }
  
  try {
    // 检查是否是管理员账户
    const [checkRows] = await pool.query('SELECT username, role FROM users WHERE id = ?', [userId]);
    if (checkRows.length === 0) {
      return res.json({ success: false, message: '用户不存在' });
    }
    if (checkRows[0].role === 'admin') {
      return res.json({ success: false, message: '不能删除管理员账户' });
    }
    
    await pool.query('DELETE FROM users WHERE id = ?', [userId]);
    return res.json({ success: true, message: '用户已删除' });
  } catch (err) {
    return res.json({ success: false, message: '删除用户失败' });
  }
});

module.exports = router;
