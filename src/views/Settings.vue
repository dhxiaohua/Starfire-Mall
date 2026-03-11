<template>
  <AppLayout :hide-navbar="true" :show-back-button="true" :show-search-bar="false">
    <div class="settings-page">
      <div class="settings-container">
        <!-- 顶部用户信息 -->
        <div class="settings-header">
          <div class="user-profile">
            <div class="profile-avatar">
              <img v-if="userInfo.avatar" :src="userInfo.avatar" alt="头像" />
              <span v-else>{{ (userInfo.nickname || userInfo.username || '?').charAt(0).toUpperCase() }}</span>
            </div>
            <div class="profile-info">
              <h2>{{ userInfo.nickname || userInfo.username }}</h2>
              <p>@{{ userInfo.username }}</p>
              <span class="user-badge" :class="userInfo.role">{{ userInfo.role === 'admin' ? '管理员' : '普通用户' }}</span>
            </div>
          </div>
        </div>

        <div class="settings-content">
          <!-- 左侧菜单 -->
          <div class="settings-menu">
            <div 
              class="menu-item" 
              :class="{ active: activeMenu === 'profile' }"
              @click="activeMenu = 'profile'"
            >
              <span class="menu-icon">👤</span>
              <span>个人资料</span>
            </div>
            <div 
              class="menu-item" 
              :class="{ active: activeMenu === 'security' }"
              @click="activeMenu = 'security'"
            >
              <span class="menu-icon">🔒</span>
              <span>账号安全</span>
            </div>
            <div 
              class="menu-item" 
              :class="{ active: activeMenu === 'notifications' }"
              @click="activeMenu = 'notifications'"
            >
              <span class="menu-icon">🔔</span>
              <span>通知设置</span>
            </div>
            <div 
              class="menu-item" 
              :class="{ active: activeMenu === 'admin' }"
              @click="activeMenu = 'admin'"
              v-if="!userStore.isAdmin"
            >
              <span class="menu-icon">⭐</span>
              <span>申请管理员</span>
            </div>
            <div class="menu-divider"></div>
            <div class="menu-item logout-item" @click="handleLogout">
              <span class="menu-icon">🚪</span>
              <span>退出登录</span>
            </div>
          </div>
          
          <!-- 右侧内容 -->
          <div class="settings-main">
            <!-- 个人资料 -->
            <div v-if="activeMenu === 'profile'" class="settings-section">
              <div class="section-header">
                <h3>个人资料</h3>
                <p>管理您的个人信息</p>
              </div>
              
              <div class="profile-card">
                <div class="avatar-edit-section">
                  <div class="avatar-large" @click="triggerAvatarUpload">
                    <img v-if="editForm.avatar" :src="editForm.avatar" alt="头像" />
                    <span v-else>{{ (editForm.nickname || userInfo.username || '?').charAt(0).toUpperCase() }}</span>
                    <div class="avatar-overlay">
                      <span>更换</span>
                    </div>
                  </div>
                  <input 
                    type="file" 
                    ref="avatarInput" 
                    @change="handleAvatarChange" 
                    accept="image/*" 
                    style="display: none"
                  />
                  <p class="avatar-hint">点击上传头像，支持 JPG、PNG 格式</p>
                </div>
                
                <div class="form-grid">
                  <div class="form-group">
                    <label>昵称</label>
                    <input 
                      type="text" 
                      v-model="editForm.nickname" 
                      placeholder="请输入昵称"
                      class="form-input"
                    />
                  </div>
                  <div class="form-group">
                    <label>用户名</label>
                    <input 
                      type="text" 
                      :value="userInfo.username" 
                      disabled
                      class="form-input disabled"
                    />
                    <span class="form-hint">用户名不可修改</span>
                  </div>
                  <div class="form-group">
                    <label>邮箱</label>
                    <input 
                      type="email" 
                      v-model="editForm.email" 
                      placeholder="请输入邮箱"
                      class="form-input"
                    />
                  </div>
                  <div class="form-group">
                    <label>手机号</label>
                    <input 
                      type="tel" 
                      v-model="editForm.phone" 
                      placeholder="请输入手机号"
                      class="form-input"
                    />
                  </div>
                </div>
                
                <div class="form-actions">
                  <button @click="saveProfile" class="save-btn primary">
                    <span>💾</span> 保存修改
                  </button>
                </div>
              </div>
            </div>
            
            <!-- 账号安全 -->
            <div v-if="activeMenu === 'security'" class="settings-section">
              <div class="section-header">
                <h3>账号安全</h3>
                <p>保护您的账号安全</p>
              </div>
              
              <div class="security-card">
                <div class="security-item">
                  <div class="security-icon">🔑</div>
                  <div class="security-info">
                    <h4>登录密码</h4>
                    <p>定期更换密码可以提高账号安全性</p>
                  </div>
                  <button @click="showPasswordModal = true" class="action-btn">修改密码</button>
                </div>
                
                <div class="security-item">
                  <div class="security-icon">📱</div>
                  <div class="security-info">
                    <h4>绑定手机</h4>
                    <p>{{ userInfo.phone ? '已绑定: ' + userInfo.phone : '未绑定手机号' }}</p>
                  </div>
                  <button @click="activeMenu = 'profile'" class="action-btn">{{ userInfo.phone ? '修改' : '绑定' }}</button>
                </div>
                
                <div class="security-item">
                  <div class="security-icon">📧</div>
                  <div class="security-info">
                    <h4>绑定邮箱</h4>
                    <p>{{ userInfo.email ? '已绑定: ' + userInfo.email : '未绑定邮箱' }}</p>
                  </div>
                  <button @click="activeMenu = 'profile'" class="action-btn">{{ userInfo.email ? '修改' : '绑定' }}</button>
                </div>
              </div>
              
              <div class="danger-zone">
                <h4>⚠️ 危险区域</h4>
                <div class="danger-item">
                  <div class="danger-info">
                    <h5>注销账号</h5>
                    <p>注销后将清除所有数据，无法恢复</p>
                  </div>
                  <button class="danger-btn">注销账号</button>
                </div>
              </div>
            </div>
            
            <!-- 通知设置 -->
            <div v-if="activeMenu === 'notifications'" class="settings-section">
              <div class="section-header">
                <h3>通知设置</h3>
                <p>管理您的通知偏好</p>
              </div>
              
              <div class="notifications-card">
                <div class="notification-item">
                  <div class="notification-info">
                    <h4>订单通知</h4>
                    <p>接收订单状态更新通知</p>
                  </div>
                  <label class="switch">
                    <input type="checkbox" v-model="preferences.orderNotify">
                    <span class="slider"></span>
                  </label>
                </div>
                
                <div class="notification-item">
                  <div class="notification-info">
                    <h4>促销通知</h4>
                    <p>接收优惠活动和促销信息</p>
                  </div>
                  <label class="switch">
                    <input type="checkbox" v-model="preferences.promoNotify">
                    <span class="slider"></span>
                  </label>
                </div>
                
                <div class="notification-item">
                  <div class="notification-info">
                    <h4>系统通知</h4>
                    <p>接收系统公告和更新提醒</p>
                  </div>
                  <label class="switch">
                    <input type="checkbox" v-model="preferences.sysNotify">
                    <span class="slider"></span>
                  </label>
                </div>
                
                <div class="notification-item">
                  <div class="notification-info">
                    <h4>邮件通知</h4>
                    <p>通过邮件接收重要通知</p>
                  </div>
                  <label class="switch">
                    <input type="checkbox" v-model="preferences.emailNotify">
                    <span class="slider"></span>
                  </label>
                </div>
              </div>
            </div>
            
            <!-- 申请管理员 -->
            <div v-if="activeMenu === 'admin' && !userStore.isAdmin" class="settings-section">
              <div class="section-header">
                <h3>申请成为管理员</h3>
                <p>申请后可进入后台管理系统</p>
              </div>
              
              <div class="admin-card">
                <div class="admin-icon">⭐</div>
                <h4>申请管理员权限</h4>
                <p>成为管理员后，您将能够：</p>
                <ul class="admin-benefits">
                  <li>📦 管理商品（添加、编辑、删除）</li>
                  <li>📊 查看销售统计</li>
                  <li>👥 管理用户</li>
                  <li>💬 处理客服消息</li>
                </ul>
                
                <div v-if="userInfo.adminStatus === 'pending'" class="admin-pending">
                  <span class="pending-icon">⏳</span>
                  <p>您的申请正在审核中，请耐心等待...</p>
                </div>
                
                <div v-else-if="userInfo.adminStatus === 'rejected'" class="admin-rejected">
                  <span class="rejected-icon">❌</span>
                  <p>您的申请已被拒绝</p>
                  <button @click="applyAdmin" class="retry-btn">重新申请</button>
                </div>
                
                <button v-else @click="applyAdmin" class="apply-btn">
                  立即申请
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
    
    <!-- 修改密码弹窗 -->
    <div class="modal-overlay" v-if="showPasswordModal" @click.self="showPasswordModal = false">
      <div class="modal-content">
        <div class="modal-header">
          <h3>修改密码</h3>
          <button class="modal-close" @click="showPasswordModal = false">×</button>
        </div>
        <div class="modal-body">
          <div class="form-group">
            <label>当前密码</label>
            <input 
              type="password" 
              v-model="passwordForm.oldPassword" 
              placeholder="请输入当前密码"
              class="form-input"
            />
          </div>
          <div class="form-group">
            <label>新密码</label>
            <input 
              type="password" 
              v-model="passwordForm.newPassword" 
              placeholder="请输入新密码（6-20位）"
              class="form-input"
            />
          </div>
          <div class="form-group">
            <label>确认新密码</label>
            <input 
              type="password" 
              v-model="passwordForm.confirmPassword" 
              placeholder="请再次输入新密码"
              class="form-input"
            />
          </div>
        </div>
        <div class="modal-footer">
          <button @click="showPasswordModal = false" class="cancel-btn">取消</button>
          <button @click="changePassword" class="confirm-btn">确认修改</button>
        </div>
      </div>
    </div>
  </AppLayout>
</template>

<script>
import { ref, onMounted, reactive } from 'vue'
import { useRouter } from 'vue-router'
import AppLayout from '../components/layout/AppLayout.vue'
import { userStore, updateCurrentUser } from '../stores/userStore'
import { getUserInfo, updateUserInfo, changePasswordWithOld, applyAdmin } from '../api'

export default {
  name: 'Settings',
  components: {
    AppLayout
  },
  setup() {
    const router = useRouter()
    const avatarInput = ref(null)
    const activeMenu = ref('profile')
    const showPasswordModal = ref(false)
    
    const userInfo = ref({
      id: '',
      username: '',
      nickname: '',
      email: '',
      phone: '',
      avatar: '',
      role: 'user',
      adminStatus: 'none',
      createTime: ''
    })
    
    const editForm = reactive({
      nickname: '',
      email: '',
      phone: '',
      avatar: ''
    })
    
    const passwordForm = reactive({
      oldPassword: '',
      newPassword: '',
      confirmPassword: ''
    })
    
    const preferences = reactive({
      orderNotify: true,
      promoNotify: true,
      sysNotify: true,
      emailNotify: false
    })

    const loadUserInfo = async () => {
      const savedUser = sessionStorage.getItem('currentUser')
      if (!savedUser) {
        router.push('/')
        return
      }
      
      let username = ''
      try {
        const user = JSON.parse(savedUser)
        username = user.username
        userStore.value.isLoggedIn = true
        userStore.value.username = username
        userStore.value.nickname = user.nickname || ''
        userStore.value.avatar = user.avatar || ''
        userStore.value.isAdmin = user.isAdmin || false
        userStore.value.adminStatus = user.adminStatus || 'none'
      } catch (e) {
        console.error('解析用户数据失败:', e)
        return
      }
      
      if (username) {
        const result = await getUserInfo(username)
        if (result.success) {
          userInfo.value = result.user
          userStore.value.nickname = result.user.nickname || ''
          userStore.value.avatar = result.user.avatar || ''
          userStore.value.isAdmin = result.user.role === 'admin'
          userStore.value.adminStatus = result.user.admin_status || 'none'
          
          editForm.nickname = result.user.nickname || ''
          editForm.email = result.user.email || ''
          editForm.phone = result.user.phone || ''
          editForm.avatar = result.user.avatar || ''
        }
      }
    }

    const triggerAvatarUpload = () => {
      avatarInput.value.click()
    }

    const handleAvatarChange = async (event) => {
      const file = event.target.files[0]
      if (!file) return
      
      if (file.size > 2 * 1024 * 1024) {
        alert('图片大小不能超过2MB')
        return
      }
      
      const reader = new FileReader()
      reader.onload = async (e) => {
        const base64Avatar = e.target.result
        editForm.avatar = base64Avatar
      }
      reader.readAsDataURL(file)
    }

    const saveProfile = async () => {
      const result = await updateUserInfo(
        userStore.value.username,
        editForm.nickname,
        editForm.email,
        editForm.phone,
        editForm.avatar
      )
      
      if (result.success) {
        userStore.value.nickname = editForm.nickname
        userStore.value.avatar = editForm.avatar
        updateCurrentUser({
          nickname: editForm.nickname,
          avatar: editForm.avatar
        })
        userInfo.value.nickname = editForm.nickname
        userInfo.value.email = editForm.email
        userInfo.value.phone = editForm.phone
        userInfo.value.avatar = editForm.avatar
        alert('信息保存成功！')
      } else {
        alert(result.message || '保存失败')
      }
    }

    const changePassword = async () => {
      if (!passwordForm.oldPassword || !passwordForm.newPassword || !passwordForm.confirmPassword) {
        alert('请填写完整信息')
        return
      }
      
      if (passwordForm.newPassword !== passwordForm.confirmPassword) {
        alert('两次密码输入不一致')
        return
      }
      
      if (passwordForm.newPassword.length < 6 || passwordForm.newPassword.length > 20) {
        alert('密码长度应为6-20位')
        return
      }

      const result = await changePasswordWithOld(
        userStore.value.username,
        passwordForm.oldPassword,
        passwordForm.newPassword
      )

      if (result.success) {
        alert('密码修改成功！')
        showPasswordModal.value = false
        passwordForm.oldPassword = ''
        passwordForm.newPassword = ''
        passwordForm.confirmPassword = ''
      } else {
        alert(result.message || '修改失败')
      }
    }

    const handleApplyAdmin = async () => {
      const result = await applyAdmin(userStore.value.username)
      if (result.success) {
        alert('申请已提交，请等待审核')
        userInfo.value.adminStatus = 'pending'
        userStore.value.adminStatus = 'pending'
      } else {
        alert(result.message || '申请失败')
      }
    }

    const handleLogout = () => {
      if (confirm('确定要退出登录吗？')) {
        userStore.value.isLoggedIn = false
        userStore.value.username = ''
        userStore.value.nickname = ''
        userStore.value.avatar = ''
        userStore.value.isAdmin = false
        sessionStorage.removeItem('currentUser')
        router.push('/')
      }
    }

    onMounted(() => {
      loadUserInfo()
    })

    return {
      avatarInput,
      activeMenu,
      showPasswordModal,
      userInfo,
      editForm,
      passwordForm,
      preferences,
      userStore,
      triggerAvatarUpload,
      handleAvatarChange,
      saveProfile,
      changePassword,
      applyAdmin: handleApplyAdmin,
      handleLogout
    }
  }
}
</script>

<style scoped>
.settings-page {
  min-height: 100vh;
  background: #0d0d0d;
  padding: 30px 20px;
  margin-top: -70px;
  padding-top: 100px;
}

.settings-container {
  max-width: 1000px;
  margin: 0 auto;
}

.settings-header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 16px;
  padding: 30px;
  margin-bottom: 24px;
}

.user-profile {
  display: flex;
  align-items: center;
  gap: 24px;
}

.profile-avatar {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 32px;
  font-weight: bold;
  background: rgba(255, 255, 255, 0.2);
  overflow: hidden;
  border: 3px solid rgba(255, 255, 255, 0.5);
}

.profile-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.profile-info h2 {
  color: white;
  font-size: 24px;
  margin-bottom: 4px;
}

.profile-info p {
  color: rgba(255, 255, 255, 0.8);
  font-size: 14px;
}

.user-badge {
  display: inline-block;
  padding: 4px 12px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 20px;
  color: white;
  font-size: 12px;
  margin-top: 8px;
}

.user-badge.admin {
  background: linear-gradient(135deg, #ffd93d 0%, #ff8e53 100%);
  color: #333;
}

.settings-content {
  display: flex;
  gap: 24px;
}

.settings-menu {
  width: 220px;
  background: #1a1a1a;
  border-radius: 12px;
  padding: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.3);
  height: fit-content;
}

.menu-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 14px 16px;
  border-radius: 12px;
  cursor: pointer;
  color: #aaa;
  transition: all 0.3s;
  font-size: 14px;
}

.menu-item:hover {
  background: rgba(102, 126, 234, 0.1);
  color: #667eea;
}

.menu-item.active {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.menu-icon {
  font-size: 18px;
}

.menu-divider {
  height: 1px;
  background: #333;
  margin: 12px 0;
}

.logout-item {
  color: #ff6b6b;
}

.logout-item:hover {
  background: rgba(255, 107, 107, 0.1);
}

.settings-main {
  flex: 1;
}

.settings-section {
  background: #1a1a1a;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.3);
}

.section-header {
  margin-bottom: 24px;
}

.section-header h3 {
  color: #fff;
  font-size: 20px;
  margin-bottom: 8px;
}

.section-header p {
  color: #888;
  font-size: 14px;
}

.profile-card {
  max-width: 600px;
}

.avatar-edit-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
  margin-bottom: 30px;
  padding-bottom: 30px;
  border-bottom: 1px solid #333;
}

.avatar-large {
  width: 100px;
  height: 100px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 40px;
  font-weight: bold;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  cursor: pointer;
  overflow: hidden;
  position: relative;
}

.avatar-large:hover .avatar-overlay {
  opacity: 1;
}

.avatar-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.3s;
}

.avatar-overlay span {
  color: white;
  font-size: 14px;
}

.avatar-large img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.avatar-hint {
  font-size: 12px;
  color: #999;
}

.form-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20px;
}

.form-group {
  margin-bottom: 0;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  color: #333;
  font-size: 14px;
  font-weight: 500;
}

.form-input {
  width: 100%;
  padding: 12px 16px;
  border: 2px solid #e8e8e8;
  border-radius: 10px;
  font-size: 14px;
  transition: all 0.3s;
  background: #fafafa;
}

.form-input:focus {
  outline: none;
  border-color: #667eea;
  background: white;
}

.form-input.disabled {
  background: #f5f5f5;
  color: #999;
  cursor: not-allowed;
}

.form-hint {
  font-size: 12px;
  color: #999;
  margin-top: 4px;
  display: block;
}

.form-actions {
  margin-top: 30px;
  padding-top: 20px;
  border-top: 1px solid #333;
}

.save-btn {
  padding: 12px 32px;
  border: none;
  border-radius: 10px;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s;
}

.save-btn.primary {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.save-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 5px 20px rgba(102, 126, 234, 0.4);
}

/* 安全设置 */
.security-card {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.security-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 20px;
  background: #f8f9fa;
  border-radius: 12px;
}

.security-icon {
  width: 48px;
  height: 48px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
}

.security-info {
  flex: 1;
}

.security-info h4 {
  color: #333;
  font-size: 15px;
  margin-bottom: 4px;
}

.security-info p {
  color: #999;
  font-size: 13px;
}

.action-btn {
  padding: 8px 20px;
  background: white;
  border: 2px solid #667eea;
  border-radius: 8px;
  color: #667eea;
  font-size: 13px;
  cursor: pointer;
  transition: all 0.3s;
}

.action-btn:hover {
  background: #667eea;
  color: white;
}

.danger-zone {
  margin-top: 40px;
  padding-top: 30px;
  border-top: 1px solid #ffebee;
}

.danger-zone h4 {
  color: #f85149;
  font-size: 16px;
  margin-bottom: 16px;
}

.danger-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px 20px;
  background: #2a1a1a;
  border-radius: 12px;
  border: 1px solid #ffcdd2;
}

.danger-info h5 {
  color: #333;
  font-size: 14px;
  margin-bottom: 4px;
}

.danger-info p {
  color: #999;
  font-size: 12px;
}

.danger-btn {
  padding: 8px 16px;
  background: #f85149;
  border: none;
  border-radius: 8px;
  color: white;
  font-size: 13px;
  cursor: pointer;
}

/* 通知设置 */
.notifications-card {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.notification-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 20px;
  background: #f8f9fa;
  border-radius: 12px;
}

.notification-info h4 {
  color: #333;
  font-size: 15px;
  margin-bottom: 4px;
}

.notification-info p {
  color: #999;
  font-size: 13px;
}

.switch {
  position: relative;
  display: inline-block;
  width: 50px;
  height: 28px;
}

.switch input {
  opacity: 0;
  width: 0;
  height: 0;
}

.slider {
  position: absolute;
  cursor: pointer;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: #ccc;
  transition: 0.3s;
  border-radius: 28px;
}

.slider:before {
  position: absolute;
  content: "";
  height: 22px;
  width: 22px;
  left: 3px;
  bottom: 3px;
  background-color: white;
  transition: 0.3s;
  border-radius: 50%;
}

input:checked + .slider {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

input:checked + .slider:before {
  transform: translateX(22px);
}

/* 申请管理员 */
.admin-card {
  text-align: center;
  padding: 40px;
  background: linear-gradient(135deg, #2a2a1a 0%, #2a2510 100%);
  border-radius: 16px;
  border: 2px dashed #ffd93d;
}

.admin-icon {
  font-size: 60px;
  margin-bottom: 20px;
}

.admin-card h4 {
  color: #333;
  font-size: 20px;
  margin-bottom: 12px;
}

.admin-card p {
  color: #666;
  font-size: 14px;
  margin-bottom: 20px;
}

.admin-benefits {
  list-style: none;
  text-align: left;
  display: inline-block;
  margin-bottom: 30px;
}

.admin-benefits li {
  padding: 8px 0;
  color: #555;
  font-size: 14px;
}

.admin-pending, .admin-rejected {
  padding: 20px;
  border-radius: 12px;
  margin-top: 20px;
}

.admin-pending {
  background: #2a2015;
}

.admin-pending .pending-icon {
  font-size: 32px;
}

.admin-pending p {
  margin-top: 10px;
  color: #f57c00;
}

.admin-rejected {
  background: #ffebee;
}

.admin-rejected .rejected-icon {
  font-size: 32px;
}

.admin-rejected p {
  margin-top: 10px;
  color: #f85149;
}

.retry-btn {
  margin-top: 15px;
  padding: 10px 24px;
  background: #667eea;
  border: none;
  border-radius: 8px;
  color: white;
  cursor: pointer;
}

.apply-btn {
  padding: 14px 40px;
  background: linear-gradient(135deg, #ffd93d 0%, #ff8e53 100%);
  border: none;
  border-radius: 12px;
  color: #333;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
}

.apply-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 5px 20px rgba(255, 217, 61, 0.5);
}

/* 弹窗 */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 10000;
}

.modal-content {
  background: white;
  border-radius: 16px;
  width: 420px;
  max-width: 90vw;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 24px;
  border-bottom: 1px solid #f0f0f0;
}

.modal-header h3 {
  color: #333;
  font-size: 18px;
}

.modal-close {
  width: 32px;
  height: 32px;
  background: #f5f5f5;
  border: none;
  border-radius: 50%;
  color: #666;
  font-size: 20px;
  cursor: pointer;
}

.modal-body {
  padding: 24px;
}

.modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding: 16px 24px;
  border-top: 1px solid #f0f0f0;
}

.cancel-btn {
  padding: 10px 20px;
  background: #f5f5f5;
  border: none;
  border-radius: 8px;
  color: #666;
  cursor: pointer;
}

.confirm-btn {
  padding: 10px 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  border-radius: 8px;
  color: white;
  cursor: pointer;
}

@media (max-width: 768px) {
  .settings-content {
    flex-direction: column;
  }
  
  .settings-menu {
    width: 100%;
  }
  
  .form-grid {
    grid-template-columns: 1fr;
  }
}
</style>
