# 星火商场 (Starfire-Mall)

一个基于 Vue 3 + Spring Boot 的全栈电竞设备电商网站。

![Vue 3](https://img.shields.io/badge/Vue-3.4+-42b883?style=flat&logo=vue.js)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2-brightgreen?style=flat&logo=spring)
![MySQL](https://img.shields.io/badge/MySQL-8.0-00758f?style=flat&logo=mysql)
![License](https://img.shields.io/badge/License-MIT-yellow)

## 简介

星火商场是一款专业的电竞设备电商平台，提供完整的商品展示、用户系统、购物车、订单管理和客服系统。

## 技术栈

### 前端
- **Vue 3** - 渐进式前端框架 (Composition API)
- **Vue Router 4** - 路由管理
- **Vite 5** - 构建工具
- **Pinia** - 状态管理

### 后端
- **Spring Boot 3.2** - Java 微服务框架
- **MyBatis Plus** - ORM 框架
- **Spring Security** - 安全框架
- **JWT** - 身份认证
- **WebSocket** - 实时通信

### 数据库
- **MySQL 8.0** - 关系型数据库

## 功能特性

### 用户系统
- 用户注册登录（4-16位账号，6-20位密码）
- 验证码登录
- 修改密码
- 个人资料管理

### 商品系统
- 商品分类展示（鼠标、键盘、耳机、手柄）
- 商品搜索
- 商品详情
- 库存管理

### 购物车 & 订单
- 购物车管理
- 订单创建与处理
- 收货地址管理

### 客服系统
- 浮动客服按钮
- 实时聊天
- 消息提醒

### 管理员系统
- 管理后台
- 用户管理
- 商品管理
- 销售统计
- 权限管理

## 项目结构

```
Starfire-Mall/
├── src/                      # Vue 3 前端
│   ├── api/                  # API 接口
│   ├── assets/               # 静态资源
│   ├── components/           # 组件
│   │   └── layout/           # 布局组件
│   ├── composables/          # 组合式函数
│   ├── router/               # 路由配置
│   ├── stores/               # 状态管理
│   └── views/                # 页面视图
│
├── backend/                  # Spring Boot 后端
│   └── src/main/java/
│       └── com/starfire/
│           ├── config/       # 配置类
│           ├── controller/  # 控制器
│           ├── entity/      # 实体类
│           ├── mapper/      # 数据访问层
│           ├── service/     # 服务层
│           └── security/    # 安全配置
│
├── images/                   # 商品图片
│   ├── mouse/               # 鼠标
│   ├── keyboard/            # 键盘
│   ├── headset/             # 耳机
│   └── controller/          # 手柄
│
└── public/                  # 公开静态资源
```

## 快速开始

### 环境要求

- Node.js 18+
- JDK 17+
- MySQL 8.0+
- Maven 3.8+

### 1. 克隆项目

```bash
git clone https://github.com/your-repo/starfire-mall.git
cd starfire-mall
```

### 2. 配置数据库

创建 MySQL 数据库：

```sql
CREATE DATABASE starfire_mall CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 3. 配置后端

编辑 `backend/src/main/resources/application.yml`：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/starfire_mall
    username: root
    password: your_password  # 改为你的数据库密码

admin:
  username: admin            # 管理员账号
  password: admin123         # 管理员密码
```

### 4. 启动后端

```bash
cd backend
mvn spring-boot:run
```

后端将在 http://localhost:8080 启动。

### 5. 启动前端

```bash
# 安装依赖
npm install

# 启动开发服务器
npm run dev
```

前端将在 http://localhost:5173 启动。

### 6. 构建生产版本

```bash
npm run build
```

## 默认账号

| 角色 | 用户名 | 密码 |
|------|--------|------|
| 管理员 | admin | admin123 |
| 测试用户 | testuser | test123 |

> ⚠️ 首次启动后请及时修改默认密码！

## 页面路由

| 路径 | 页面 | 权限 |
|------|------|------|
| `/` | 首页 | 公开 |
| `/login` | 登录 | 访客 |
| `/register` | 注册 | 访客 |
| `/settings` | 个人设置 | 需登录 |
| `/page1` | 产品列表 | 公开 |
| `/page2` | 关于我们 | 公开 |
| `/page3` | 联系我们 | 公开 |
| `/admin` | 管理后台 | 管理员 |

## API 接口

### 认证 API

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/auth/captcha` | 获取验证码 |
| POST | `/api/auth/check-username` | 检查用户名 |
| POST | `/api/auth/register` | 用户注册 |
| POST | `/api/auth/login` | 用户登录 |
| POST | `/api/auth/change-password` | 修改密码 |

### 用户 API

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/user/info` | 获取用户信息 |
| PUT | `/api/user/info` | 更新用户信息 |
| PUT | `/api/user/password` | 修改密码 |

### 商品 API

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/products` | 获取商品列表 |
| GET | `/api/products/{id}` | 获取商品详情 |
| POST | `/api/products` | 添加商品 |
| PUT | `/api/products/{id}` | 更新商品 |
| DELETE | `/api/products/{id}` | 删除商品 |

### 管理员 API

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/admin/users` | 用户列表 |
| POST | `/api/admin/approve` | 审批权限 |
| POST | `/api/admin/revoke` | 撤销权限 |

## 数据库表

- `users` - 用户表
- `products` - 商品表
- `orders` - 订单表
- `order_items` - 订单项表
- `cart` - 购物车表
- `address` - 地址表
- `categories` - 分类表
- `customer_messages` - 客服消息表
- `product_reviews` - 商品评论表

详细表结构请参考 `backend/src/main/resources/sql/`

## 环境变量

| 变量名 | 说明 | 默认值 |
|--------|------|--------|
| `DB_PASSWORD` | 数据库密码 | - |
| `ADMIN_USERNAME` | 管理员账号 | admin |
| `ADMIN_PASSWORD` | 管理员密码 | admin123 |

## 开发指南

### 前端开发

```bash
# 代码规范检查
npm run lint

# 预览生产构建
npm run preview
```

### 后端开发

```bash
# 编译项目
mvn compile

# 运行测试
mvn test

# 打包项目
mvn package
```

## 贡献指南

欢迎提交 Issue 和 Pull Request！

1. Fork 本仓库
2. 创建特性分支 (`git checkout -b feature/xxx`)
3. 提交更改 (`git commit -m 'Add xxx'`)
4. 推送分支 (`git push origin feature/xxx`)
5. 创建 Pull Request

## 许可证

MIT License - 详见 [LICENSE](LICENSE) 文件

## 截图

![首页](images/screenshot/home.png)
![产品页](images/screenshot/products.png)
![管理后台](images/screenshot/admin.png)

---

⭐ 如果对你有帮助，请给项目点个 Star！
