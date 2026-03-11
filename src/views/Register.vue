<template>
  <AppLayout :show-particles="true" :show-back-button="true" :show-search-bar="false" :hide-navbar="true">
    <!-- 注册表单 -->
    <div class="register-container">
      <div class="register-box">
        <h2>账号注册</h2>
        
        <div class="form-group">
          <input 
            type="text" 
            v-model="registerForm.username" 
            placeholder="请输入账号（4-16位字母数字）" 
            class="form-input"
            @blur="checkUsernameHandler"
          />
          <span class="error-tip" v-if="usernameError">{{ usernameError }}</span>
          <span class="success-tip" v-if="usernameValid && registerForm.username">✓ 账号可用</span>
        </div>
        
        <div class="form-group password-group">
          <input 
            :type="showPassword ? 'text' : 'password'" 
            v-model="registerForm.password" 
            placeholder="请输入密码（6-20位）" 
            class="form-input"
            autocomplete="new-password"
          />
          <button type="button" class="toggle-password" @click="showPassword = !showPassword">
            {{ showPassword ? '隐藏' : '显示' }}
          </button>
        </div>
        
        <div class="form-group password-group">
          <input 
            :type="showConfirmPassword ? 'text' : 'password'" 
            v-model="registerForm.confirmPassword" 
            placeholder="请确认密码" 
            class="form-input"
            autocomplete="new-password"
          />
          <button type="button" class="toggle-password" @click="showConfirmPassword = !showConfirmPassword">
            {{ showConfirmPassword ? '隐藏' : '显示' }}
          </button>
          <span class="error-tip" v-if="passwordError">{{ passwordError }}</span>
        </div>
        
        <div class="form-group captcha-group">
          <div class="captcha-display">
            <span class="captcha-label">验证码：</span>
            <span class="captcha-code">{{ captchaCode }}</span>
            <span class="refresh-btn" @click="fetchCaptcha">刷新</span>
          </div>
          <input 
            type="text" 
            v-model="registerForm.captcha" 
            placeholder="请输入验证码" 
            class="form-input captcha-input"
            maxlength="4"
          />
        </div>
        
        <button @click="handleRegister" class="register-button">注 册</button>
        
        <div class="login-tip">
          <span>已有账号？</span>
          <router-link to="/login">立即登录</router-link>
        </div>
      </div>
    </div>
  </AppLayout>
</template>

<script>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import AppLayout from '../components/layout/AppLayout.vue'
import { register as registerApi, checkUsername as checkUsernameApi, getCaptcha as getCaptchaApi } from '../api'

export default {
  name: 'Register',
  components: {
    AppLayout
  },
  setup() {
    const router = useRouter()
    const usernameError = ref('')
    const usernameValid = ref(false)
    const passwordError = ref('')
    const showPassword = ref(false)
    const showConfirmPassword = ref(false)
    const registerForm = ref({
      username: '',
      password: '',
      confirmPassword: '',
      captcha: ''
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

    // 检查账号是否重复
    const checkUsernameHandler = async () => {
      const username = registerForm.value.username
      
      if (!username) {
        usernameError.value = '请输入账号'
        usernameValid.value = false
        return
      }
      
      const usernameRegex = /^[a-zA-Z0-9]{4,16}$/
      if (!usernameRegex.test(username)) {
        usernameError.value = '账号只能包含字母和数字，4-16位'
        usernameValid.value = false
        return
      }

      const result = await checkUsernameApi(username)
      
      // 后端返回 success: false 表示账号已被注册
      if (!result.success) {
        usernameError.value = result.message || '该账号已被注册'
        usernameValid.value = false
      } else {
        usernameError.value = ''
        usernameValid.value = true
      }
    }

    // 注册处理
    const handleRegister = async () => {
      const { username, password, confirmPassword, captcha } = registerForm.value
      
      if (!username || !password || !confirmPassword) {
        alert('请填写完整信息')
        return
      }
      
      await checkUsernameHandler()
      if (usernameError.value || !usernameValid.value) {
        return
      }
      
      if (password.length < 6 || password.length > 20) {
        alert('密码长度应为6-20位')
        return
      }
      
      if (password !== confirmPassword) {
        passwordError.value = '两次密码输入不一致'
        return
      } else {
        passwordError.value = ''
      }
      
      if (captcha.toUpperCase() !== captchaCode.value) {
        alert('验证码错误')
        fetchCaptcha()
        registerForm.value.captcha = ''
        return
      }

      const result = await registerApi(username, password, captcha, captchaCode.value)
      
      if (result.success) {
        alert('注册成功！请登录')
        router.push('/login')
      } else {
        alert(result.message)
        fetchCaptcha()
        registerForm.value.captcha = ''
      }
    }

    onMounted(() => {
      fetchCaptcha()
    })

    return {
      registerForm,
      captchaCode,
      usernameError,
      usernameValid,
      passwordError,
      showPassword,
      showConfirmPassword,
      checkUsername: checkUsernameHandler,
      handleRegister,
      fetchCaptcha
    }
  }
}
</script>

<style scoped>
.register-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  padding: 100px 20px 40px;
}

.register-box {
  background: rgba(255, 255, 255, 0.7);
  backdrop-filter: blur(20px);
  border-radius: 20px;
  padding: 50px;
  width: 420px;
  box-shadow: 0 20px 60px rgba(102, 126, 234, 0.15);
  border: 1px solid rgba(255, 255, 255, 0.5);
}

.register-box h2 {
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

.error-tip {
  display: block;
  color: #ff6b6b;
  font-size: 12px;
  margin-top: 5px;
}

.success-tip {
  display: block;
  color: #51cf66;
  font-size: 12px;
  margin-top: 5px;
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

.register-button {
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
  margin-top: 10px;
}

.register-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 10px 30px rgba(102, 126, 234, 0.4);
}

.login-tip {
  text-align: center;
  margin-top: 25px;
  color: #666;
}

.login-tip a {
  color: #667eea;
  text-decoration: none;
  margin-left: 5px;
}

.login-tip a:hover {
  text-decoration: underline;
}
</style>
