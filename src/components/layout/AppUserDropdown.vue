<template>
  <div class="user-dropdown" @click="toggleDropdown">
    <div class="user-avatar">
      <img v-if="userStore.avatar" :src="userStore.avatar" alt="头像" />
      <span v-else>{{ usernameFirstChar }}</span>
    </div>
    <div class="dropdown-menu" v-if="showDropdown">
      <div class="dropdown-header">
        <span class="dropdown-nickname">{{ displayName }}</span>
        <span class="dropdown-username">@{{ userStore.username }}</span>
        <span v-if="userStore.isAdmin" class="admin-badge">管理员</span>
      </div>
      
      <!-- 管理员入口 -->
      <template v-if="userStore.isAdmin">
        <div class="dropdown-divider"></div>
        <div class="dropdown-item" @click="goToAdmin" v-if="!userStore.isAdminMode">
          <span class="dropdown-icon">⚙️</span>
          进入管理端
        </div>
        <div class="dropdown-item" @click="switchToUser" v-else>
          <span class="dropdown-icon">👤</span>
          切换到用户端
        </div>
      </template>
      
      <!-- 申请成为管理员 -->
      <template v-if="!userStore.isAdmin && userStore.canApply">
        <div class="dropdown-divider"></div>
        <div class="dropdown-item" @click="applyForAdmin">
          <span class="dropdown-icon">⭐</span>
          申请成为管理员
        </div>
      </template>
      
      <!-- 申请状态提示 -->
      <template v-if="!userStore.isAdmin && userStore.adminStatus === 'pending'">
        <div class="dropdown-divider"></div>
        <div class="dropdown-item disabled">
          <span class="dropdown-icon">⏳</span>
          申请待审批中...
        </div>
      </template>
      
      <div class="dropdown-divider"></div>
      <router-link to="/settings" class="dropdown-item" @click="showDropdown = false">
        <span class="dropdown-icon">🔧</span>
        个人设置
      </router-link>
      <div class="dropdown-item" @click="handleLogout">
        <span class="dropdown-icon">🚪</span>
        退出登录
      </div>
    </div>
  </div>
</template>

<script>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { userStore, toggleAdminMode, updateAdminStatus } from '../../stores/userStore'
import { applyAdmin, getUserAdminStatus } from '../../api'

export default {
  name: 'UserDropdown',
  emits: ['logout'],
  setup(props, { emit }) {
    const router = useRouter()
    const showDropdown = ref(false)

    const usernameFirstChar = computed(() => {
      const name = userStore.value.nickname || userStore.value.username || '?'
      return name.charAt(0).toUpperCase()
    })

    const displayName = computed(() => {
      return userStore.value.nickname || userStore.value.username || '用户'
    })

    const toggleDropdown = () => {
      showDropdown.value = !showDropdown.value
    }

    // 加载用户管理员状态
    const loadAdminStatus = async () => {
      if (userStore.value.isLoggedIn && userStore.value.username) {
        const result = await getUserAdminStatus(userStore.value.username)
        if (result.success) {
          updateAdminStatus(result.isAdmin, result.adminStatus, result.canApply)
        }
      }
    }

    // 进入管理端
    const goToAdmin = () => {
      showDropdown.value = false
      userStore.value.isAdminMode = true
      const user = JSON.parse(sessionStorage.getItem('currentUser') || '{}')
      user.isAdminMode = true
      sessionStorage.setItem('currentUser', JSON.stringify(user))
      router.push('/admin')
    }

    // 切换到用户端
    const switchToUser = () => {
      showDropdown.value = false
      toggleAdminMode()
      router.push('/home')
    }

    // 申请成为管理员
    const applyForAdmin = async () => {
      showDropdown.value = false
      const result = await applyAdmin(userStore.value.username)
      if (result.success) {
        alert(result.message)
        userStore.value.adminStatus = 'pending'
        userStore.value.canApply = false
      } else {
        alert(result.message)
      }
    }

    const handleLogout = () => {
      showDropdown.value = false
      emit('logout')
    }

    const handleClickOutside = (e) => {
      if (!e.target.closest('.user-dropdown')) {
        showDropdown.value = false
      }
    }

    onMounted(() => {
      document.addEventListener('click', handleClickOutside)
      loadAdminStatus()
    })

    onUnmounted(() => {
      document.removeEventListener('click', handleClickOutside)
    })

    return {
      userStore,
      showDropdown,
      usernameFirstChar,
      displayName,
      toggleDropdown,
      goToAdmin,
      switchToUser,
      applyForAdmin,
      handleLogout
    }
  }
}
</script>

<style scoped>
.user-dropdown {
  position: relative;
  cursor: pointer;
}

.user-avatar {
  width: 42px;
  height: 42px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 18px;
  font-weight: bold;
  transition: transform 0.3s, box-shadow 0.3s;
  overflow: hidden;
}

.user-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.user-avatar:hover {
  transform: scale(1.05);
  box-shadow: 0 5px 20px rgba(102, 126, 234, 0.4);
}

.dropdown-menu {
  position: absolute;
  top: 55px;
  right: 0;
  background: white;
  border-radius: 12px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.15);
  min-width: 200px;
  overflow: hidden;
  animation: dropdownShow 0.2s ease;
}

@keyframes dropdownShow {
  from {
    opacity: 0;
    transform: translateY(-10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.dropdown-header {
  padding: 15px 20px;
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.1) 0%, rgba(118, 75, 162, 0.1) 100%);
  border-bottom: 1px solid #f0f0f0;
}

.dropdown-nickname {
  display: block;
  font-weight: 600;
  color: #333;
}

.dropdown-username {
  display: block;
  font-size: 12px;
  color: #999;
  margin-top: 2px;
}

.admin-badge {
  display: inline-block;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 2px 10px;
  border-radius: 10px;
  font-size: 11px;
  margin-top: 6px;
}

.dropdown-divider {
  height: 1px;
  background: #f0f0f0;
  margin: 4px 0;
}

.dropdown-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 12px 20px;
  color: #333;
  cursor: pointer;
  transition: background 0.2s;
  text-decoration: none;
}

.dropdown-item:hover {
  background: rgba(102, 126, 234, 0.05);
}

.dropdown-item.disabled {
  color: #999;
  cursor: not-allowed;
}

.dropdown-item.disabled:hover {
  background: transparent;
}

.dropdown-icon {
  font-size: 16px;
  width: 24px;
  text-align: center;
}
</style>