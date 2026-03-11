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
            placeholder="搜索商品..." 
            class="search-input"
            @input="loadProducts"
          />
          <button class="refresh-btn" @click="loadProducts">🔄 刷新</button>
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
                <span :class="['status-badge', product.status]">
                  {{ product.status === 'active' ? '在售' : '下架' }}
                </span>
              </td>
              <td>
                <div class="action-buttons">
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
      
      <!-- 分类销售统计 -->
      <div class="category-sales">
        <h4>分类销售统计</h4>
        <div class="category-grid">
          <div class="category-item" v-for="cat in categoryStats" :key="cat.category">
            <div class="cat-icon">{{ getCategoryIcon(cat.category) }}</div>
            <div class="cat-name">{{ getCategoryName(cat.category) }}</div>
            <div class="cat-sales">{{ cat.sales }} 件</div>
            <div class="cat-products">{{ cat.count }} 款</div>
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
            <div class="msg-avatar">{{ (conv.username || '?').charAt(0).toUpperCase() }}</div>
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
              <div class="detail-avatar">{{ (selectedUsername || '?').charAt(0).toUpperCase() }}</div>
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
              <!-- 用户消息：显示在左边，头像在右边 -->
              <template v-if="msg.isMine">
                <div class="message-bubble user-bubble">
                  <div class="detail-text">{{ msg.content }}</div>
                  <div class="message-time">{{ msg.time }}</div>
                </div>
                <div class="message-avatar user-avatar">
                  <span>{{ selectedUsername?.charAt(0).toUpperCase() }}</span>
                </div>
              </template>
              <!-- 客服消息：显示在右边，头像在右边 -->
              <template v-else>
                <div class="message-bubble service-bubble">
                  <div class="detail-text">{{ msg.content }}</div>
                  <div class="message-time">{{ msg.time }}</div>
                </div>
                <div class="message-avatar service-avatar">
                  <span>服</span>
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
            <label>商品图片路径</label>
            <input type="text" v-model="productForm.image" placeholder="/images/xxx/1.jpg" />
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
  updateStock,
  getAllCustomerMessages,
  markMessageAsRead,
  sendCustomerReply
} from '../api'

export default {
  name: 'Admin',
  setup() {
    const router = useRouter()
    const activeTab = ref('products')
    
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
    
    // 库存管理相关
    const showStockModal = ref(false)
    const stockProduct = ref(null)
    const addStockNum = ref(50)
    
    // 客服消息相关
    const conversations = ref([])  // 对话列表（按用户分组）
    let messageRefreshTimer = null  // 客服消息自动刷新定时器
    const currentMessages = ref([])  // 当前选中的对话消息
    const detailContentRef = ref(null)  // 消息内容区域引用
    const selectedUsername = ref(null)
    const replyContent = ref('')
    const unreadMessages = computed(() => conversations.value.reduce((sum, c) => sum + c.unreadCount, 0))
    const lowStockCount = computed(() => lowStockProducts.value.length)
    
    // 加载商品列表
    const loadProducts = async () => {
      const result = await getProducts(productFilter.category, productFilter.keyword)
      if (result.success) {
        products.value = result.products
      }
    }
    
    // 加载销售统计
    const loadSalesStats = async () => {
      const result = await getSalesStats()
      if (result.success) {
        salesStats.totalSales = result.stats.totalSales
        salesStats.totalOrders = result.stats.totalOrders
        salesStats.todaySales = result.stats.todaySales
        salesStats.todayOrders = result.stats.todayOrders
        categoryStats.value = result.stats.categoryStats || []
        lowStockProducts.value = result.stats.lowStockProducts || []
      }
    }
    
    // 加载用户列表
    const loadUsers = async () => {
      const result = await getAdminUsers()
      if (result.success) {
        users.value = result.users
      }
    }
    
    // 加载待审批申请
    const loadPendingRequests = async () => {
      const result = await getPendingRequests()
      if (result.success) {
        pendingRequests.value = result.requests
      }
    }
    
    // 加载统计信息
    const loadStats = async () => {
      const result = await getAdminStats()
      if (result.success) {
        stats.totalUsers = result.stats.totalUsers
        stats.adminUsers = result.stats.adminUsers
        stats.pendingRequests = result.stats.pendingRequests
        stats.activeUsers = result.stats.activeUsers
      }
    }
    
    // 加载消息
    // 加载客服消息列表
    const loadMessages = async () => {
      try {
        const result = await getAllCustomerMessages()
        if (result.success) {
          conversations.value = result.conversations || []
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
        const response = await fetch(`${window.API_BASE_URL || 'http://localhost:3001/api'}/customer/conversation/${encodeURIComponent(username)}`)
        const result = await response.json()
        if (result.success) {
          currentMessages.value = result.messages || []
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
      const names = { mouse: '电竞鼠标', keyboard: '机械键盘', headset: '游戏耳机', controller: '游戏手柄' }
      return names[category] || category
    }
    
    // 获取分类图标
    const getCategoryIcon = (category) => {
      const icons = { mouse: '🖱️', keyboard: '⌨️', headset: '🎧', controller: '🎮' }
      return icons[category] || '📦'
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
      productForm.category = product.category
      productForm.description = product.description || ''
      productForm.price = product.price
      productForm.stock = product.stock
      productForm.image = product.image || ''
      productForm.features = product.features || ''
      productForm.status = product.status || 'active'
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
    
    // 保存商品
    const saveProduct = async () => {
      if (!productForm.name || !productForm.price) {
        alert('请填写必要的商品信息')
        return
      }
      
      const data = { ...productForm }
      
      if (editingProduct.value) {
        const result = await updateProduct(editingProduct.value.id, data)
        if (result.success) {
          alert('商品更新成功')
          closeProductModal()
          loadProducts()
        } else {
          alert(result.message || '更新失败')
        }
      } else {
        const result = await addProduct(data)
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
      router.push('/home')
    }
    
    onMounted(() => {
      loadProducts()
      loadSalesStats()
      loadUsers()
      loadPendingRequests()
      loadStats()
      loadMessages()
      
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
    })
    
    return {
      // 通用
      activeTab,
      userStore,
      formatDate,
      formatNumber,
      getCategoryName,
      getCategoryIcon,
      switchToUserMode,
      
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
      editProduct,
      deleteProduct,
      saveProduct,
      closeProductModal,
      loadProducts,
      
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
      replyContent,
      unreadMessages,
      loadConversation,
      sendReply,
      loadMessages
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
}

.detail-message-item .message-avatar.user-avatar {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
}

.detail-message-item .message-avatar.service-avatar {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.message-bubble {
  background: #21262d;
  padding: 12px 16px;
  border-radius: 16px;
  border-bottom-left-radius: 4px;
}

.detail-message-item.is-user-msg .message-bubble {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-bottom-left-radius: 16px;
  border-bottom-right-radius: 4px;
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
</style>