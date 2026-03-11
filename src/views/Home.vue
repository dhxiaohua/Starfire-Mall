<template>
  <WebsiteLayout current-page="home">
    <!-- 未登录：显示星空背景欢迎页 -->
    <template v-if="!userStore.isLoggedIn">
      <!-- 粒子星空背景 -->
      <div class="star-background">
        <canvas ref="canvas" class="star-canvas"></canvas>
      </div>
      
      <!-- 欢迎区域 -->
      <section class="hero">
        <div class="hero-content">
          <h1 class="hero-title">欢迎来到星火游戏设备</h1>
          <p class="hero-subtitle">专业游戏设备一站式购物平台，为玩家带来极致游戏体验</p>
          
          <div class="hero-search">
            <div class="search-box">
              <svg class="search-icon" viewBox="0 0 20 20" fill="currentColor">
                <path fill-rule="evenodd" d="M8 4a4 4 0 100 8 4 4 0 000-8zM2 8a6 6 0 1110.89 3.476l4.817 4.817a1 1 0 01-1.414 1.414l-4.816-4.816A6 6 0 012 8z" clip-rule="evenodd" />
              </svg>
              <input 
                type="text" 
                v-model="searchText" 
                placeholder="搜索游戏设备..." 
                class="search-input"
              />
              <button class="search-btn">搜索</button>
            </div>
          </div>
        </div>
      </section>

      <!-- 为什么选择我们 -->
      <section class="features" ref="featuresSection">
        <div class="section-content">
          <h2 class="section-title">为什么选择我们</h2>
          <p class="section-desc">专注游戏设备，为玩家提供极致游戏体验</p>
          
          <div class="features-grid">
            <div class="feature-card" v-for="(feature, index) in features" :key="index">
              <div class="feature-icon">{{ feature.icon }}</div>
              <h3>{{ feature.title }}</h3>
              <p>{{ feature.desc }}</p>
            </div>
          </div>
        </div>
      </section>

      <!-- 销量统计 -->
      <section class="stats">
        <div class="stats-container">
          <div class="stat-item" v-for="(stat, index) in stats" :key="index">
            <div class="stat-value">{{ stat.value }}</div>
            <div class="stat-label">{{ stat.label }}</div>
          </div>
        </div>
      </section>

      <!-- 最新产品 - 轮播图样式 -->
      <section class="products">
        <div class="section-content">
          <h2 class="section-title">最新产品</h2>
          <p class="section-desc">热门游戏设备，等你来选</p>
          
          <!-- 轮播图容器 -->
          <div class="carousel-container">
            <div class="carousel-wrapper">
              <div class="carousel-track" :style="{ transform: `translateX(-${currentSlide * 100}%)` }">
                <div 
                  class="carousel-slide" 
                  v-for="(product, index) in products" 
                  :key="index"
                  @click="goToCategory(product.category)"
                  style="cursor: pointer;"
                >
                  <div class="product-showcase">
                    <div class="showcase-image">
                      <img :src="product.image" :alt="product.name" class="showcase-product-img" />
                    </div>
                    <div class="showcase-info">
                      <h3>{{ product.name }}</h3>
                      <p class="showcase-desc">{{ product.desc }}</p>
                      <div class="showcase-features">
                        <span v-for="(feat, i) in product.features" :key="i" class="feature-tag">
                          {{ feat }}
                        </span>
                      </div>
                      <button class="detail-btn" @click.stop="goToCategory(product.category)">查看详情</button>
                    </div>
                  </div>
                </div>
              </div>
            </div>
            
            <!-- 轮播指示器 -->
            <div class="carousel-indicators">
              <span 
                v-for="(product, index) in products" 
                :key="index"
                class="indicator"
                :class="{ active: currentSlide === index }"
                @click="currentSlide = index"
              ></span>
            </div>
          </div>
        </div>
      </section>

      <!-- 底部联系区域 -->
      <footer class="footer-section">
        <div class="footer-content">
          <div class="footer-main">
            <div class="footer-brand">
              <div class="footer-logo">
                <img src="/images/logo.jpg" alt="Logo" />
              </div>
              <p class="footer-desc">星火游戏设备 - 专业游戏设备一站式购物平台</p>
            </div>
            <div class="footer-links">
              <div class="link-group">
                <h4>联系我们</h4>
                <p>客服热线：400-888-8888</p>
                <p>邮箱：support@xinghuo.com</p>
                <p>地址：上海市浦东新区游戏路88号</p>
              </div>
              <div class="link-group">
                <h4>关注我们</h4>
                <div class="social-icons">
                  <span class="social-icon">📱</span>
                  <span class="social-icon">💬</span>
                  <span class="social-icon">📧</span>
                </div>
              </div>
            </div>
          </div>
          <div class="footer-bottom">
            <p>© 2026 星火游戏设备. All rights reserved.</p>
          </div>
        </div>
      </footer>
    </template>

    <!-- 已登录：显示产品宣传页面 -->
    <template v-else>
      <div class="promo-page">
        <!-- 品牌故事区域 -->
        <section class="brand-story">
          <div class="story-content">
            <h2 class="story-title">星火起源</h2>
            <p class="story-text">
              2024年，一群热爱游戏的工程师在一次电竞赛事中相识。他们发现市面上的游戏设备要么价格高昂，要么品质堪忧。于是决定打造属于自己的游戏设备品牌——星火。<br><br>
              "星火"取自"星星之火，可以燎原"之意，寓意着每一个普通玩家都有成为电竞明星的潜力。团队成员来自全球顶级外设厂商，拥有超过20年的研发经验。<br><br>
              我们相信，优质的装备是通往胜利的第一步。星火致力于为每一位玩家提供专业级的游戏设备，让游戏体验更加极致。
            </p>
          </div>
        </section>

        <!-- 产品展示区域 -->
        <section class="products-showcase">
          <h2 class="showcase-title">明星产品</h2>
          
          <!-- 电竞鼠标系列 -->
          <div class="product-line">
            <div class="line-header">
              <h3>电竞鼠标系列</h3>
              <p>精准定位每一次操作</p>
            </div>
            <div class="product-posters">
              <div class="poster-card" v-for="(item, index) in mouseProducts" :key="index">
                <div class="poster-image">
                  <img :src="item.image" :alt="item.name" />
                </div>
                <div class="poster-info">
                  <h4>{{ item.name }}</h4>
                  <p>{{ item.feature }}</p>
                </div>
              </div>
            </div>
          </div>

          <!-- 机械键盘系列 -->
          <div class="product-line">
            <div class="line-header">
              <h3>机械键盘系列</h3>
              <p>畅快敲击体验</p>
            </div>
            <div class="product-posters">
              <div class="poster-card" v-for="(item, index) in keyboardProducts" :key="index">
                <div class="poster-image">
                  <img :src="item.image" :alt="item.name" />
                </div>
                <div class="poster-info">
                  <h4>{{ item.name }}</h4>
                  <p>{{ item.feature }}</p>
                </div>
              </div>
            </div>
          </div>

          <!-- 游戏耳机系列 -->
          <div class="product-line">
            <div class="line-header">
              <h3>游戏耳机系列</h3>
              <p>听声辨位先发制人</p>
            </div>
            <div class="product-posters">
              <div class="poster-card" v-for="(item, index) in headsetProducts" :key="index">
                <div class="poster-image">
                  <img :src="item.image" :alt="item.name" />
                </div>
                <div class="poster-info">
                  <h4>{{ item.name }}</h4>
                  <p>{{ item.feature }}</p>
                </div>
              </div>
            </div>
          </div>

          <!-- 游戏手柄系列 -->
          <div class="product-line">
            <div class="line-header">
              <h3>游戏手柄系列</h3>
              <p>全新操控体验</p>
            </div>
            <div class="product-posters">
              <div class="poster-card" v-for="(item, index) in controllerProducts" :key="index">
                <div class="poster-image">
                  <img :src="item.image" :alt="item.name" />
                </div>
                <div class="poster-info">
                  <h4>{{ item.name }}</h4>
                  <p>{{ item.feature }}</p>
                </div>
              </div>
            </div>
          </div>
        </section>
      </div>
    </template>
  </WebsiteLayout>
</template>

<script>
import { ref, onMounted, onUnmounted, watch, computed } from 'vue'
import { useRouter } from 'vue-router'
import WebsiteLayout from '../components/WebsiteLayout.vue'
import { userStore } from '../stores/userStore'

export default {
  name: 'Home',
  components: {
    WebsiteLayout
  },
  setup() {
    const router = useRouter()
    const searchText = ref('')
    const canvas = ref(null)
    const featuresSection = ref(null)
    const currentSlide = ref(0)
    let animationId = null
    let particles = []
    let lastScrollY = 0

    // 产品宣传数据
    const mouseProducts = [
      { name: '星火RGB电竞鼠标', image: '/images/mouse/1.jpg', feature: '16000DPI | RGB炫彩' },
      { name: '星火无线游戏鼠标', image: '/images/mouse/2.jpg', feature: '72小时超长续航' },
      { name: '星火轻量化鼠标', image: '/images/mouse/3.jpg', feature: '58克超轻设计' },
    ]

    const keyboardProducts = [
      { name: '星火青轴机械键盘', image: '/images/keyboard/1.jpg', feature: '经典青轴 | RGB背光' },
      { name: '星火红轴机械键盘', image: '/images/keyboard/2.jpg', feature: '线性手感 | 热插拔' },
      { name: '星火87键键盘', image: '/images/keyboard/3.jpg', feature: '紧凑布局 | 节省空间' },
    ]

    const headsetProducts = [
      { name: '星火7.1游戏耳机', image: '/images/headset/1.jpg', feature: '虚拟7.1环绕声' },
      { name: '星火无线游戏耳机', image: '/images/headset/2.jpg', feature: '2.4G低延迟' },
      { name: '星火降噪游戏耳机', image: '/images/headset/3.jpg', feature: '主动降噪技术' },
    ]

    const controllerProducts = [
      { name: '星火无线游戏手柄', image: '/images/controller/1.jpg', feature: '多平台兼容' },
      { name: '星火有线游戏手柄', image: '/images/controller/2.jpg', feature: '0延迟竞技' },
      { name: '星火精英游戏手柄', image: '/images/controller/3.jpg', feature: '4背键自定义' },
    ]

    // 登录后页面数据
    const loggedInCategory = ref('all')
    const selectedProductLogged = ref(null)
    const showCartLogged = ref(false)
    const cartItemsLogged = ref([])

    // 登录后产品数据
    const loggedInProducts = [
      { id: 1, category: 'mouse', name: '星火RGB电竞鼠标', description: '16000DPI光学传感器，RGB灯效', price: 299, image: '/images/mouse/1.jpg', features: ['16000 DPI', 'RGB灯效', '轻量化'] },
      { id: 2, category: 'mouse', name: '星火无线游戏鼠标', description: '低延迟无线连接，超长续航', price: 399, image: '/images/mouse/2.jpg', features: ['无线连接', '72小时续航'] },
      { id: 3, category: 'mouse', name: '星火轻量化鼠标', description: '58克超轻设计，FPS神器', price: 349, image: '/images/mouse/3.jpg', features: ['58克', 'PMW3389'] },
      { id: 4, category: 'keyboard', name: '星火青轴机械键盘', description: '经典青轴，段落感强', price: 399, image: '/images/keyboard/1.jpg', features: ['青轴', 'RGB背光'] },
      { id: 5, category: 'keyboard', name: '星火红轴机械键盘', description: '线性手感，快速触发', price: 449, image: '/images/keyboard/2.jpg', features: ['红轴', '热插拔'] },
      { id: 6, category: 'keyboard', name: '星火87键机械键盘', description: '紧凑布局，节省桌面空间', price: 349, image: '/images/keyboard/4.jpg', features: ['87键', '紧凑'] },
      { id: 7, category: 'headset', name: '星火7.1游戏耳机', description: '虚拟7.1环绕声，听声辨位', price: 299, image: '/images/headset/1.jpg', features: ['7.1声道', '降噪麦'] },
      { id: 8, category: 'headset', name: '星火无线游戏耳机', description: '低延迟无线，震撼低音', price: 499, image: '/images/headset/2.jpg', features: ['无线', '2.4G'] },
      { id: 9, category: 'controller', name: '星火无线游戏手柄', description: '多平台兼容，震动反馈', price: 299, image: '/images/controller/1.jpg', features: ['无线', '双震动'] },
      { id: 10, category: 'controller', name: '星火有线游戏手柄', description: '0延迟，竞技首选', price: 199, image: '/images/controller/2.jpg', features: ['有线', '0延迟'] },
      { id: 11, category: 'controller', name: '星火精英游戏手柄', description: '自定义宏，4背键', price: 499, image: '/images/controller/3.jpg', features: ['4背键', '自定义'] },
      { id: 12, category: 'mouse', name: '星火有线电竞鼠标', description: '1ms响应时间，职业选手选择', price: 199, image: '/images/mouse/5.jpg', features: ['1ms响应', '欧姆龙微动'] },
    ]

    const filteredLoggedProducts = computed(() => {
      if (loggedInCategory.value === 'all') {
        return loggedInProducts
      }
      return loggedInProducts.filter(p => p.category === loggedInCategory.value)
    })

    const cartTotalLogged = computed(() => {
      return cartItemsLogged.value.filter(item => item.selected).reduce((sum, item) => sum + item.price, 0)
    })

    const getCategoryName = (category) => {
      const names = { mouse: '电竞鼠标', keyboard: '机械键盘', headset: '游戏耳机', controller: '游戏手柄' }
      return names[category] || '热门推荐'
    }

    const openProductDetail = (product) => {
      selectedProductLogged.value = product
    }

    const addToCartLogged = (product) => {
      const existing = cartItemsLogged.value.find(item => item.id === product.id)
      if (existing) {
        existing.quantity = (existing.quantity || 1) + 1
      } else {
        cartItemsLogged.value.push({ ...product, quantity: 1, selected: true })
      }
    }

    const toggleCartItemLogged = (index) => {
      cartItemsLogged.value[index].selected = !cartItemsLogged.value[index].selected
    }

    const removeFromCartLogged = (index) => {
      cartItemsLogged.value.splice(index, 1)
    }

    const checkoutLogged = () => {
      const selected = cartItemsLogged.value.filter(item => item.selected)
      if (selected.length === 0) {
        alert('请选择要结算的商品')
        return
      }
      alert('支付成功！感谢您的购买')
      cartItemsLogged.value = cartItemsLogged.value.filter(item => !item.selected)
      showCartLogged.value = false
    }

    const buyNowLogged = (product) => {
      alert('购买成功！感谢您的购买')
      selectedProductLogged.value = null
    }

    // 跳转到产品分类页面
    const goToCategory = (category) => {
      router.push({ path: '/page1', query: { category } })
    }

    const features = [
      { icon: '🎯', title: '正品保障', desc: '官方授权，正品保证' },
      { icon: '🚚', title: '极速发货', desc: '48小时内快速发货' },
      { icon: '🛡️', title: '品质保证', desc: '一年质保，只换不修' },
      { icon: '💬', title: '专业客服', desc: '7×24小时在线服务' },
      { icon: '💰', title: '价格优惠', desc: '官方授权，价格最优' },
      { icon: '🔄', title: '退换无忧', desc: '7天无理由退换货' }
    ]

    const stats = [
      { value: '50,000+', label: '累计用户' },
      { value: '10,000+', label: '商品种类' },
      { value: '98%', label: '好评率' },
      { value: '24h', label: '平均发货时间' }
    ]

    const products = [
      {
        image: '/images/mouse/1.jpg',
        category: 'mouse',
        name: '星火专业电竞鼠标',
        desc: '采用顶级光学传感器，精准定位每一次操作',
        features: ['16000 DPI', 'RGB灯效', '轻量化设计']
      },
      {
        image: '/images/keyboard/1.jpg',
        category: 'keyboard',
        name: '星火机械键盘',
        desc: '青轴/红轴可选，畅快敲击体验',
        features: ['全键无冲', 'RGB背光', 'PBT键帽']
      },
      {
        image: '/images/headset/1.jpg',
        category: 'headset',
        name: '星火游戏耳机',
        desc: '7.1环绕立体声，听声辨位先发制人',
        features: ['虚拟7.1', '降噪麦克风', '舒适佩戴']
      },
      {
        image: '/images/controller/1.jpg',
        category: 'controller',
        name: '星火游戏手柄',
        desc: '兼容多平台，全新操控体验',
        features: ['双震动', '自定义宏', '有线无线']
      }
    ]

    // 星空背景初始化
    const initParticles = () => {
      const cvs = canvas.value
      if (!cvs) return
      
      cvs.width = window.innerWidth
      cvs.height = window.innerHeight
      
      const particleCount = Math.floor((cvs.width * cvs.height) / 8000)
      particles = []
      
      for (let i = 0; i < particleCount; i++) {
        particles.push({
          x: Math.random() * cvs.width,
          y: Math.random() * cvs.height,
          radius: Math.random() * 1.5 + 0.5,
          alpha: Math.random() * 0.8 + 0.2,
          speed: Math.random() * 0.3 + 0.1,
          direction: Math.random() > 0.5 ? 1 : -1
        })
      }
    }

    const animate = () => {
      const cvs = canvas.value
      if (!cvs) return
      
      const ctx = cvs.getContext('2d')
      ctx.clearRect(0, 0, cvs.width, cvs.height)
      
      // 绘制星空背景渐变
      const gradient = ctx.createRadialGradient(
        cvs.width / 2, cvs.height / 2, 0,
        cvs.width / 2, cvs.height / 2, cvs.width
      )
      gradient.addColorStop(0, '#0a0a1a')
      gradient.addColorStop(0.5, '#0d1117')
      gradient.addColorStop(1, '#161b22')
      ctx.fillStyle = gradient
      ctx.fillRect(0, 0, cvs.width, cvs.height)
      
      // 绘制星星
      particles.forEach(p => {
        p.y += p.speed * p.direction
        if (p.y < 0) p.y = cvs.height
        if (p.y > cvs.height) p.y = 0
        
        p.alpha += Math.random() * 0.02 - 0.01
        if (p.alpha > 1) p.alpha = 1
        if (p.alpha < 0.2) p.alpha = 0.2
        
        ctx.beginPath()
        ctx.arc(p.x, p.y, p.radius, 0, Math.PI * 2)
        ctx.fillStyle = `rgba(255, 255, 255, ${p.alpha})`
        ctx.fill()
        
        if (p.radius > 1) {
          ctx.beginPath()
          ctx.arc(p.x, p.y, p.radius * 2, 0, Math.PI * 2)
          const glowGradient = ctx.createRadialGradient(p.x, p.y, 0, p.x, p.y, p.radius * 2)
          glowGradient.addColorStop(0, `rgba(100, 150, 255, ${p.alpha * 0.3})`)
          glowGradient.addColorStop(1, 'transparent')
          ctx.fillStyle = glowGradient
          ctx.fill()
        }
      })
      
      animationId = requestAnimationFrame(animate)
    }

    const handleResize = () => {
      initParticles()
    }

    // 自动播放轮播图
    let carouselInterval = null
    
    const startCarousel = () => {
      carouselInterval = setInterval(() => {
        currentSlide.value = (currentSlide.value + 1) % products.length
      }, 3000)
    }

    const stopCarousel = () => {
      if (carouselInterval) {
        clearInterval(carouselInterval)
        carouselInterval = null
      }
    }

    // 滚动动画处理
    const handleScroll = () => {
      const scrollY = window.scrollY
      const hero = document.querySelector('.hero')
      
      if (hero && !userStore.isLoggedIn) {
        const heroHeight = hero.offsetHeight
        
        if (scrollY > heroHeight * 0.3) {
          const opacity = 1 - (scrollY - heroHeight * 0.3) / (heroHeight * 0.7)
          hero.style.opacity = Math.max(0, opacity)
          hero.style.transform = `translateY(${(scrollY - heroHeight * 0.3) * 0.3}px)`
        } else {
          hero.style.opacity = 1
          hero.style.transform = 'translateY(0)'
        }
        
        if (scrollY > heroHeight * 0.5) {
          featuresSection.value?.classList.add('visible')
        }
      }
      
      lastScrollY = scrollY
    }

    // 初始化星空背景
    const initStarBackground = () => {
      if (!userStore.isLoggedIn) {
        initParticles()
        animate()
      }
    }

    // 监听登录状态变化
    watch(() => userStore.isLoggedIn, (newVal) => {
      if (!newVal) {
        // 退出登录后重新初始化星空背景
        setTimeout(() => {
          initStarBackground()
        }, 100)
      }
    })

    onMounted(() => {
      initStarBackground()
      startCarousel()
      window.addEventListener('resize', handleResize)
      window.addEventListener('scroll', handleScroll)
    })

    onUnmounted(() => {
      if (animationId) {
        cancelAnimationFrame(animationId)
      }
      stopCarousel()
      window.removeEventListener('resize', handleResize)
      window.removeEventListener('scroll', handleScroll)
    })

    return {
      searchText,
      canvas,
      featuresSection,
      features,
      stats,
      products,
      currentSlide,
      goToCategory,
      userStore,
      loggedInCategory,
      filteredLoggedProducts,
      selectedProductLogged,
      showCartLogged,
      cartItemsLogged,
      cartTotalLogged,
      getCategoryName,
      openProductDetail,
      addToCartLogged,
      toggleCartItemLogged,
      removeFromCartLogged,
      checkoutLogged,
      buyNowLogged,
      mouseProducts,
      keyboardProducts,
      headsetProducts,
      controllerProducts
    }
  }
}
</script>

<style scoped>
/* 星空背景 */
.star-background {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 0;
}

.star-canvas {
  width: 100%;
  height: 100%;
}

/* 欢迎区域 */
.hero {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  padding: 60px 24px;
  transition: opacity 0.3s ease, transform 0.3s ease;
  z-index: 1;
}

.hero-content {
  text-align: center;
  max-width: 800px;
}

.hero-title {
  font-size: 64px;
  font-weight: 700;
  color: #ffffff;
  margin-bottom: 24px;
  text-shadow: 0 0 40px rgba(100, 150, 255, 0.5);
  animation: titleGlow 3s ease-in-out infinite;
}

@keyframes titleGlow {
  0%, 100% { text-shadow: 0 0 40px rgba(100, 150, 255, 0.5); }
  50% { text-shadow: 0 0 60px rgba(100, 150, 255, 0.8), 0 0 100px rgba(100, 150, 255, 0.4); }
}

.hero-subtitle {
  font-size: 22px;
  color: #8b949e;
  margin-bottom: 48px;
  line-height: 1.6;
}

.hero-search {
  display: flex;
  justify-content: center;
}

.search-box {
  display: flex;
  align-items: center;
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: 50px;
  padding: 8px 8px 8px 20px;
  width: 100%;
  max-width: 560px;
  transition: all 0.3s ease;
}

.search-box:focus-within {
  background: rgba(255, 255, 255, 0.15);
  border-color: rgba(100, 150, 255, 0.5);
  box-shadow: 0 0 30px rgba(100, 150, 255, 0.2);
}

.search-icon {
  width: 20px;
  height: 20px;
  color: #8b949e;
  flex-shrink: 0;
}

.search-input {
  flex: 1;
  height: 44px;
  background: transparent;
  border: none;
  color: #ffffff;
  font-size: 16px;
  outline: none;
  padding: 0 16px;
}

.search-input::placeholder {
  color: #8b949e;
}

.search-btn {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  padding: 12px 32px;
  border-radius: 25px;
  font-size: 16px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
}

.search-btn:hover {
  transform: scale(1.05);
  box-shadow: 0 5px 20px rgba(102, 126, 234, 0.4);
}

/* 为什么选择我们 */
.features {
  padding: 100px 24px;
  background: linear-gradient(180deg, transparent 0%, rgba(13, 17, 23, 0.95) 30%);
  position: relative;
  z-index: 1;
  opacity: 0;
  transform: translateY(50px);
  transition: all 0.6s ease;
}

.features.visible {
  opacity: 1;
  transform: translateY(0);
}

.section-content {
  max-width: 1200px;
  margin: 0 auto;
}

.section-title {
  font-size: 42px;
  font-weight: 600;
  color: #ffffff;
  text-align: center;
  margin-bottom: 16px;
}

.section-desc {
  font-size: 18px;
  color: #8b949e;
  text-align: center;
  margin-bottom: 60px;
}

.features-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 30px;
}

.feature-card {
  background: rgba(22, 27, 34, 0.8);
  border: 1px solid #30363d;
  border-radius: 12px;
  padding: 40px 30px;
  text-align: center;
  transition: all 0.3s ease;
}

.feature-card:hover {
  transform: translateY(-8px);
  border-color: #667eea;
  box-shadow: 0 20px 40px rgba(102, 126, 234, 0.2);
}

.feature-icon {
  font-size: 48px;
  margin-bottom: 20px;
}

.feature-card h3 {
  font-size: 20px;
  font-weight: 600;
  color: #ffffff;
  margin-bottom: 12px;
}

.feature-card p {
  font-size: 14px;
  color: #8b949e;
  line-height: 1.6;
}

/* 销量统计 */
.stats {
  padding: 80px 24px;
  background: #0d1117;
  border-top: 1px solid #30363d;
  border-bottom: 1px solid #30363d;
  position: relative;
  z-index: 1;
}

.stats-container {
  max-width: 1000px;
  margin: 0 auto;
  display: flex;
  justify-content: space-around;
}

.stat-item {
  text-align: center;
}

.stat-value {
  font-size: 48px;
  font-weight: 700;
  color: #667eea;
  margin-bottom: 8px;
}

.stat-label {
  font-size: 16px;
  color: #8b949e;
}

/* 最新产品 - 轮播图样式 */
.products, .user-section {
  padding: 100px 24px;
  background: #0d1117;
  position: relative;
  z-index: 1;
}

.carousel-container {
  position: relative;
  max-width: 900px;
  margin: 0 auto;
  overflow: hidden;
}

.carousel-wrapper {
  overflow: hidden;
}

.carousel-track {
  display: flex;
  transition: transform 0.5s ease-in-out;
}

.carousel-slide {
  min-width: 100%;
  padding: 0 10px;
}

/* 产品展示卡片 - 1:1比例 */
.product-showcase {
  display: flex;
  background: #161b22;
  border: 1px solid #30363d;
  border-radius: 20px;
  overflow: hidden;
  transition: all 0.4s ease;
  cursor: pointer;
}

.product-showcase:hover {
  transform: scale(1.02);
  border-color: #667eea;
  box-shadow: 0 0 40px rgba(102, 126, 234, 0.4), 0 0 80px rgba(102, 126, 234, 0.2);
}

.showcase-image {
  width: 50%;
  aspect-ratio: 1;
  background: linear-gradient(135deg, #21262d 0%, #161b22 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  overflow: hidden;
}

.showcase-product-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: all 0.4s ease;
}

.product-showcase:hover .showcase-product-img {
  transform: scale(1.05);
}

.showcase-info {
  width: 50%;
  padding: 50px 40px;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.showcase-info h3 {
  font-size: 28px;
  font-weight: 600;
  color: #ffffff;
  margin-bottom: 16px;
}

.showcase-desc {
  font-size: 16px;
  color: #8b949e;
  line-height: 1.6;
  margin-bottom: 24px;
}

.showcase-features {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-bottom: 30px;
}

.feature-tag {
  background: rgba(102, 126, 234, 0.15);
  color: #667eea;
  padding: 8px 16px;
  border-radius: 20px;
  font-size: 14px;
  border: 1px solid rgba(102, 126, 234, 0.3);
}

.detail-btn {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  padding: 14px 36px;
  border-radius: 25px;
  font-size: 16px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
  align-self: flex-start;
}

.detail-btn:hover {
  transform: scale(1.05);
  box-shadow: 0 5px 20px rgba(102, 126, 234, 0.4);
}

/* 轮播指示器 */
.carousel-indicators {
  display: flex;
  justify-content: center;
  gap: 12px;
  margin-top: 30px;
}

.indicator {
  width: 12px;
  height: 12px;
  border-radius: 50%;
  background: #30363d;
  cursor: pointer;
  transition: all 0.3s ease;
}

.indicator.active {
  background: #667eea;
  box-shadow: 0 0 10px rgba(102, 126, 234, 0.5);
}

.indicator:hover {
  background: #8b949e;
}

/* 轮播按钮 */
.carousel-btn {
  position: absolute;
  top: 50%;
  transform: translateY(-50%);
  width: 50px;
  height: 50px;
  border-radius: 50%;
  background: rgba(22, 27, 34, 0.9);
  border: 1px solid #30363d;
  color: #ffffff;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;
  z-index: 10;
}

.carousel-btn:hover {
  background: #667eea;
  border-color: #667eea;
}

.carousel-btn svg {
  width: 24px;
  height: 24px;
}

.carousel-btn.prev {
  left: -20px;
}

.carousel-btn.next {
  right: -20px;
}

/* 底部联系区域 */
.footer-section {
  background: #000000;
  padding: 60px 24px 24px;
  position: relative;
  z-index: 1;
}

.footer-content {
  max-width: 1200px;
  margin: 0 auto;
}

.footer-main {
  display: flex;
  justify-content: space-between;
  margin-bottom: 40px;
}

.footer-brand {
  max-width: 300px;
}

.footer-logo {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  overflow: hidden;
  border: 2px solid #30363d;
  margin-bottom: 16px;
}

.footer-logo img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.footer-desc {
  color: #8b949e;
  font-size: 14px;
  line-height: 1.6;
}

.footer-links {
  display: flex;
  gap: 80px;
}

.link-group h4 {
  color: #ffffff;
  font-size: 16px;
  margin-bottom: 16px;
}

.link-group p {
  color: #8b949e;
  font-size: 14px;
  margin-bottom: 8px;
}

.social-icons {
  display: flex;
  gap: 12px;
}

.social-icon {
  font-size: 24px;
  cursor: pointer;
  transition: transform 0.2s ease;
}

.social-icon:hover {
  transform: scale(1.2);
}

.footer-bottom {
  padding-top: 24px;
  border-top: 1px solid #30363d;
  text-align: center;
  color: #8b949e;
  font-size: 14px;
}

/* 登录后页面样式 - 产品宣传页面 */
.promo-page {
  min-height: calc(100vh - 60px);
  background: linear-gradient(180deg, #0a0a12 0%, #0d0d1a 50%, #121220 100%);
  padding-bottom: 60px;
}

/* 品牌故事区域 */
.brand-story {
  padding: 80px 40px;
  background: linear-gradient(180deg, #0a0a12 0%, #12121a 100%);
  position: relative;
  overflow: hidden;
}

.brand-story::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: 
    radial-gradient(ellipse at 30% 30%, rgba(126, 58, 242, 0.1) 0%, transparent 50%),
    radial-gradient(ellipse at 70% 70%, rgba(255, 107, 107, 0.08) 0%, transparent 50%);
  pointer-events: none;
}

.story-content {
  max-width: 900px;
  margin: 0 auto;
  position: relative;
  z-index: 1;
}

.story-title {
  font-size: 42px;
  font-weight: 700;
  color: #ffffff;
  text-align: center;
  margin-bottom: 32px;
  background: linear-gradient(135deg, #ffffff 0%, #c0c0d0 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.story-text {
  font-size: 17px;
  color: #b0b0c0;
  line-height: 1.9;
  text-align: center;
}

/* 产品展示区域 */
.products-showcase {
  padding: 60px 40px;
  max-width: 1400px;
  margin: 0 auto;
}

.showcase-title {
  font-size: 36px;
  font-weight: 700;
  color: #ffffff;
  text-align: center;
  margin-bottom: 50px;
}

.product-line {
  margin-bottom: 60px;
}

.line-header {
  margin-bottom: 30px;
  padding-left: 20px;
  border-left: 4px solid #7e3af2;
}

.line-header h3 {
  font-size: 28px;
  font-weight: 600;
  color: #ffffff;
  margin-bottom: 8px;
}

.line-header p {
  font-size: 16px;
  color: #888899;
}

.product-posters {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 24px;
}

.poster-card {
  background: #1a1a25;
  border-radius: 16px;
  overflow: hidden;
  border: 1px solid #2a2a3a;
  transition: all 0.3s;
}

.poster-card:hover {
  transform: translateY(-8px);
  border-color: #7e3af2;
  box-shadow: 0 12px 40px rgba(126, 58, 242, 0.2);
}

.poster-image {
  width: 100%;
  aspect-ratio: 4/3;
  overflow: hidden;
}

.poster-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.4s;
}

.poster-card:hover .poster-image img {
  transform: scale(1.05);
}

.poster-info {
  padding: 20px;
}

.poster-info h4 {
  font-size: 18px;
  font-weight: 600;
  color: #ffffff;
  margin-bottom: 8px;
}

.poster-info p {
  font-size: 14px;
  color: #888899;
}
</style>
