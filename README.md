# 星火商场 (Starfire-Mall)

一个基于 Vue 3 + Spring Boot 的全栈电竞设备电商网站，拥有精美的粒子动画效果。

![Vue 3](https://img.shields.io/badge/Vue-3.4+-42b883?style=flat&logo=vue.js)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2-brightgreen?style=flat&logo=spring)
![MySQL](https://img.shields.io/badge/MySQL-8.0-00758f?style=flat&logo=mysql)
![Redis](https://img.shields.io/badge/Redis-6.0+-dc382d?style=flat&logo=redis)
![License](https://img.shields.io/badge/License-MIT-yellow)

## 简介

星火商场是一款专业的电竞设备电商平台，提供完整的商品展示、用户系统、购物车、订单管理和客服系统。



## 技术栈

### 前端
- **Vue 3** - 渐进式前端框架 (Composition API)
- **Vue Router 4** - 路由管理
- **Vite 5** - 构建工具
- **Canvas API** - 粒子动画效果
- **WebSocket** - 实时通信

### 后端
- **Spring Boot 3.2** - Java 微服务框架
- **MyBatis Plus 3.5.5** - ORM 框架
- **Spring Security** - 安全框架
- **JWT 0.12.3** - 身份认证
- **WebSocket** - 实时通信
- **Redis** - 缓存服务
- **AnythingLLM** - AI 智能客服集成（可选）

### 数据库 & 缓存
- **MySQL 8.0** - 关系型数据库
- **Redis** - 缓存服务

## 功能特性

### 视觉效果
- **星空背景**
- **火焰粒子**
- **平滑过渡**

### 用户系统
- 用户注册登录（4-16位账号，6-20位密码）
- 验证码登录（验证码与用户名绑定）
- 修改密码（验证旧密码）
- 个人资料管理
- 权限管理（普通用户/管理员/管理员申请）
- 多标签页同时登录支持

### 商品系统
- 商品分类展示（鼠标、键盘、耳机、手柄）
- 商品搜索（按分类、关键词）
- 商品详情
- 库存管理
- 销售统计
- 商品评论

### 购物车 & 订单
- 购物车管理
- 订单创建与处理
- 收货地址管理
- 订单项管理

### 客服系统
- 浮动客服按钮
- 实时聊天（WebSocket）
- 消息提醒
- 管理员客服回复功能

### AI 智能客服系统
- 集成 AnythingLLM，支持基于知识库的智能问答
- 支持多种聊天模式（query 模式、chat 模式）
- 清除上下文功能
- 用户头像显示
- 会话记录自动保存
- 管理员可查看所有用户的 AI 对话记录

### 管理员系统
- 管理后台
- 用户管理（查看、删除、更新状态）
- 商品管理（增删改查、库存管理）
- 订单管理
- 权限管理（审批管理员申请、撤销权限）
- 销售统计
- 客服消息管理

### 缓存系统
- 基于 Redis 的缓存服务
- 用户信息缓存
- 商品信息缓存
- 查询性能优化

### 文件上传系统
- 支持文件上传（MultipartFile）
- 支持 Base64 图片上传
- 用户头像上传功能
- 文件大小限制（默认10MB）

## 项目结构

```
Starfire-Mall/
├── frontend/                    # Vue 3 前端应用
│   ├── src/
│   │   ├── api/                # API 接口封装
│   │   │   └── index.js        # 所有API调用函数
│   │   ├── assets/             # 静态资源
│   │   │   └── global.css      # 全局样式
│   │   ├── components/         # Vue 组件
│   │   │   ├── CustomerService.vue    # 客服组件
│   │   │   ├── StarBackground.vue     # 星空背景组件
│   │   │   ├── WebsiteLayout.vue      # 网站布局
│   │   │   └── layout/        # 布局组件
│   │   ├── composables/       # 组合式函数
│   │   │   └── useWebSocket.js        # WebSocket hook
│   │   ├── router/            # 路由配置
│   │   │   └── index.js       # 路由定义和守卫
│   │   ├── stores/            # 状态管理
│   │   │   └── userStore.js   # 用户状态管理（基于 Vue 3 ref）
│   │   ├── views/             # 页面视图
│   │   │   ├── Admin.vue      # 管理后台
│   │   │   ├── Home.vue       # 首页（含粒子效果）
│   │   │   ├── Search.vue     # 搜索页
│   │   │   ├── Settings.vue   # 个人设置
│   │   │   ├── Products.vue   # 产品列表
│   │   │   ├── About.vue      # 关于我们
│   │   │   ├── Contact.vue    # 联系我们
│   │   │   ├── AICustomerService.vue  # AI智能客服页面
│   │   │   └── UserOrders.vue # 我的订单
│   │   ├── App.vue            # 根组件
│   │   ├── config.js          # 前端配置文件
│   │   └── main.js            # 入口文件
│   ├── public/                # 公开静态资源
│   │   └── images/            # 图片资源
│   │       ├── logo.jpg
│   │       ├── avatars/       # 用户头像（上传的文件）
│   │       ├── controller/    # 手柄图片
│   │       ├── headset/       # 耳机图片
│   │       ├── keyboard/      # 键盘图片
│   │       └── mouse/         # 鼠标图片
│   ├── index.html             # HTML 入口
│   ├── package.json           # 前端依赖配置
│   └── vite.config.js         # Vite 配置
│
├── backend/                    # Spring Boot 后端应用
│   ├── src/main/java/com/starfire/
│   │   ├── StarfireMallApplication.java    # 主启动类
│   │   ├── config/            # 配置类（数据库、安全、Redis、WebSocket等）
│   │   ├── controller/        # 控制器（REST API）
│   │   │   ├── AIController.java          # AI客服控制器
│   │   │   ├── AIChatController.java      # AI聊天记录控制器
│   │   │   ├── AdminController.java
│   │   │   ├── AuthController.java
│   │   │   ├── CartController.java
│   │   │   ├── CustomerController.java
│   │   │   ├── FileUploadController.java  # 文件上传控制器
│   │   │   ├── OrderController.java
│   │   │   ├── ProductController.java
│   │   │   ├── ProductReviewController.java
│   │   │   └── UserController.java
│   │   ├── dto/               # 数据传输对象
│   │   ├── entity/            # 实体类（数据库模型）
│   │   ├── mapper/            # MyBatis Mapper
│   │   ├── security/          # Spring Security 安全配置
│   │   └── service/           # 服务层
│   │       ├── AIService.java         # AI客服服务
│   │       ├── AIChatService.java     # AI聊天记录服务
│   │       └── impl/          # 服务实现类
│   ├── src/main/resources/
│   │   ├── application.yml    # 应用配置
│   │   └── sql/               # SQL 脚本
│   │       └── init.sql       # 数据库初始化脚本
│   └── pom.xml                # Maven 配置
│
└── README.md                  # 项目说明文档
```

## 快速开始

### 环境要求

- Node.js 18+
- JDK 17+
- MySQL 8.0+
- Maven 3.8+
- Redis 5.0+（必需，用于缓存）
- AnythingLLM 1.0+（AI客服功能，可选）

### 1. 克隆项目

```bash
git clone https://github.com/dhxiaohua/starfire-mall.git
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
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://127.0.0.1:3306/starfire_mall?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true
    username: your_username  # 改为你的数据库用户名
    password: your_password  # 改为你的数据库密码
  redis:
    host: localhost
    port: 6379
    password:              # Redis 密码（如果有）
    database: 0

admin:
  username: admin            # 管理员账号
  password: admin123         # 管理员密码

file:
  upload:
    path: Starfire-Mall/frontend/public/images/avatars  # 你的文件上传路径（相对路径）

# JWT配置
jwt:
  secret:  # JWT 密钥（必须填写，建议使用复杂的随机字符串）
  expiration: 86400000  # 24小时

# AnythingLLM 配置（AI客服功能，可选）
anythingllm:
  api-url: http://127.0.0.1:3001  # AnythingLLM API 地址
  api-key: your_api_key_here        # API 密钥（从 AnythingLLM 设置中获取）
  workspace: your_workspace_uuid   # 工作区 UUID（从 AnythingLLM 工作区设置中获取）
```



### 4. 启动 Redis

```bash
# Windows
redis-server

# Linux/Mac
redis-server
```

### 5. 启动后端

```bash
cd backend
mvn spring-boot:run
```

后端将在 http://localhost:8080 启动

### 6. 启动前端

```bash
# 安装依赖
npm install

# 启动开发服务器
npm run dev
```

前端将在 http://localhost:3000 启动

### 7. 构建生产版本

```bash
npm run build
```

## 默认配置

### 服务器端口

- **前端开发服务器：** 3000
- **后端 API 服务器：** 8080
- **Redis 服务器：** 6379

### API 基础地址

- **前端配置：** `http://localhost:8080/api`（在 `frontend/src/config.js` 中配置）

### 默认账号

| 角色 | 用户名 | 密码 |
|------|--------|------|
| 管理员 | admin | admin123 |

⚠️ **首次启动后请及时修改默认密码！**

## 页面路由

| 路径 | 页面 | 权限要求 |
|------|------|----------|
| `/` | 首页（星空/火焰效果） | 公开 |
| `/search` | 搜索页 | 公开 |
| `/products` | 产品列表 | 公开 |
| `/about` | 关于我们 | 公开 |
| `/contact` | 联系我们 | 公开 |
| `/ai-service` | AI智能客服 | 公开 |
| `/settings` | 个人设置 | 需登录 |
| `/orders` | 我的订单 | 需登录 |
| `/admin` | 管理后台 | 需管理员权限 |

**注意：** 登录和注册功能集成在首页和设置页面中，没有独立的登录/注册页面路由。

## API 接口

### 认证 API

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/auth/captcha` | 获取验证码（支持绑定用户名） |
| POST | `/api/auth/check-username` | 检查用户名是否存在 |
| POST | `/api/auth/register` | 用户注册 |
| POST | `/api/auth/login` | 用户登录 |
| POST | `/api/auth/change-password` | 修改密码 |

### 用户 API

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/user/info` | 获取用户信息 |
| PUT | `/api/user/update` | 更新用户信息 |
| PUT | `/api/user/password` | 修改密码（验证旧密码） |

### 商品 API

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/products` | 获取商品列表（支持分类、关键词过滤） |
| GET | `/api/products/{id}` | 获取商品详情 |
| POST | `/api/products` | 添加商品（需管理员） |
| PUT | `/api/products/{id}` | 更新商品（需管理员） |
| DELETE | `/api/products/{id}` | 删除商品（需管理员） |
| PUT | `/api/products/{id}/stock` | 更新库存（需管理员） |
| GET | `/api/products/stats` | 获取销售统计 |

### 订单 API

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/api/orders` | 创建订单 |
| GET | `/api/orders` | 获取订单列表 |
| GET | `/api/orders/{id}` | 获取订单详情 |
| PUT | `/api/orders/{id}/status` | 更新订单状态 |

### 购物车 API

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/cart` | 获取购物车 |
| POST | `/api/cart` | 添加到购物车 |
| PUT | `/api/cart/{id}` | 更新购物车项 |
| DELETE | `/api/cart/{id}` | 删除购物车项 |

### 管理员 API

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/admin/users` | 获取所有用户 |
| GET | `/api/admin/pending-requests` | 获取待审批申请 |
| POST | `/api/admin/approve-request` | 审批权限申请 |
| POST | `/api/admin/apply` | 申请成为管理员 |
| POST | `/api/admin/cancel-application` | 撤销管理员申请 |
| GET | `/api/admin/user-status` | 获取用户权限状态 |
| POST | `/api/admin/revoke` | 撤销管理员权限 |
| GET | `/api/admin/stats` | 获取统计信息 |
| POST | `/api/admin/update-user-status` | 更新用户状态 |
| POST | `/api/admin/delete-user` | 删除用户 |

### 客服 API

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/customer/messages` | 获取用户客服消息 |
| POST | `/api/customer/send` | 发送客服消息 |
| GET | `/api/customer/all-messages` | 获取所有客服消息（管理员） |
| POST | `/api/customer/mark-read` | 标记消息为已读 |
| POST | `/api/customer/reply` | 发送客服回复（管理员） |

### AI 客服 API

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/api/ai/chat` | AI 聊天接口 |
| GET | `/api/ai/test` | 测试 AI 服务连接 |
| POST | `/api/ai/clear-session` | 清除 AI 会话上下文 |

### AI 聊天记录管理 API

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/ai/chat/sessions` | 获取所有 AI 会话列表（管理员） |
| GET | `/api/ai/chat/messages/{username}` | 获取用户 AI 聊天记录（管理员） |
| GET | `/api/ai/chat/session/{sessionId}/messages` | 获取指定会话的聊天记录（管理员） |

### 商品评论 API

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/product-reviews/{productId}` | 获取商品评论 |
| POST | `/api/product-reviews` | 添加商品评论 |

### 文件上传 API

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/api/upload/file` | 上传文件（支持用户头像等） |
| POST | `/api/upload/base64` | 上传 Base64 格式图片 |

## 数据库表

主要数据表：
- `users` - 用户表
- `products` - 商品表
- `orders` - 订单表
- `order_items` - 订单项表
- `cart` - 购物车表
- `addresses` - 地址表
- `categories` - 分类表
- `customer_messages` - 客服消息表
- `product_reviews` - 商品评论表
- `contact_messages` - 留言表
- `ai_chat_messages` - AI 聊天记录表

详细表结构请参考 `backend/src/main/resources/sql/` 目录下的 SQL 脚本。

## 环境变量

| 变量名 | 说明 | 默认值 |
|--------|------|--------|
| `DB_PASSWORD` | 数据库密码 |        |
| `ADMIN_USERNAME` | 管理员账号 | admin |
| `ADMIN_PASSWORD` | 管理员密码 | admin123 |
| `REDIS_HOST` | Redis 主机 | localhost |
| `REDIS_PORT` | Redis 端口 | 6379 |
| `REDIS_PASSWORD` | Redis 密码 | （无） |
| `ANYTHINGLLM_API_URL` | AnythingLLM API 地址 | http://127.0.0.1:3001 |
| `ANYTHINGLLM_API_KEY` | AnythingLLM API 密钥 | （无） |
| `ANYTHINGLLM_WORKSPACE` | AnythingLLM 工作区 UUID | （无） |

## 故障排查

### 前端无法连接后端

1. 检查后端是否已启动（访问 http://localhost:8080）
2. 检查 `frontend/src/config.js` 中的 API 地址是否正确
3. 检查浏览器控制台是否有跨域错误

### 数据库连接失败

1. 检查 MySQL 服务是否启动
2. 检查 `application.yml` 中的数据库配置是否正确
3. 确认数据库 `starfire_mall` 是否已创建

### Redis 连接失败

1. 检查 Redis 服务是否启动（默认端口 6379）
2. 检查 `application.yml` 中的 Redis 配置是否正确
3. 确认 Redis 密码配置（如果有）

### 验证码失效

1. 验证码有效期为 5 分钟
2. 验证码与用户名绑定，确保使用的验证码与登录/注册的用户名一致
3. 每次获取验证码后，旧的验证码会失效

### 缓存相关问题

1. Redis 缓存服务未启动会导致应用启动失败
2. 检查 `RedisCacheService` 配置是否正确
3. 如需清除缓存，可以重启 Redis 服务或调用缓存清除接口

### 文件上传问题

1. **上传失败：** 检查文件上传路径是否正确，确保目录存在且可写
2. **路径配置：** application.yml 中的 `file.upload.path` 需要根据实际情况修改
3. **文件大小限制：** 默认限制为 10MB，可在 `spring.servlet.multipart` 中调整
4. **权限问题：** 确保应用有权限写入上传目录

### AI 客服相关问题

1. **AI 客服无法回答问题：**
   - 检查 AnythingLLM 服务是否正常运行
   - 确认 `application.yml` 中的 `anythingllm` 配置是否正确
   - 检查 API 密钥和工作区 ID 是否有效
   - 查看后端日志确认 API 调用是否成功

2. **AI 聊天记录查看失败：**
   - 检查数据库中是否存在 `ai_chat_messages` 表
   - 确认后端 DTO 类是否手动添加了 getter/setter 方法
   - 重新编译并重启后端服务

3. **导航栏按钮激活状态不显示：**
   - 确保子组件传递了正确的 `currentPage` 属性
   - 例如：`<WebsiteLayout currentPage="ai-service" />`

## 开发指南

### 前端开发

```bash
cd frontend

# 安装依赖
npm install

# 启动开发服务器
npm run dev

# 构建生产版本
npm run build

# 预览生产构建
npm run preview
```

### 后端开发

```bash
cd backend

# 编译项目
mvn clean compile

# 运行测试
mvn test

# 打包项目
mvn package

# 运行应用
mvn spring-boot:run
```

## 开发约定

### 前端开发规范

1. **组件命名：**
   - 使用 PascalCase 命名组件文件（如 `CustomerService.vue`）
   - 组件内部使用语义化的命名

2. **API 调用：**
   - 所有 API 调用统一在 `src/api/index.js` 中定义
   - 使用 `async/await` 处理异步请求
   - 错误处理使用 try-catch 块

3. **状态管理：**
   - 使用 Vue 3 的 `ref` 创建响应式状态（而非 Pinia）
   - 用户状态存储在 `src/stores/userStore.js`
   - 使用 sessionStorage 持久化用户登录状态
   - 支持多标签页同时登录

4. **路由管理：**
   - 路由配置在 `src/router/index.js`
   - 使用路由守卫进行权限控制
   - 路由元信息（meta）用于权限标识

5. **样式规范：**
   - 全局样式在 `src/assets/global.css`
   - 组件样式使用 scoped 样式

### 后端开发规范

1. **包结构：**
   - `config/`：配置类（数据库、安全、WebSocket、Redis等）
   - `controller/`：REST API 控制器
   - `dto/`：数据传输对象
   - `entity/`：数据库实体类
   - `mapper/`：MyBatis 数据访问层
   - `security/`：Spring Security 安全配置
   - `service/`：业务逻辑层（包括 Redis 缓存服务）

2. **API 规范：**
   - RESTful 风格 API
   - 统一响应格式：`{ success: boolean, message: string, data: any }`
   - 使用 JWT 进行身份认证

3. **数据库规范：**
   - 使用 MyBatis Plus 进行数据库操作
   - 实体类使用 Lombok 注解简化代码
   - 逻辑删除字段：`deleted`（0-未删除，1-已删除）

4. **安全规范：**
   - 密码使用 bcrypt 加密
   - JWT token 有效期：24小时
   - 验证码有效期：5分钟

5. **缓存规范：**
   - 使用 Redis 进行数据缓存
   - Redis 配置在 `application.yml` 中
   - `RedisCacheService` 提供统一的缓存操作接口

## 项目特性

### 粒子效果系统

#### 星空背景（未登录）
- **高密度星星：** 
- **北极星：** 
- **北斗七星：** 
- **闪烁效果：** 

#### 火焰效果（登录后）
- **像素风格：** 
- **多种粒子类型：**
- **温度渐变：** 
- **鼠标交互：** 
- **全屏覆盖：** 

### 页面过渡效果
- **平滑动画：** 

### 状态管理

- **多标签页支持：** 
- **实时同步：** 

## 贡献指南

欢迎提交 Issue 和 Pull Request！

1. Fork 本仓库
2. 创建特性分支 (`git checkout -b feature/xxx`)
3. 提交更改 (`git commit -m 'Add xxx'`)
4. 推送分支 (`git push origin feature/xxx`)
5. 创建 Pull Request

## 许可证

MIT License - 详见 [LICENSE](LICENSE) 文件

## 联系方式

如有问题或建议，欢迎通过以下方式联系：

- 提交 Issue
- 发送邮件


## 显示效果

[首页](./frontend/public/images/screenshot/home.png)
[首页](./frontend/public/images/screenshot/home2.png)
[登录页](./frontend/public/images/screenshot/login.png)
[产品页](./frontend/public/images/screenshot/products.png)
[后台](./frontend/public/images/screenshot/admin.png)
---
⭐ 如果对你有帮助，请给项目点个 Star！
