# 星火游戏设备

基于 Vue 3 + Express + MySQL 的全栈游戏设备电商网站，提供完整的用户系统、商品管理、客服系统和管理后台。

## 技术栈

### 前端
- Vue 3 (Composition API)
- Vue Router 4
- Vite
- Pinia (状态管理)

### 后端
- Express
- MySQL2
- CORS
- Body-Parser

## 功能特性

### 用户系统
- 用户注册（4-16位字母数字账号，6-20位密码）
- 用户登录（带验证码）
- 忘记密码
- 修改密码（需验证旧密码）
- 个人资料管理（昵称、邮箱、手机、头像上传）

### 商品系统
- 商品分类展示（鼠标、键盘、耳机、手柄）
- 商品搜索
- 商品详情查看
- 库存管理
- 销售统计

### 客服系统
- 浮动客服按钮（全局显示）
- 实时聊天（1秒自动刷新）
- 多用户同时咨询
- 客服消息提醒

### 管理员系统
- 权限申请机制（用户可申请成为管理员）
- 管理后台
  - 用户管理（查看、启用/禁用、删除）
  - 商品管理（增删改查）
  - 销售统计（销售额、订单数、库存预警）
  - 权限管理（批准/拒绝申请、撤销管理员）
  - 客服消息处理

## 项目结构

```
├── server/                    # Express 后端
│   ├── index.js              # 服务器入口、数据库初始化
│   ├── config/
│   │   └── index.js          # 数据库配置
│   └── routes/
│       ├── users.js          # 用户相关 API
│       ├── captcha.js        # 验证码 API
│       ├── admin.js          # 管理员 API
│       ├── products.js       # 商品管理 API
│       └── customer.js       # 客服系统 API
│
├── src/                      # Vue 前端
│   ├── main.js              # 应用入口
│   ├── App.vue              # 根组件
│   ├── config.js            # 前端配置
│   ├── api/
│   │   └── index.js         # API 接口封装
│   ├── components/
│   │   ├── CustomerService.vue    # 客服组件
│   │   ├── StarBackground.vue     # 星空背景
│   │   ├── WebsiteLayout.vue      # 网站布局
│   │   └── layout/               # 布局组件
│   │       ├── AppLayout.vue
│   │       ├── AppNavbar.vue
│   │       ├── AppSearchBar.vue
│   │       └── AppUserDropdown.vue
│   ├── router/
│   │   └── index.js         # 路由配置
│   ├── stores/
│   │   └── userStore.js     # 用户状态管理
│   └── views/               # 页面视图
│       ├── Home.vue         # 首页
│       ├── Search.vue       # 搜索页
│       ├── Login.vue        # 登录
│       ├── Register.vue     # 注册
│       ├── Settings.vue     # 个人设置
│       ├── Admin.vue        # 管理后台
│       └── Page1-5.vue      # 示例页面
│
├── images/                   # 商品图片
│   ├── mouse/              # 鼠标图片
│   ├── keyboard/           # 键盘图片
│   ├── headset/            # 耳机图片
│   └── controller/         # 手柄图片
│
├── public/                  # 静态资源
├── index.html              # HTML 入口
├── package.json            # 项目配置
├── vite.config.js          # Vite 配置
└── README.md               # 项目文档
```

## 快速开始

### 环境要求
- Node.js 16+
- MySQL 5.7+

### 安装依赖

```bash
npm install
```

### 配置数据库

编辑 `server/config/index.js` 中的数据库配置：

```javascript
database: {
  host: 'localhost',      // MySQL 主机
  port: 3306,             // 端口
  user: 'your_mysql_user',  // 用户名
  password: 'your_mysql_password',  // 密码
  database: 'web_template'     // 数据库名
}
```

### 启动项目

```bash
# 启动后端服务器（端口 3001）
npm run server

# 启动前端开发服务器（端口 5173）
npm run dev
```

### 构建生产版本

```bash
npm run build
```

## 默认账号

| 角色 | 用户名 | 密码 |
|------|--------|------|
| 管理员 | admin | admin123 |
| 普通用户 | (自行注册) | (自行设置) |

## 页面路由

| 路径 | 页面 | 说明 |
|------|------|------|
| / | Search | 搜索页（默认首页） |
| /home | Home | 首页 |
| /login | Login | 登录页 |
| /register | Register | 注册页 |
| /settings | Settings | 个人设置 |
| /admin | Admin | 管理后台（需管理员权限） |
| /page1 ~ /page5 | Page1~5 | 示例页面 |

## API 接口

### 用户 API

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /api/check-username | 检查账号是否存在 |
| POST | /api/register | 用户注册 |
| POST | /api/login | 用户登录 |
| POST | /api/change-password-with-old | 修改密码（需验证旧密码） |
| GET | /api/user-info | 获取用户信息 |
| POST | /api/update-user-info | 更新用户信息 |
| POST | /api/upload-avatar | 上传头像 |

### 商品 API

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /api/products | 获取商品列表 |
| GET | /api/products/:id | 获取商品详情 |
| POST | /api/products | 添加商品 |
| PUT | /api/products/:id | 更新商品 |
| DELETE | /api/products/:id | 删除商品 |
| GET | /api/sales/stats | 获取销售统计 |
| PUT | /api/products/:id/stock | 更新库存 |
| PUT | /api/products/:id/sales | 更新销量 |

### 客服 API

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /api/customer/messages | 获取消息列表 |
| GET | /api/customer/conversation/:username | 获取对话记录 |
| POST | /api/customer/send | 发送消息 |
| PUT | /api/customer/read | 标记已读 |

### 验证码 API

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /api/captcha | 获取验证码 |

### 管理员 API

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /api/admin/users | 获取所有用户列表 |
| GET | /api/admin/pending-requests | 获取待审批申请 |
| POST | /api/admin/approve-request | 审批权限申请 |
| POST | /api/admin/apply | 申请成为管理员 |
| GET | /api/admin/user-status | 获取用户权限状态 |
| POST | /api/admin/revoke | 撤销管理员权限 |
| GET | /api/admin/stats | 获取统计信息 |
| POST | /api/admin/update-user-status | 更新用户状态 |
| POST | /api/admin/delete-user | 删除用户 |

## 数据库表结构

### users 表

| 字段 | 类型 | 说明 |
|------|------|------|
| id | INT | 主键，自增 |
| username | VARCHAR(50) | 用户名，唯一 |
| nickname | VARCHAR(50) | 昵称 |
| password | VARCHAR(255) | 密码 |
| email | VARCHAR(100) | 邮箱 |
| phone | VARCHAR(20) | 手机号 |
| avatar | TEXT | 头像（Base64） |
| status | TINYINT | 账号状态（1正常/0禁用） |
| role | VARCHAR(20) | 角色（admin/user） |
| admin_status | VARCHAR(20) | 管理员申请状态 |
| admin_request_time | TIMESTAMP | 申请时间 |
| create_time | TIMESTAMP | 创建时间 |
| update_time | TIMESTAMP | 更新时间 |

### products 表

| 字段 | 类型 | 说明 |
|------|------|------|
| id | INT | 主键，自增 |
| name | VARCHAR(255) | 商品名称 |
| category | VARCHAR(50) | 分类（mouse/keyboard/headset/controller） |
| description | TEXT | 商品描述 |
| price | DECIMAL(10,2) | 价格 |
| stock | INT | 库存 |
| image | VARCHAR(500) | 图片路径 |
| features | VARCHAR(500) | 特点 |
| sales | INT | 销量 |
| status | VARCHAR(20) | 状态 |
| create_time | DATETIME | 创建时间 |
| update_time | DATETIME | 更新时间 |

## 注意事项

1. **验证码**：验证码有效期 5 分钟
2. **多账户登录**：支持浏览器多标签页同时登录不同账户
3. **头像存储**：头像以 Base64 格式存储在数据库 avatar 字段
4. **客服消息**：客服消息自动刷新间隔为 1 秒
5. **初始商品**：首次启动会自动初始化商品数据

## 目录说明

- `images/` - 存放商品图片（mouse、keyboard、headset、controller）
- `public/images/` - 公开访问的静态资源
- `dist/` - 生产构建输出目录