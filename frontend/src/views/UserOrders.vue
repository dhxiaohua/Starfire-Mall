<template>
  <WebsiteLayout current-page="orders">
    <div class="orders-page">
      <div class="page-header">
        <h1>我的订单</h1>
        <p>查看和管理您的所有订单</p>
      </div>

      <!-- 订单状态筛选 -->
      <div class="orders-tabs">
        <button
          class="tab-btn"
          :class="{ active: activeStatus === '' }"
          @click="activeStatus = ''"
        >
          全部订单
        </button>
        <button
          class="tab-btn"
          :class="{ active: activeStatus === 'pending' }"
          @click="activeStatus = 'pending'"
        >
          待支付
        </button>
        <button
          class="tab-btn"
          :class="{ active: activeStatus === 'paid' }"
          @click="activeStatus = 'paid'"
        >
          已支付
        </button>
        <button
          class="tab-btn"
          :class="{ active: activeStatus === 'shipped' }"
          @click="activeStatus = 'shipped'"
        >
          配送中
        </button>
        <button
          class="tab-btn"
          :class="{ active: activeStatus === 'completed' }"
          @click="activeStatus = 'completed'"
        >
          已完成
        </button>
      </div>

      <!-- 订单列表 -->
      <div class="orders-list" v-if="loading">
        <div class="loading">加载中...</div>
      </div>

      <div class="orders-list" v-else-if="orders.length === 0">
        <div class="empty">
          <span>📦</span>
          <p>暂无订单</p>
        </div>
      </div>

      <div class="orders-list" v-else>
        <div class="order-card" v-for="order in orders" :key="order.id">
          <!-- 订单头部 -->
          <div class="order-header">
            <div class="order-info">
              <span class="order-no">订单号：{{ order.orderNo }}</span>
              <span class="order-date">{{ formatDate(order.createTime) }}</span>
            </div>
            <div class="order-status" :class="'status-' + order.status">
              {{ getStatusText(order.status) }}
            </div>
          </div>

          <!-- 订单商品 -->
          <div class="order-items">
            <div class="order-item" v-for="item in order.items" :key="item.id">
              <img :src="item.productImage" :alt="item.productName" class="item-image" />
              <div class="item-details">
                <h4>{{ item.productName }}</h4>
                <span class="item-price">￥{{ item.price.toFixed(2) }}</span>
              </div>
              <span class="item-quantity">× {{ item.quantity }}</span>
              <span class="item-subtotal">￥{{ (item.price * item.quantity).toFixed(2) }}</span>
            </div>
          </div>

          <!-- 订单信息 -->
          <div class="order-details">
            <div class="detail-row">
              <span class="label">收货人：</span>
              <span>{{ order.receiverName }}</span>
            </div>
            <div class="detail-row">
              <span class="label">联系电话：</span>
              <span>{{ order.receiverPhone }}</span>
            </div>
            <div class="detail-row">
              <span class="label">收货地址：</span>
              <span>{{ order.receiverAddress }}</span>
            </div>
            <div class="detail-row" v-if="order.remark">
              <span class="label">订单备注：</span>
              <span>{{ order.remark }}</span>
            </div>
          </div>

          <!-- 订单金额 -->
          <div class="order-footer">
            <div class="order-summary">
              <span>共 {{ getTotalQuantity(order) }} 件商品</span>
              <span>实付款：</span>
              <span class="total-amount">￥{{ order.payAmount.toFixed(2) }}</span>
            </div>
            <div class="order-actions">
              <button
                v-if="order.status === 'pending'"
                class="action-btn pay-btn"
                @click="handlePayOrder(order.id)"
              >
                立即支付
              </button>
              <button
                v-if="order.status === 'pending'"
                class="action-btn cancel-btn"
                @click="handleCancelOrder(order.id)"
              >
                取消订单
              </button>
              <button
                class="action-btn detail-btn"
                @click="viewOrderDetail(order)"
              >
                查看详情
              </button>
            </div>
          </div>
        </div>
      </div>

      <!-- 分页 -->
      <div class="pagination" v-if="totalPages > 1">
        <button
          class="page-btn"
          :disabled="currentPage === 1"
          @click="changePage(currentPage - 1)"
        >
          上一页
        </button>
        <button
          v-for="page in totalPages"
          :key="page"
          class="page-btn"
          :class="{ active: currentPage === page }"
          @click="changePage(page)"
        >
          {{ page }}
        </button>
        <button
          class="page-btn"
          :disabled="currentPage === totalPages"
          @click="changePage(currentPage + 1)"
        >
          下一页
        </button>
      </div>
    </div>

    <!-- 订单详情弹窗 -->
    <div class="order-detail-modal" v-if="showDetailModal" @click.self="closeDetailModal">
      <div class="detail-modal-content">
        <div class="modal-header">
          <h2>订单详情</h2>
          <button class="close-modal" @click="closeDetailModal">
            <svg viewBox="0 0 20 20" fill="currentColor">
              <path fill-rule="evenodd" d="M4.293 4.293a1 1 0 011.414 0L10 8.586l4.293-4.293a1 1 0 111.414 1.414L11.414 10l4.293 4.293a1 1 0 01-1.414 1.414L10 11.414l-4.293 4.293a1 1 0 01-1.414-1.414L8.586 10 4.293 5.707a1 1 0 010-1.414z" clip-rule="evenodd" />
            </svg>
          </button>
        </div>

        <div v-if="selectedOrder" class="detail-content">
          <!-- 订单基本信息 -->
          <div class="detail-section">
            <h3>订单信息</h3>
            <div class="info-grid">
              <div class="info-item">
                <span class="label">订单号：</span>
                <span>{{ selectedOrder.orderNo }}</span>
              </div>
              <div class="info-item">
                <span class="label">订单状态：</span>
                <span class="order-status" :class="'status-' + selectedOrder.status">
                  {{ getStatusText(selectedOrder.status) }}
                </span>
              </div>
              <div class="info-item">
                <span class="label">下单时间：</span>
                <span>{{ formatDateTime(selectedOrder.createTime) }}</span>
              </div>
              <div class="info-item" v-if="selectedOrder.payTime">
                <span class="label">支付时间：</span>
                <span>{{ formatDateTime(selectedOrder.payTime) }}</span>
              </div>
            </div>
          </div>

          <!-- 收货信息 -->
          <div class="detail-section">
            <h3>收货信息</h3>
            <div class="info-grid">
              <div class="info-item">
                <span class="label">收货人：</span>
                <span>{{ selectedOrder.receiverName }}</span>
              </div>
              <div class="info-item">
                <span class="label">联系电话：</span>
                <span>{{ selectedOrder.receiverPhone }}</span>
              </div>
              <div class="info-item full-width">
                <span class="label">收货地址：</span>
                <span>{{ selectedOrder.receiverAddress }}</span>
              </div>
            </div>
          </div>

          <!-- 商品清单 -->
          <div class="detail-section">
            <h3>商品清单</h3>
            <div class="detail-items">
              <div class="detail-item" v-for="item in selectedOrder.items" :key="item.id">
                <img :src="item.productImage" :alt="item.productName" class="item-image" />
                <div class="item-info">
                  <h4>{{ item.productName }}</h4>
                  <div class="item-meta">
                    <span class="price">￥{{ item.price.toFixed(2) }}</span>
                    <span class="quantity">× {{ item.quantity }}</span>
                  </div>
                </div>
                <span class="item-total">￥{{ (item.price * item.quantity).toFixed(2) }}</span>
              </div>
            </div>
          </div>

          <!-- 费用明细 -->
          <div class="detail-section">
            <h3>费用明细</h3>
            <div class="detail-summary">
              <div class="summary-row">
                <span>商品总价：</span>
                <span>￥{{ selectedOrder.totalAmount.toFixed(2) }}</span>
              </div>
              <div class="summary-row" v-if="selectedOrder.discountAmount > 0">
                <span>优惠金额：</span>
                <span class="discount">-￥{{ selectedOrder.discountAmount.toFixed(2) }}</span>
              </div>
              <div class="summary-row total">
                <span>实付款：</span>
                <span class="total-amount">￥{{ selectedOrder.payAmount.toFixed(2) }}</span>
              </div>
            </div>
          </div>

          <!-- 支付信息 -->
          <div class="detail-section" v-if="selectedOrder.status === 'paid' || selectedOrder.status === 'shipped' || selectedOrder.status === 'completed'">
            <h3>支付信息</h3>
            <div class="info-grid">
              <div class="info-item">
                <span class="label">支付方式：</span>
                <span>{{ getPaymentMethodText(selectedOrder.payMethod) }}</span>
              </div>
              <div class="info-item">
                <span class="label">支付时间：</span>
                <span>{{ formatDateTime(selectedOrder.payTime) }}</span>
              </div>
            </div>
          </div>

          <!-- 备注 -->
          <div class="detail-section" v-if="selectedOrder.remark">
            <h3>订单备注</h3>
            <div class="remark-text">
              {{ selectedOrder.remark }}
            </div>
          </div>
        </div>
      </div>
    </div>
  </WebsiteLayout>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import WebsiteLayout from '../components/WebsiteLayout.vue'
import { getUserOrders, completePayment, cancelOrder } from '../api/index'

// 状态管理
const loading = ref(false)
const orders = ref([])
const activeStatus = ref('')
const currentPage = ref(1)
const pageSize = ref(10)
const totalPages = ref(1)
const total = ref(0)

// 订单详情弹窗
const showDetailModal = ref(false)
const selectedOrder = ref(null)

// 格式化日期
const formatDate = (dateStr) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return date.toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit'
  })
}

// 格式化日期时间
const formatDateTime = (dateStr) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit'
  })
}

// 获取订单状态文本
const getStatusText = (status) => {
  const statusMap = {
    'pending': '待支付',
    'paid': '已支付',
    'shipping': '待发货',
    'shipped': '配送中',
    'completed': '已完成'
  }
  return statusMap[status] || status
}

// 获取支付方式文本
const getPaymentMethodText = (method) => {
  const methodMap = {
    'alipay': '支付宝',
    'wechat': '微信支付',
    'card': '银行卡'
  }
  return methodMap[method] || method
}

// 计算订单总数量
const getTotalQuantity = (order) => {
  if (!order.items || order.items.length === 0) return 0
  return order.items.reduce((sum, item) => sum + item.quantity, 0)
}

// 加载订单列表
const loadOrders = async () => {
  // 从sessionStorage获取用户信息
  const userData = sessionStorage.getItem('currentUser')
  if (!userData) {
    alert('请先登录')
    return
  }

  try {
    const user = JSON.parse(userData)
    if (!user.username) {
      alert('用户信息不完整')
      return
    }

    loading.value = true
    const result = await getUserOrders(user.username, currentPage.value, pageSize.value, activeStatus.value)

    if (result.success) {
      orders.value = result.data.records || []
      total.value = result.data.total || 0
      totalPages.value = Math.ceil(total.value / pageSize.value) || 1
    } else {
      alert('加载订单失败：' + (result.message || '未知错误'))
    }
  } catch (error) {
    console.error('加载订单失败：', error)
    alert('加载订单失败：' + error.message)
  } finally {
    loading.value = false
  }
}

// 切换页码
const changePage = (page) => {
  if (page < 1 || page > totalPages.value) return
  currentPage.value = page
  loadOrders()
}

// 支付订单
const handlePayOrder = async (orderId) => {
  if (!confirm('确认要支付该订单吗？')) {
    return
  }

  try {
    const result = await completePayment(orderId)
    if (result.success) {
      alert('支付成功！')
      loadOrders() // 重新加载订单列表
    } else {
      alert('支付失败：' + (result.message || '未知错误'))
    }
  } catch (error) {
    console.error('支付失败：', error)
    alert('支付失败：' + error.message)
  }
}

// 取消订单
const handleCancelOrder = async (orderId) => {
  if (!confirm('确定要取消该订单吗？取消后订单将被删除，无法恢复。')) {
    return
  }

  try {
    const result = await cancelOrder(orderId)
    if (result.success) {
      alert('订单已取消')
      loadOrders() // 重新加载订单列表
    } else {
      alert('取消订单失败：' + (result.message || '未知错误'))
    }
  } catch (error) {
    console.error('取消订单失败：', error)
    alert('取消订单失败：' + error.message)
  }
}

// 查看订单详情
const viewOrderDetail = (order) => {
  selectedOrder.value = order
  showDetailModal.value = true
}

// 关闭详情弹窗
const closeDetailModal = () => {
  showDetailModal.value = false
  selectedOrder.value = null
}

// 监听状态变化，重新加载订单
watch(activeStatus, () => {
  currentPage.value = 1
  loadOrders()
})

// 组件挂载时加载订单
onMounted(() => {
  loadOrders()
})
</script>

<style scoped>
.orders-page {
  max-width: 1200px;
  margin: 0 auto;
  padding: 40px 20px;
}

.page-header {
  text-align: center;
  margin-bottom: 40px;
}

.page-header h1 {
  font-size: 32px;
  color: #ffffff;
  margin-bottom: 8px;
  font-weight: 700;
}

.page-header p {
  color: #8b949e;
  font-size: 16px;
}

/* 订单状态筛选 */
.orders-tabs {
  display: flex;
  gap: 12px;
  margin-bottom: 30px;
  justify-content: center;
  flex-wrap: wrap;
}

.tab-btn {
  padding: 12px 24px;
  background: #21262d;
  border: 1px solid #30363d;
  color: #c9d1d9;
  border-radius: 10px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s;
  white-space: nowrap;
}

.tab-btn:hover {
  background: #30363d;
  border-color: #484f58;
  color: #ffffff;
}

.tab-btn.active {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-color: transparent;
  color: white;
}

/* 订单列表 */
.orders-list {
  margin-bottom: 30px;
}

.loading,
.empty {
  text-align: center;
  padding: 60px 20px;
  color: #8b949e;
}

.empty span {
  font-size: 64px;
  display: block;
  margin-bottom: 20px;
}

.empty p {
  font-size: 18px;
  margin: 0;
}

/* 订单卡片 */
.order-card {
  background: #161b22;
  border: 1px solid #30363d;
  border-radius: 16px;
  margin-bottom: 20px;
  overflow: hidden;
  transition: all 0.3s;
}

.order-card:hover {
  border-color: #484f58;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.3);
}

/* 订单头部 */
.order-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 20px 24px;
  background: #21262d;
  border-bottom: 1px solid #30363d;
}

.order-info {
  display: flex;
  align-items: center;
  gap: 20px;
}

.order-no {
  color: #c9d1d9;
  font-size: 14px;
}

.order-date {
  color: #8b949e;
  font-size: 13px;
}

.order-status {
  padding: 6px 12px;
  border-radius: 20px;
  font-size: 13px;
  font-weight: 600;
  white-space: nowrap;
}

.order-status.status-pending {
  background: rgba(255, 193, 7, 0.2);
  color: #ffc107;
}

.order-status.status-paid {
  background: rgba(102, 126, 234, 0.2);
  color: #667eea;
}

.order-status.status-shipping {
  background: rgba(255, 193, 7, 0.2);
  color: #ffc107;
}

.order-status.status-shipped {
  background: rgba(255, 107, 107, 0.2);
  color: #ff6b6b;
}

.order-status.status-completed {
  background: rgba(63, 185, 80, 0.2);
  color: #3fb950;
}

/* 订单商品 */
.order-items {
  padding: 20px 24px;
}

.order-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 16px;
  background: #21262d;
  border-radius: 10px;
  margin-bottom: 12px;
}

.order-item:last-child {
  margin-bottom: 0;
}

.item-image {
  width: 80px;
  height: 80px;
  border-radius: 8px;
  object-fit: cover;
  flex-shrink: 0;
}

.item-details {
  flex: 1;
  min-width: 0;
}

.item-details h4 {
  color: #ffffff;
  font-size: 16px;
  margin-bottom: 8px;
  font-weight: 500;
}

.item-price {
  color: #ff6b6b;
  font-size: 14px;
}

.item-quantity {
  color: #8b949e;
  font-size: 14px;
}

.item-subtotal {
  color: #ffffff;
  font-size: 16px;
  font-weight: 600;
  white-space: nowrap;
}

/* 订单信息 */
.order-details {
  padding: 0 24px 20px;
}

.detail-row {
  display: flex;
  gap: 12px;
  padding: 8px 0;
  font-size: 14px;
}

.detail-row .label {
  color: #8b949e;
  min-width: 80px;
}

.detail-row span:last-child {
  color: #c9d1d9;
  word-break: break-word;
}

/* 订单底部 */
.order-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 20px 24px;
  border-top: 1px solid #30363d;
  background: #21262d;
}

.order-summary {
  display: flex;
  align-items: center;
  gap: 12px;
  color: #c9d1d9;
  font-size: 14px;
}

.total-amount {
  font-size: 24px;
  font-weight: 700;
  color: #ff6b6b;
  margin-left: 8px;
}

.order-actions {
  display: flex;
  gap: 12px;
}

.action-btn {
  padding: 10px 20px;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
  border: none;
  white-space: nowrap;
}

.pay-btn {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.pay-btn:hover {
  transform: scale(1.02);
  box-shadow: 0 4px 15px rgba(102, 126, 234, 0.4);
}

.cancel-btn {
  background: #30363d;
  color: #ff6b6b;
  border: 1px solid #484f58;
}

.cancel-btn:hover {
  background: #3d444d;
  border-color: #ff6b6b;
}

.detail-btn {
  background: transparent;
  color: #667eea;
  border: 1px solid #667eea;
}

.detail-btn:hover {
  background: rgba(102, 126, 234, 0.1);
}

/* 分页 */
.pagination {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  margin-top: 30px;
  flex-wrap: wrap;
}

.page-btn {
  min-width: 40px;
  height: 40px;
  padding: 8px 12px;
  background: #21262d;
  border: 1px solid #30363d;
  color: #c9d1d9;
  border-radius: 8px;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s;
}

.page-btn:hover:not(:disabled) {
  background: #30363d;
  border-color: #484f58;
}

.page-btn.active {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-color: transparent;
  color: white;
}

.page-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

/* 订单详情弹窗 */
.order-detail-modal {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.8);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1002;
  padding: 20px;
}

.detail-modal-content {
  background: #161b22;
  border-radius: 20px;
  max-width: 800px;
  width: 100%;
  max-height: 90vh;
  overflow-y: auto;
  border: 1px solid #30363d;
}

.modal-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 24px;
  border-bottom: 1px solid #30363d;
}

.modal-header h2 {
  font-size: 24px;
  color: #ffffff;
  margin: 0;
  font-weight: 700;
}

.close-modal {
  background: transparent;
  border: none;
  color: #8b949e;
  cursor: pointer;
  padding: 8px;
  border-radius: 8px;
  transition: all 0.3s;
  display: flex;
  align-items: center;
  justify-content: center;
}

.close-modal:hover {
  background: #30363d;
  color: #ffffff;
}

.close-modal svg {
  width: 24px;
  height: 24px;
}

.detail-content {
  padding: 24px;
}

.detail-section {
  margin-bottom: 24px;
}

.detail-section h3 {
  color: #ffffff;
  font-size: 18px;
  margin-bottom: 16px;
  font-weight: 600;
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
}

.info-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.info-item.full-width {
  grid-column: 1 / -1;
}

.info-item .label {
  color: #8b949e;
  font-size: 13px;
}

.info-item span:last-child {
  color: #c9d1d9;
  font-size: 14px;
  word-break: break-word;
}

.detail-items {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.detail-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 16px;
  background: #21262d;
  border-radius: 10px;
}

.detail-item .item-image {
  width: 60px;
  height: 60px;
  border-radius: 8px;
  object-fit: cover;
}

.item-info {
  flex: 1;
  min-width: 0;
}

.item-info h4 {
  color: #ffffff;
  font-size: 15px;
  margin-bottom: 8px;
  font-weight: 500;
}

.item-meta {
  display: flex;
  align-items: center;
  gap: 12px;
  color: #8b949e;
  font-size: 13px;
}

.detail-item .item-total {
  color: #ff6b6b;
  font-size: 16px;
  font-weight: 600;
  white-space: nowrap;
}

.detail-summary {
  background: #21262d;
  padding: 20px;
  border-radius: 10px;
}

.summary-row {
  display: flex;
  justify-content: space-between;
  padding: 8px 0;
  color: #c9d1d9;
  font-size: 14px;
}

.summary-row.total {
  border-top: 1px solid #30363d;
  padding-top: 16px;
  margin-top: 8px;
  font-size: 16px;
  font-weight: 600;
}

.summary-row .discount {
  color: #ff6b6b;
}

.summary-row.total .total-amount {
  color: #ff6b6b;
  font-size: 24px;
  font-weight: 700;
}

.remark-text {
  background: #21262d;
  padding: 16px;
  border-radius: 10px;
  color: #c9d1d9;
  line-height: 1.6;
}
</style>