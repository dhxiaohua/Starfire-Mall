<template>
  <div class="admin-container">
    <!-- 管理员顶部导航 -->
    <div class="admin-header">
      <div class="admin-title">
        <span class="admin-icon">⚙️</span>
        <span>管理中心</span>
      </div>
      <div class="header-actions">
        <button class="switch-mode-btn" @click="switchToUserMode">
          <span>👤</span> 返回用户端
        </button>
        <div class="admin-user">
          <span class="user-name">{{ userStore.username }}</span>
          <span class="admin-badge">管理员</span>
        </div>
      </div>
    </div>

    <!-- 统计卡片 -->
    <div class="stats-container">
      <div class="stat-card">
        <div class="stat-icon" style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);">
          <span>👥</span>
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ stats.totalUsers }}</div>
          <div class="stat-label">总用户数</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon" style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);">
          <span>💰</span>
        </div>
        <div class="stat-info">
          <div class="stat-value">¥{{ formatNumber(salesStats.totalSales) }}</div>
          <div class="stat-label">总销售额</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon" style="background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);">
          <span>📦</span>
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ salesStats.totalOrders }}</div>
          <div class="stat-label">总订单</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon" style="background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);">
          <span>📅</span>
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ salesStats.todayOrders }}</div>
          <div class="stat-label">今日订单</div>
        </div>
      </div>
      <div class="stat-card warning">
        <div class="stat-icon" style="background: linear-gradient(135deg, #ff6b6b 0%, #ff8e53 100%);">
          <span>⚠️</span>
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ lowStockCount }}</div>
          <div class="stat-label">库存预警</div>
        </div>
      </div>
    </div>

    <!-- 标签页导航 -->
    <div class="tab-navigation">
      <button 
        :class="['tab-btn', { active: activeTab === 'products' }]" 
        @click="activeTab = 'products'"
      >
        📦 商品管理
      </button>
      <button 
        :class="['tab-btn', { active: activeTab === 'orders' }]" 
        @click="activeTab = 'orders'"
      >
        🛒 订单管理
        <span class="badge" v-if="orderStats.pendingOrders > 0">{{ orderStats.pendingOrders }}</span>
      </button>
      <button 
        :class="['tab-btn', { active: activeTab === 'sales' }]" 
        @click="activeTab = 'sales'"
      >
        📊 销售统计
      </button>
      <button 
        :class="['tab-btn', { active: activeTab === 'inventory' }]" 
        @click="activeTab = 'inventory'"
      >
        📋 库存管理
      </button>
      <button 
        :class="['tab-btn', { active: activeTab === 'users' }]" 
        @click="activeTab = 'users'"
      >
        👥 用户管理
      </button>
      <button 
        :class="['tab-btn', { active: activeTab === 'requests' }]" 
        @click="activeTab = 'requests'"
      >
        📝 权限申请
        <span class="badge" v-if="stats.pendingRequests > 0">{{ stats.pendingRequests }}</span>
      </button>
      <button
        :class="['tab-btn', { active: activeTab === 'service' }]"
        @click="activeTab = 'service'"
      >
        💬 客服消息
        <span class="badge" v-if="unreadMessages > 0">{{ unreadMessages }}</span>
      </button>
      <button
        :class="['tab-btn', { active: activeTab === 'messages' }]"
        @click="activeTab = 'messages'"
      >
        📧 留言管理
        <span class="badge" v-if="messageStats.pendingMessages > 0">{{ messageStats.pendingMessages }}</span>
      </button>
      <button
        :class="['tab-btn', { active: activeTab === 'ai-chat' }]"
        @click="activeTab = 'ai-chat'"
      >
        🤖 AI聊天记录
      </button>
    </div>

    <!-- 商品管理 -->
    <div class="content-card" v-if="activeTab === 'products'">
      <div class="card-header">
        <h3>商品列表</h3>
        <div class="header-actions">
          <select v-model="productFilter.category" class="filter-select" @change="loadProducts">
            <option value="all">全部分类</option>
            <option value="mouse">电竞鼠标</option>
            <option value="keyboard">机械键盘</option>
            <option value="headset">游戏耳机</option>
            <option value="controller">游戏手柄</option>
          </select>
          <input 
            type="text" 
            v-model="productFilter.keyword" 
            placeholder="搜索商品名称或描述..." 
            class="search-input"
            @input="handleSearchInput"
            @keyup.enter="loadProducts"
          />
          <button class="refresh-btn" @click="loadProducts" title="刷新商品列表">🔄 刷新</button>
          <button class="add-btn" @click="showProductModal = true">➕ 添加商品</button>
        </div>
      </div>
      <div class="table-container">
        <table class="data-table">
          <thead>
            <tr>
              <th>ID</th>
              <th>商品图片</th>
              <th>商品名称</th>
              <th>分类</th>
              <th>价格</th>
              <th>库存</th>
              <th>销量</th>
              <th>状态</th>
              <th>操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="product in products" :key="product.id">
              <td>{{ product.id }}</td>
              <td>
                <div class="product-thumb">
                  <img :src="product.image" :alt="product.name" />
                </div>
              </td>
              <td>{{ product.name }}</td>
              <td>
                <span class="category-badge">{{ getCategoryName(product.category) }}</span>
              </td>
              <td class="price">¥{{ product.price }}</td>
              <td :class="{'low-stock': product.stock < 10}">
                {{ product.stock }}
                <span v-if="product.stock < 10" class="warning-icon">⚠️</span>
              </td>
              <td>{{ product.sales }}</td>
              <td>
                <span :class="['status-badge', product.status === 1 ? 'active' : 'inactive']">
                  {{ product.status === 1 ? '在售' : '下架' }}
                </span>
              </td>
              <td>
                <div class="action-buttons">
                  <button 
                    class="action-btn status" 
                    @click="toggleProductStatus(product)"
                    :title="product.status === 1 ? '下架' : '上架'"
                  >
                    {{ product.status === 1 ? '⛔' : '✅' }}
                  </button>
                  <button class="action-btn edit" @click="editProduct(product)" title="编辑">✏️</button>
                  <button class="action-btn delete" @click="deleteProduct(product)" title="删除">🗑️</button>
                </div>
              </td>
            </tr>
            <tr v-if="products.length === 0">
              <td colspan="9" class="empty-row">暂无商品数据</td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- 销售统计 -->
    <div class="content-card" v-if="activeTab === 'sales'">
      <div class="card-header">
        <h3>销售统计</h3>
        <button class="refresh-btn" @click="loadSalesStats">🔄 刷新</button>
      </div>
      
      <!-- 统计概览 -->
      <div class="sales-overview">
        <div class="overview-card">
          <div class="overview-icon">💰</div>
          <div class="overview-info">
            <div class="overview-value">¥{{ formatNumber(salesStats.totalSales) }}</div>
            <div class="overview-label">总销售额</div>
          </div>
        </div>
        <div class="overview-card">
          <div class="overview-icon">📦</div>
          <div class="overview-info">
            <div class="overview-value">{{ salesStats.totalOrders }}</div>
            <div class="overview-label">总订单数</div>
          </div>
        </div>
        <div class="overview-card">
          <div class="overview-icon">📅</div>
          <div class="overview-info">
            <div class="overview-value">¥{{ formatNumber(salesStats.todaySales) }}</div>
            <div class="overview-label">今日销售额</div>
          </div>
        </div>
        <div class="overview-card">
          <div class="overview-icon">🛒</div>
          <div class="overview-info">
            <div class="overview-value">{{ salesStats.todayOrders }}</div>
            <div class="overview-label">今日订单</div>
          </div>
        </div>
      </div>

      <!-- 订单状态统计 -->
      <div class="order-status-stats">
        <h4>订单状态统计</h4>
        <div class="status-grid">
          <div class="status-item pending">
            <div class="status-count">{{ orderStats.pendingOrders }}</div>
            <div class="status-label">待支付</div>
          </div>
          <div class="status-item shipping">
            <div class="status-count">{{ orderStats.shippingOrders }}</div>
            <div class="status-label">待发货</div>
          </div>
          <div class="status-item completed">
            <div class="status-count">{{ orderStats.completedOrders }}</div>
            <div class="status-label">已完成</div>
          </div>
        </div>
      </div>
    </div>

    <!-- 库存管理 -->
    <div class="content-card" v-if="activeTab === 'inventory'">
      <div class="card-header">
        <h3>库存管理</h3>
        <button class="refresh-btn" @click="loadSalesStats">🔄 刷新</button>
      </div>
      
      <!-- 库存预警列表 -->
      <div class="inventory-warning" v-if="lowStockProducts.length > 0">
        <div class="warning-header">
          <span class="warning-icon">⚠️</span>
          <span>库存预警商品 ({{ lowStockProducts.length }})</span>
        </div>
        <div class="warning-list">
          <div class="warning-item" v-for="product in lowStockProducts" :key="product.id">
            <img :src="product.image" :alt="product.name" class="warning-thumb" />
            <div class="warning-info">
              <div class="warning-name">{{ product.name }}</div>
              <div class="warning-category">{{ getCategoryName(product.category) }}</div>
            </div>
            <div class="warning-stock">
              <span class="stock-value" :class="{danger: product.stock < 5}">{{ product.stock }}</span>
              <span class="stock-label">件</span>
            </div>
            <button class="stock-btn" @click="quickUpdateStock(product)">补货</button>
          </div>
        </div>
      </div>
      
      <div class="inventory-empty" v-else>
        <span class="empty-icon">✅</span>
        <p>所有商品库存充足</p>
      </div>
      
      <!-- 全部商品库存 -->
      <div class="inventory-all">
        <h4>全部商品库存</h4>
        <div class="inventory-grid">
          <div class="inventory-card" v-for="product in products" :key="product.id">
            <img :src="product.image" :alt="product.name" class="inv-thumb" />
            <div class="inv-info">
              <div class="inv-name">{{ product.name }}</div>
              <div class="inv-category">{{ getCategoryName(product.category) }}</div>
            </div>
            <div class="inv-stock" :class="{low: product.stock < 10, danger: product.stock < 5}">
              {{ product.stock }}
            </div>
            <input 
              type="number" 
              v-model="product.stock" 
              class="stock-input"
              @change="updateProductStock(product)"
              min="0"
            />
          </div>
        </div>
      </div>
    </div>

    <!-- 用户管理 -->
    <div class="content-card" v-if="activeTab === 'users'">
      <div class="card-header">
        <h3>用户列表</h3>
        <button class="refresh-btn" @click="loadUsers">🔄 刷新</button>
      </div>
      <div class="table-container">
        <table class="data-table">
          <thead>
            <tr>
              <th>ID</th>
              <th>用户名</th>
              <th>昵称</th>
              <th>邮箱</th>
              <th>角色</th>
              <th>状态</th>
              <th>注册时间</th>
              <th>操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="user in users" :key="user.id">
              <td>{{ user.id }}</td>
              <td>{{ user.username }}</td>
              <td>{{ user.nickname || '-' }}</td>
              <td>{{ user.email || '-' }}</td>
              <td>
                <span :class="['role-badge', user.role]">{{ user.role === 'admin' ? '管理员' : '用户' }}</span>
              </td>
              <td>
                <span :class="['status-badge', user.status === 1 ? 'active' : 'inactive']">
                  {{ user.status === 1 ? '正常' : '禁用' }}
                </span>
              </td>
              <td>{{ formatDate(user.create_time) }}</td>
              <td>
                <div class="action-buttons">
                  <button 
                    class="action-btn status" 
                    @click="toggleUserStatus(user)"
                    :title="user.status === 1 ? '禁用' : '启用'"
                  >
                    {{ user.status === 1 ? '⛔' : '✅' }}
                  </button>
                  <button 
                    v-if="user.role !== 'admin'"
                    class="action-btn promote" 
                    @click="promoteToAdmin(user)"
                    title="提升为管理员"
                  >
                    ⭐
                  </button>
                  <button 
                    v-if="user.role === 'admin' && user.username !== 'admin'"
                    class="action-btn revoke" 
                    @click="revokeAdmin(user)"
                    title="撤销权限"
                  >
                    ❌
                  </button>
                  <button 
                    v-if="user.role !== 'admin'"
                    class="action-btn delete" 
                    @click="deleteUser(user)"
                    title="删除用户"
                  >
                    🗑️
                  </button>
                </div>
              </td>
            </tr>
            <tr v-if="users.length === 0">
              <td colspan="8" class="empty-row">暂无用户数据</td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- 权限申请 -->
    <div class="content-card" v-if="activeTab === 'requests'">
      <div class="card-header">
        <h3>权限申请列表</h3>
        <button class="refresh-btn" @click="loadPendingRequests">🔄 刷新</button>
      </div>
      <div class="table-container">
        <table class="data-table">
          <thead>
            <tr>
              <th>ID</th>
              <th>用户名</th>
              <th>昵称</th>
              <th>申请时间</th>
              <th>状态</th>
              <th>操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="request in pendingRequests" :key="request.id">
              <td>{{ request.id }}</td>
              <td>{{ request.username }}</td>
              <td>{{ request.nickname || '-' }}</td>
              <td>{{ formatDate(request.admin_request_time) }}</td>
              <td>
                <span class="status-badge pending">待审批</span>
              </td>
              <td>
                <div class="action-buttons">
                  <button class="action-btn approve" @click="handleRequest(request, true)">✅ 批准</button>
                  <button class="action-btn reject" @click="handleRequest(request, false)">❌ 拒绝</button>
                </div>
              </td>
            </tr>
            <tr v-if="pendingRequests.length === 0">
              <td colspan="6" class="empty-row">暂无待审批的申请</td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- 订单管理 -->
    <div class="content-card" v-if="activeTab === 'orders'">
      <div class="card-header">
        <h3>订单列表</h3>
        <div class="header-actions">
          <select v-model="orderFilter.status" class="filter-select" @change="loadOrders">
            <option value="">全部状态</option>
            <option value="pending">待支付</option>
            <option value="paid">已支付</option>
            <option value="shipping">待发货</option>
            <option value="shipped">已发货</option>
            <option value="completed">已完成</option>
            <option value="cancelled">已取消</option>
          </select>
          <input 
            type="text" 
            v-model="orderFilter.keyword" 
            placeholder="搜索订单号/用户/收货人..." 
            class="search-input"
            @input="loadOrders"
          />
          <button class="refresh-btn" @click="loadOrders">🔄 刷新</button>
        </div>
      </div>
      <div class="table-container">
        <table class="data-table">
          <thead>
            <tr>
              <th>订单号</th>
              <th>用户</th>
              <th>收货人</th>
              <th>订单金额</th>
              <th>状态</th>
              <th>下单时间</th>
              <th>操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="order in orders" :key="order.id">
              <td>{{ order.orderNo }}</td>
              <td>{{ order.username }}</td>
              <td>{{ order.receiverName }}</td>
              <td class="price">¥{{ order.payAmount }}</td>
              <td>
                <span :class="['order-status-badge', order.status]">
                  {{ getOrderStatusText(order.status) }}
                </span>
              </td>
              <td>{{ formatDateTime(order.createTime) }}</td>
              <td>
                <div class="action-buttons">
                  <button class="action-btn view" @click="viewOrderDetail(order)" title="查看详情">👁️</button>
                  <button 
                    class="action-btn ship" 
                    @click="updateOrderStatus(order.id, 'shipped')" 
                    title="发货"
                    v-if="order.status === 'paid' || order.status === 'shipping'"
                  >
                    📦
                  </button>
                  <button 
                    class="action-btn complete" 
                    @click="updateOrderStatus(order.id, 'completed')" 
                    title="完成"
                    v-if="order.status === 'shipped'"
                  >
                    ✅
                  </button>
                </div>
              </td>
            </tr>
            <tr v-if="orders.length === 0">
              <td colspan="7" class="empty-row">暂无订单数据</td>
            </tr>
          </tbody>
        </table>
      </div>
      <!-- 分页 -->
      <div class="pagination">
        <button 
          class="page-btn" 
          :disabled="orderPagination.page === 1" 
          @click="changeOrderPage(orderPagination.page - 1)"
        >
          上一页
        </button>
        <span class="page-info">第 {{ orderPagination.page }} 页 / 共 {{ orderPagination.pages }} 页</span>
        <button 
          class="page-btn" 
          :disabled="orderPagination.page >= orderPagination.pages" 
          @click="changeOrderPage(orderPagination.page + 1)"
        >
          下一页
        </button>
      </div>
    </div>

    <!-- 客服消息 -->
    <div class="content-card" v-if="activeTab === 'service'">
      <div class="card-header">
        <h3>客服消息</h3>
        <button class="refresh-btn" @click="loadMessages">🔄 刷新</button>
      </div>
      
      <div class="service-container">
        <!-- 对话列表 -->
        <div class="message-list">
          <div 
            class="message-item" 
            v-for="conv in conversations" 
            :key="conv.username"
            :class="{ unread: conv.unreadCount > 0, active: selectedUsername === conv.username }"
            @click="loadConversation(conv.username)"
          >
            <div class="msg-avatar">
              <img v-if="conv.avatar" :src="conv.avatar" alt="头像" />
              <span v-else>{{ (conv.username || '?').charAt(0).toUpperCase() }}</span>
            </div>
            <div class="msg-content">
              <div class="msg-header">
                <span class="msg-username">{{ conv.username }}</span>
                <span class="msg-time">{{ formatDate(conv.lastTime) }}</span>
              </div>
              <div class="msg-preview">{{ conv.lastMessage }}</div>
            </div>
            <span class="msg-status" v-if="conv.unreadCount > 0">{{ conv.unreadCount }}</span>
          </div>
          <div class="message-empty" v-if="conversations.length === 0">
            <span class="empty-icon">📭</span>
            <p>暂无客服消息</p>
          </div>
        </div>
        
        <!-- 消息详情 -->
        <div class="message-detail" v-if="selectedUsername">
          <div class="detail-header">
            <div class="detail-user">
              <div class="detail-avatar">
                <img v-if="selectedUserAvatar" :src="selectedUserAvatar" alt="头像" />
                <span v-else>{{ (selectedUsername || '?').charAt(0).toUpperCase() }}</span>
              </div>
              <div class="detail-info">
                <div class="detail-username">{{ selectedUsername }}</div>
              </div>
            </div>
          </div>
          <div class="detail-content" ref="detailContentRef">
            <div 
              class="detail-message-item"
              v-for="msg in currentMessages"
              :key="msg.id"
              :class="msg.isMine ? 'is-user-msg' : 'is-service-msg'"
            >
              <!-- 用户发送的消息 (isMine=true)：显示在左边，头像在左，气泡在头像右边 -->
              <template v-if="msg.isMine">
                <div class="message-avatar service-avatar">
                  <img v-if="msg.avatar" :src="msg.avatar" alt="头像" />
                  <span v-else>{{ selectedUsername?.charAt(0).toUpperCase() }}</span>
                </div>
                <div class="message-bubble service-bubble">
                  <div class="detail-text">{{ msg.content }}</div>
                  <div class="message-time">{{ msg.time }}</div>
                </div>
              </template>
              <!-- 管理员发送的消息 (isMine=false)：显示在右边，头像在右，气泡在头像左边 -->
              <template v-else>
                <div class="message-bubble user-bubble">
                  <div class="detail-text">{{ msg.content }}</div>
                  <div class="message-time">{{ msg.time }}</div>
                </div>
                <div class="message-avatar user-avatar">
                  <img v-if="userStore.avatar" :src="userStore.avatar" alt="头像" />
                  <span v-else>{{ userStore.username?.charAt(0).toUpperCase() }}</span>
                </div>
              </template>
            </div>
            <div class="no-messages" v-if="currentMessages.length === 0">
              <p>暂无消息记录</p>
            </div>
          </div>
          <div class="detail-reply">
            <textarea 
              v-model="replyContent" 
              placeholder="请输入回复内容..." 
              class="reply-input"
              @keydown.enter.exact.prevent="sendReply"
            ></textarea>
            <button class="reply-btn" @click="sendReply">发送回复</button>
          </div>
        </div>
        
        <div class="message-detail-empty" v-else>
          <span class="empty-icon">💬</span>
          <p>选择一个用户查看对话</p>
        </div>
      </div>
    </div>

    <!-- 留言管理 -->
    <div class="content-card" v-if="activeTab === 'messages'">
      <div class="card-header">
        <h3>留言列表</h3>
        <div class="header-actions">
          <select v-model="messageFilter.status" class="filter-select" @change="loadMessages">
            <option value="">全部状态</option>
            <option value="0">待处理</option>
            <option value="1">已处理</option>
          </select>
          <input
            type="text"
            v-model="messageFilter.keyword"
            placeholder="搜索姓名/邮箱/内容..."
            class="search-input"
            @input="loadMessages"
          />
          <button class="refresh-btn" @click="loadContactMessages">🔄 刷新</button>
        </div>
      </div>
      <div class="table-container">
        <table class="data-table">
          <thead>
            <tr>
              <th>ID</th>
              <th>姓名</th>
              <th>邮箱</th>
              <th>电话</th>
              <th>类型</th>
              <th>留言内容</th>
              <th>状态</th>
              <th>提交时间</th>
              <th>操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="msg in messages" :key="msg.id">
              <td>{{ msg.id }}</td>
              <td>{{ msg.name }}</td>
              <td>{{ msg.email || '-' }}</td>
              <td>{{ msg.phone || '-' }}</td>
              <td>{{ getMessageTypeText(msg.type) }}</td>
              <td class="message-content">{{ msg.message }}</td>
              <td>
                <span :class="['status-badge', msg.status === 0 ? 'pending' : 'processed']">
                  {{ msg.status === 0 ? '待处理' : '已处理' }}
                </span>
              </td>
              <td>{{ formatDateTime(msg.createTime) }}</td>
              <td>
                <div class="action-buttons">
                  <button class="action-btn view" @click="viewMessageDetail(msg)" title="查看详情">👁️</button>
                  <button
                    class="action-btn reply"
                    @click="showReplyModal(msg)"
                    title="回复"
                    v-if="msg.status === 0"
                  >
                    💬
                  </button>
                  <button
                    class="action-btn delete"
                    @click="deleteMessage(msg)"
                    title="删除"
                  >
                    🗑️
                  </button>
                </div>
              </td>
            </tr>
            <tr v-if="messages.length === 0">
              <td colspan="9" class="empty-row">暂无留言数据</td>
            </tr>
          </tbody>
        </table>
      </div>
      <!-- 分页 -->
      <div class="pagination">
        <button
          class="page-btn"
          :disabled="messagePagination.page === 1"
          @click="changeMessagePage(messagePagination.page - 1)"
        >
          上一页
        </button>
        <span class="page-info">第 {{ messagePagination.page }} 页 / 共 {{ messagePagination.pages }} 页</span>
        <button
          class="page-btn"
          :disabled="messagePagination.page >= messagePagination.pages"
          @click="changeMessagePage(messagePagination.page + 1)"
        >
          下一页
        </button>
      </div>
    </div>

    <!-- AI聊天记录 -->
    <div class="content-card" v-if="activeTab === 'ai-chat'">
      <div class="card-header">
        <h3>AI客服聊天记录</h3>
        <div class="header-actions">
          <button class="refresh-btn" @click="loadAISessions">🔄 刷新</button>
        </div>
      </div>
      <div class="table-container">
        <table class="data-table">
          <thead>
            <tr>
              <th>用户名</th>
              <th>会话ID</th>
              <th>消息数量</th>
              <th>最后一条消息</th>
              <th>最后活跃时间</th>
              <th>操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="session in aiSessions" :key="session.session_id">
              <td>{{ session.username }}</td>
              <td>{{ session.session_id }}</td>
              <td>{{ session.message_count }}</td>
              <td class="last-message">{{ truncateMessage(session.last_message) }}</td>
              <td>{{ formatDateTime(session.last_message_time) }}</td>
              <td>
                <button class="action-btn view" @click="viewChatDetail(session)" title="查看详情">👁️</button>
              </td>
            </tr>
            <tr v-if="aiSessions.length === 0">
              <td colspan="6" class="empty-row">暂无AI聊天记录</td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- 商品添加/编辑弹窗 -->
    <div class="modal-overlay" v-if="showProductModal" @click.self="closeProductModal">
      <div class="modal-content product-modal">
        <div class="modal-header">
          <h3>{{ editingProduct ? '编辑商品' : '添加商品' }}</h3>
          <button class="modal-close" @click="closeProductModal">×</button>
        </div>
        <div class="modal-body">
          <div class="form-group">
            <label>商品名称</label>
            <input type="text" v-model="productForm.name" placeholder="请输入商品名称" />
          </div>
          <div class="form-group">
            <label>商品分类</label>
            <select v-model="productForm.category">
              <option value="mouse">电竞鼠标</option>
              <option value="keyboard">机械键盘</option>
              <option value="headset">游戏耳机</option>
              <option value="controller">游戏手柄</option>
            </select>
          </div>
          <div class="form-group">
            <label>商品价格</label>
            <input type="number" v-model="productForm.price" placeholder="请输入价格" min="0" step="0.01" />
          </div>
          <div class="form-group">
            <label>商品库存</label>
            <input type="number" v-model="productForm.stock" placeholder="请输入库存数量" min="0" />
          </div>
          <div class="form-group">
            <label>商品图片</label>
            <div class="image-upload">
              <!-- 图片预览 - 采用与商品列表相同的显示逻辑 -->
              <div v-if="productForm.image && productForm.image !== ''" class="image-preview">
                <img :src="productForm.image" :alt="productForm.name || '商品图片'" />
                <button @click="removeImage" class="remove-image-btn">×</button>
              </div>
              
              <!-- 上传区域 -->
              <input 
                type="file" 
                ref="imageInputRef" 
                accept="image/*" 
                @change="handleImageUpload"
                style="display: none"
              />
              <div 
                class="upload-area" 
                @click="$refs.imageInputRef?.click()"
                :class="{ 'has-image': productForm.image && productForm.image !== '' }"
                v-if="!productForm.image || productForm.image === ''"
              >
                <div class="upload-placeholder">
                  <span class="upload-icon">📷</span>
                  <span class="upload-text">点击选择图片</span>
                </div>
              </div>
            </div>
          </div>
          <div class="form-group">
            <label>商品描述</label>
            <textarea v-model="productForm.description" placeholder="请输入商品描述" rows="3"></textarea>
          </div>
          <div class="form-group">
            <label>商品特点（用逗号分隔）</label>
            <input type="text" v-model="productForm.features" placeholder="特点1,特点2,特点3" />
          </div>
          <div class="form-group">
            <label>商品状态</label>
            <select v-model="productForm.status">
              <option value="active">在售</option>
              <option value="inactive">下架</option>
            </select>
          </div>
        </div>
        <div class="modal-footer">
          <button class="cancel-btn" @click="closeProductModal">取消</button>
          <button class="confirm-btn" @click="saveProduct">{{ editingProduct ? '保存' : '添加' }}</button>
        </div>
      </div>
    </div>

    <!-- 快速补货弹窗 -->
    <div class="modal-overlay" v-if="showStockModal" @click.self="showStockModal = false">
      <div class="modal-content stock-modal">
        <div class="modal-header">
          <h3>快速补货 - {{ stockProduct?.name }}</h3>
          <button class="modal-close" @click="showStockModal = false">×</button>
        </div>
        <div class="modal-body">
          <div class="current-stock">
            当前库存: <span class="stock-num">{{ stockProduct?.stock }}</span>
          </div>
          <div class="form-group">
            <label>补货数量</label>
            <input type="number" v-model="addStockNum" min="1" />
          </div>
        </div>
        <div class="modal-footer">
          <button class="cancel-btn" @click="showStockModal = false">取消</button>
          <button class="confirm-btn" @click="confirmAddStock">确认补货</button>
        </div>
      </div>
    </div>

    <!-- 留言详情弹窗 -->
    <div class="modal-overlay" v-if="showMessageDetailModal" @click.self="showMessageDetailModal = false">
      <div class="modal-content message-detail-modal">
        <div class="modal-header">
          <h3>留言详情</h3>
          <button class="modal-close" @click="showMessageDetailModal = false">×</button>
        </div>
        <div class="modal-body">
          <div class="detail-section">
            <h4>留言信息</h4>
            <div class="detail-row">
              <span class="detail-label">姓名：</span>
              <span class="detail-value">{{ currentMessage?.name }}</span>
            </div>
            <div class="detail-row">
              <span class="detail-label">邮箱：</span>
              <span class="detail-value">{{ currentMessage?.email || '-' }}</span>
            </div>
            <div class="detail-row">
              <span class="detail-label">电话：</span>
              <span class="detail-value">{{ currentMessage?.phone || '-' }}</span>
            </div>
            <div class="detail-row">
              <span class="detail-label">类型：</span>
              <span class="detail-value">{{ getMessageTypeText(currentMessage?.type) }}</span>
            </div>
            <div class="detail-row">
              <span class="detail-label">状态：</span>
              <span :class="['status-badge', currentMessage?.status === 0 ? 'pending' : 'processed']">
                {{ currentMessage?.status === 0 ? '待处理' : '已处理' }}
              </span>
            </div>
            <div class="detail-row">
              <span class="detail-label">提交时间：</span>
              <span class="detail-value">{{ formatDateTime(currentMessage?.createTime) }}</span>
            </div>
          </div>
          <div class="detail-section">
            <h4>留言内容</h4>
            <div class="message-content-full">{{ currentMessage?.message }}</div>
          </div>
          <div class="detail-section" v-if="currentMessage?.reply">
            <h4>回复内容</h4>
            <div class="reply-content-full">{{ currentMessage?.reply }}</div>
          </div>
        </div>
        <div class="modal-footer">
          <button class="cancel-btn" @click="showMessageDetailModal = false">关闭</button>
          <button
            class="confirm-btn"
            @click="showReplyModal(currentMessage)"
            v-if="currentMessage?.status === 0"
          >
            回复
          </button>
        </div>
      </div>
    </div>

    <!-- 回复留言弹窗 -->
    <div class="modal-overlay" v-if="showReplyModalVisible" @click.self="showReplyModalVisible = false">
      <div class="modal-content reply-modal">
        <div class="modal-header">
          <h3>回复留言</h3>
          <button class="modal-close" @click="showReplyModalVisible = false">×</button>
        </div>
        <div class="modal-body">
          <div class="message-preview">
            <div class="preview-label">原留言：</div>
            <div class="preview-content">{{ currentMessage?.message }}</div>
          </div>
          <div class="form-group">
            <label>回复内容</label>
            <textarea v-model="replyContent" placeholder="请输入回复内容..." rows="5"></textarea>
          </div>
        </div>
        <div class="modal-footer">
          <button class="cancel-btn" @click="showReplyModalVisible = false">取消</button>
          <button class="confirm-btn" @click="submitReply">发送回复</button>
        </div>
      </div>
    </div>

    <!-- AI聊天记录详情弹窗 -->
    <div class="modal-overlay" v-if="showChatDetailModal" @click.self="showChatDetailModal = false">
      <div class="modal-content chat-detail-modal">
        <div class="modal-header">
          <h3>AI聊天记录详情 - {{ currentChatSession?.username }}</h3>
          <button class="modal-close" @click="showChatDetailModal = false">×</button>
        </div>
        <div class="modal-body">
          <div class="chat-info">
            <div class="chat-info-row">
              <label>用户名:</label>
              <span>{{ currentChatSession?.username }}</span>
            </div>
            <div class="chat-info-row">
              <label>会话ID:</label>
              <span>{{ currentChatSession?.session_id }}</span>
            </div>
            <div class="chat-info-row">
              <label>消息数量:</label>
              <span>{{ currentChatMessages?.length || 0 }}</span>
            </div>
          </div>
          <div class="chat-messages">
            <div v-if="currentChatMessages.length === 0" class="empty-messages">
              <span class="empty-icon">💬</span>
              <p>暂无聊天记录</p>
            </div>
            <div
              v-for="msg in currentChatMessages"
              :key="msg.id"
              class="chat-message"
              :class="{ 'is-user': msg.isUser }"
            >
              <div class="message-avatar">
                <span>{{ msg.isUser ? '👤' : '🤖' }}</span>
              </div>
              <div class="message-content">
                <div class="message-text">{{ msg.message }}</div>
                <div class="message-time">{{ formatDateTime(msg.createdAt) }}</div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 订单详情弹窗 -->
    <div class="modal-overlay" v-if="showOrderDetailModal" @click.self="showOrderDetailModal = false">
      <div class="modal-content order-detail-modal">
        <div class="modal-header">
          <h3>订单详情 - {{ currentOrder?.orderNo }}</h3>
          <button class="modal-close" @click="showOrderDetailModal = false">×</button>
        </div>
        <div class="modal-body">
          <!-- 订单基本信息 -->
          <div class="order-detail-section">
            <h4>订单信息</h4>
            <div class="detail-row">
              <span class="detail-label">订单号：</span>
              <span class="detail-value">{{ currentOrder?.orderNo }}</span>
            </div>
            <div class="detail-row">
              <span class="detail-label">用户：</span>
              <span class="detail-value">{{ currentOrder?.username }}</span>
            </div>
            <div class="detail-row">
              <span class="detail-label">订单状态：</span>
              <span :class="['order-status-badge', currentOrder?.status]">
                {{ getOrderStatusText(currentOrder?.status) }}
              </span>
            </div>
            <div class="detail-row">
              <span class="detail-label">下单时间：</span>
              <span class="detail-value">{{ formatDateTime(currentOrder?.createTime) }}</span>
            </div>
          </div>

          <!-- 收货信息 -->
          <div class="order-detail-section">
            <h4>收货信息</h4>
            <div class="detail-row">
              <span class="detail-label">收货人：</span>
              <span class="detail-value">{{ currentOrder?.receiverName }}</span>
            </div>
            <div class="detail-row">
              <span class="detail-label">联系电话：</span>
              <span class="detail-value">{{ currentOrder?.receiverPhone }}</span>
            </div>
            <div class="detail-row">
              <span class="detail-label">收货地址：</span>
              <span class="detail-value">{{ currentOrder?.receiverAddress }}</span>
            </div>
          </div>

          <!-- 订单金额 -->
          <div class="order-detail-section">
            <h4>金额信息</h4>
            <div class="detail-row">
              <span class="detail-label">订单总额：</span>
              <span class="detail-value">¥{{ currentOrder?.totalAmount }}</span>
            </div>
            <div class="detail-row">
              <span class="detail-label">优惠金额：</span>
              <span class="detail-value">-¥{{ currentOrder?.discountAmount }}</span>
            </div>
            <div class="detail-row">
              <span class="detail-label">实付金额：</span>
              <span class="detail-value price">¥{{ currentOrder?.payAmount }}</span>
            </div>
            <div class="detail-row" v-if="currentOrder?.payTime">
              <span class="detail-label">支付时间：</span>
              <span class="detail-value">{{ formatDateTime(currentOrder?.payTime) }}</span>
            </div>
          </div>

          <!-- 订单商品 -->
          <div class="order-detail-section">
            <h4>订单商品</h4>
            <div class="order-items-list">
              <div class="order-item-row" v-for="item in currentOrderItems" :key="item.id">
                <img :src="item.productImage" :alt="item.productName" class="order-item-image" />
                <div class="order-item-info">
                  <div class="order-item-name">{{ item.productName }}</div>
                  <div class="order-item-specs">
                    <span>单价：¥{{ item.price }}</span>
                    <span>数量：{{ item.quantity }}</span>
                  </div>
                </div>
                <div class="order-item-total">¥{{ item.subtotal }}</div>
              </div>
            </div>
          </div>

          <!-- 备注 -->
          <div class="order-detail-section" v-if="currentOrder?.remark">
            <h4>订单备注</h4>
            <div class="order-remark">{{ currentOrder.remark }}</div>
          </div>
        </div>
        <div class="modal-footer">
          <button class="cancel-btn" @click="showOrderDetailModal = false">关闭</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, reactive, onMounted, onUnmounted, computed, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { userStore, toggleAdminMode } from '../stores/userStore'
import {
  getAdminUsers,
  getPendingRequests,
  approveRequest,
  revokeAdmin as revokeAdminApi,
  getAdminStats,
  updateUserStatus,
  deleteUser as deleteUserApi,
  getProducts,
  addProduct,
  updateProduct,
  deleteProduct as deleteProductApi,
  getSalesStats,
  getOrderStats,
  updateStock,
  getAllCustomerMessages,
  markMessageAsRead,
  sendCustomerReply,
  getOrders,
  getOrderDetail,
  updateOrderStatus as updateOrderStatusApi,
  getContactMessages,
  updateMessageStatus,
  replyMessage as replyMessageApi,
  deleteMessage as deleteMessageApi,
  getAllAISessions,
  getAISessionMessages
} from '../api'
import { useWebSocket } from '../composables/useWebSocket'

export default {
  name: 'Admin',
  setup() {
    const router = useRouter()
    const activeTab = ref('products')
    
    // WebSocket通知处理
    const handleWebSocketNotification = async (event) => {
      const data = event.detail
      console.log('收到WebSocket通知:', data)
      
      if (data.type === 'new_admin_request') {
        // 刷新待处理申请列表
        await loadPendingRequests()
        alert(`新管理员申请通知: ${data.message}`)
      }
    }
    
    // 使用WebSocket连接到通知服务
    let username = ''
    const savedUser = sessionStorage.getItem('currentUser')
    if (savedUser) {
      const user = JSON.parse(savedUser)
      username = user.username || 'admin'
    }
    
    const { connected, notifications, connect, disconnect, clearNotifications } = useWebSocket(username)
    
    // 用户管理相关
    const users = ref([])
    const pendingRequests = ref([])
    const stats = reactive({
      totalUsers: 0,
      adminUsers: 0,
      pendingRequests: 0,
      activeUsers: 0
    })
    
    // 商品管理相关
    const products = ref([])
    const productFilter = reactive({ category: 'all', keyword: '' })
    let searchTimeout = null
    
    // 带防抖的搜索函数
    const handleSearchInput = () => {
      if (searchTimeout) {
        clearTimeout(searchTimeout)
      }
      searchTimeout = setTimeout(() => {
        loadProducts()
      }, 500) // 500ms 后才执行搜索
    }
    const showProductModal = ref(false)
    const editingProduct = ref(null)
    const productForm = reactive({
      name: '',
      category: 'mouse',
      description: '',
      price: 0,
      stock: 0,
      image: '',
      features: '',
      status: 'active'
    })
    
    // 销售统计相关
    const salesStats = reactive({
      totalSales: 0,
      totalOrders: 0,
      todaySales: 0,
      todayOrders: 0
    })
    const categoryStats = ref([])
    const lowStockProducts = ref([])
    
    // 订单管理相关
    const orders = ref([])
    const orderFilter = reactive({ status: '', keyword: '' })
    const orderPagination = reactive({ page: 1, size: 10, pages: 1, total: 0 })
    const orderStats = reactive({
      totalSales: 0,
      totalOrders: 0,
      todaySales: 0,
      todayOrders: 0,
      pendingOrders: 0,
      shippingOrders: 0,
      completedOrders: 0
    })
    const showOrderDetailModal = ref(false)
    const currentOrder = ref(null)
    const currentOrderItems = ref([])
    
    // 库存管理相关
    const showStockModal = ref(false)
    const stockProduct = ref(null)
    const addStockNum = ref(50)

    // 留言管理相关
    const messages = ref([])
    const messageFilter = reactive({ status: '', keyword: '' })
    const messagePagination = reactive({ page: 1, size: 10, pages: 1, total: 0 })
    const messageStats = reactive({
      pendingMessages: 0
    })
    const showMessageDetailModal = ref(false)
    const showReplyModalVisible = ref(false)
    const currentMessage = ref(null)
    const replyContent = ref('')
    
    // 客服消息相关
    const conversations = ref([])  // 对话列表（按用户分组）
    let messageRefreshTimer = null  // 客服消息自动刷新定时器
    const currentMessages = ref([])  // 当前选中的对话消息
    const detailContentRef = ref(null)  // 消息内容区域引用
    const selectedUsername = ref(null)
    const selectedUserAvatar = ref('')  // 当前选中用户的头像
    const unreadMessages = computed(() => conversations.value.reduce((sum, c) => sum + c.unreadCount, 0))
    const lowStockCount = computed(() => lowStockProducts.value.length)

    // AI聊天记录相关
    const aiSessions = ref([])
    const showChatDetailModal = ref(false)
    const currentChatSession = ref(null)
    const currentChatMessages = ref([])

    // 加载商品列表
    const loadProducts = async () => {
      console.log('开始加载商品列表，分类:', productFilter.category, '关键词:', productFilter.keyword)
      const result = await getProducts(productFilter.category, productFilter.keyword)
      console.log('商品列表返回结果:', result)
      
      if (result.success && result.data) {
        products.value = result.data.products || []
        console.log('商品列表加载成功，商品数量:', products.value.length)
        if (products.value.length > 0) {
          console.log('第一个商品:', products.value[0])
        }
      } else {
        console.error('商品列表加载失败:', result)
        products.value = []
      }
    }
    
    // 加载销售统计
    const loadSalesStats = async () => {
      const result = await getSalesStats()
      if (result.success) {
        const statsData = result.data || result.stats
        salesStats.totalSales = statsData?.totalSales || 0
        salesStats.totalOrders = statsData?.totalOrders || 0
        salesStats.todaySales = statsData?.todaySales || 0
        salesStats.todayOrders = statsData?.todayOrders || 0
        categoryStats.value = statsData?.categoryStats || []
        lowStockProducts.value = statsData?.lowStockProducts || []
      }
    }

    // 加载订单列表
    const loadOrders = async () => {
      const result = await getOrders(orderPagination.page, orderPagination.size, orderFilter.keyword, orderFilter.status)
      if (result.success) {
        const data = result.data || result
        orders.value = data?.records || []
        orderPagination.pages = data?.pages || 1
        orderPagination.total = data?.total || 0
      }
      // 同时刷新订单统计数据
      await loadOrderStats()
    }

    // 切换订单页码
    const changeOrderPage = (page) => {
      if (page < 1 || page > orderPagination.pages) return
      orderPagination.page = page
      loadOrders()
    }

    // 查看订单详情
    const viewOrderDetail = async (order) => {
      currentOrder.value = order
      const result = await getOrderDetail(order.id)
      if (result.success) {
        const data = result.data || result
        currentOrderItems.value = data?.items || []
      }
      showOrderDetailModal.value = true
    }

    // 更新订单状态
    const updateOrderStatus = async (id, status) => {
      const result = await updateOrderStatusApi(id, status)
      if (result.success) {
        alert('订单状态更新成功')
        loadOrders()
        loadOrderStats()
      } else {
        alert(result.message || '更新失败')
      }
    }

    // 加载订单统计
    const loadOrderStats = async () => {
      const result = await getOrderStats()
      if (result.success) {
        const statsData = result.data || result
        orderStats.totalSales = statsData?.totalSales || 0
        orderStats.totalOrders = statsData?.totalOrders || 0
        orderStats.todaySales = statsData?.todaySales || 0
        orderStats.todayOrders = statsData?.todayOrders || 0
        orderStats.pendingOrders = statsData?.pendingOrders || 0
        orderStats.shippingOrders = statsData?.shippingOrders || 0
        orderStats.completedOrders = statsData?.completedOrders || 0
      }
    }

    // 获取订单状态文本
    const getOrderStatusText = (status) => {
      const statusMap = {
        pending: '待支付',
        paid: '已支付',
        shipping: '待发货',
        shipped: '已发货',
        completed: '已完成',
        cancelled: '已取消'
      }
      return statusMap[status] || status
    }

    // 格式化日期时间
    const formatDateTime = (date) => {
      if (!date) return '-'
      return new Date(date).toLocaleString('zh-CN')
    }

    // 留言管理相关函数
    const loadContactMessages = async () => {
      const result = await getContactMessages(messagePagination.page, messagePagination.size, messageFilter.keyword, messageFilter.status !== '' ? parseInt(messageFilter.status) : null)
      if (result.success) {
        const data = result.data || result
        messages.value = data?.records || []
        messagePagination.pages = data?.pages || 1
        messagePagination.total = data?.total || 0

        // 统计待处理留言
        const allMessages = await getContactMessages(1, 1000, '', 0)
        if (allMessages.success) {
          messageStats.pendingMessages = allMessages.data?.total || 0
        }
      }
    }

    const changeMessagePage = (page) => {
      if (page < 1 || page > messagePagination.pages) return
      messagePagination.page = page
      loadContactMessages()
    }

    const getMessageTypeText = (type) => {
      const typeMap = {
        'product': '产品咨询',
        'order': '订单问题',
        'aftersale': '售后服务',
        'cooperation': '商务合作',
        'other': '其他'
      }
      return typeMap[type] || type
    }

    const viewMessageDetail = (msg) => {
      currentMessage.value = msg
      showMessageDetailModal.value = true
    }

    const showReplyModal = (msg) => {
      currentMessage.value = msg
      replyContent.value = msg.reply || ''
      showReplyModalVisible.value = true
    }

    const submitReply = async () => {
      if (!replyContent.value.trim()) {
        alert('请输入回复内容')
        return
      }

      const result = await replyMessageApi(currentMessage.value.id, replyContent.value)
      if (result.success) {
        alert('回复成功')
        showReplyModalVisible.value = false
        showMessageDetailModal.value = false
        loadContactMessages()
      } else {
        alert('回复失败：' + (result.message || '未知错误'))
      }
    }

    // 加载AI会话列表
    const loadAISessions = async () => {
      try {
        const result = await getAllAISessions()
        console.log('获取AI会话列表结果:', result)
        if (result.success) {
          aiSessions.value = result.data || []
          console.log('AI会话数量:', aiSessions.value.length)
        } else {
          console.error('获取AI会话列表失败:', result.message)
          alert('获取AI会话列表失败：' + (result.message || '未知错误'))
        }
      } catch (error) {
        console.error('加载AI会话列表失败:', error)
        alert('加载AI会话列表失败')
      }
    }

    // 查看聊天详情
    const viewChatDetail = async (session) => {
      console.log('查看聊天详情:', session)
      currentChatSession.value = session
      try {
        const result = await getAISessionMessages(session.session_id)
        console.log('获取聊天消息结果:', result)
        if (result.success) {
          currentChatMessages.value = result.data || []
          console.log('聊天消息数量:', currentChatMessages.value.length)
        } else {
          console.error('获取聊天消息失败:', result.message)
          alert('获取聊天消息失败：' + (result.message || '未知错误'))
        }
      } catch (error) {
        console.error('加载聊天详情失败:', error)
        alert('加载聊天详情失败')
      }
      showChatDetailModal.value = true
    }

    // 截断消息文本
    const truncateMessage = (message) => {
      if (!message) return '-'
      return message.length > 50 ? message.substring(0, 50) + '...' : message
    }

    const deleteMessage = async (msg) => {
      if (!confirm('确定要删除这条留言吗？')) {
        return
      }

      const result = await deleteMessageApi(msg.id)
      if (result.success) {
        alert('删除成功')
        loadContactMessages()
      } else {
        alert('删除失败：' + (result.message || '未知错误'))
      }
    }

    // 加载用户列表
    const loadUsers = async () => {
      const result = await getAdminUsers()
      if (result.success) {
        const data = result.data || result
        users.value = data?.users || []
      }
    }
    
    // 加载待审批申请
    const loadPendingRequests = async () => {
      const result = await getPendingRequests()
      if (result.success) {
        const data = result.data || result
        pendingRequests.value = data?.requests || []
      }
    }
    
    // 加载统计信息
    const loadStats = async () => {
      const result = await getAdminStats()
      if (result.success) {
        const data = result.data || result
        stats.totalUsers = data?.stats?.totalUsers || data?.totalUsers || 0
        stats.adminUsers = data?.stats?.adminUsers || data?.adminUsers || 0
        stats.pendingRequests = data?.stats?.pendingRequests || data?.pendingRequests || 0
        stats.activeUsers = data?.stats?.activeUsers || data?.activeUsers || 0
      }
    }
    
    // 加载消息
    // 加载客服消息列表
    const loadMessages = async () => {
      try {
        const result = await getAllCustomerMessages()
        if (result.success) {
          const data = result.data || result
          const convs = data.conversations || []
          
          // 为每个对话获取用户头像
          const conversationsWithAvatars = await Promise.all(
            convs.map(async (conv) => {
              let userAvatar = ''
              try {
                const userResponse = await fetch(`${window.API_BASE_URL || 'http://localhost:8080/api'}/user/info?username=${encodeURIComponent(conv.username)}`)
                const userResult = await userResponse.json()
                if (userResult.success) {
                  userAvatar = userResult.data?.avatar || userResult.user?.avatar || ''
                }
              } catch (e) {
                console.error(`获取用户 ${conv.username} 头像失败:`, e)
              }
              return {
                ...conv,
                avatar: userAvatar
              }
            })
          )
          
          conversations.value = conversationsWithAvatars
        } else {
          console.error('获取客服消息失败:', result.message)
          conversations.value = []
        }
      } catch (error) {
        console.error('加载客服消息出错:', error)
        conversations.value = []
      }
    }
    
    // 加载指定用户的对话
    const loadConversation = async (username) => {
      selectedUsername.value = username
      try {
        const response = await fetch(`${window.API_BASE_URL || 'http://localhost:8080/api'}/customer/conversation/${encodeURIComponent(username)}`)
        const result = await response.json()
        if (result.success) {
          const data = result.data || result
          const rawMessages = data.messages || data.records || []
          
          // 获取用户信息以获取头像
          let userAvatar = ''
          try {
            const userResponse = await fetch(`${window.API_BASE_URL || 'http://localhost:8080/api'}/user/info?username=${encodeURIComponent(username)}`)
            const userResult = await userResponse.json()
            if (userResult.success) {
              userAvatar = userResult.data?.avatar || userResult.user?.avatar || ''
            }
          } catch (e) {
            console.error('获取用户头像失败:', e)
          }
          
          // 保存用户头像
          selectedUserAvatar.value = userAvatar
          
          // 将 isFromAdmin 转换为 isMine（在后台视角：isFromAdmin=true是客服发的，isMine=false）
          currentMessages.value = rawMessages.map(msg => ({
            ...msg,
            isMine: !msg.isFromAdmin,  // isFromAdmin为false/null时是用户发的消息
            avatar: msg.isFromAdmin ? '' : userAvatar  // 用户消息使用用户头像
          }))
          
          // 滚动到最新消息
          nextTick(() => {
            const detailContent = document.querySelector('.detail-content')
            if (detailContent) {
              detailContent.scrollTop = detailContent.scrollHeight
            }
          })
        }
      } catch (error) {
        console.error('加载对话失败:', error)
      }
      // 标记为已读
      await markMessageAsRead(username)
    }
    
    // 获取分类名称
    const getCategoryName = (category) => {
      const names = { 
        1: '电竞鼠标', 
        2: '机械键盘', 
        3: '游戏耳机', 
        4: '游戏手柄',
        mouse: '电竞鼠标', 
        keyboard: '机械键盘', 
        headset: '游戏耳机', 
        controller: '游戏手柄'
      }
      return names[category] || category
    }
    
    // 获取分类图标
    const getCategoryIcon = (category) => {
      const icons = { 
        1: '🖱️', 
        2: '⌨️', 
        3: '🎧', 
        4: '🎮',
        mouse: '🖱️', 
        keyboard: '⌨️', 
        headset: '🎧', 
        controller: '🎮'
      }
      return icons[category] || '📦'
    }
    
    // 将 categoryId 转换为 category 字符串
    const categoryIdToName = (categoryId) => {
      const map = { 1: 'mouse', 2: 'keyboard', 3: 'headset', 4: 'controller' }
      return map[categoryId] || 'mouse'
    }
    
    // 格式化数字
    const formatNumber = (num) => {
      return num ? Number(num).toLocaleString() : '0'
    }
    
    // 格式化日期
    const formatDate = (date) => {
      if (!date) return '-'
      return new Date(date).toLocaleDateString('zh-CN')
    }
    
    // 编辑商品
    const editProduct = (product) => {
      editingProduct.value = product
      productForm.name = product.name
      // 优先使用 category 字段，如果没有则根据 categoryId 转换
      productForm.category = product.category || categoryIdToName(product.categoryId)
      productForm.description = product.description || ''
      productForm.price = product.price
      productForm.stock = product.stock
      // 确保图片路径是完整的URL
      const imagePath = product.image || ''
      productForm.image = imagePath.startsWith('http') ? imagePath : `${window.API_BASE_URL || 'http://localhost:8080'}${imagePath}`
      productForm.features = product.features || ''
      // 将数字状态转换为字符串
      productForm.status = product.status === 1 ? 'active' : 'inactive'
      console.log('编辑商品 - 图片路径:', productForm.image)
      showProductModal.value = true
    }
    
    // 删除商品
    const deleteProduct = async (product) => {
      if (!confirm(`确定要删除商品"${product.name}"吗？`)) return
      
      const result = await deleteProductApi(product.id)
      if (result.success) {
        alert('商品删除成功')
        loadProducts()
        loadSalesStats()
      } else {
        alert(result.message || '删除失败')
      }
    }
    
    // 上/下架商品
    const toggleProductStatus = async (product) => {
      const newStatus = product.status === 1 ? 0 : 1
      const action = newStatus === 1 ? '上架' : '下架'
      
      if (!confirm(`确定要${action}商品"${product.name}"吗？`)) return
      
      const result = await updateProduct(product.id, { status: newStatus })
      if (result.success) {
        alert(`商品${action}成功`)
        loadProducts()
        loadSalesStats()
      } else {
        alert(result.message || `${action}失败`)
      }
    }
    
    // 保存商品
    const saveProduct = async () => {
      if (!productForm.name || !productForm.price) {
        alert('请填写必要的商品信息')
        return
      }
      
      const data = { ...productForm }
      console.log('保存商品前的数据:', data)
      
      // 将 category 字符串转换为 categoryId
      const categoryMap = {
        mouse: 1,
        keyboard: 2,
        headset: 3,
        controller: 4
      }
      data.categoryId = categoryMap[data.category] || 1
      delete data.category  // 删除 category 字段，只发送 categoryId
      
      // 将完整URL转换为相对路径用于保存
      if (data.image && data.image.startsWith('http')) {
        const baseUrl = window.API_BASE_URL || 'http://localhost:8080'
        data.image = data.image.replace(baseUrl, '')
      }
      
      // 确保价格是数字类型
      data.price = Number(data.price) || 0
      
      // 将 status 字符串转换为数字
      data.status = data.status === 'active' ? 1 : 0
      
      console.log('发送给后端的商品数据:', data)
      
      if (editingProduct.value) {
        console.log('更新商品 ID:', editingProduct.value.id)
        const result = await updateProduct(editingProduct.value.id, data)
        console.log('更新商品返回结果:', result)
        if (result.success) {
          alert('商品更新成功')
          closeProductModal()
          loadProducts()
        } else {
          alert(result.message || '更新失败')
        }
      } else {
        console.log('添加新商品')
        const result = await addProduct(data)
        console.log('添加商品返回结果:', result)
        if (result.success) {
          alert('商品添加成功')
          closeProductModal()
          loadProducts()
          loadSalesStats()
        } else {
          alert(result.message || '添加失败')
        }
      }
    }
    
    // 图片上传相关
    const imageInputRef = ref(null)
    const uploadingImage = ref(false)

    // 处理图片上传
    const handleImageUpload = async (event) => {
      const file = event.target.files[0]
      if (!file) return

      // 验证文件类型
      if (!file.type.startsWith('image/')) {
        alert('请选择图片文件')
        return
      }

      // 验证文件大小（限制为10MB）
      if (file.size > 10 * 1024 * 1024) {
        alert('图片大小不能超过10MB')
        return
      }

      uploadingImage.value = true

      try {
        // 转换为 base64
        const reader = new FileReader()
        reader.onload = async (e) => {
          const base64Data = e.target.result

          // 获取用户ID
          const currentUser = sessionStorage.getItem('currentUser')
          let userId = 0
          if (currentUser) {
            try {
              const user = JSON.parse(currentUser)
              userId = user.id || 0
            } catch (e) {
              console.error('解析用户数据失败:', e)
            }
          }

          // 调用上传 API
          const response = await fetch(`${window.API_BASE_URL || 'http://localhost:8080/api'}/upload/base64`, {
            method: 'POST',
            headers: {
              'Content-Type': 'application/json'
            },
            body: JSON.stringify({
              image: base64Data,
              userId: userId
            })
          })

          const result = await response.json()
          console.log('上传结果:', result)

          if (result.success && result.data) {
            // 服务器返回 url 和 fullUrl，使用相对路径 url
            const imagePath = result.data.url || result.data.path || result.data
            console.log('上传成功，完整返回数据:', result)
            console.log('图片路径:', imagePath)
            console.log('路径类型:', typeof imagePath)
            console.log('路径长度:', imagePath ? imagePath.length : 0)
            console.log('路径是否为空字符串:', imagePath === '')
            console.log('路径是否为null:', imagePath === null)
            console.log('路径是否为undefined:', imagePath === undefined)
            
            if (imagePath && imagePath !== '') {
              productForm.image = imagePath
              console.log('productForm.image 已设置为:', productForm.image)
              
              // 强制更新视图
              nextTick(() => {
                console.log('nextTick 后的 productForm.image:', productForm.image)
                console.log('v-if 判断结果，productForm.image 是否为真值:', !!productForm.image)
              })
              
              alert('图片上传成功！')
            } else {
              console.error('图片路径无效:', imagePath)
              alert('图片上传成功，但获取路径失败')
            }
          } else {
            console.error('上传失败，返回结果:', result)
            alert(result.message || '图片上传失败')
          }

          uploadingImage.value = false
        }

        reader.onerror = () => {
          alert('图片读取失败')
          uploadingImage.value = false
        }

        reader.readAsDataURL(file)
      } catch (error) {
        console.error('上传图片时出错:', error)
        alert('图片上传失败')
        uploadingImage.value = false
      }
    }

    // 移除图片
    const removeImage = () => {
      productForm.image = ''
      if (imageInputRef.value) {
        imageInputRef.value.value = ''
      }
    }

    // 关闭商品弹窗
    const closeProductModal = () => {
      showProductModal.value = false
      editingProduct.value = null
      productForm.name = ''
      productForm.category = 'mouse'
      productForm.description = ''
      productForm.price = 0
      productForm.stock = 0
      productForm.image = ''
      productForm.features = ''
      productForm.status = 'active'
      removeImage()
    }
    
    // 快速更新库存
    const quickUpdateStock = (product) => {
      stockProduct.value = product
      addStockNum.value = 50
      showStockModal.value = true
    }
    
    // 确认补货
    const confirmAddStock = async () => {
      if (!stockProduct.value) return
      
      const newStock = stockProduct.value.stock + addStockNum.value
      const result = await updateStock(stockProduct.value.id, newStock)
      
      if (result.success) {
        alert('补货成功')
        showStockModal.value = false
        loadProducts()
        loadSalesStats()
      } else {
        alert(result.message || '补货失败')
      }
    }
    
    // 更新商品库存
    const updateProductStock = async (product) => {
      const result = await updateStock(product.id, product.stock)
      if (result.success) {
        loadProducts()
        loadSalesStats()
      }
    }
    
    // 切换用户状态
    const toggleUserStatus = async (user) => {
      const newStatus = user.status === 1 ? 0 : 1
      const result = await updateUserStatus(user.id, newStatus)
      if (result.success) {
        user.status = newStatus
        loadStats()
      } else {
        alert(result.message)
      }
    }
    
    // 提升为管理员
    const promoteToAdmin = async (user) => {
      if (!confirm(`确定要将用户"${user.username}"提升为管理员吗？`)) return
      
      const result = await approveRequest(user.id, true)
      if (result.success) {
        alert('已提升为管理员')
        loadUsers()
        loadStats()
      } else {
        alert(result.message)
      }
    }
    
    // 撤销管理员权限
    const revokeAdmin = async (user) => {
      if (!confirm(`确定要撤销用户"${user.username}"的管理员权限吗？`)) return
      
      const result = await revokeAdminApi(user.id)
      if (result.success) {
        alert('已撤销管理员权限')
        loadUsers()
        loadStats()
      } else {
        alert(result.message)
      }
    }
    
    // 删除用户
    const deleteUser = async (user) => {
      if (!confirm(`确定要删除用户"${user.username}"吗？此操作不可恢复！`)) return
      
      const result = await deleteUserApi(user.id)
      if (result.success) {
        alert('用户已删除')
        loadUsers()
        loadStats()
      } else {
        alert(result.message)
      }
    }
    
    // 处理权限申请
    const handleRequest = async (request, approved) => {
      const result = await approveRequest(request.id, approved)
      if (result.success) {
        alert(approved ? '已批准申请' : '已拒绝申请')
        loadPendingRequests()
        loadStats()
      } else {
        alert(result.message)
      }
    }
    
    // 选择消息
    const selectMessage = (msg) => {
      selectedMessage.value = msg
      msg.read = true
    }
    
    // 发送回复
    const sendReply = async () => {
      if (!replyContent.value.trim() || !selectedUsername.value) {
        return
      }
      
      const result = await sendCustomerReply({
        username: selectedUsername.value,
        content: replyContent.value.trim()
      })
      
      if (result.success) {
        replyContent.value = ''
        // 重新加载对话
        loadConversation(selectedUsername.value)
      }
    }
    
    // 返回用户端
    const switchToUserMode = () => {
      userStore.value.isAdminMode = false
      router.push('/')
    }
    
    onMounted(() => {
      loadProducts()
      loadSalesStats()
      loadUsers()
      loadPendingRequests()
      loadStats()
      loadMessages()
      loadOrders()
      loadOrderStats()
      loadContactMessages()
      loadAISessions()

      // 监听WebSocket通知
      window.addEventListener('websocket-notification', handleWebSocketNotification)
      
      // 自动刷新客服消息列表，每秒刷新一次
      messageRefreshTimer = setInterval(() => {
        loadMessages()
        // 如果有选中的用户，也刷新对话
        if (selectedUsername.value) {
          loadConversation(selectedUsername.value)
        }
      }, 1000)
    })
    
    onUnmounted(() => {
      // 清除定时器
      if (messageRefreshTimer) {
        clearInterval(messageRefreshTimer)
      }
      // 移除WebSocket事件监听
      window.removeEventListener('websocket-notification', handleWebSocketNotification)
      // 断开WebSocket连接
      disconnect()
    })
    
    return {
      // 通用
      activeTab,
      userStore,
      formatDate,
      formatNumber,
      formatDateTime,
      getCategoryName,
      getCategoryIcon,
      switchToUserMode,
      connected,
      notifications,
      
      // 用户管理
      users,
      pendingRequests,
      stats,
      toggleUserStatus,
      promoteToAdmin,
      revokeAdmin,
      deleteUser,
      handleRequest,
      loadUsers,
      loadPendingRequests,
      
      // 商品管理
      products,
      productFilter,
      showProductModal,
      editingProduct,
      productForm,
      imageInputRef,
      uploadingImage,
      handleImageUpload,
      removeImage,
      editProduct,
      deleteProduct,
      toggleProductStatus,
      saveProduct,
      closeProductModal,
      loadProducts,
      handleSearchInput,
      loadSalesStats,
      
      // 订单管理
      orders,
      orderFilter,
      orderPagination,
      orderStats,
      showOrderDetailModal,
      currentOrder,
      currentOrderItems,
      loadOrders,
      changeOrderPage,
      viewOrderDetail,
      updateOrderStatus,
      getOrderStatusText,
      
      // 销售统计
      salesStats,
      categoryStats,
      loadSalesStats,
      
      // 库存管理
      lowStockProducts,
      lowStockCount,
      showStockModal,
      stockProduct,
      addStockNum,
      quickUpdateStock,
      confirmAddStock,
      updateProductStock,
      
      // 客服消息
      conversations,
      currentMessages,
      detailContentRef,
      selectedUsername,
      selectedUserAvatar,
      replyContent,
      unreadMessages,
      loadConversation,
      sendReply,
      loadMessages,

      // 留言管理
      messages,
      messageFilter,
      messagePagination,
      messageStats,
      showMessageDetailModal,
      showReplyModalVisible,
      currentMessage,
      loadContactMessages,
      changeMessagePage,
      getMessageTypeText,
      viewMessageDetail,
      showReplyModal,
      submitReply,
      deleteMessage,

      // AI聊天记录
      aiSessions,
      showChatDetailModal,
      currentChatSession,
      currentChatMessages,
      loadAISessions,
      viewChatDetail,
      truncateMessage
    }
  }
}
</script>

<style scoped>
/* 通用样式 */
.admin-container {
  min-height: 100vh;
  background: #0d1117;
  color: #c9d1d9;
}

.admin-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 30px;
  background: #161b22;
  border-bottom: 1px solid #30363d;
}

.admin-title {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 24px;
  font-weight: 600;
  color: #ffffff;
}

.admin-icon {
  font-size: 28px;
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 16px;
}

.switch-mode-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 20px;
  background: #21262d;
  border: 1px solid #30363d;
  border-radius: 8px;
  color: #c9d1d9;
  cursor: pointer;
  transition: all 0.2s;
}

.switch-mode-btn:hover {
  background: #30363d;
  border-color: #667eea;
}

.admin-user {
  display: flex;
  align-items: center;
  gap: 10px;
}

.user-name {
  color: #ffffff;
  font-weight: 500;
}

.admin-badge {
  padding: 4px 10px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 12px;
  font-size: 12px;
  color: white;
}

/* 统计卡片 */
.stats-container {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  padding: 20px 30px;
}

.stat-card {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 24px;
  background: #161b22;
  border-radius: 12px;
  border: 1px solid #30363d;
}

.stat-card.warning {
  border-color: #ff6b6b;
}

.stat-icon {
  width: 56px;
  height: 56px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 12px;
  font-size: 28px;
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 28px;
  font-weight: 700;
  color: #ffffff;
}

.stat-label {
  font-size: 14px;
  color: #8b949e;
}

/* 标签页导航 */
.tab-navigation {
  display: flex;
  gap: 8px;
  padding: 0 30px 20px;
  border-bottom: 1px solid #30363d;
  overflow-x: auto;
}

.tab-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 20px;
  background: transparent;
  border: none;
  border-radius: 8px;
  color: #8b949e;
  font-size: 14px;
  cursor: pointer;
  white-space: nowrap;
  transition: all 0.2s;
}

.tab-btn:hover {
  background: #21262d;
  color: #ffffff;
}

.tab-btn.active {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.tab-btn .badge {
  padding: 2px 8px;
  background: #ff6b6b;
  border-radius: 10px;
  font-size: 12px;
}

/* 内容卡片 */
.content-card {
  padding: 20px 30px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.card-header h3 {
  font-size: 20px;
  color: #ffffff;
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 12px;
}

.filter-select, .search-input {
  padding: 10px 16px;
  background: #21262d;
  border: 1px solid #30363d;
  border-radius: 8px;
  color: #c9d1d9;
  font-size: 14px;
}

.filter-select:focus, .search-input:focus {
  outline: none;
  border-color: #667eea;
}

.search-input {
  width: 200px;
}

.refresh-btn {
  padding: 10px 20px;
  background: #21262d;
  border: 1px solid #30363d;
  border-radius: 8px;
  color: #c9d1d9;
  cursor: pointer;
  transition: all 0.2s;
}

.refresh-btn:hover {
  background: #30363d;
  border-color: #667eea;
}

.add-btn {
  padding: 10px 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  border-radius: 8px;
  color: white;
  cursor: pointer;
  font-weight: 500;
}

.add-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 15px rgba(102, 126, 234, 0.4);
}

/* 表格 */
.table-container {
  background: #161b22;
  border-radius: 12px;
  border: 1px solid #30363d;
  overflow: hidden;
}

.data-table {
  width: 100%;
  border-collapse: collapse;
}

.data-table th {
  padding: 16px;
  text-align: left;
  background: #21262d;
  color: #8b949e;
  font-weight: 500;
  font-size: 14px;
}

.data-table td {
  padding: 16px;
  border-top: 1px solid #30363d;
}

.data-table tr:hover {
  background: #1c2128;
}

.product-thumb {
  width: 50px;
  height: 50px;
  border-radius: 8px;
  overflow: hidden;
}

.product-thumb img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.product-path {
  font-size: 12px;
  color: #8b949e;
  max-width: 150px;
  word-break: break-all;
  line-height: 1.4;
}

.category-badge {
  padding: 4px 12px;
  background: #21262d;
  border-radius: 15px;
  font-size: 12px;
}

.price {
  color: #ff6b6b;
  font-weight: 600;
}

.low-stock {
  color: #ff6b6b;
}

.warning-icon {
  margin-left: 4px;
}

.status-badge {
  padding: 4px 12px;
  border-radius: 15px;
  font-size: 12px;
}

.status-badge.active, .status-badge.active {
  background: rgba(63, 185, 80, 0.2);
  color: #3fb950;
}

.status-badge.inactive, .status-badge.inactive {
  background: rgba(248, 81, 73, 0.2);
  color: #f85149;
}

.status-badge.pending {
  background: rgba(255, 166, 0, 0.2);
  color: #ffa600;
}

.role-badge.admin {
  background: rgba(102, 126, 234, 0.2);
  color: #667eea;
}

.role-badge.user {
  background: rgba(139, 148, 158, 0.2);
  color: #8b949e;
}

.action-buttons {
  display: flex;
  gap: 8px;
}

.action-btn {
  width: 36px;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #21262d;
  border: 1px solid #30363d;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s;
}

.action-btn:hover {
  background: #30363d;
}

.action-btn.edit:hover {
  border-color: #667eea;
}

.action-btn.delete:hover {
  border-color: #f85149;
}

.action-btn.approve {
  background: rgba(63, 185, 80, 0.2);
  border-color: #3fb950;
  color: #3fb950;
  width: auto;
  padding: 8px 16px;
}

.action-btn.reject {
  background: rgba(248, 81, 73, 0.2);
  border-color: #f85149;
  color: #f85149;
  width: auto;
  padding: 8px 16px;
}

.empty-row {
  text-align: center;
  color: #8b949e;
  padding: 40px !important;
}

/* 订单管理 */
.order-status-badge {
  padding: 4px 12px;
  border-radius: 15px;
  font-size: 12px;
  font-weight: 500;
}

.order-status-badge.pending {
  background: rgba(255, 166, 0, 0.2);
  color: #ffa600;
}

.order-status-badge.paid {
  background: rgba(63, 185, 80, 0.2);
  color: #3fb950;
}

.order-status-badge.shipping {
  background: rgba(102, 126, 234, 0.2);
  color: #667eea;
}

.order-status-badge.shipped {
  background: rgba(79, 172, 254, 0.2);
  color: #4facfe;
}

.order-status-badge.completed {
  background: rgba(34, 197, 94, 0.2);
  color: #22c55e;
}

.order-status-badge.cancelled {
  background: rgba(248, 81, 73, 0.2);
  color: #f85149;
}

.action-btn.view:hover {
  border-color: #667eea;
}

.action-btn.ship:hover {
  border-color: #4facfe;
}

.action-btn.complete:hover {
  border-color: #22c55e;
}

/* 分页 */
.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 16px;
  padding: 20px 0;
}

.page-btn {
  padding: 8px 16px;
  background: #21262d;
  border: 1px solid #30363d;
  border-radius: 8px;
  color: #c9d1d9;
  cursor: pointer;
  transition: all 0.2s;
}

.page-btn:hover:not(:disabled) {
  background: #30363d;
  border-color: #667eea;
}

.page-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.page-info {
  color: #8b949e;
  font-size: 14px;
}

/* 订单详情弹窗 */
.order-detail-modal {
  width: 700px;
  max-height: 85vh;
}

.order-detail-section {
  margin-bottom: 24px;
  padding-bottom: 24px;
  border-bottom: 1px solid #30363d;
}

.order-detail-section:last-child {
  border-bottom: none;
  margin-bottom: 0;
  padding-bottom: 0;
}

.order-detail-section h4 {
  font-size: 16px;
  color: #ffffff;
  margin-bottom: 16px;
}

.detail-row {
  display: flex;
  margin-bottom: 12px;
  align-items: center;
}

.detail-label {
  width: 120px;
  color: #8b949e;
  font-size: 14px;
}

.detail-value {
  flex: 1;
  color: #c9d1d9;
  font-size: 14px;
}

.order-items-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.order-item-row {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 12px;
  background: #21262d;
  border-radius: 8px;
}

.order-item-image {
  width: 60px;
  height: 60px;
  border-radius: 8px;
  object-fit: cover;
}

.order-item-info {
  flex: 1;
}

.order-item-name {
  color: #ffffff;
  font-weight: 500;
  margin-bottom: 4px;
}

.order-item-specs {
  color: #8b949e;
  font-size: 12px;
  display: flex;
  gap: 16px;
}

.order-item-total {
  color: #ff6b6b;
  font-weight: 600;
  font-size: 16px;
}

.order-remark {
  padding: 12px;
  background: #21262d;
  border-radius: 8px;
  color: #c9d1d9;
  font-size: 14px;
}

/* 订单状态统计 */
.order-status-stats h4 {
  font-size: 18px;
  color: #ffffff;
  margin-bottom: 16px;
}

.status-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
}

.status-item {
  padding: 24px;
  background: #161b22;
  border-radius: 12px;
  border: 1px solid #30363d;
  text-align: center;
}

.status-item.pending {
  border-color: #ffa600;
  background: rgba(255, 166, 0, 0.1);
}

.status-item.shipping {
  border-color: #667eea;
  background: rgba(102, 126, 234, 0.1);
}

.status-item.completed {
  border-color: #22c55e;
  background: rgba(34, 197, 94, 0.1);
}

.status-count {
  font-size: 32px;
  font-weight: 700;
  color: #ffffff;
  margin-bottom: 8px;
}

.status-label {
  font-size: 14px;
  color: #8b949e;
}

/* 销售统计 */
.sales-overview {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  margin-bottom: 30px;
}

.overview-card {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 24px;
  background: #161b22;
  border-radius: 12px;
  border: 1px solid #30363d;
}

.overview-icon {
  font-size: 36px;
}

.overview-value {
  font-size: 24px;
  font-weight: 700;
  color: #ffffff;
}

.overview-label {
  font-size: 14px;
  color: #8b949e;
}

.category-sales h4 {
  font-size: 18px;
  color: #ffffff;
  margin-bottom: 16px;
}

.category-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
}

.category-item {
  padding: 20px;
  background: #161b22;
  border-radius: 12px;
  border: 1px solid #30363d;
  text-align: center;
}

.cat-icon {
  font-size: 32px;
  margin-bottom: 12px;
}

.cat-name {
  font-size: 16px;
  color: #ffffff;
  margin-bottom: 8px;
}

.cat-sales {
  font-size: 20px;
  font-weight: 600;
  color: #667eea;
  margin-bottom: 4px;
}

.cat-products {
  font-size: 12px;
  color: #8b949e;
}

/* 库存管理 */
.inventory-warning {
  background: rgba(248, 81, 73, 0.1);
  border: 1px solid #f85149;
  border-radius: 12px;
  padding: 20px;
  margin-bottom: 30px;
}

.warning-header {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #f85149;
  font-size: 16px;
  font-weight: 600;
  margin-bottom: 16px;
}

.warning-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.warning-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 12px;
  background: #161b22;
  border-radius: 8px;
}

.warning-thumb {
  width: 48px;
  height: 48px;
  border-radius: 8px;
  object-fit: cover;
}

.warning-info {
  flex: 1;
}

.warning-name {
  color: #ffffff;
  font-weight: 500;
}

.warning-category {
  font-size: 12px;
  color: #8b949e;
}

.warning-stock {
  display: flex;
  align-items: baseline;
  gap: 4px;
}

.stock-value {
  font-size: 24px;
  font-weight: 700;
  color: #ff6b6b;
}

.stock-value.danger {
  color: #f85149;
}

.stock-label {
  font-size: 12px;
  color: #8b949e;
}

.stock-btn {
  padding: 8px 16px;
  background: #ff6b6b;
  border: none;
  border-radius: 6px;
  color: white;
  cursor: pointer;
}

.stock-btn:hover {
  background: #f85149;
}

.inventory-empty {
  text-align: center;
  padding: 60px;
  background: #161b22;
  border-radius: 12px;
}

.inventory-empty .empty-icon {
  font-size: 48px;
}

.inventory-empty p {
  color: #8b949e;
  margin-top: 16px;
}

.inventory-all h4 {
  font-size: 18px;
  color: #ffffff;
  margin-bottom: 16px;
}

.inventory-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
}

.inventory-card {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px;
  background: #161b22;
  border-radius: 12px;
  border: 1px solid #30363d;
}

.inv-thumb {
  width: 50px;
  height: 50px;
  border-radius: 8px;
  object-fit: cover;
}

.inv-info {
  flex: 1;
  min-width: 0;
}

.inv-name {
  font-size: 14px;
  color: #ffffff;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.inv-category {
  font-size: 12px;
  color: #8b949e;
}

.inv-stock {
  font-size: 20px;
  font-weight: 700;
  color: #3fb950;
  min-width: 40px;
  text-align: center;
}

.inv-stock.low {
  color: #ffa600;
}

.inv-stock.danger {
  color: #f85149;
}

.stock-input {
  width: 60px;
  padding: 6px;
  background: #21262d;
  border: 1px solid #30363d;
  border-radius: 6px;
  color: #c9d1d9;
  text-align: center;
}

/* 客服消息 */
.service-container {
  display: grid;
  grid-template-columns: 300px 1fr;
  gap: 20px;
  min-height: 500px;
}

.message-list {
  background: #161b22;
  border-radius: 12px;
  border: 1px solid #30363d;
  overflow-y: auto;
}

.message-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px;
  border-bottom: 1px solid #30363d;
  cursor: pointer;
  transition: background 0.2s;
}

.message-item:hover {
  background: #1c2128;
}

.message-item.unread {
  background: rgba(102, 126, 234, 0.1);
}

.msg-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-weight: 600;
  overflow: hidden;
}

.msg-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.msg-content {
  flex: 1;
  min-width: 0;
}

.msg-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 4px;
}

.msg-username {
  color: #ffffff;
  font-weight: 500;
}

.msg-time {
  font-size: 12px;
  color: #8b949e;
}

.msg-preview {
  font-size: 13px;
  color: #8b949e;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.msg-status {
  color: #667eea;
  font-size: 12px;
}

.message-empty {
  text-align: center;
  padding: 60px 20px;
}

.message-empty .empty-icon {
  font-size: 48px;
}

.message-empty p {
  color: #8b949e;
  margin-top: 16px;
}

.message-detail {
  background: #161b22;
  border-radius: 12px;
  border: 1px solid #30363d;
  display: flex;
  flex-direction: column;
  height: 100%;
  min-height: 500px;
  max-height: 600px;
}

.detail-header {
  padding: 20px;
  border-bottom: 1px solid #30363d;
}

.detail-user {
  display: flex;
  align-items: center;
  gap: 12px;
}

.detail-avatar {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 20px;
  font-weight: 600;
  overflow: hidden;
}

.detail-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.detail-username {
  color: #ffffff;
  font-weight: 600;
}

.detail-time {
  font-size: 12px;
  color: #8b949e;
}

.detail-content {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
  gap: 12px;
  max-height: 420px;
  min-height: 300px;
}

.detail-message-item {
  display: flex;
  align-items: flex-end;
  gap: 10px;
  max-width: 80%;
}

.detail-message-item.is-user-msg {
  align-self: flex-start;
}

.detail-message-item.is-service-msg {
  align-self: flex-end;
}

.detail-message-item .message-avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 14px;
  font-weight: 600;
  flex-shrink: 0;
  border: 2px solid #667eea;
  box-shadow: 0 2px 8px rgba(102, 126, 234, 0.3);
  overflow: hidden;
}

.detail-message-item .message-avatar.user-avatar {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
}

.detail-message-item .message-avatar.service-avatar {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.detail-message-item .message-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.message-bubble {
  background: #21262d;
  padding: 12px 16px;
  border-radius: 16px;
  border-top-right-radius: 4px;
}

.detail-message-item.is-user-msg .message-bubble {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-top-left-radius: 4px;
  border-top-right-radius: 16px;
}

.detail-text {
  color: #c9d1d9;
  line-height: 1.5;
  word-break: break-word;
}

.detail-message-item.is-user-msg .detail-text {
  color: white;
}

.message-time {
  font-size: 11px;
  color: #8b949e;
  margin-top: 4px;
}

.detail-message-item.is-user-msg .message-time {
  color: rgba(255, 255, 255, 0.7);
  text-align: right;
}

.no-messages {
  text-align: center;
  padding: 40px;
  color: #8b949e;
}

.detail-reply {
  padding: 20px;
  border-top: 1px solid #30363d;
}

.reply-input {
  width: 100%;
  height: 80px;
  padding: 12px;
  background: #21262d;
  border: 1px solid #30363d;
  border-radius: 8px;
  color: #c9d1d9;
  resize: none;
  margin-bottom: 12px;
}

.reply-input:focus {
  outline: none;
  border-color: #667eea;
}

.reply-btn {
  padding: 10px 24px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  border-radius: 8px;
  color: white;
  cursor: pointer;
  transition: all 0.3s ease;
}

.reply-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 15px rgba(102, 126, 234, 0.4);
}

.message-detail-empty {
  background: #161b22;
  border-radius: 12px;
  border: 1px solid #30363d;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}

.message-detail-empty .empty-icon {
  font-size: 48px;
}

.message-detail-empty p {
  color: #8b949e;
  margin-top: 16px;
}

/* 客服消息列表选中状态 */
.message-item.active {
  background: rgba(102, 126, 234, 0.2);
  border-left: 3px solid #667eea;
}

.message-item .msg-status {
  min-width: 20px;
  height: 20px;
  background: #667eea;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 11px;
}

/* 消息详情对话样式 */
.detail-content {
  flex: 1;
  overflow-y: auto;
  padding: 20px;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.detail-message-item {
  display: flex;
  gap: 12px;
  max-width: 85%;
  align-items: flex-start;
}

/* 弹窗 */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.7);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal-content {
  background: #161b22;
  border-radius: 16px;
  border: 1px solid #30363d;
  max-width: 90vw;
  max-height: 90vh;
  display: flex;
  flex-direction: column;
}

.product-modal {
  width: 500px;
}

.stock-modal {
  width: 400px;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 24px;
  border-bottom: 1px solid #30363d;
}

.modal-header h3 {
  font-size: 18px;
  color: #ffffff;
}

.modal-close {
  width: 32px;
  height: 32px;
  background: none;
  border: none;
  color: #8b949e;
  font-size: 24px;
  cursor: pointer;
  border-radius: 8px;
}

.modal-close:hover {
  background: #21262d;
  color: #ffffff;
}

.modal-body {
  padding: 24px;
  overflow-y: auto;
  flex: 1;
}

.modal-body .form-group {
  margin-bottom: 16px;
}

.modal-body label {
  display: block;
  color: #8b949e;
  font-size: 14px;
  margin-bottom: 8px;
}

.modal-body input,
.modal-body select,
.modal-body textarea {
  width: 100%;
  padding: 12px;
  background: #21262d;
  border: 1px solid #30363d;
  border-radius: 8px;
  color: #c9d1d9;
  font-size: 14px;
}

.modal-body input:focus,
.modal-body select:focus,
.modal-body textarea:focus {
  outline: none;
  border-color: #667eea;
}

.current-stock {
  text-align: center;
  padding: 20px;
  background: #21262d;
  border-radius: 8px;
  margin-bottom: 20px;
}

.stock-num {
  font-size: 24px;
  font-weight: 700;
  color: #ff6b6b;
  margin-left: 8px;
}

.modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding: 16px 24px;
  border-top: 1px solid #30363d;
}

.cancel-btn {
  padding: 10px 24px;
  background: #21262d;
  border: 1px solid #30363d;
  border-radius: 8px;
  color: #c9d1d9;
  cursor: pointer;
}

.confirm-btn {
  padding: 10px 24px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  border-radius: 8px;
  color: white;
  cursor: pointer;
}

.confirm-btn:hover {
  opacity: 0.9;
}

/* 图片上传样式 */
.image-upload {
    position: relative;
    width: 100%;
  }

  .image-path-hint {
    margin-bottom: 12px;
    padding: 12px;
    background: rgba(102, 126, 234, 0.1);
    border: 1px solid #667eea;
    border-radius: 8px;
    display: flex;
    align-items: center;
    gap: 12px;
  }

  .path-icon {
    color: #667eea;
    font-size: 18px;
    flex-shrink: 0;
  }

  .path-text {
    flex: 1;
    color: #667eea;
    font-size: 13px;
    word-break: break-all;
    line-height: 1.4;
  }

  .clear-path-btn {
    padding: 4px 12px;
    background: #f85149;
    border: none;
    border-radius: 4px;
    color: white;
    font-size: 12px;
    cursor: pointer;
    flex-shrink: 0;
  }

  .clear-path-btn:hover {
    background: #da3633;
  }

  .upload-area {  width: 100%;
  height: 200px;
  border: 2px dashed #30363d;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.3s ease;
  overflow: hidden;
  background: #21262d;
}

.upload-area:hover {
  border-color: #667eea;
  background: #1c2128;
}

.upload-area.has-image {
  border-style: solid;
}

.upload-placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
  color: #8b949e;
}

.upload-icon {
  font-size: 48px;
}

.upload-text {
  font-size: 14px;
}

.preview-image {
    width: 100%;
    height: 100%;
    object-fit: contain;
    background: #0d1117;
  }

.image-preview {
  position: relative;
  width: 100%;
  height: 200px;
  border-radius: 8px;
  overflow: hidden;
  background: #0d1117;
  border: 2px solid #30363d;
}

.image-preview img {
  width: 100%;
  height: 100%;
  object-fit: contain;
}

.remove-image-btn {
  position: absolute;
  top: 8px;
  right: 8px;
  width: 28px;
  height: 28px;
  background: rgba(0, 0, 0, 0.7);
  border: none;
  border-radius: 50%;
  color: #ff6b6b;
  font-size: 18px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;
  z-index: 1;
}

.remove-image-btn:hover {
  background: rgba(255, 107, 107, 0.9);
  color: white;
  transform: scale(1.1);
}

/* 留言管理相关样式 */
.message-content {
  max-width: 300px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.message-content-full {
  background: #21262d;
  padding: 16px;
  border-radius: 8px;
  color: #c9d1d9;
  line-height: 1.6;
  max-height: 300px;
  overflow-y: auto;
}

.reply-content-full {
  background: rgba(102, 126, 234, 0.1);
  padding: 16px;
  border-radius: 8px;
  color: #c9d1d9;
  line-height: 1.6;
  border-left: 4px solid #667eea;
}

.status-badge.pending {
  background: rgba(255, 193, 7, 0.2);
  color: #ffc107;
}

.status-badge.processed {
  background: rgba(63, 185, 80, 0.2);
  color: #3fb950;
}

.message-detail-modal {
  max-width: 700px;
}

.reply-modal {
  max-width: 600px;
}

.message-preview {
  margin-bottom: 20px;
}

.preview-label {
  color: #8b949e;
  font-size: 13px;
  margin-bottom: 8px;
}

.preview-content {
  background: #21262d;
  padding: 12px;
  border-radius: 8px;
  color: #c9d1d9;
  line-height: 1.6;
}

/* AI聊天记录相关样式 */
.chat-detail-modal {
  max-width: 800px;
}

.chat-info {
  background: #161b22;
  padding: 15px;
  border-radius: 8px;
  margin-bottom: 20px;
  border: 1px solid #30363d;
}

.chat-info-row {
  display: flex;
  margin-bottom: 10px;
}

.chat-info-row:last-child {
  margin-bottom: 0;
}

.chat-info-row label {
  width: 100px;
  font-weight: 600;
  color: #8b949e;
}

.chat-info-row span {
  flex: 1;
  color: #c9d1d9;
}

.chat-messages {
  max-height: 500px;
  overflow-y: auto;
  padding: 15px;
  background: #161b22;
  border-radius: 8px;
  border: 1px solid #30363d;
}

.chat-message {
  display: flex;
  gap: 10px;
  margin-bottom: 15px;
}

.chat-message.is-user {
  flex-direction: row-reverse;
}

.message-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: #58a6ff;
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  flex-shrink: 0;
}

.chat-message.is-user .message-avatar {
  background: #238636;
}

.message-content {
  max-width: 70%;
}

.message-text {
  padding: 12px 16px;
  border-radius: 12px;
  background: #21262d;
  border: 1px solid #30363d;
  color: #c9d1d9;
  line-height: 1.5;
}

.chat-message.is-user .message-text {
  background: #238636;
  color: white;
  border: none;
}

.message-time {
  font-size: 11px;
  color: #8b949e;
  margin-top: 5px;
}

.empty-messages {
  text-align: center;
  padding: 40px 20px;
  color: #8b949e;
}

.empty-messages .empty-icon {
  font-size: 48px;
  margin-bottom: 10px;
  display: block;
}

.last-message {
  max-width: 200px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
</style>