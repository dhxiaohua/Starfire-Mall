<template>
  <AppLayout :show-particles="true" :show-back-button="true" :show-search-bar="false" :hide-navbar="true">
    <!-- 登录表单 -->
    <div class="login-container">
      <div class="login-box">
        <h2>账号登录</h2>
        
        <div class="form-group">
          <input 
            type="text" 
            v-model="loginForm.username" 
            placeholder="请输入账号" 
            class="form-input"
          />
        </div>
        
        <div class="form-group password-group">
          <input 
            :type="showPassword ? 'text' : 'password'" 
            v-model="loginForm.password" 
            placeholder="请输入密码" 
            class="form-input"
            autocomplete="current-password"
          />
          <button type="button" class="toggle-password" @click="showPassword = !showPassword">
            {{ showPassword ? '隐藏' : '显示' }}
          </button>
        </div>
        
        <div class="form-group captcha-group">
          <div class="captcha-display">
            <span class="captcha-label">验证码：</span>
            <span class="captcha-code">{{ captchaCode }}</span>
            <span class="refresh-btn" @click="fetchCaptcha">刷新</span>
          </div>
          <input 
            type="text" 
            v-model="loginForm.captcha" 
            placeholder="请输入验证码" 
            class="form-input captcha-input"
            maxlength="4"
          />
        </div>
        
        <div class="forgot-password">
          <a href="#" @click.prevent="showForgotPassword = true">忘记密码？</a>
        </div>
        
        <button @click="handleLogin" class="login-button">登 录</button>
        
        <div class="register-tip">
          <span>还没有账号？</span>
          <router-link to="/register">立即注册</router-link>
        </div>
      </div>
    </div>

    <!-- 忘记密码弹窗 -->
    <div class="modal" v-if="showForgotPassword" @click.self="showForgotPassword = false">
      <div class="modal-content">
        <h3>修改密码</h3>
        <div class="form-group">
          <input 
            type="text" 
            v-model="forgotForm.username" 
            placeholder="请输入账号" 
            class="form-input"
          />
        </div>
        <div class="form-group">
          <input 
            type="password" 
            v-model="forgotForm.newPassword" 
            placeholder="请输入新密码" 
            class="form-input"
          />
        </div>
        <div class="form-group">
          <input 
            type="password" 
            v-model="forgotForm.confirmPassword" 
            placeholder="请确认新密码" 
            class="form-input"
          />
        </div>
        <button @click="handleForgotPassword" class="login-button">确认修改</button>
      </div>
    </div>
  </AppLayout>
</template>

<script>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import AppLayout from '../components/layout/AppLayout.vue'
import { login as loginApi, changePassword as changePasswordApi, getCaptcha as getCaptchaApi } from '../api'
import { userStore, setUserLogin } from '../stores/userStore'
import { getUserInfo, getUserAdminStatus } from '../api'

export default {
  name: 'Login',
  components: {
    AppLayout
  },
  setup() {
    const router = useRouter()
    const showForgotPassword = ref(false)
    const showPassword = ref(false)
    const loginForm = ref({
      username: '',
      password: '',
      captcha: ''
    })
    const forgotForm = ref({
      username: '',
      newPassword: '',
      confirmPassword: ''
    })
    const captchaCode = ref('')

    const fetchCaptcha = async () => {
      try {
        const result = await getCaptchaApi()
        captchaCode.value = result.code || Math.random().toString(36).slice(-4).toUpperCase()
      } catch (e) {
        captchaCode.value = Math.random().toString(36).slice(-4).toUpperCase()
      }
    }

    // 登录处理
    const handleLogin = async () => {
      if (!loginForm.value.username || !loginForm.value.password) {
        alert('请输入账号和密码')
        return
      }
      
      if (loginForm.value.captcha.toUpperCase() !== captchaCode.value) {
        alert('验证码错误')
        fetchCaptcha()
        loginForm.value.captcha = ''
        return
      }

      const result = await loginApi(
        loginForm.value.username,
        loginForm.value.password,
        loginForm.value.captcha,
        captchaCode.value
      )
      
      if (result.success) {
        // 获取完整用户信息
        const userInfo = await getUserInfo(result.user.username)
        const userData = userInfo.success ? userInfo.user : result.user
        
        // 使用登录API返回的isAdmin状态
        const isAdmin = result.user.isAdmin || false
        
        // 获取管理员申请状态
        const adminStatusResult = await getUserAdminStatus(result.user.username)
        const adminStatus = adminStatusResult.success ? adminStatusResult.adminStatus : 'none'
        const canApply = adminStatusResult.success ? adminStatusResult.canApply : false
        
        // 合并用户数据
        const finalUserData = {
          ...userData,
          isAdmin: isAdmin,
          adminStatus: adminStatus,
          canApply: canApply,
          isAdminMode: false
        }
        
        // 使用setUserLogin支持多用户登录
        setUserLogin(finalUserData)
        userStore.value.adminStatus = adminStatus
        userStore.value.canApply = canApply
        userStore.value.isAdminMode = false
        
        alert('登录成功！')
        router.push('/home')
      } else {
        alert(result.message)
        fetchCaptcha()
        loginForm.value.captcha = ''
      }
    }

    // 忘记密码处理
    const handleForgotPassword = async () => {
      if (!forgotForm.value.username || !forgotForm.value.newPassword) {
        alert('请填写完整信息')
        return
      }
      
      if (forgotForm.value.newPassword !== forgotForm.value.confirmPassword) {
        alert('两次密码输入不一致')
        return
      }

      const result = await changePasswordApi(
        forgotForm.value.username,
        forgotForm.value.newPassword,
        forgotForm.value.confirmPassword
      )
      
      if (result.success) {
        alert('密码修改成功！')
        showForgotPassword.value = false
        forgotForm.value = { username: '', newPassword: '', confirmPassword: '' }
      } else {
        alert(result.message)
      }
    }

    onMounted(() => {
      fetchCaptcha()
    })

    return {
      loginForm,
      forgotForm,
      captchaCode,
      showForgotPassword,
      showPassword,
      handleLogin,
      handleForgotPassword,
      fetchCaptcha
    }
  }
}
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  padding: 100px 20px 40px;
}

.login-box {
  background: rgba(255, 255, 255, 0.7);
  backdrop-filter: blur(20px);
  border-radius: 20px;
  padding: 50px;
  width: 420px;
  box-shadow: 0 20px 60px rgba(102, 126, 234, 0.15);
  border: 1px solid rgba(255, 255, 255, 0.5);
}

.login-box h2 {
  text-align: center;
  margin-bottom: 40px;
  color: #333;
  font-size: 28px;
}

.form-group {
  margin-bottom: 20px;
}

.password-group {
  position: relative;
}

.toggle-password {
  position: absolute;
  right: 12px;
  top: 50%;
  transform: translateY(-50%);
  background: none;
  border: none;
  color: #667eea;
  cursor: pointer;
  font-size: 13px;
  padding: 4px 8px;
}

.toggle-password:hover {
  text-decoration: underline;
}

.form-input {
  width: 100%;
  padding: 15px 20px;
  border: 2px solid rgba(102, 126, 234, 0.3);
  border-radius: 10px;
  font-size: 15px;
  outline: none;
  transition: border-color 0.3s, box-shadow 0.3s;
  background: rgba(255, 255, 255, 0.5);
}

.form-input:focus {
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

.captcha-group {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.captcha-display {
  display: flex;
  align-items: center;
  gap: 10px;
}

.captcha-label {
  color: #666;
  font-size: 14px;
}

.captcha-code {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 8px 20px;
  border-radius: 8px;
  font-size: 18px;
  font-weight: bold;
  letter-spacing: 4px;
}

.refresh-btn {
  color: #667eea;
  cursor: pointer;
  font-size: 14px;
}

.refresh-btn:hover {
  text-decoration: underline;
}

.captcha-input {
  text-align: center;
  letter-spacing: 8px;
  font-size: 18px !important;
}

.forgot-password {
  text-align: right;
  margin-bottom: 20px;
}

.forgot-password a {
  color: #667eea;
  text-decoration: none;
  font-size: 14px;
}

.forgot-password a:hover {
  text-decoration: underline;
}

.login-button {
  width: 100%;
  padding: 16px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  border-radius: 10px;
  font-size: 18px;
  font-weight: 500;
  cursor: pointer;
  transition: transform 0.3s, box-shadow 0.3s;
}

.login-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 10px 30px rgba(102, 126, 234, 0.4);
}

.register-tip {
  text-align: center;
  margin-top: 25px;
  color: #666;
}

.register-tip a {
  color: #667eea;
  text-decoration: none;
  margin-left: 5px;
}

.register-tip a:hover {
  text-decoration: underline;
}

/* 弹窗样式 */
.modal {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.3);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.modal-content {
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(20px);
  border-radius: 20px;
  padding: 40px;
  width: 380px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.2);
}

.modal-content h3 {
  text-align: center;
  margin-bottom: 30px;
  color: #333;
}

.modal-content .form-group {
  margin-bottom: 15px;
}

.modal-content .login-button {
  margin-top: 10px;
}
</style>
