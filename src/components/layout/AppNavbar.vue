<template>
  <nav class="navbar" :class="{ 'navbar-transparent': transparent }" :style="{ background: customBgColor }">
    <div class="nav-content">
      <!-- Logo -->
      <div class="nav-logo">
        <router-link to="/home">网站Logo</router-link>
      </div>
      
      <!-- 导航链接 -->
      <div class="nav-links">
        <router-link to="/home">首页</router-link>
        <router-link to="/page1">页面1</router-link>
        <router-link to="/page2">页面2</router-link>
        <router-link to="/page3">页面3</router-link>
        <router-link to="/page4">页面4</router-link>
        <router-link to="/page5">页面5</router-link>
      </div>
      
      <!-- 用户区域 -->
      <div class="nav-user">
        <template v-if="!userStore.isLoggedIn">
          <router-link to="/login" class="login-link">登录</router-link>
          <router-link to="/register" class="register-link">注册</router-link>
        </template>
        <template v-else>
          <UserDropdown @logout="handleLogout" />
        </template>
      </div>
    </div>
  </nav>
</template>

<script>
import { ref, onMounted, onUnmounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { userStore } from '../../stores/userStore'
import UserDropdown from './AppUserDropdown.vue'

export default {
  name: 'AppNavbar',
  components: {
    UserDropdown
  },
  props: {
    // 导航栏是否透明（首页用）
    transparent: {
      type: Boolean,
      default: false
    },
    // 是否显示导航栏（首页滚动时用）
    showNavbar: {
      type: Boolean,
      default: true
    },
    // 自定义背景色
    bgColor: {
      type: String,
      default: ''
    }
  },
  emits: ['logout'],
  setup(props, { emit }) {
    const router = useRouter()
    
    const customBgColor = computed(() => {
      return props.bgColor || (props.transparent ? 'transparent' : 'rgba(191, 219, 254, 0.95)')
    })

    const handleLogout = () => {
      userStore.value.isLoggedIn = false
      userStore.value.username = ''
      sessionStorage.removeItem('currentUser')
      alert('已退出登录')
      router.push('/')
      emit('logout')
    }

    return {
      userStore,
      customBgColor,
      handleLogout
    }
  }
}
</script>

<style scoped>
.navbar {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  padding: 15px 0;
  background: rgba(191, 219, 254, 0.95);
  backdrop-filter: blur(20px);
  z-index: 1000;
  transition: all 0.3s ease;
}

.navbar-transparent {
  background: transparent !important;
  backdrop-filter: none;
}

.navbar-show {
  transform: translateY(0);
}

.nav-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 40px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.nav-logo a {
  font-size: 24px;
  font-weight: bold;
  color: #1e40af;
  text-decoration: none;
}

.navbar-transparent .nav-logo a {
  color: #1e40af;
}

.nav-links {
  display: flex;
  gap: 35px;
}

.nav-links a {
  color: #1e3a8a;
  text-decoration: none;
  font-size: 15px;
  font-weight: 500;
  padding: 8px 0;
  position: relative;
  transition: color 0.3s;
}

.navbar-transparent .nav-links a {
  color: #1e3a8a;
}

.nav-links a::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 0;
  width: 0;
  height: 2px;
  background: #1e40af;
  transition: width 0.3s;
}

.nav-links a:hover {
  color: #1e40af;
}

.nav-links a:hover::after,
.nav-links a.router-link-active::after {
  width: 100%;
}

.nav-links a.router-link-active {
  color: #1e40af;
}

.nav-user {
  display: flex;
  align-items: center;
  gap: 15px;
}

.login-link, .register-link {
  color: #1e40af !important;
  padding: 10px 24px;
  border-radius: 25px;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.3s;
  text-decoration: none;
}

.register-link {
  background: #1e40af;
  color: white !important;
}

.login-link:hover, .register-link:hover {
  transform: translateY(-2px);
  box-shadow: 0 5px 20px rgba(30, 64, 175, 0.3);
}

@media (max-width: 768px) {
  .nav-links {
    display: none;
  }
}
</style>
