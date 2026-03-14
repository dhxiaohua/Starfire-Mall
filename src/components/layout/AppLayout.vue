<template>
  <div class="app-layout" :class="{ 'has-particles': showParticles }">
    <!-- 粒子动画背景（可选） -->
    <canvas v-if="showParticles" ref="particleCanvas" class="particle-canvas"></canvas>
    
    <!-- 返回首页按钮（登录/注册/设置页用） -->
    <div v-if="showBackButton" class="back-section">
      <router-link to="/" class="back-home">← 返回首页</router-link>
    </div>

    <!-- 统一布局容器 -->
    <div class="layout-container">
      <!-- 顶部导航栏 - 可通过hideNavbar隐藏 -->
      <Navbar 
        v-if="!hideNavbar"
        :transparent="transparentNavbar"
        :bg-color="navbarBgColor"
        @logout="handleLogout"
      />
      
      <!-- 固定搜索栏 -->
      <SearchBar v-if="showSearchBar" />
      
      <!-- 页面内容 - 添加过渡动画 -->
      <main class="main-content" :class="{ 'has-search': showSearchBar }">
        <transition name="page-fade" mode="out-in">
          <slot></slot>
        </transition>
      </main>
    </div>
  </div>
</template>

<script>
import { ref, onMounted, onUnmounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import Navbar from './AppNavbar.vue'
import SearchBar from './AppSearchBar.vue'

export default {
  name: 'AppLayout',
  components: {
    Navbar,
    SearchBar
  },
  props: {
    // 是否显示粒子动画背景
    showParticles: {
      type: Boolean,
      default: false
    },
    // 是否显示返回首页按钮
    showBackButton: {
      type: Boolean,
      default: false
    },
    // 是否显示搜索栏
    showSearchBar: {
      type: Boolean,
      default: true
    },
    // 是否隐藏导航栏
    hideNavbar: {
      type: Boolean,
      default: false
    },
    // 导航栏是否透明（首页用）
    transparentNavbar: {
      type: Boolean,
      default: false
    },
    // 导航栏背景色
    navbarBgColor: {
      type: String,
      default: 'rgba(191, 219, 254, 0.95)'
    }
  },
  setup(props, { emit }) {
    const router = useRouter()
    const route = useRoute()
    const particleCanvas = ref(null)
    let animationId = null
    let particles = []
    let mouseX = -100
    let mouseY = -100

    // 粒子动画初始化
    const initParticles = () => {
      const canvas = particleCanvas.value
      if (!canvas) return
      
      const ctx = canvas.getContext('2d')
      canvas.width = window.innerWidth
      canvas.height = window.innerHeight
      
      particles = []
      const particleCount = Math.floor((canvas.width * canvas.height) / 15000)
      
      for (let i = 0; i < particleCount; i++) {
        particles.push({
          x: Math.random() * canvas.width,
          y: Math.random() * canvas.height,
          vx: (Math.random() - 0.5) * 0.5,
          vy: (Math.random() - 0.5) * 0.5,
          radius: Math.random() * 2 + 1
        })
      }
    }

    // 粒子动画
    const animateParticles = () => {
      const canvas = particleCanvas.value
      if (!canvas) return
      
      const ctx = canvas.getContext('2d')
      ctx.clearRect(0, 0, canvas.width, canvas.height)
      
      particles.forEach((p, i) => {
        p.x += p.vx
        p.y += p.vy
        
        if (p.x < 0 || p.x > canvas.width) p.vx *= -1
        if (p.y < 0 || p.y > canvas.height) p.vy *= -1
        
        ctx.beginPath()
        ctx.arc(p.x, p.y, p.radius, 0, Math.PI * 2)
        ctx.fillStyle = 'rgba(102, 126, 234, 0.6)'
        ctx.fill()
        
        // 粒子与鼠标连线
        const dx = p.x - mouseX
        const dy = p.y - mouseY
        const distance = Math.sqrt(dx * dx + dy * dy)
        
        if (distance < 150) {
          const opacity = 1 - distance / 150
          ctx.beginPath()
          ctx.moveTo(p.x, p.y)
          ctx.lineTo(mouseX, mouseY)
          ctx.strokeStyle = `rgba(102, 126, 234, ${opacity * 0.5})`
          ctx.lineWidth = 1
          ctx.stroke()
        }
        
        // 粒子之间连线
        particles.slice(i + 1).forEach(p2 => {
          const dx = p.x - p2.x
          const dy = p.y - p2.y
          const dist = Math.sqrt(dx * dx + dy * dy)
          
          if (dist < 120) {
            ctx.beginPath()
            ctx.moveTo(p.x, p.y)
            ctx.lineTo(p2.x, p2.y)
            ctx.strokeStyle = `rgba(102, 126, 234, ${0.3 * (1 - dist / 120)})`
            ctx.stroke()
          }
        })
      })
      
      animationId = requestAnimationFrame(animateParticles)
    }

    const handleMouseMove = (e) => {
      mouseX = e.clientX
      mouseY = e.clientY
    }

    const handleResize = () => {
      if (props.showParticles) {
        initParticles()
      }
    }

    const handleLogout = () => {
      emit('logout')
    }

    onMounted(() => {
      if (props.showParticles) {
        initParticles()
        animateParticles()
        window.addEventListener('mousemove', handleMouseMove)
        window.addEventListener('resize', handleResize)
      }
    })

    onUnmounted(() => {
      if (animationId) {
        cancelAnimationFrame(animationId)
      }
      window.removeEventListener('mousemove', handleMouseMove)
      window.removeEventListener('resize', handleResize)
    })

    return {
      particleCanvas,
      handleLogout
    }
  }
}
</script>

<style scoped>
.app-layout {
  min-height: 100vh;
  position: relative;
}

.particle-canvas {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 1;
}

.back-section {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  padding: 25px 40px;
  z-index: 1000;
}

.back-home {
  color: rgba(102, 126, 234, 0.8);
  text-decoration: none;
  font-size: 16px;
  font-weight: 500;
  transition: color 0.3s;
  background: rgba(255, 255, 255, 0.8);
  padding: 10px 20px;
  border-radius: 25px;
  backdrop-filter: blur(10px);
}

.back-home:hover {
  color: #667eea;
}

.layout-container {
  position: relative;
  z-index: 10;
}

.main-content {
  min-height: 100vh;
  margin-top: 70px;
}

.main-content.has-search {
  margin-top: 140px;
}

/* 页面切换过渡动画 */
.page-fade-enter-active,
.page-fade-leave-active {
  transition: opacity 0.3s ease, transform 0.3s ease;
}

.page-fade-enter-from {
  opacity: 0;
  transform: translateX(20px);
}

.page-fade-leave-to {
  opacity: 0;
  transform: translateX(-20px);
}
</style>
