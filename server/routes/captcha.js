/**
 * 验证码API路由
 */

// 工具函数：生成随机验证码
const generateCaptcha = () => {
  return Math.random().toString(36).slice(-4).toUpperCase();
};

// 获取验证码
const captchaStore = new Map();

// 验证码路由
const express = require('express');
const router = express.Router();

router.get('/captcha', (req, res) => {
  const code = generateCaptcha();
  const timestamp = Date.now();
  
  // 存储验证码（有效期5分钟）
  captchaStore.set(timestamp, code);
  setTimeout(() => {
    captchaStore.delete(timestamp);
  }, 5 * 60 * 1000);
  
  res.json({ 
    captcha: code,
    code: code,
    timestamp: timestamp
  });
});

module.exports = router;
