<template>
  <WebsiteLayout current-page="contact">
    <!-- 页面头部 -->
    <section class="contact-hero">
      <div class="hero-content">
        <h1 class="hero-title">联系我们</h1>
        <p class="hero-subtitle">有任何问题？我们随时为您服务</p>
      </div>
    </section>

    <!-- 联系方式 -->
    <section class="contact-section">
      <div class="contact-grid">
        <div class="contact-card" v-for="(contact, index) in contacts" :key="index">
          <div class="contact-icon">{{ contact.icon }}</div>
          <h3>{{ contact.title }}</h3>
          <p>{{ contact.content }}</p>
        </div>
      </div>
    </section>

    <!-- 二维码区域 -->
    <section class="qr-section">
      <div class="section-header">
        <h2>扫码关注</h2>
        <p>关注我们的官方账号，了解最新资讯</p>
      </div>
      <div class="qr-grid">
        <div class="qr-card" v-for="(qr, index) in qrcodes" :key="index">
          <div class="qr-image">
            <div class="qr-placeholder">
              <span>{{ qr.icon }}</span>
            </div>
          </div>
          <h3>{{ qr.title }}</h3>
          <p>{{ qr.desc }}</p>
        </div>
      </div>
    </section>

    <!-- 在线留言 -->
    <section class="message-section">
      <div class="section-header">
        <h2>在线留言</h2>
        <p>有任何建议或问题，请告诉我们</p>
      </div>
      <div class="message-form">
        <div class="form-row">
          <input type="text" v-model="formData.name" placeholder="您的姓名" class="form-input" />
          <input type="email" v-model="formData.email" placeholder="电子邮箱" class="form-input" />
        </div>
        <input type="tel" v-model="formData.phone" placeholder="联系电话" class="form-input" />
        <select v-model="formData.type" class="form-input">
          <option value="">请选择咨询类型</option>
          <option value="product">产品咨询</option>
          <option value="order">订单问题</option>
          <option value="aftersale">售后服务</option>
          <option value="cooperation">商务合作</option>
          <option value="other">其他</option>
        </select>
        <textarea v-model="formData.message" placeholder="请输入您的留言内容..." class="form-input form-textarea"></textarea>
        <button class="submit-btn" @click="submitMessage">提交留言</button>
      </div>
    </section>

    <!-- 地图区域 -->
    <section class="map-section">
      <div class="section-header">
        <h2>公司地址</h2>
        <p>欢迎来到我们的办公地点</p>
      </div>
      <div class="map-container">
        <div class="map-placeholder">
          <div class="map-content">
            <span class="map-icon">📍</span>
            <h3>星火游戏设备有限公司</h3>
            <p>上海市浦东新区游戏路88号</p>
            <p>星火大厦 A座 15层</p>
          </div>
        </div>
      </div>
    </section>

    <!-- 常见问题 -->
    <section class="faq-section">
      <div class="section-header">
        <h2>常见问题</h2>
        <p>这些问题的答案或许能帮到您</p>
      </div>
      <div class="faq-list">
        <div 
          class="faq-item" 
          v-for="(faq, index) in faqs" 
          :key="index"
          @click="toggleFaq(index)"
        >
          <div class="faq-question">
            <span>{{ faq.question }}</span>
            <span class="faq-icon">{{ faqOpen === index ? '−' : '+' }}</span>
          </div>
          <div class="faq-answer" v-show="faqOpen === index">
            <p>{{ faq.answer }}</p>
          </div>
        </div>
      </div>
    </section>
  </WebsiteLayout>
</template>

<script>
import { ref } from 'vue'
import WebsiteLayout from '../components/WebsiteLayout.vue'

export default {
  name: 'Contact',
  components: {
    WebsiteLayout
  },
  setup() {
    const faqOpen = ref(null)
    
    const contacts = [
      { icon: '📞', title: '客服热线', content: '400-888-8888' },
      { icon: '⏰', title: '服务时间', content: '周一至周日 9:00-21:00' },
      { icon: '📧', title: '商务邮箱', content: 'business@xinghuo.com' },
      { icon: '💬', title: '在线客服', content: '点击右侧图标在线咨询' }
    ]

    const qrcodes = [
      { icon: '💚', title: '微信公众号', desc: '扫码关注获取最新资讯' },
      { icon: '🧡', title: '官方微博', desc: '第一时间了解活动信息' },
      { icon: '💙', title: 'B站官方', desc: '精彩视频不容错过' },
      { icon: '💜', title: 'Discord社区', desc: '加入玩家大家庭' }
    ]

    const faqs = [
      { 
        question: '产品有质保吗？', 
        answer: '我们所有产品都提供一年质保服务。在质保期内，非人为损坏的产品可享受免费维修或更换服务。' 
      },
      { 
        question: '支持哪些支付方式？', 
        answer: '我们支持支付宝、微信支付、银行卡等多种支付方式，让您购物更加便捷。' 
      },
      { 
        question: '发货时间是怎样的？', 
        answer: '订单确认后，我们会在48小时内发货。江浙沪地区通常1-2天到达，其他地区2-4天到达。' 
      },
      { 
        question: '支持无理由退货吗？', 
        answer: '我们支持7天无理由退货（定制产品除外）。退货时请确保产品包装完整、不影响二次销售。' 
      },
      { 
        question: '如何申请成为代理商？', 
        answer: '您可以通过商务邮箱与我们联系，提交您的合作意向和相关资质，我们会尽快与您对接。' 
      }
    ]

    const formData = ref({
      name: '',
      email: '',
      phone: '',
      type: '',
      message: ''
    })

    const toggleFaq = (index) => {
      faqOpen.value = faqOpen.value === index ? null : index
    }

    const submitMessage = () => {
      if (!formData.value.name || !formData.value.message) {
        alert('请填写姓名和留言内容')
        return
      }
      alert('感谢您的留言！我们会尽快回复您。')
      formData.value = {
        name: '',
        email: '',
        phone: '',
        type: '',
        message: ''
      }
    }

    return {
      contacts,
      qrcodes,
      faqs,
      formData,
      faqOpen,
      toggleFaq,
      submitMessage
    }
  }
}
</script>

<style scoped>
/* 页面头部 */
.contact-hero {
  min-height: 40vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #0d1117 0%, #161b22 100%);
  position: relative;
  overflow: hidden;
}

.contact-hero::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: radial-gradient(circle at 30% 50%, rgba(102, 126, 234, 0.15) 0%, transparent 50%),
              radial-gradient(circle at 70% 50%, rgba(118, 75, 162, 0.15) 0%, transparent 50%);
}

.hero-content {
  text-align: center;
  position: relative;
  z-index: 1;
}

.hero-title {
  font-size: 56px;
  font-weight: 700;
  color: #ffffff;
  margin-bottom: 16px;
}

.hero-subtitle {
  font-size: 20px;
  color: #8b949e;
}

/* 通用样式 */
.section-header {
  text-align: center;
  margin-bottom: 60px;
}

.section-header h2 {
  font-size: 42px;
  font-weight: 600;
  color: #ffffff;
  margin-bottom: 16px;
}

.section-header p {
  font-size: 18px;
  color: #8b949e;
}

/* 联系方式 */
.contact-section {
  background: #0d1117;
  padding: 80px 24px;
}

.contact-grid {
  max-width: 1200px;
  margin: 0 auto;
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 30px;
}

.contact-card {
  background: rgba(22, 27, 34, 0.8);
  border: 1px solid #30363d;
  border-radius: 16px;
  padding: 40px 24px;
  text-align: center;
  transition: all 0.3s ease;
}

.contact-card:hover {
  transform: translateY(-8px);
  border-color: #667eea;
  box-shadow: 0 20px 40px rgba(102, 126, 234, 0.2);
}

.contact-icon {
  font-size: 48px;
  margin-bottom: 20px;
}

.contact-card h3 {
  font-size: 18px;
  color: #ffffff;
  margin-bottom: 12px;
}

.contact-card p {
  font-size: 16px;
  color: #8b949e;
}

/* 二维码区域 */
.qr-section {
  background: linear-gradient(180deg, #0d1117 0%, #161b22 100%);
  padding: 100px 24px;
}

.qr-grid {
  max-width: 1200px;
  margin: 0 auto;
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 30px;
}

.qr-card {
  background: rgba(22, 27, 34, 0.8);
  border: 1px solid #30363d;
  border-radius: 20px;
  padding: 40px 24px;
  text-align: center;
  transition: all 0.3s ease;
}

.qr-card:hover {
  transform: translateY(-8px);
  border-color: #667eea;
}

.qr-image {
  margin-bottom: 20px;
}

.qr-placeholder {
  width: 150px;
  height: 150px;
  background: linear-gradient(135deg, #21262d 0%, #161b22 100%);
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto;
  border: 2px dashed #30363d;
}

.qr-placeholder span {
  font-size: 56px;
}

.qr-card h3 {
  font-size: 18px;
  color: #ffffff;
  margin-bottom: 8px;
}

.qr-card p {
  font-size: 14px;
  color: #8b949e;
}

/* 在线留言 */
.message-section {
  background: #161b22;
  padding: 100px 24px;
}

.message-form {
  max-width: 700px;
  margin: 0 auto;
}

.form-row {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20px;
  margin-bottom: 20px;
}

.form-input {
  width: 100%;
  background: #0d1117;
  border: 1px solid #30363d;
  border-radius: 8px;
  padding: 16px 20px;
  color: #ffffff;
  font-size: 16px;
  outline: none;
  transition: border-color 0.3s ease;
  margin-bottom: 20px;
}

.form-input::placeholder {
  color: #6e7681;
}

.form-input:focus {
  border-color: #667eea;
}

.form-textarea {
  min-height: 150px;
  resize: vertical;
}

.submit-btn {
  width: 100%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  padding: 18px;
  border-radius: 8px;
  font-size: 18px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
}

.submit-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 10px 30px rgba(102, 126, 234, 0.4);
}

/* 地图区域 */
.map-section {
  background: #0d1117;
  padding: 100px 24px;
}

.map-container {
  max-width: 1000px;
  margin: 0 auto;
}

.map-placeholder {
  background: linear-gradient(135deg, #21262d 0%, #161b22 100%);
  border: 1px solid #30363d;
  border-radius: 20px;
  padding: 80px;
  text-align: center;
}

.map-content {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.map-icon {
  font-size: 64px;
  margin-bottom: 20px;
}

.map-content h3 {
  font-size: 24px;
  color: #ffffff;
  margin-bottom: 16px;
}

.map-content p {
  font-size: 16px;
  color: #8b949e;
  margin-bottom: 8px;
}

/* 常见问题 */
.faq-section {
  background: linear-gradient(180deg, #161b22 0%, #0d1117 100%);
  padding: 100px 24px;
}

.faq-list {
  max-width: 800px;
  margin: 0 auto;
}

.faq-item {
  background: rgba(22, 27, 34, 0.8);
  border: 1px solid #30363d;
  border-radius: 12px;
  margin-bottom: 16px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s ease;
}

.faq-item:hover {
  border-color: #667eea;
}

.faq-question {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 24px;
  color: #ffffff;
  font-size: 16px;
  font-weight: 500;
}

.faq-icon {
  font-size: 24px;
  color: #667eea;
}

.faq-answer {
  padding: 0 24px 20px;
}

.faq-answer p {
  color: #8b949e;
  font-size: 14px;
  line-height: 1.8;
}

/* 响应式 */
@media (max-width: 1024px) {
  .contact-grid,
  .qr-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .hero-title {
    font-size: 36px;
  }
  
  .hero-subtitle {
    font-size: 16px;
  }
  
  .section-header h2 {
    font-size: 28px;
  }
  
  .contact-grid,
  .qr-grid {
    grid-template-columns: 1fr;
  }
  
  .form-row {
    grid-template-columns: 1fr;
  }
  
  .map-placeholder {
    padding: 40px 20px;
  }
}
</style>