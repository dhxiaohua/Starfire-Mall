/**
 * 统一配置文件
 * 所有可配置项都放在这里，便于修改和维护
 */

module.exports = {
  // 数据库配置
  database: {
    host: 'localhost',
    port: 3306,
    user: 'your_mysql_user',
    password: 'your_mysql_password',
    database: 'web_template',
    // 连接池配置
    pool: {
      max: 10,
      min: 0,
      acquire: 30000,
      idle: 10000
    }
  },

  // 服务器配置
  server: {
    port: 3001,
    host: 'localhost'
  },

  // 默认管理员配置
  admin: {
    username: 'admin',
    password: 'admin123'
  },

  // 验证码配置
  captcha: {
    expireTime: 5 * 60 * 1000  // 5分钟有效期
  }
}
