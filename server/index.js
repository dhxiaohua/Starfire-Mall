const express = require('express');
const mysql = require('mysql2');
const cors = require('cors');
const bodyParser = require('body-parser');

// 引入统一配置
const config = require('./config');

const app = express();
const PORT = config.server.port;

// 中间件 - CORS允许所有来源
app.use(cors({
  origin: '*',
  methods: ['GET', 'POST', 'PUT', 'DELETE', 'OPTIONS'],
  allowedHeaders: ['Content-Type', 'Authorization']
}));
app.use(bodyParser.json({ limit: '10mb' }));
app.use(bodyParser.urlencoded({ extended: true, limit: '10mb' }));

// 使用配置中的数据库配置
const dbConfig = {
  host: config.database.host,
  user: config.database.user,
  password: config.database.password,
  database: config.database.database
};

// 创建数据库连接池
const pool = mysql.createPool(dbConfig);

// 初始化数据库和表
const initDatabase = () => {
  const tempConfig = { ...dbConfig };
  delete tempConfig.database;
  
  const connection = mysql.createConnection(tempConfig);
  
  connection.connect((err) => {
    if (err) {
      console.error('MySQL连接失败:', err.message);
      return;
    }
    
    console.log('MySQL连接成功');
    
    // 创建数据库
    connection.query('CREATE DATABASE IF NOT EXISTS web_template', (err) => {
      if (err) {
        console.error('创建数据库失败:', err.message);
        connection.end();
        return;
      }
      
      console.log('数据库创建成功或已存在');
      
      // 切换到目标数据库
      connection.query('USE web_template', (err) => {
        if (err) {
          console.error('切换数据库失败:', err.message);
          connection.end();
          return;
        }
        
        // 检查用户表是否存在
        connection.query('SHOW TABLES LIKE "users"', (err, tables) => {
          if (err) {
            console.error('检查表失败:', err.message);
            connection.end();
            return;
          }
          
          if (tables.length === 0) {
            // 创建用户表（完整结构，包含管理员字段）
            const createTableSQL = `
              CREATE TABLE IF NOT EXISTS users (
                id INT AUTO_INCREMENT PRIMARY KEY,
                username VARCHAR(50) NOT NULL UNIQUE,
                nickname VARCHAR(50) DEFAULT NULL,
                password VARCHAR(255) NOT NULL,
                email VARCHAR(100) DEFAULT NULL,
                phone VARCHAR(20) DEFAULT NULL,
                avatar TEXT DEFAULT NULL,
                status TINYINT DEFAULT 1,
                role VARCHAR(20) DEFAULT 'user',
                admin_status VARCHAR(20) DEFAULT 'none',
                admin_request_time TIMESTAMP NULL DEFAULT NULL,
                create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                INDEX idx_username (username),
                INDEX idx_status (status),
                INDEX idx_role (role),
                INDEX idx_admin_status (admin_status)
              ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
            `;
            
            connection.query(createTableSQL, (err) => {
              if (err) {
                console.error('创建用户表失败:', err.message);
              } else {
                console.log('用户表创建成功');
              }
              createDefaultAdmin(connection);
            });
          } else {
            // 表已存在，检查并添加新字段
            checkAndAddColumns(connection);
          }
        });
      });
    });
  });
};

// 检查并添加新字段
const checkAndAddColumns = (connection) => {
  // 检查avatar字段类型，如果是VARCHAR则改为TEXT
  connection.query('SHOW COLUMNS FROM users LIKE "avatar"', (err, rows) => {
    if (err) {
      console.error('检查字段失败:', err.message);
      createDefaultAdmin(connection);
      return;
    }
    
    if (rows.length > 0 && rows[0].Type && rows[0].Type.startsWith('varchar')) {
      // 将avatar字段从VARCHAR改为TEXT
      connection.query('ALTER TABLE users MODIFY COLUMN avatar TEXT', (err) => {
        if (err) console.error('修改avatar字段类型失败:', err.message);
        else console.log('avatar字段已更新为TEXT类型');
      });
    }
  });
  
  connection.query('SHOW COLUMNS FROM users LIKE "role"', (err, rows) => {
    if (err) {
      console.error('检查字段失败:', err.message);
      createDefaultAdmin(connection);
      return;
    }
    
    if (rows.length === 0) {
      // 添加role字段
      connection.query('ALTER TABLE users ADD COLUMN role VARCHAR(20) DEFAULT "user"', (err) => {
        if (err) console.error('添加role字段失败:', err.message);
      });
    }
    
    connection.query('SHOW COLUMNS FROM users LIKE "admin_status"', (err, rows) => {
      if (rows.length === 0) {
        connection.query('ALTER TABLE users ADD COLUMN admin_status VARCHAR(20) DEFAULT "none"', (err) => {
          if (err) console.error('添加admin_status字段失败:', err.message);
        });
      }
      
      connection.query('SHOW COLUMNS FROM users LIKE "admin_request_time"', (err, rows) => {
        if (rows.length === 0) {
          connection.query('ALTER TABLE users ADD COLUMN admin_request_time TIMESTAMP NULL', (err) => {
            if (err) console.error('添加admin_request_time字段失败:', err.message);
          });
        }
        
        console.log('用户表字段检查完成');
        createDefaultAdmin(connection);
      });
    });
  });
};

// 创建默认管理员
const createDefaultAdmin = (connection) => {
  const { username, password } = config.admin;
  
  const checkAdminSQL = 'SELECT id FROM users WHERE username = ?';
  connection.query(checkAdminSQL, [username], (err, rows) => {
    if (err) {
      console.error('检查管理员账户失败:', err.message);
      connection.end();
      return;
    }
    
    if (rows.length === 0) {
      // 创建默认管理员
      const insertAdminSQL = 'INSERT INTO users (username, password, role, admin_status) VALUES (?, ?, ?, ?)';
      connection.query(insertAdminSQL, [username, password, 'admin', 'approved'], (err) => {
        if (err) {
          console.error('创建默认管理员失败:', err.message);
        } else {
          console.log(`默认管理员账户创建成功: ${username} / ${password}`);
        }
        connection.end();
      });
    } else {
      // 更新已有账户为管理员
      const updateSQL = 'UPDATE users SET role = "admin", admin_status = "approved" WHERE username = ?';
      connection.query(updateSQL, [username], (err) => {
        if (err) {
          console.error('更新管理员状态失败:', err.message);
        } else {
          console.log('管理员账户已存在并更新为管理员');
        }
        connection.end();
      });
    }
  });
};

// 初始化数据库
initDatabase();

// 导入路由
const userRoutes = require('./routes/users');
const captchaRoutes = require('./routes/captcha');
const adminRoutes = require('./routes/admin');
const productRoutes = require('./routes/products');
const customerRoutes = require('./routes/customer');

// 使用路由
app.use('/api', userRoutes);
app.use('/api', captchaRoutes);
app.use('/api', adminRoutes);
app.use('/api', productRoutes);
app.use('/api', customerRoutes);

// 启动服务器
app.listen(PORT, () => {
  console.log(`服务器运行在 http://localhost:${PORT}`);
  console.log('MySQL数据库已配置完成');
});