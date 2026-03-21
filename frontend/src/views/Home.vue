<template>
  <WebsiteLayout current-page="home">
    <!-- 未登录：显示星空背景欢迎页面页面 -->
    <template v-if="!userStore.isLoggedIn">
      <!-- 粒子星空背景 -->
      <div class="star-background">
        <canvas ref="canvas" class="star-canvas"></canvas>
      </div>
      
      <!-- 欢迎页面区域 -->
      <section class="hero">
        <div class="hero-content">
          <h1 class="hero-title">欢迎来到星火设备</h1>
          <p class="hero-subtitle">专业游戏设备一站式购买物平台，为玩家带来极致游戏体验</p>
          
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

      <!-- 销量统计?-->
      <section class="stats">
        <div class="stats-container">
          <div class="stat-item" v-for="(stat, index) in stats" :key="index">
            <div class="stat-value">{{ stat.value }}</div>
            <div class="stat-label">{{ stat.label }}</div>
          </div>
        </div>
      </section>

      <!-- 最新产品?- 轮播图图样式?-->
      <section class="products">
        <div class="section-content">
          <h2 class="section-title">最新产品</h2>
          <p class="section-desc">热门游戏设备，等你来抢</p>
          
          <!-- 轮播图图容器 -->
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
            
            <!-- 轮播图指示器?-->
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
              <p class="footer-desc">星火设备 - 专业游戏设备</p>
            </div>
            <div class="footer-links">
              <div class="link-group">
                <h4>联系我们</h4>
                <p>客服热线：?00-888-8888</p>
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
            <p>© 2026 星火设备. All rights reserved.</p>
          </div>
        </div>
      </footer>
    </template>

    <!-- 已登录：显示产品宣传页面 -->
    <template v-else>
      <div class="promo-page">
        <!-- 品牌故事区域 -->
        <section class="brand-story">
          <!-- 世界地图 Canvas 背景 -->
          <div class="map-container" ref="container">
            <canvas ref="mapCanvas" @click="handleWaveClick" @mousemove="handleMouseMove" @mouseleave="handleMouseLeave"></canvas>
          </div>
          
          <!-- 左下角标题组 -->
          <div class="title-group">
            <h1 class="title-en">STARFIRE MALL</h1>
            <h2 class="title-zh">星火设备，为游戏而生</h2>
          </div>
          
          <!-- 右侧内容区 -->
          <div class="content-right">
            <p class="desc-text">专业的电竞装备<br>极致的游戏体验,助力每一位玩家走向胜利!</p>
            <button class="about-btn" @click="$router.push('/about')">
              了解我们
              <svg class="arrow-icon" viewBox="0 0 20 20" fill="currentColor">
                <path fill-rule="evenodd" d="M10.293 3.293a1 1 0 011.414 0l6 6a1 1 0 010 1.414l-6 6a1 1 0 01-1.414-1.414L14.586 11H3a1 1 0 110-2h11.586l-4.293-4.293a1 1 0 010-1.414z" clip-rule="evenodd" />
              </svg>
            </button>
          </div>
        </section>

        <!-- 占位元素，用于将产品展示区域推到页面底部 -->
        <div class="brand-story-placeholder"></div>

        <!-- 产品展示区域 -->
        <section class="products-showcase" ref="productsShowcaseSection">
          <h2 class="showcase-title">明星产品</h2>
          
          <!-- 电竞鼠标系列 -->
          <div class="product-line">
            <div class="line-header">
              <h3>电竞鼠标系列</h3>
              <p>精准定义位每一次操作</p>
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
              <p>听声辨位先发货制人</p>
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
              <p>全新操作控体验</p>
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
    const productsShowcaseSection = ref(null)
    const mapCanvas = ref(null)
    const container = ref(null)
    const currentSlide = ref(0)
    let animationId = null
    let particles = []
    let mapAnimationId = null
    let lastScrollY = 0

    // 产品宣传数据
    const mouseProducts = [
      { name: '星火商城RGB电竞鼠标', image: '/images/mouse/1.jpg', feature: '16000DPI | RGB炫彩' },
      { name: '星火商城无线游戏鼠标', image: '/images/mouse/2.jpg', feature: '72小时超长续航' },
      { name: '星火商城轻量化化鼠标', image: '/images/mouse/3.jpg', feature: '58克克超轻设计' },
    ]

    const keyboardProducts = [
      { name: '星火商城青轴机械键盘', image: '/images/keyboard/1.jpg', feature: '经典青轴 | RGB背光' },
      { name: '星火商城红轴机械键盘', image: '/images/keyboard/2.jpg', feature: '线性手感?| 热插拔' },
      { name: '星火商城87键键键盘', image: '/images/keyboard/3.jpg', feature: '紧凑布局 | 节省空间间' },
    ]

    const headsetProducts = [
      { name: '星火商城7.1游戏耳机', image: '/images/headset/1.jpg', feature: '虚拟7.1环绕声' },
      { name: '星火商城无线游戏耳机', image: '/images/headset/2.jpg', feature: '2.4G低延迟' },
      { name: '星火商城降噪麦游戏耳机', image: '/images/headset/3.jpg', feature: '主动降噪麦技术'},
    ]

    const controllerProducts = [
      { name: '星火商城无线游戏手柄', image: '/images/controller/1.jpg', feature: '多平台兼容'},
      { name: '星火商城有线游戏手柄', image: '/images/controller/2.jpg', feature: '0延迟竞技' },
      { name: '星火商城精英游戏手柄', image: '/images/controller/3.jpg', feature: '4背键自定义'},
    ]

    // 登录后页面数?    
    const loggedInCategory = ref('all')
    const selectedProductLogged = ref(null)
    const showCartLogged = ref(false)
    const cartItemsLogged = ref([])

    // 登录后产品数?    
    const loggedInProducts = [
      { id: 1, category: 'mouse', name: '星火商城RGB电竞鼠标', description: '16000DPI光学传感器,RGB灯效', price:299, image: '/images/mouse/1.jpg', features: ['16000 DPI', 'RGB灯效', '轻量化'] },
      { id: 2, category: 'mouse', name: '星火商城无线游戏鼠标', description: '低延迟无线连接，超长续航', price: 399, image: '/images/mouse/2.jpg', features: ['无线连接', '72小时续航'] },
      { id: 3, category: 'mouse', name: '星火商城轻量化化鼠标', description: '58克克超轻设计,FPS神器', price: 349, image: '/images/mouse/3.jpg', features: ['58克', 'PMW3389'] },
      { id: 4, category: 'keyboard', name: '星火商城青轴机械键盘', description: '经典青轴，段落感强', price: 399, image: '/images/keyboard/1.jpg', features: ['青轴', 'RGB背光'] },
      { id: 5, category: 'keyboard', name: '星火商城红轴机械键盘', description: '线性手感，快速触发', price: 449, image: '/images/keyboard/2.jpg', features: ['红轴', '热插拔'] },
      { id: 6, category: 'keyboard', name:  '星火商城87键键机械键盘', description: '紧凑布局，节省桌面空间', price: 349, image: '/images/keyboard/4.jpg', features: ['87键', '紧凑'] },
      { id: 7, category: 'headset', name: '星火商城7.1游戏耳机', description: '虚拟7.1环绕声，听声辨位', price: 299, image: '/images/headset/1.jpg', features: ['7.1声道', '降噪麦'] },
      { id: 8, category: 'headset', name: '星火商城无线游戏耳机', description: '低延迟无线，震动撼低音', price: 499, image: '/images/headset/2.jpg', features: ['无线', '2.4G'] },
      { id: 9, category: 'controller', name: '星火商城无线游戏手柄', description: '多平台兼容容，震动动反馈', price: 299, image: '/images/controller/1.jpg', features: ['无线', '双震动'] },
      { id: 10, category: 'controller', name: '星火商城有线游戏手柄', description: '0延迟,竞技首选', price: 199, image: '/images/controller/2.jpg', features: ['有线', '0延迟'] },
      { id: 11, category: 'controller', name: '星火商城精英游戏手柄', description: '自定义宏4背键', price: 499, image: '/images/controller/3.jpg', features: ['4背键', '自定义'] },
      { id: 12, category: 'mouse', name: '星火商城有线电竞鼠标', description: '1ms响应时间,职业选手感选择', price: 199, image: '/images/mouse/5.jpg', features: ['1ms响应', '欧姆龙微动'] },
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

    // 跳转到产品分类页面?    
    const goToCategory = (category) => {
      router.push({ path: '/products', query: { category } })
    }

    const features = [
      { icon: '🎯', title: '正品保证障', desc: '官方授权，正品保证' },
      { icon: '🚚', title: '极速发货', desc: '48小时内快速发货' },
      { icon: '🛡️', title: '品质保证证', desc: '一年质保证,只换不修' },
      { icon: '💬', title: '专业客服', desc: '7x24小时在线服务' },
      { icon: '💰', title: '价格优惠', desc: '官方授权，价格最优'},
      { icon: '🔄', title: '退换无忧', desc: '7天无理由退换货' }
    ]

    const stats = [
      { value: '50,000+', label: '累计用户' },
      { value: '10,000+', label: '商品种类' },
      { value: '98%', label: '好好评率' },
      { value: '24h', label: '平均发货货时间' }
    ]

    const products = [
      {
        image: '/images/mouse/1.jpg',
        category: 'mouse',
        name: '星火商城专业电竞鼠标',
        desc: '采用顶级光学传感器，精准定义位每一次操作',
        features: ['16000 DPI', 'RGB灯效', '轻量化化设计']
      },
      {
        image: '/images/keyboard/1.jpg',
        category: 'keyboard',
        name: '星火商城机械键盘',
        desc: '青轴/红轴可选，畅快敲击体验',
        features: ['全键盘无冲', 'RGB背光', 'PBT键盘帽']
      },
      {
        image: '/images/headset/1.jpg',
        category: 'headset',
        name: '星火商城游戏耳机',
        desc: '7.1环绕声立体声，听声辨位先发货制人',
        features: ['虚拟7.1', '降噪麦麦克风', '舒适佩戴']
      },
      {
        image: '/images/controller/1.jpg',
        category: 'controller',
        name: '星火商城游戏手柄',
        desc: '兼容容多平台，全新操作控体验',
        features: ['双震动', '自定义宏', '有线无线']
      }
    ]

    // 星空背景初始化    
    const initParticles = () => {
      const cvs = canvas.value
      if (!cvs) return
      
      cvs.width = window.innerWidth
      cvs.height = window.innerHeight
      
      // 增加星星数量，密度更大
      const particleCount = Math.floor((cvs.width * cvs.height) / 2500)
      particles = []
      
      // 创建普通星星
      for (let i = 0; i < particleCount; i++) {
        particles.push({
          x: Math.random() * cvs.width,
          y: Math.random() * cvs.height, // 星星分布在整个容器
          radius: Math.random() * 1.8 + 0.8, // 更大的星星
          alpha: Math.random() * 0.7 + 0.3, // 更亮的星星
          baseAlpha: Math.random() * 0.7 + 0.3, // 基础亮度
          twinkleSpeed: Math.random() * 0.05 + 0.02, // 更快的闪烁速度
          twinkleOffset: Math.random() * Math.PI * 2, // 闪烁偏移
          type: 'star'
        })
      }
      
      // 创建北极星（位置固定，稍大一点，常亮，淡黄色）
      particles.push({
        x: cvs.width * 0.7,
        y: cvs.height * 0.15,
        radius: 3.5,
        alpha: 1,
        baseAlpha: 1,
        twinkleSpeed: 0, // 常亮
        twinkleOffset: 0,
        type: 'pole-star'
      })
      
      // 创建北斗七星（7颗星形成勺子形状，在左下角，常亮，淡蓝色）
      // 斗魁（勺头）4星：天枢（右下）、天璇（左下）、天玑（左上）、天权（右上）
      // 斗杓（勺柄）3星：玉衡、开阳、摇光（向左上方弯曲）
      const bigDipper = [
        { x: 0.22, y: 0.10 },   // 天枢 - 斗魁右下角，最靠近北极星
        { x: 0.18, y: 0.18 },   // 天璇 - 斗魁左下角，与天枢并列
        { x: 0.10, y: 0.10 },   // 天玑 - 斗魁左上角
        { x: 0.10, y: -0.02 },   // 天权 - 斗魁右上角，连接勺柄
        { x: 0.06, y: -0.09 },   // 玉衡 - 勺柄第1颗，向左
        { x: 0.03, y: -0.18 },  // 开阳 - 勺柄第2颗，继续向左上
        { x: -0.05, y: -0.20 }   // 摇光 - 勺柄末端，最左上角
      ]
      
      bigDipper.forEach((star, index) => {
        particles.push({
          x: cvs.width * (0.10 + star.x),
          y: cvs.height * (0.70 + star.y),
          radius: 3,
          alpha: 1,
          baseAlpha: 1,
          twinkleSpeed: 0, // 常亮
          twinkleOffset: 0,
          type: 'dipper'
        })
      })
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
      gradient.addColorStop(0, '#050508')
      gradient.addColorStop(0.5, '#08080c')
      gradient.addColorStop(1, '#0a0a10')
      ctx.fillStyle = gradient
      ctx.fillRect(0, 0, cvs.width, cvs.height)
      
      // 绘制星星
      particles.forEach(p => {
        // 星星闪烁，但北极星和北斗七星常亮
        if (p.type === 'star') {
          const time = Date.now() * 0.001
          p.alpha = p.baseAlpha + Math.sin(time * p.twinkleSpeed * 100 + p.twinkleOffset) * 0.4
          p.alpha = Math.max(0.1, Math.min(1, p.alpha))
        }
        
        ctx.beginPath()
        ctx.arc(p.x, p.y, p.radius, 0, Math.PI * 2)
        
        if (p.type === 'pole-star') {
          // 北极星渲染（常亮，淡黄色）
          ctx.fillStyle = `rgba(255, 255, 180, ${p.alpha})`
          ctx.fill()
          
          // 北极星多层光晕
          // 内层光晕
          ctx.beginPath()
          ctx.arc(p.x, p.y, p.radius * 2.5, 0, Math.PI * 2)
          const innerGlow = ctx.createRadialGradient(p.x, p.y, 0, p.x, p.y, p.radius * 2.5)
          innerGlow.addColorStop(0, `rgba(255, 255, 180, ${p.alpha * 0.5})`)
          innerGlow.addColorStop(1, 'transparent')
          ctx.fillStyle = innerGlow
          ctx.fill()
          
          // 外层光晕
          ctx.beginPath()
          ctx.arc(p.x, p.y, p.radius * 4, 0, Math.PI * 2)
          const outerGlow = ctx.createRadialGradient(p.x, p.y, 0, p.x, p.y, p.radius * 4)
          outerGlow.addColorStop(0, `rgba(255, 255, 150, ${p.alpha * 0.3})`)
          outerGlow.addColorStop(1, 'transparent')
          ctx.fillStyle = outerGlow
          ctx.fill()
          
          // 十字光芒
          ctx.strokeStyle = `rgba(255, 255, 180, ${p.alpha * 0.6})`
          ctx.lineWidth = 1.5
          ctx.beginPath()
          ctx.moveTo(p.x - p.radius * 2, p.y)
          ctx.lineTo(p.x + p.radius * 2, p.y)
          ctx.moveTo(p.x, p.y - p.radius * 2)
          ctx.lineTo(p.x, p.y + p.radius * 2)
          ctx.stroke()
        } else if (p.type === 'dipper') {
          // 北斗七星渲染（常亮，淡蓝色，与北极星相同样式）
          ctx.fillStyle = `rgba(200, 230, 255, ${p.alpha})`
          ctx.fill()
          
          // 星星多层光晕
          // 内层光晕
          ctx.beginPath()
          ctx.arc(p.x, p.y, p.radius * 2.5, 0, Math.PI * 2)
          const innerGlow = ctx.createRadialGradient(p.x, p.y, 0, p.x, p.y, p.radius * 2.5)
          innerGlow.addColorStop(0, `rgba(200, 230, 255, ${p.alpha * 0.5})`)
          innerGlow.addColorStop(1, 'transparent')
          ctx.fillStyle = innerGlow
          ctx.fill()
          
          // 外层光晕
          ctx.beginPath()
          ctx.arc(p.x, p.y, p.radius * 4, 0, Math.PI * 2)
          const outerGlow = ctx.createRadialGradient(p.x, p.y, 0, p.x, p.y, p.radius * 4)
          outerGlow.addColorStop(0, `rgba(180, 220, 255, ${p.alpha * 0.3})`)
          outerGlow.addColorStop(1, 'transparent')
          ctx.fillStyle = outerGlow
          ctx.fill()
          
          // 十字光芒
          ctx.strokeStyle = `rgba(200, 230, 255, ${p.alpha * 0.6})`
          ctx.lineWidth = 1.5
          ctx.beginPath()
          ctx.moveTo(p.x - p.radius * 2, p.y)
          ctx.lineTo(p.x + p.radius * 2, p.y)
          ctx.moveTo(p.x, p.y - p.radius * 2)
          ctx.lineTo(p.x, p.y + p.radius * 2)
          ctx.stroke()
        } else {
          // 普通星星渲染
          ctx.fillStyle = `rgba(255, 255, 255, ${p.alpha})`
          ctx.fill()
          
          // 较大星星的小光晕
          if (p.radius > 1.5) {
            ctx.beginPath()
            ctx.arc(p.x, p.y, p.radius * 2, 0, Math.PI * 2)
            const glowGradient = ctx.createRadialGradient(p.x, p.y, 0, p.x, p.y, p.radius * 2)
            glowGradient.addColorStop(0, `rgba(200, 220, 255, ${p.alpha * 0.3})`)
            glowGradient.addColorStop(1, 'transparent')
            ctx.fillStyle = glowGradient
            ctx.fill()
          }
        }
      })
      
      animationId = requestAnimationFrame(animate)
    }

    const handleResize = () => {
      if (userStore.value.isLoggedIn) {
        initWaveCanvas()
      } else {
        initParticles()
      }
    }

    // 像素粒子相关
    let waveCtx = null
    let waveParticles = []
    let waveAnimationId = null
    let mouseX = -1000
    let mouseY = -1000

    // 初始化像素粒子

        const initWaveParticles = () => {

          waveParticles = []

          const canvasEl = mapCanvas.value

          const containerEl = container.value

          

          if (!canvasEl || !containerEl) {

            console.log('Canvas 或 container 未找到')

            return

          }

    

          const { width, height } = containerEl.getBoundingClientRect()

          canvasEl.width = width

          canvasEl.height = height

          

          waveCtx = canvasEl.getContext('2d')

    

          // 整个容器底部都是起火点 - 增加密度

          const pixelSize = 3 // 减小像素大小增加密度

          const bottomHeight = 30 // 增加底部起火高度

    

          // 创建像素火焰粒子 - 每个位置创建更多层

          for (let x = 0; x < width; x += pixelSize) {

            // 每个像素位置创建5层火焰（原来3层），增加密度

            for (let layer = 0; layer < 5; layer++) {

              const pixel = {

                x: x,

                y: height - Math.random() * bottomHeight,

                baseX: x,

                baseY: height - layer * 6,

                width: pixelSize + Math.random() * 2,

                height: pixelSize + Math.random() * 3, // 高度变化更多

                vx: (Math.random() - 0.5) * 2, // 增加水平速度

                vy: -1.5 - Math.random() * 3.5, // 增加垂直速度范围

                alpha: 0,

                phase: 'appearing',

                phaseTimer: Math.random() * 60,

                floatDuration: 40 + Math.random() * 120, // 生命周期变化更大

                maxAlpha: 0.5 + Math.random() * 0.5, // 透明度变化更大

                oscillationPhase: Math.random() * Math.PI * 2,

                oscillationSpeed: 0.04 + Math.random() * 0.12, // 摇摆速度变化更大

                type: 'flame-pixel',

                temperature: 0.3 + Math.random() * 0.7,

                layer: layer,

                shapeType: Math.random() // 形状类型

              }

              waveParticles.push(pixel)

            }

          }

          

          // 添加额外的火星粒子 - 增加数量

          const emberCount = Math.floor(width / 4) // 增加火星数量

          for (let i = 0; i < emberCount; i++) {

            const ember = {

              x: Math.random() * width,

              y: height - Math.random() * 40,

              width: 2 + Math.random() * 3,

              height: 2 + Math.random() * 3,

              vx: (Math.random() - 0.5) * 4, // 增加速度

              vy: -2.5 - Math.random() * 5, // 增加垂直速度

              alpha: 0,

              phase: 'appearing',

              phaseTimer: Math.random() * 60,

              floatDuration: 30 + Math.random() * 100, // 生命周期变化

              maxAlpha: 0.6 + Math.random() * 0.4,

              trail: [],

              trailLength: 3 + Math.floor(Math.random() * 6), // 拖尾长度变化

              type: 'ember',

              temperature: 0.2 + Math.random() * 0.5

            }

            waveParticles.push(ember)

          }

          

          // 背景飘浮的余烬 - 增加数量

          const floatingEmbers = 250

          for (let i = 0; i < floatingEmbers; i++) {

            const particle = {

              x: Math.random() * width,

              y: Math.random() * height * 0.7,

              width: 2 + Math.random() * 4, // 尺寸变化更大

              height: 2 + Math.random() * 4,

              vx: (Math.random() - 0.5) * 1.5,

              vy: -0.5 - Math.random() * 1.5,

              alpha: 0,

              phase: 'appearing',

              phaseTimer: Math.random() * 60,

              floatDuration: 80 + Math.random() * 200, // 生命周期变化很大

              maxAlpha: 0.2 + Math.random() * 0.4,

              type: 'floating',

              temperature: 0.1 + Math.random() * 0.4

            }

            waveParticles.push(particle)

          }

          

          console.log('创建了', waveParticles.length, '个像素火焰粒子')

        }    // 鼠标移动处理
    const handleMouseMove = (e) => {
      const rect = mapCanvas.value.getBoundingClientRect()
      mouseX = e.clientX - rect.left
      mouseY = e.clientY - rect.top
    }

    // 鼠标离开处理
    const handleMouseLeave = () => {
      mouseX = -1000
      mouseY = -1000
    }

    // 绘制像素粒子动画
    const animateWave = () => {
      if (!waveCtx || !mapCanvas.value) {
        console.log('waveCtx 或 mapCanvas 不存在')
        return
      }

      const width = mapCanvas.value.width
      const height = mapCanvas.value.height
      
      waveCtx.clearRect(0, 0, width, height)
      
      // 绘制深色背景
      waveCtx.fillStyle = '#0a0a0a'
      waveCtx.fillRect(0, 0, width, height)
      
      // 绘制底部火焰光晕
      const glowHeight = height * 0.1
      const bottomGlow = waveCtx.createLinearGradient(0, height - glowHeight, 0, height)
      bottomGlow.addColorStop(0, 'rgba(255, 100, 0, 0)')
      bottomGlow.addColorStop(0.5, 'rgba(255, 150, 0, 0.08)')
      bottomGlow.addColorStop(1, 'rgba(255, 200, 0, 0.12)')
      waveCtx.fillStyle = bottomGlow
      waveCtx.fillRect(0, height - glowHeight, width, glowHeight)
      
      // 更新并绘制粒子
      for (let i = waveParticles.length - 1; i >= 0; i--) {
        const p = waveParticles[i]
        
        // 更新粒子状态
        p.phaseTimer++
        
        // 像素火焰粒子
        if (p.type === 'flame-pixel') {
          if (p.phase === 'appearing') {
            p.alpha = Math.min(p.alpha + 0.03, p.maxAlpha)
            if (p.alpha >= p.maxAlpha) {
              p.phase = 'floating'
              p.phaseTimer = 0
            }
          } else if (p.phase === 'floating') {
            // 移动粒子
            p.x += p.vx
            p.y += p.vy
            
            // 左右摇摆
            const oscillation = Math.sin(p.phaseTimer * p.oscillationSpeed + p.oscillationPhase) * 0.8
            p.x += oscillation
            
            // 随着高度增加，速度衰减
            const heightFromBottom = Math.max(0, height - p.y)
            p.vx *= 0.99
            
            // 边界检查
            if (p.x < -20 || p.x > width + 20 || p.y < -80 || p.y > height + 20) {
              p.phase = 'disappearing'
              p.phaseTimer = 0
            }
            
            if (p.phaseTimer > p.floatDuration) {
              p.phase = 'disappearing'
              p.phaseTimer = 0
            }
          } else if (p.phase === 'disappearing') {
            // 根据粒子层和其他属性调整消失速度
            const fadeSpeed = 0.02 + (p.layer || 0) * 0.01 + Math.random() * 0.03
            p.alpha = Math.max(p.alpha - fadeSpeed, 0)
            if (p.alpha <= 0) {
              waveParticles.splice(i, 1)
              // 重新创建像素粒子
              const pixel = {
                x: p.baseX,
                y: height - Math.random() * 20,
                baseX: p.baseX,
                baseY: height - p.layer * 6,
                width: p.width,
                height: p.height,
                vx: (Math.random() - 0.5) * 2,
                vy: -1.5 - Math.random() * 3.5,
                alpha: 0,
                phase: 'appearing',
                phaseTimer: Math.random() * 60,
                floatDuration: 40 + Math.random() * 120,
                maxAlpha: 0.5 + Math.random() * 0.5,
                oscillationPhase: Math.random() * Math.PI * 2,
                oscillationSpeed: 0.04 + Math.random() * 0.12,
                type: 'flame-pixel',
                temperature: 0.3 + Math.random() * 0.7,
                layer: p.layer,
                shapeType: Math.random()
              }
              waveParticles.push(pixel)
              continue
            }
          }
          
          // 鼠标排斥
          if (mouseX > -500) {
            const dx = p.x - mouseX
            const dy = p.y - mouseY
            const distance = Math.sqrt(dx * dx + dy * dy)
            if (distance < 80) {
              const force = (80 - distance) / 80
              const angle = Math.atan2(dy, dx)
              p.x += Math.cos(angle) * force * 3
              p.y += Math.sin(angle) * force * 3
            }
          }
        }
        // 火星粒子
        else if (p.type === 'ember') {
          if (p.phase === 'appearing') {
            p.alpha = Math.min(p.alpha + 0.03, p.maxAlpha)
            if (p.alpha >= p.maxAlpha) {
              p.phase = 'floating'
              p.phaseTimer = 0
            }
          } else if (p.phase === 'floating') {
            // 移动粒子
            p.x += p.vx
            p.y += p.vy
            p.vy += 0.02 // 重力
            
            // 更新拖尾
            p.trail.unshift({ x: p.x, y: p.y, alpha: p.alpha })
            if (p.trail.length > p.trailLength) {
              p.trail.pop()
            }
            
            // 边界检查
            if (p.x < -30 || p.x > width + 30 || p.y < -80 || p.y > height + 30) {
              p.phase = 'disappearing'
              p.phaseTimer = 0
            }
            
            if (p.phaseTimer > p.floatDuration) {
              p.phase = 'disappearing'
              p.phaseTimer = 0
            }
          } else if (p.phase === 'disappearing') {
            const fadeSpeed = 0.03 + Math.random() * 0.04
            p.alpha = Math.max(p.alpha - fadeSpeed, 0)
            if (p.alpha <= 0) {
              waveParticles.splice(i, 1)
              const ember = {
                x: Math.random() * width,
                y: height - Math.random() * 30,
                width: p.width,
                height: p.height,
                vx: (Math.random() - 0.5) * 4,
                vy: -2 - Math.random() * 4,
                alpha: 0,
                phase: 'appearing',
                phaseTimer: Math.random() * 60,
                floatDuration: 50 + Math.random() * 80,
                maxAlpha: 0.8 + Math.random() * 0.2,
                trail: [],
                trailLength: 4 + Math.floor(Math.random() * 4),
                type: 'ember',
                temperature: 0.3 + Math.random() * 0.4
              }
              waveParticles.push(ember)
              continue
            }
          }
        }
        // 漂浮余烬
        else if (p.type === 'floating') {
          if (p.phase === 'appearing') {
            p.alpha = Math.min(p.alpha + 0.01, p.maxAlpha)
            if (p.alpha >= p.maxAlpha) {
              p.phase = 'floating'
              p.phaseTimer = 0
            }
          } else if (p.phase === 'floating') {
            p.x += p.vx
            p.y += p.vy
            
            if (p.x < -30 || p.x > width + 30 || p.y < -80 || p.y > height + 30) {
              p.phase = 'disappearing'
              p.phaseTimer = 0
            }
            
            if (p.phaseTimer > p.floatDuration) {
              p.phase = 'disappearing'
              p.phaseTimer = 0
            }
          } else if (p.phase === 'disappearing') {
            const fadeSpeed = 0.01 + Math.random() * 0.03
            p.alpha = Math.max(p.alpha - fadeSpeed, 0)
            if (p.alpha <= 0) {
              waveParticles.splice(i, 1)
              const particle = {
                x: Math.random() * width,
                y: Math.random() * height * 0.6,
                width: p.width,
                height: p.height,
                vx: (Math.random() - 0.5) * 1,
                vy: -0.5 - Math.random() * 1,
                alpha: 0,
                phase: 'appearing',
                phaseTimer: Math.random() * 60,
                floatDuration: 100 + Math.random() * 150,
                maxAlpha: 0.3 + Math.random() * 0.3,
                type: 'floating',
                temperature: 0.2 + Math.random() * 0.3
              }
              waveParticles.push(particle)
              continue
            }
          }
        }
        
        // 选择颜色
        let color, glowColor
        if (p.type === 'flame-pixel') {
          // 根据温度和高度选择颜色
          const heightFromBottom = Math.max(0, height - p.y)
          const heightFactor = 1 - (heightFromBottom / (height * 0.7))
          
          if (heightFactor > 0.7) {
            // 底部：亮黄白
            color = `hsla(45, 100%, ${65 + p.temperature * 20}%, ${p.alpha})`
            glowColor = `hsla(45, 100%, 80%, ${p.alpha * 0.9})`
          } else if (heightFactor > 0.3) {
            // 中部：橙色
            color = `hsla(30, 100%, ${50 + p.temperature * 15}%, ${p.alpha})`
            glowColor = `hsla(30, 100%, 65%, ${p.alpha * 0.8})`
          } else {
            // 顶部：橙红
            color = `hsla(15, 100%, ${40 + p.temperature * 10}%, ${p.alpha})`
            glowColor = `hsla(15, 100%, 55%, ${p.alpha * 0.7})`
          }
        } else if (p.type === 'ember') {
          // 火星：暗红
          const hue = 10 + p.temperature * 15
          const lightness = 35 + p.temperature * 15
          color = `hsla(${hue}, 90%, ${lightness}%, ${p.alpha})`
          glowColor = `hsla(${hue}, 90%, 50%, ${p.alpha * 0.8})`
        } else if (p.type === 'floating') {
          // 漂浮：暗橙色
          color = `hsla(20, 70%, ${25 + p.temperature * 10}%, ${p.alpha})`
          glowColor = `hsla(20, 70%, 40%, ${p.alpha * 0.6})`
        }
        
        // 绘制拖尾（火星）
        if (p.type === 'ember' && p.trail && p.trail.length > 1) {
          waveCtx.beginPath()
          waveCtx.moveTo(p.trail[0].x, p.trail[0].y)
          for (let j = 1; j < p.trail.length; j++) {
            waveCtx.lineTo(p.trail[j].x, p.trail[j].y)
          }
          waveCtx.strokeStyle = `hsla(10, 90%, 40%, ${p.alpha * 0.3})`
          waveCtx.lineWidth = p.width * 0.6
          waveCtx.lineCap = 'round'
          waveCtx.stroke()
        }
        
        // 绘制像素方块 - 根据shapeType绘制不同形状
        waveCtx.fillStyle = color
        if (p.type === 'flame-pixel') {
          const shape = p.shapeType || Math.random()
          if (shape < 0.4) {
            // 标准矩形
            waveCtx.fillRect(p.x - p.width/2, p.y - p.height/2, p.width, p.height)
          } else if (shape < 0.7) {
            // 拉长的矩形（模拟向上流动）
            waveCtx.fillRect(p.x - p.width/2, p.y - p.height, p.width, p.height * 2)
          } else {
            // 小矩形（模拟远处的粒子）
            waveCtx.fillRect(p.x - p.width/4, p.y - p.height/4, p.width/2, p.height/2)
          }
        } else {
          // 火星和漂浮余烬使用标准矩形
          waveCtx.fillRect(p.x - p.width/2, p.y - p.height/2, p.width, p.height)
        }
        
        // 发光效果
        if (p.type === 'flame-pixel' && p.alpha > 0.4) {
          waveCtx.shadowBlur = 8
          waveCtx.shadowColor = glowColor
        } else {
          waveCtx.shadowBlur = 0
        }
      }
      
      waveCtx.shadowBlur = 0

      waveAnimationId = requestAnimationFrame(animateWave)
    }

    // 初始化像素粒子画布
    const initWaveCanvas = () => {
      initWaveParticles()
      animateWave()
    }

    // 点击交互：创建火焰爆发效果
    const handleWaveClick = (e) => {
      const rect = mapCanvas.value.getBoundingClientRect()
      const clickX = e.clientX - rect.left
      const clickY = e.clientY - rect.top
      const width = mapCanvas.value.width
      const height = mapCanvas.value.height
      
      // 在点击位置创建像素火焰爆发 - 增加粒子数量
      const burstRadius = 80
      for (let angle = 0; angle < Math.PI * 2; angle += 0.15) {
        for (let r = 0; r < burstRadius; r += 6) {
          const x = clickX + Math.cos(angle) * r
          const y = clickY + Math.sin(angle) * r
          
          const pixel = {
            x: x,
            y: y,
            baseX: x,
            baseY: y,
            width: 3 + Math.random() * 3,
            height: 3 + Math.random() * 4,
            vx: Math.cos(angle) * (1.5 + Math.random() * 2.5),
            vy: Math.sin(angle) * (1.5 + Math.random() * 2.5) - 1,
            alpha: 1,
            phase: 'appearing',
            phaseTimer: 0,
            floatDuration: 20 + Math.random() * 50,
            maxAlpha: 0.7 + Math.random() * 0.3,
            oscillationPhase: Math.random() * Math.PI * 2,
            oscillationSpeed: 0.04 + Math.random() * 0.12,
            type: 'flame-pixel',
            temperature: 0.5 + Math.random() * 0.5,
            layer: 0,
            shapeType: Math.random()
          }
          waveParticles.push(pixel)
        }
      }
      
      // 添加火星 - 增加数量
      for (let i = 0; i < 30; i++) {
        const angle = Math.random() * Math.PI * 2
        const speed = 2 + Math.random() * 4
        const ember = {
          x: clickX,
          y: clickY,
          width: 2 + Math.random() * 3,
          height: 2 + Math.random() * 3,
          vx: Math.cos(angle) * speed,
          vy: Math.sin(angle) * speed - 1,
          alpha: 1,
          phase: 'appearing',
          phaseTimer: 0,
          floatDuration: 30 + Math.random() * 60,
          maxAlpha: 0.7 + Math.random() * 0.3,
          trail: [],
          trailLength: 3 + Math.floor(Math.random() * 6),
          type: 'ember',
          temperature: 0.3 + Math.random() * 0.5
        }
        waveParticles.push(ember)
      }
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
      const brandStory = document.querySelector('.brand-story')

      // 未登录：处理星空背景的滑动效果
      if (hero && !userStore.value.isLoggedIn) {
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

      // 已登录：处理火焰背景的滑动效果
      if (brandStory && userStore.value.isLoggedIn) {
        const brandStoryHeight = brandStory.offsetHeight

        if (scrollY > brandStoryHeight * 0.3) {
          const opacity = 1 - (scrollY - brandStoryHeight * 0.3) / (brandStoryHeight * 0.7)
          brandStory.style.opacity = Math.max(0, opacity)
          brandStory.style.transform = `translateY(${(scrollY - brandStoryHeight * 0.3) * 0.3}px)`
        } else {
          brandStory.style.opacity = 1
          brandStory.style.transform = 'translateY(0)'
        }

        // 当滚动超过品牌故事的一半时，显示产品展示区域
        if (scrollY > brandStoryHeight * 0.5) {
          productsShowcaseSection.value?.classList.add('visible')
        }
      }

      lastScrollY = scrollY
    }

    // 初始化星空背景
    const initStarBackground = () => {
      if (!userStore.value.isLoggedIn) {
        // 停止海浪动画
        if (waveAnimationId) {
          cancelAnimationFrame(waveAnimationId)
          waveAnimationId = null
        }
        initParticles()
        animate()
      }
    }

    // 监听登录状态变化?
    watch(() => userStore.value.isLoggedIn, (newVal) => {
      if (!newVal) {
        // 退出登录后重新初始化星空背景
        initStarBackground()
      } else {
        // 登录后初始化火焰效果
        initWaveCanvas()
      }
    })

    onMounted(() => {
      initStarBackground()
      if (userStore.value.isLoggedIn) {
        initWaveCanvas()
      }
      startCarousel()
      window.addEventListener('resize', handleResize)
      window.addEventListener('scroll', handleScroll)
    })

    onUnmounted(() => {
      if (animationId) {
        cancelAnimationFrame(animationId)
      }
      if (waveAnimationId) {
        cancelAnimationFrame(waveAnimationId)
      }
      stopCarousel()
      window.removeEventListener('resize', handleResize)
      window.removeEventListener('scroll', handleScroll)
    })

    return {
      searchText,
      canvas,
      mapCanvas,
      container,
      featuresSection,
      productsShowcaseSection,
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

/* 欢迎页面区域 */
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

/* 销量统计?*/
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

/* 最新产品?- 轮播图图样式?*/
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

/* 轮播图指示器?*/
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

/* 轮播图按钮 */
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

/* 登录后页面样式?- 产品宣传页面 */
.promo-page {
  min-height: calc(100vh - 60px);
  background: linear-gradient(180deg, #0a0a12 0%, #0d0d1a 50%, #121220 100%);
  padding-bottom: 60px;
}

/* 品牌故事区域 */
.brand-story {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  min-height: 100vh;
  padding: 80px 40px;
  background: #141414;
  overflow: hidden;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  z-index: 0;
  transition: opacity 0.3s ease, transform 0.3s ease;
}

/* 品牌故事占位元素 */
.brand-story-placeholder {
  height: 100vh;
  width: 100%;
}

/* 世界地图 Canvas 容器 */
.map-container {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: #000;
  overflow: hidden;
}

.map-container canvas {
  display: block;
  width: 100%;
  height: 100%;
}

/* 左下角标题组 */
.title-group {
  position: absolute;
  left: 8vw;
  bottom: 20vh;
  max-width: 600px;
  z-index: 10;
}

.title-group .title-en {
  font-family: 'Inter', -apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif;
  font-size: 128px;
  font-weight: 700;
  color: #FFFFFF;
  letter-spacing: -0.5px;
  line-height: 1.1;
  margin: 0;
  text-shadow: 0 0 30px rgba(255, 100, 50, 0.6),
               0 0 60px rgba(255, 50, 0, 0.4);
}

.title-group .title-zh {
  font-family: 'PingFang SC', 'Noto Sans CJK SC', 'Microsoft YaHei', sans-serif;
  font-size: 48px;
  font-weight: 500;
  color: #E0E0E0;
  letter-spacing: 0.5px;
  line-height: 1.2;
  margin: 12px 0 0 0;
  text-shadow: 0 0 20px rgba(255, 150, 50, 0.4);
}

/* 右侧内容区 */
.content-right {
  position: absolute;
  right: 8vw;
  bottom: 20vh;
  max-width: 450px;
  z-index: 10;
  text-align: left;
}

.content-right .desc-text {
  font-size: 18px;
  font-weight: 600;
  color: #FFFFFF;
  line-height: 1.8;
  margin: 0 0 24px 0;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.8);
}

.content-right .about-btn {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 14px 32px;
  background: linear-gradient(135deg, #22c55e 0%, #16a34a 100%);
  color: #ffffff;
  font-size: 16px;
  font-weight: 600;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 4px 15px rgba(34, 197, 94, 0.4);
}

.content-right .about-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 25px rgba(34, 197, 94, 0.6);
  background: linear-gradient(135deg, #16a34a 0%, #15803d 100%);
}

.content-right .arrow-icon {
  width: 20px;
  height: 20px;
  transition: transform 0.3s ease;
}

.content-right .about-btn:hover .arrow-icon {
  transform: translateX(4px);
}

/* 产品展示区域 */
.products-showcase {
  padding: 100px 40px 60px 40px;
  max-width: 1400px;
  margin: 0 auto;
  background: linear-gradient(180deg, transparent 0%, rgba(13, 17, 23, 0.95) 30%);
  position: relative;
  z-index: 1;
  opacity: 0;
  transform: translateY(50px);
  transition: all 0.6s ease;
}

.products-showcase.visible {
  opacity: 1;
  transform: translateY(0);
}

.showcase-title {
  font-size: 36px;
  font-weight: 700;
  color: #ffffff;
  text-align: center;
  margin-bottom: 50px;
  padding-top: 60px;
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
  box-shadow: 0 12px 40px rgba(126, 58克, 242, 0.2);
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

/* 响应式设计 */
@media (max-width: 1200px) {
  .title-group .title-en {
    font-size: 96px;
  }
  
  .title-group .title-zh {
    font-size: 42px;
  }
  
  .content-right {
    max-width: 400px;
  }
}

@media (max-width: 768px) {
  .title-group {
    left: 5vw;
    bottom: 15vh;
    max-width: 100%;
  }
  
  .title-group .title-en {
    font-size: 64px;
  }
  
  .title-group .title-zh {
    font-size: 32px;
  }
  
  .content-right {
    left: 5vw;
    right: 5vw;
    bottom: 5vh;
    max-width: 100%;
    text-align: left;
  }
  
  .content-right .desc-text {
    font-size: 16px;
  }
  
  .content-right .about-btn {
    width: 100%;
    justify-content: center;
  }
}
</style>




