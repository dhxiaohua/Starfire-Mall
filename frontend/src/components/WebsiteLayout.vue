<template>
  <div class="website">
    <!-- 顶部导航栏?-->
    <header class="navbar">
      <div class="navbar-container">
        <!-- Logo（不跳转?-->
        <div class="navbar-logo">
            <div class="logo-circle">
              <img src="/images/logo.jpg" alt="Logo" class="logo-img" />
            </div>
          </div>

        <!-- 导航栏文字 -->
        <nav class="navbar-nav">
          <router-link to="/" class="nav-item" :class="{ active: currentPage === 'home' }">首页</router-link>
          
          <!-- 产品下拉菜单 -->
          <div class="nav-dropdown" @mouseenter="showProductMenu = true" @mouseleave="showProductMenu = false">
            <router-link to="/products" class="nav-item dropdown-trigger" :class="{ active: currentPage === 'product' }">
              产品
              <svg class="dropdown-arrow" viewBox="0 0 20 20" fill="currentColor">
                <path fill-rule="evenodd" d="M5.293 7.293a1 1 0 011.414 0L10 10.586l3.293-3.293a1 1 0 111.414 1.414l-4 4a1 1 0 01-1.414 0l-4-4a1 1 0 010-1.414z" clip-rule="evenodd" />
              </svg>
            </router-link>
            <div class="dropdown-menu-large" v-if="showProductMenu">
              <div class="dropdown-category" @click="goToProduct('mouse')">
                <span class="category-icon">🖱️</span>
                <div class="category-info">
                  <h4>电竞鼠标</h4>
                  <p>专业级游戏鼠标</p>
                </div>
              </div>
              <div class="dropdown-category" @click="goToProduct('keyboard')">
                <span class="category-icon">⌨️</span>
                <div class="category-info">
                  <h4>机械键盘</h4>
                  <p>青轴/红轴可选</p>
                </div>
              </div>
              <div class="dropdown-category" @click="goToProduct('headset')">
                <span class="category-icon">🎧</span>
                <div class="category-info">
                  <h4>游戏耳机</h4>
                  <p>7.1环绕立体声</p>
                </div>
              </div>
              <div class="dropdown-category" @click="goToProduct('controller')">
                <span class="category-icon">🎮</span>
                <div class="category-info">
                  <h4>游戏手柄</h4>
                  <p>多平台兼容</p>
                </div>
              </div>
            </div>
          </div>
          
          <router-link to="/about" class="nav-item" :class="{ active: currentPage === 'about' }">关于我们</router-link>
          <router-link to="/contact" class="nav-item" :class="{ active: currentPage === 'contact' }">联系我们</router-link>
        </nav>

        <!-- 右侧：搜索框框 + 用户区域 -->
        <div class="navbar-right">
          <!-- 搜索框?-->
          <div class="navbar-search">
            <div class="search-box">
              <svg class="search-icon" viewBox="0 0 20 20" fill="currentColor">
                <path fill-rule="evenodd" d="M8 4a4 4 0 100 8 4 4 0 000-8zM2 8a6 6 0 1110.89 3.476l4.817 4.817a1 1 0 01-1.414 1.414l-4.816-4.816A6 6 0 012 8z" clip-rule="evenodd" />
              </svg>
              <input 
                type="text" 
                v-model="searchText" 
                placeholder="搜索框..." 
                class="search-input"
                @keyup.enter="handleSearch"
              />
            </div>
          </div>

          <!-- 用户区域 -->
          <div class="navbar-user">
            <template v-if="!userStore.isLoggedIn">
              <a href="javascript:;" class="user-link" @click="showAuthModal = true">登录</a>
              <a href="javascript:;" class="user-link signup-btn" @click="showAuthModal = true">注册</a>
            </template>
            <template v-else>
              <!-- 头像 -->
              <div class="user-dropdown" @click="toggleDropdown">
                <img 
                  :src="userStore.avatar || defaultAvatar" 
                  alt="avatar" 
                  class="user-avatar"
                  @error="handleAvatarError"
                />
                <div class="dropdown-menu" v-if="showDropdown">
                  <div class="dropdown-header">
                    <span class="dropdown-username">{{ userStore.username }}</span>
                    <span class="admin-badge" v-if="userStore.isAdmin">管理</span>
                  </div>
                  
                  <div class="dropdown-divider"></div>
                  <router-link to="/settings" class="dropdown-item" @click="showDropdown = false">
                    个人设置
                  </router-link>
                  <template v-if="userStore.isAdmin">
                    <div class="dropdown-divider"></div>
                    <router-link to="/admin" class="dropdown-item" @click="showDropdown = false">
                      管理后台
                    </router-link>
                  </template>
                  <div class="dropdown-divider"></div>
                  <div class="dropdown-item logout" @click="logout">退出登录</div>
                </div>
              </div>
              <!-- 设置图标 -->
              <router-link to="/settings" class="user-icon">
                <svg viewBox="0 0 20 20" fill="currentColor">
                  <path fill-rule="evenodd" d="M11.49 3.17c-.38-1.56-2.6-1.56-2.98 0a1.532 1.532 0 01-2.286.948c-1.372-.836-2.942.734-2.106 2.106.54.886.061 2.042-.947 2.287-1.561.379-1.561 2.6 0 2.978a1.532 1.532 0 01.947 2.287c-.836 1.372.734 2.942 2.106 2.106a1.532 1.532 0 012.287.947c.379 1.561 2.6 1.561 2.978 0a1.533 1.533 0 012.287-.947c1.372.836 2.942-.734 2.106-2.106a1.533 1.533 0 01.947-2.287c1.561-.379 1.561-2.6 0-2.978a1.532 1.532 0 01-.947-2.287c.836-1.372-.734-2.942-2.106-2.106a1.532 1.532 0 01-2.287-.947zM10 13a3 3 0 100-6 3 3 0 000 6z" clip-rule="evenodd" />
                </svg>
              </router-link>
              <!-- 收藏图标 -->
              <div class="user-icon">
                <svg viewBox="0 0 20 20" fill="currentColor">
                  <path d="M9.049 2.927c.3-.921 1.603-.921 1.902 0l1.07 3.292a1 1 0 00.95.69h3.462c.969 0 1.371 1.24.588 1.81l-2.8 2.034a1 1 0 00-.364 1.118l1.07 3.292c.3.921-.755 1.688-1.54 1.118l-2.8-2.034a1 1 0 00-1.175 0l-2.8 2.034c-.784.57-1.838-.197-1.539-1.118l1.07-3.292a1 1 0 00-.364-1.118L2.98 8.72c-.783-.57-.38-1.81.588-1.81h3.461a1 1 0 00.951-.69l1.07-3.292z" />
                </svg>
              </div>
            </template>
          </div>
        </div>
      </div>
    </header>

    <!-- 登录/注册弹窗 -->
    <div class="auth-modal-overlay" v-if="showAuthModal" @click.self="showAuthModal = false">
      <div class="auth-modal">
        <button class="modal-close" @click="showAuthModal = false">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M6 18L18 6M6 6l12 12"/>
          </svg>
        </button>
        
        <!-- 标签切换 -->
        <div class="auth-tabs">
          <button 
            class="auth-tab" 
            :class="{ active: authMode === 'login' }"
            @click="authMode = 'login'"
          >登录</button>
          <button 
            class="auth-tab" 
            :class="{ active: authMode === 'register' }"
            @click="authMode = 'register'"
          >注册</button>
        </div>
        
        <!-- 登录表单 -->
        <div class="auth-form" v-if="authMode === 'login'">
          <div class="form-group">
            <input 
              type="text" 
              v-model="loginForm.username" 
              placeholder="请输入账号"
              class="form-input"
              @input="onUsernameInput"
            />
          </div>
          <div class="form-group">
            <input 
              :type="showPassword ? 'text' : 'password'" 
              v-model="loginForm.password" 
              placeholder="请输入密码"
              class="form-input"
            />
            <button type="button" class="toggle-password" @click="showPassword = !showPassword">
              {{ showPassword ? '隐藏' : '显示' }}
            </button>
          </div>
          <div class="form-group captcha-group">
            <div class="captcha-row">
              <input 
                type="text" 
                              v-model="loginForm.captcha" 
                              placeholder="请输入验证码"                class="form-input captcha-input"
                maxlength="4"
              />
              <button
                type="button"
                class="get-captcha-btn"
                @click="fetchLoginCaptcha"
                :disabled="loginCountdown > 0"
              >
                {{ loginCountdown > 0 ? loginCountdown + '秒后重新获取' : '获取验证码' }}
              </button>
            </div>
            <div class="captcha-display" v-if="captchaCode">
              <span class="captcha-label">验证码：</span>
              <span class="captcha-code">{{ captchaCode }}</span>
              <span 
                class="refresh-btn" 
                @click="fetchLoginCaptcha"
                :class="{ disabled: loginCountdown > 0 }"
                :style="{ cursor: loginCountdown > 0 ? 'not-allowed' : 'pointer' }"
              >
                刷新
              </span>
            </div>
          </div>
          <button @click="handleLogin" class="auth-button">登录</button>
          <div class="auth-tip">
            <span>还没有账号？</span>
            <a href="javascript:;" @click="authMode = 'register'">立即注册</a>
          </div>
        </div>
        
        <!-- 注册表单 -->
        <div class="auth-form" v-else>
          <div class="form-group">
            <input 
              type="text" 
              v-model="registerForm.username" 
              placeholder="请输入账号(4-20位)" 
              class="form-input"
              @input="onRegisterUsernameInput"
            />
          </div>
          <div class="form-group">
            <input 
              type="text" 
              v-model="registerForm.nickname" 
              placeholder="请输入昵称"class="form-input"
            />
          </div>
          <div class="form-group">
            <input 
              :type="showRegisterPassword ? 'text' : 'password'" 
              v-model="registerForm.password" 
              placeholder="请输入密码(6-20位)" 
              class="form-input"
            />
            <button type="button" class="toggle-password" @click="showRegisterPassword = !showRegisterPassword">
              {{ showRegisterPassword ? '隐藏' : '显示' }}
            </button>
          </div>
          <div class="form-group">
            <input 
              :type="showRegisterPassword ? 'text' : 'password'" 
              v-model="registerForm.confirmPassword" 
              placeholder="请再次输入密码"
              class="form-input"
            />
          </div>
          <div class="form-group captcha-group">
            <div class="captcha-row">
              <input 
                type="text" 
                v-model="registerForm.captcha" 
                placeholder="请输入验证码" 
                class="form-input captcha-input"
                maxlength="4"
              />
              <button
                type="button"
                class="get-captcha-btn"
                @click="fetchRegisterCaptcha"
                :disabled="registerCountdown > 0"
              >
                {{ registerCountdown > 0 ? registerCountdown + '秒后重新获取' : '获取验证码' }}
              </button>
            </div>
            <div class="captcha-display" v-if="captchaCode">
              <span class="captcha-label">验证码：</span>
              <span class="captcha-code">{{ captchaCode }}</span>
              <span 
                class="refresh-btn" 
                @click="fetchRegisterCaptcha"
                :class="{ disabled: registerCountdown > 0 }"
                :style="{ cursor: registerCountdown > 0 ? 'not-allowed' : 'pointer' }"
              >
                刷新
              </span>
            </div>
          </div>
          <button @click="handleRegister" class="auth-button">注册</button>
          <div class="auth-tip">
            <span>已有账号？</span>
            <a href="javascript:;" @click="authMode = 'login'">立即登录</a>
          </div>
        </div>
      </div>
    </div>

    <!-- 页面主体内容 -->
    <main class="main-content">
      <div class="content-wrapper">
        <slot></slot>
      </div>
    </main>
  </div>
</template>

<script>
import { ref, onMounted, onUnmounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { userStore, setUserLogin } from '../stores/userStore'
import { login as loginApi, register as registerApi, getUserInfo, getCaptcha } from '../api'

export default {
  name: 'WebsiteLayout',
  props: {
    currentPage: {
      type: String,
      default: 'home'
    }
  },
  setup() {
    const router = useRouter()
    const searchText = ref('')
    const showDropdown = ref(false)
    const showProductMenu = ref(false)
    const defaultAvatar = 'data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHdpZHRoPSIxMDAiIGhlaWdodD0iMTAwIj48Y2lyY2xlIGN4PSI1MCIgY3k9IjUwIiByPSI0MCIgZmlsbD0iIzY2N2VlYSIvPjx0ZXh0IHg9IjUwIiB5PSI1NSIgZm9udC1zaXplPSIyMCIgdGV4dC1hbmNob3I9Im1pZGRsZSIgZmlsbD0id2hpdGUiPmF1dGhvcjwvdGV4dD48L3N2Zz4='
    
    // 弹窗相关
    const showAuthModal = ref(false)
    const authMode = ref('login')
    const showPassword = ref(false)
    const showRegisterPassword = ref(false)
    const captchaCode = ref('')
    const loginForm = ref({ username: '', password: '', captcha: '' })
    const registerForm = ref({ username: '', nickname: '', password: '', confirmPassword: '', captcha: '' })
    const loginCountdown = ref(0)
    const registerCountdown = ref(0)

    const toggleDropdown = () => {
      showDropdown.value = !showDropdown.value
    }

    const goToProduct = (category) => {
      router.push({ path: '/products', query: { category } })
      showProductMenu.value = false
    }

    const logout = () => {
      userStore.value.isLoggedIn = false
      userStore.value.username = ''
      sessionStorage.removeItem('currentUser')
      showDropdown.value = false
      // 刷新页面以重新初始化星空背景
      window.location.reload()
    }

    const handleAvatarError = (e) => {
      e.target.src = defaultAvatar
    }

    const handleSearch = () => {
      if (searchText.value.trim()) {
        console.log('搜索框:', searchText.value)
      }
    }

    const handleClickOutside = (e) => {
      if (!e.target.closest('.user-dropdown')) {
        showDropdown.value = false
      }
    }

    onMounted(() => {
      document.addEventListener('click', handleClickOutside)
    })

    onUnmounted(() => {
      document.removeEventListener('click', handleClickOutside)
    })
    
    // 验证码管理对象    
    const captchaManager = {
      // 生成随机验证码（本地备用）      
      generateLocal: () => {
        const chars = 'ABCDEFGHJKLMNPQRSTUVWXYZ23456789'
        let code = ''
        for (let i = 0; i < 4; i++) {
          code += chars.charAt(Math.floor(Math.random() * chars.length))
        }
        return code
      },
      
      // 获取验证码（从后端获取，绑定到用户名）      
      async fetch(username) {
        console.log('开始获取验证码，用户名:', username)
        const targetUsername = username || loginForm.value.username || registerForm.value.username || 'default'
        console.log('目标用户名', targetUsername)
        try {
          const result = await getCaptcha(targetUsername)
          console.log('验证码API返回结果:', result)
          if (result && result.code) {
            captchaCode.value = result.code
            console.log('使用后端返回的验证码:', captchaCode.value)
          } else {
            // 如果后端返回的验证码为空，使用本地生成的验证码
            captchaCode.value = captchaManager.generateLocal()
            console.log('使用本地生成的验证码:', captchaCode.value)
          }
        } catch (error) {
          console.error('获取验证码失败:', error)
          // 出错时使用本地生成的验证码
          captchaCode.value = captchaManager.generateLocal()
          console.log('使用本地生成的验证码:', captchaCode.value)
        }
      },
      
      // 启动倒计时?      
      startCountdown(type) {
        const countdownRef = type === 'login' ? loginCountdown : registerCountdown
        countdownRef.value = 180 // 3分钟
        
        const timer = setInterval(() => {
          if (countdownRef.value > 0) {
            countdownRef.value--
          } else {
            clearInterval(timer)
          }
        }, 1000)
      },
      
      // 登录时获取验证码
      fetchLogin() {
        
        
        if (loginCountdown.value > 0) {
         alert('请先输入用户名')
          return
        }
        
        console.log('登录时获取验证码，用户名:', loginForm.value.username)
        captchaManager.fetch(loginForm.value.username)
        captchaManager.startCountdown('login')
      },
      
      // 注册时获取验证码
      fetchRegister() {
        if (!registerForm.value.username) {
          alert('请先输入用户名')
          return
        }
        
        if (registerCountdown.value > 0) {
          return
        }
        
        captchaManager.fetch(registerForm.value.username)
        captchaManager.startCountdown('register')
      },
    }
    
    // 导出方法
    const fetchLoginCaptcha = captchaManager.fetchLogin
    const fetchRegisterCaptcha = captchaManager.fetchRegister
    const fetchCaptcha = captchaManager.fetch
    
    // 登录处理
    const handleLogin = async () => {
      const { username, password, captcha } = loginForm.value
      if (!username || !password || !captcha) {
        alert('请填写完整信息')
        return
      }
      
      console.log('准备登录，用户名:', username, '验证码:', captcha)
      
      // 调用后端 API 验证码验证码?      
      const result = await loginApi(username, password, captcha)
      
      console.log('登录API返回结果:', result)
      
      if (result.success && result.data) {
        // 从data中获取用户信息（适配新后端格式）
        const user = result.data.user
        const userInfo = await getUserInfo(user.username)
        const userData = userInfo.success ? userInfo.data : user
        
        // 使用登录API返回的isAdmin状态（根据role判断是否为管理员）        
        const isAdmin = user.role === 'admin' || user.isAdmin || false
        
        // 合并用户数据
        const finalUserData = {
          ...userData,
          isAdmin: isAdmin,
          token: result.data.token
        }
        
        // 使用setUserLogin支持多用户登录?        
        setUserLogin(finalUserData)
        
        alert('登录成功')
        // 清空表单
        loginForm.value = { username: '', password: '', captcha: '' }
        showAuthModal.value = false
        
        // 如果是管理员，跳转到后台页面
        if (isAdmin) {
          router.push('/admin')
        } else {
          // 普通用户刷新页面
          window.location.reload()
        }
      } else {
        alert(result.message || '登录失败败')
        console.log('登录失败败原因:', result.message)
        fetchCaptcha(loginForm.value.username)
        loginForm.value.captcha = ''
      }
    }
    
    // 注册处理
    const handleRegister = async () => {
      const { username, nickname, password, confirmPassword, captcha } = registerForm.value
      if (!username || !nickname || !password || !confirmPassword || !captcha) {
        alert('请填写完整信息')
        return
      }
      if (password !== confirmPassword) {
        alert('两次密码输入不一致')
        return
      }

      // 调用后端 API 注册
      const result = await registerApi(username, password, captcha, nickname)

      if (result.success) {
        alert('注册成功！请登录')
        authMode.value = 'login'
        // 清空注册表单
        registerForm.value = { username: '', nickname: '', password: '', confirmPassword: '', captcha: '' }
        fetchCaptcha()
      } else {
        alert(result.message || '注册失败')
        fetchCaptcha()
        registerForm.value.captcha = ''
      }
    }
    
    // 监听弹窗关闭，清空表单?    
    watch(showAuthModal, (newVal) => {
      if (!newVal) {
        // 弹窗关闭时清空表单?        
        loginForm.value = { username: '', password: '', captcha: '' }
        registerForm.value = { username: '', nickname: '', password: '', confirmPassword: '', captcha: '' }
        captchaCode.value = ''
      }
    })
    
    // 监听登录/注册模式切换，清空验证码
    watch(authMode, (newVal) => {
      if (showAuthModal.value) {
        // 切换模式时清空验证码
        captchaCode.value = ''
      }
    })
    
    // 初始化验证码
    return {
      searchText,
      showDropdown,
      showProductMenu,
      defaultAvatar,
      userStore,
      toggleDropdown,
      goToProduct,
      logout,
      handleAvatarError,
      handleSearch,
      // 弹窗相关
      showAuthModal,
      authMode,
      showPassword,
      showRegisterPassword,
      captchaCode,
      loginForm,
      registerForm,
      loginCountdown,
      registerCountdown,
      fetchCaptcha,
      fetchLoginCaptcha,
      fetchRegisterCaptcha,
      handleLogin,
      handleRegister
    }
  }
}
</script>

<style scoped>
/* 全局样式 */
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

.website {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
  background-color: #0d1117;
}

/* 顶部导航栏?*/
.navbar {
  background-color: #161b22;
  height: 64px;
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 1000;
  border-bottom: 1px solid #30363d;
}

.navbar-container {
  max-width: 1400px;
  height: 100%;
  margin: 0 auto;
  padding: 0 24px;
  display: flex;
  align-items: center;
  gap: 24px;
}

/* Logo - 圆形 */
.navbar-logo {
  flex-shrink: 0;
}

.logo-circle {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  overflow: hidden;
  border: 1px solid #30363d;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #21262d;
  cursor: default;
}

.logo-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

/* 导航栏链接 */
.navbar-nav {
  display: flex;
  gap: 24px;
  flex: 1;
  justify-content: flex-start;
  padding-left: 32px;
}

.nav-item {
  display: flex;
  align-items: center;
  padding: 8px 20px;
  color: #c9d1d9;
  text-decoration: none;
  font-size: 14px;
  font-weight: 500;
  border-radius: 6px;
  transition: all 0.2s ease;
}

.nav-item:hover {
  background-color: #21262d;
  color: #ffffff;
}

.nav-item.active {
  background-color: #21262d;
  color: #ffffff;
}

/* 产品下拉菜单 */
.nav-dropdown {
  position: relative;
}

.dropdown-trigger {
  gap: 4px;
}

.dropdown-arrow {
  width: 16px;
  height: 16px;
  transition: transform 0.2s ease;
}

.nav-dropdown:hover .dropdown-arrow {
  transform: rotate(180deg);
}

.dropdown-menu-large {
  position: absolute;
  top: 100%;
  left: 50%;
  transform: translateX(-50%);
  margin-top: 8px;
  background-color: #161b22;
  border: 1px solid #30363d;
  border-radius: 12px;
  min-width: 280px;
  padding: 8px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.5);
  z-index: 1001;
  animation: dropdownFadeIn 0.2s ease;
}

@keyframes dropdownFadeIn {
  from {
    opacity: 0;
    transform: translateX(-50%) translateY(-10px);
  }
  to {
    opacity: 1;
    transform: translateX(-50%) translateY(0);
  }
}

.dropdown-category {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 14px 16px;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.dropdown-category:hover {
  background-color: #21262d;
}

.category-icon {
  font-size: 28px;
}

.category-info h4 {
  color: #ffffff;
  font-size: 15px;
  font-weight: 600;
  margin-bottom: 4px;
}

.category-info p {
  color: #8b949e;
  font-size: 12px;
}

/* 右侧区域 */
.navbar-right {
  display: flex;
  align-items: center;
  gap: 16px;
  flex-shrink: 0;
}

/* 搜索框?- 加长 */
.navbar-search {
  flex-shrink: 0;
}

.search-box {
  display: flex;
  align-items: center;
  background-color: #21262d;
  border: 1px solid #30363d;
  border-radius: 6px;
  padding: 0 12px;
  width: 280px;
  transition: all 0.2s ease;
}

.search-box:focus-within {
  background-color: #0d1117;
  border-color: #58a6ff;
}

.search-icon {
  width: 16px;
  height: 16px;
  color: #8b949e;
  flex-shrink: 0;
}

.search-input {
  width: 100%;
  height: 32px;
  background: transparent;
  border: none;
  color: #c9d1d9;
  font-size: 13px;
  outline: none;
  padding-left: 8px;
}

.search-input::placeholder {
  color: #6e7681;
}

/* 用户区域 */
.navbar-user {
  display: flex;
  align-items: center;
  gap: 8px;
}

.user-link {
  color: #c9d1d9;
  text-decoration: none;
  font-size: 14px;
  padding: 8px 16px;
  border-radius: 6px;
  transition: all 0.2s ease;
  background-color: #21262d;
  border: 1px solid #30363d;
}

.user-link:hover {
  background-color: #30363d;
  border-color: #8b949e;
  color: #ffffff;
}

.signup-btn {
  background-color: #238636;
  border-color: #238636;
  color: #ffffff !important;
}

.signup-btn:hover {
  background-color: #2ea043;
  border-color: #2ea043;
}

/* 用户头像 */
.user-dropdown {
  position: relative;
  cursor: pointer;
}

.user-avatar {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  object-fit: cover;
  border: 2px solid #4A90E2;
  transition: all 0.2s ease;
}

.user-avatar:hover {
  border-color: #58a6ff;
  transform: scale(1.05);
}

/* 用户图标 - 设置和收藏?*/
.user-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 32px;
  height: 32px;
  color: #c9d1d9;
  cursor: pointer;
  transition: color 0.2s ease;
}

.user-icon:hover {
  color: #ffffff;
}

.user-icon svg {
  width: 18px;
  height: 18px;
}

.dropdown-menu {
  position: absolute;
  top: 48px;
  right: 0;
  background-color: #161b22;
  border: 1px solid #30363d;
  border-radius: 6px;
  min-width: 180px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.4);
  overflow: hidden;
}

.dropdown-header {
  padding: 12px 16px;
  border-bottom: 1px solid #30363d;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.dropdown-username {
  color: #ffffff;
  font-weight: 600;
  font-size: 14px;
}

.admin-badge {
  background-color: #238636;
  color: white;
  padding: 2px 8px;
  border-radius: 12px;
  font-size: 11px;
  width: fit-content;
}

.dropdown-divider {
  height: 1px;
  background-color: #30363d;
}

.dropdown-item {
  display: block;
  padding: 10px 16px;
  color: #c9d1d9;
  text-decoration: none;
  font-size: 14px;
  cursor: pointer;
  transition: background-color 0.2s ease;
}

.dropdown-item:hover {
  background-color: #21262d;
}

.dropdown-item.logout {
  color: #f85149;
}

.dropdown-item.logout:hover {
  background-color: rgba(248, 81, 73, 0.1);
}

/* 主体内容 */
.main-content {
  flex: 1;
  margin-top: 64px;
  min-height: calc(100vh - 64px);
}

/* 登录/注册弹窗样式 */
.auth-modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.7);
  backdrop-filter: blur(4px);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 2000;
}

.auth-modal {
  background: #1a1a25;
  border-radius: 20px;
  width: 420px;
  max-width: 90vw;
  padding: 40px;
  position: relative;
  border: 1px solid #2a2a3a;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.5);
}

.modal-close {
  position: absolute;
  top: 16px;
  right: 16px;
  background: none;
  border: none;
  color: #888899;
  cursor: pointer;
  padding: 8px;
  border-radius: 50%;
  transition: all 0.2s;
}

.modal-close:hover {
  background: #2a2a3a;
  color: #ffffff;
}

.modal-close svg {
  width: 20px;
  height: 20px;
}

.auth-tabs {
  display: flex;
  margin-bottom: 32px;
  border-bottom: 1px solid #2a2a3a;
}

.auth-tab {
  flex: 1;
  background: none;
  border: none;
  color: #888899;
  font-size: 18px;
  font-weight: 500;
  padding: 12px;
  cursor: pointer;
  position: relative;
  transition: color 0.2s;
}

.auth-tab:hover {
  color: #ffffff;
}

.auth-tab.active {
  color: #7e3af2;
}

.auth-tab.active::after {
  content: '';
  position: absolute;
  bottom: -1px;
  left: 0;
  right: 0;
  height: 2px;
  background: linear-gradient(135deg, #7e3af2 0%, #5a2dd8 100%);
}

.auth-form {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.form-group {
  position: relative;
}

.form-group .form-input {
  width: 100%;
  background: #222233;
  border: 1px solid #2a2a3a;
  border-radius: 10px;
  padding: 14px 16px;
  color: #ffffff;
  font-size: 15px;
  outline: none;
  transition: all 0.2s;
}

.form-group .form-input::placeholder {
  color: #6e7681;
}

.form-group .form-input:focus {
  border-color: #7e3af2;
  box-shadow: 0 0 0 3px rgba(126, 58, 242, 0.1);
}

.form-group .toggle-password {
  position: absolute;
  right: 12px;
  top: 50%;
  transform: translateY(-50%);
  background: none;
  border: none;
  color: #888899;
  font-size: 13px;
  cursor: pointer;
}

.captcha-group {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.captcha-row {
  display: flex;
  gap: 12px;
  align-items: center;
}

.captcha-input {
  flex: 1;
}

.get-captcha-btn {
  background: linear-gradient(135deg, #7e3af2 0%, #5a2dd8 100%);
  color: #ffffff;
  border: none;
  border-radius: 10px;
  padding: 14px 20px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s;
  white-space: nowrap;
}

.get-captcha-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(126, 58, 242, 0.3);
}

.get-captcha-btn:disabled {
  background: #666;
  cursor: not-allowed;
  transform: none;
  box-shadow: none;
}

.refresh-btn.disabled {
  color: #666;
  cursor: not-allowed;
}

.refresh-btn.disabled:hover {
  text-decoration: none;
}

.captcha-display {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-top: 8px;
}

.captcha-label {
  color: #888899;
  font-size: 14px;
}

.captcha-code {
  background: linear-gradient(135deg, #7e3af2 0%, #5a2dd8 100%);
  color: #ffffff;
  padding: 6px 16px;
  border-radius: 6px;
  font-size: 16px;
  font-weight: 600;
  letter-spacing: 4px;
}

.refresh-btn {
  color: #7e3af2;
  font-size: 14px;
  cursor: pointer;
  margin-left: auto;
}

.refresh-btn:hover {
  text-decoration: underline;
}

.auth-button {
  background: linear-gradient(135deg, #7e3af2 0%, #5a2dd8 100%);
  color: #ffffff;
  border: none;
  border-radius: 10px;
  padding: 14px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
  margin-top: 8px;
}

.auth-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(126, 58, 242, 0.3);
}

.auth-tip {
  text-align: center;
  color: #888899;
  font-size: 14px;
  margin-top: 16px;
}

.auth-tip a {
  color: #7e3af2;
  margin-left: 6px;
  text-decoration: none;
}

.auth-tip a:hover {
  text-decoration: underline;
}
</style>

