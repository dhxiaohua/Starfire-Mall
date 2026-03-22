<template>
  <WebsiteLayout current-page="about">
    <!-- 页面头部 -->
    <section class="about-hero">
      <div class="hero-content">
        <h1 class="hero-title">关于星火</h1>
        <p class="hero-subtitle">用星火 点燃玩家的激情</p>
      </div>
      <div class="hero-decoration">
        <div class="decoration-circle circle-1"></div>
        <div class="decoration-circle circle-2"></div>
        <div class="decoration-circle circle-3"></div>
      </div>
    </section>

    <!-- 公司故事 -->
    <section class="story-section">
      <div class="section-container">
        <div class="story-card">
          <div class="story-number">01</div>
          <div class="story-content">
            <h2>星火诞生</h2>
            <p>2024年，一群热爱游戏的年轻人相聚在一起，他们来自不同的城市，却拥有同一个梦想——为玩家打造最专业的游戏设备。</p>
            <p>在那个不起眼的车库里，星火正式诞生。从第一款产品研发到第一批用户反馈，每一步都走得坚定而踏实。</p>
          </div>
          <div class="story-visual">
            <div class="visual-box">
              <span class="visual-icon">🔥</span>
            </div>
          </div>
        </div>

        <div class="story-card reverse">
          <div class="story-number">02</div>
          <div class="story-content">
            <h2>成长与突破</h2>
            <p>2025年，星火用户突破5万大关。我们始终坚持"玩家至上"的理念，不断倾听用户声音，优化产品体验。</p>
            <p>从电竞鼠标到机械键盘，从游戏耳机到专业座椅，每一个细节都经过精心打磨，只为给玩家带来最极致的游戏体验。</p>
          </div>
          <div class="story-visual">
            <div class="visual-box">
              <span class="visual-icon">🚀</span>
            </div>
          </div>
        </div>

        <div class="story-card">
          <div class="story-number">03</div>
          <div class="story-content">
            <h2>未来展望</h2>
            <p>2026年，星火将继续前行。我们计划推出更多创新产品，布局智能穿戴设备领域，让每一位玩家都能感受到科技的魅力。</p>
            <p>我们的目标不仅仅是做游戏设备，更是要构建一个属于玩家的生态圈，让游戏成为连接彼此的桥梁。</p>
          </div>
          <div class="story-visual">
            <div class="visual-box">
              <span class="visual-icon">✨</span>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- 团队展示 -->
    <section class="team-section">
      <div class="section-header">
        <h2>核心团队</h2>
        <p>一群志同道合的伙伴，为了同一个梦想而奋斗</p>
      </div>
      <div class="team-grid">
        <div class="team-member" v-for="(member, index) in team" :key="index">
          <div class="member-avatar">
            <span>{{ member.avatar }}</span>
          </div>
          <h3>{{ member.name }}</h3>
          <p class="member-role">{{ member.role }}</p>
          <p class="member-desc">{{ member.desc }}</p>
        </div>
      </div>
    </section>

    <!-- 发展历程 -->
    <section class="timeline-section">
      <div class="section-header">
        <h2>发展历程</h2>
        <p>点点星光，汇聚成河</p>
      </div>
      <div class="timeline">
        <div class="timeline-item" v-for="(item, index) in timeline" :key="index">
          <div class="timeline-dot"></div>
          <div class="timeline-content">
            <span class="timeline-year">{{ item.year }}</span>
            <h3>{{ item.title }}</h3>
            <p>{{ item.desc }}</p>
          </div>
        </div>
      </div>
    </section>

    <!-- 价值观 -->
    <section class="values-section">
      <div class="section-header">
        <h2>我们的价值观</h2>
        <p>做玩家最信赖的伙伴</p>
      </div>
      <div class="values-grid">
        <div class="value-card" v-for="(value, index) in values" :key="index">
          <div class="value-icon">{{ value.icon }}</div>
          <h3>{{ value.title }}</h3>
          <p>{{ value.desc }}</p>
        </div>
      </div>
    </section>
  </WebsiteLayout>
</template>

<script>
import WebsiteLayout from '../components/WebsiteLayout.vue'

export default {
  name: 'About',
  components: {
    WebsiteLayout
  },
  setup() {
    const team = [
      { avatar: '👨‍💻', name: '筱华', role: '创始人 & CEO', desc: '前大厂游戏设计师，热爱游戏，致力于为玩家打造最好的装备' },
      { avatar: '👩‍💼', name: '苏小梦', role: '产品总监', desc: '深耕游戏外设领域10年，对产品有极致追求' },
      { avatar: '👨‍🔬', name: '陈极客', role: '技术总监', desc: '黑客级技术大牛，负责产品研发与技术创新' },
      { avatar: '👩‍🎨', name: '王艺涵', role: '设计总监', desc: '美学达人，让每一款产品都成为艺术品' }
    ]

    const timeline = [
      { year: '2024.03', title: '星火成立', desc: '在车库中诞生，第一款产品立项' },
      { year: '2024.09', title: '首款产品发布', desc: '星火电竞鼠标正式上线，获得用户好评' },
      { year: '2025.01', title: '用户突破1万', desc: '产品线扩展至6款，注册用户突破1万' },
      { year: '2025.06', title: '完成天使轮融资', desc: '获得知名投资机构千万级融资' },
      { year: '2025.12', title: '用户突破5万', desc: '成为国内游戏设备领域领军品牌' },
      { year: '2026', title: '全新启航', desc: '布局智能穿戴，开启新篇章' }
    ]

    const values = [
      { icon: '🎯', title: '玩家至上', desc: '一切以玩家体验为中心' },
      { icon: '🔬', title: '极致品质', desc: '对每一个细节精益求精' },
      { icon: '💡', title: '创新驱动', desc: '不断突破，引领行业变革' },
      { icon: '🤝', title: '开放共赢', desc: '与玩家、合作伙伴共同成长' }
    ]

    return {
      team,
      timeline,
      values
    }
  }
}
</script>

<style scoped>
/* 页面头部 */
.about-hero {
  min-height: 50vh;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  background: linear-gradient(135deg, #0d1117 0%, #161b22 100%);
  overflow: hidden;
}

.hero-content {
  text-align: center;
  z-index: 2;
}

.hero-title {
  font-size: 72px;
  font-weight: 700;
  color: #ffffff;
  margin-bottom: 16px;
  animation: titleFadeIn 1s ease-out;
}

.hero-subtitle {
  font-size: 24px;
  color: #8b949e;
  animation: subtitleFadeIn 1s ease-out 0.3s both;
}

@keyframes titleFadeIn {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes subtitleFadeIn {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.hero-decoration {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
}

.decoration-circle {
  position: absolute;
  border-radius: 50%;
  opacity: 0.1;
}

.circle-1 {
  width: 400px;
  height: 400px;
  background: linear-gradient(135deg, #667eea, #764ba2);
  top: -100px;
  right: -100px;
  animation: float 8s ease-in-out infinite;
}

.circle-2 {
  width: 300px;
  height: 300px;
  background: linear-gradient(135deg, #f093fb, #f5576c);
  bottom: -50px;
  left: -50px;
  animation: float 10s ease-in-out infinite reverse;
}

.circle-3 {
  width: 200px;
  height: 200px;
  background: linear-gradient(135deg, #4facfe, #00f2fe);
  top: 50%;
  left: 20%;
  animation: float 12s ease-in-out infinite;
}

@keyframes float {
  0%, 100% { transform: translateY(0) rotate(0deg); }
  50% { transform: translateY(-30px) rotate(10deg); }
}

/* 通用样式 */
.section-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 100px 24px;
}

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

/* 公司故事 */
.story-section {
  background: #0d1117;
}

.story-card {
  display: flex;
  align-items: center;
  gap: 60px;
  margin-bottom: 100px;
  opacity: 0;
  transform: translateY(50px);
  animation: cardFadeIn 0.8s ease-out forwards;
}

.story-card:nth-child(1) { animation-delay: 0.1s; }
.story-card:nth-child(2) { animation-delay: 0.3s; }
.story-card:nth-child(3) { animation-delay: 0.5s; }

.story-card.reverse {
  flex-direction: row-reverse;
}

@keyframes cardFadeIn {
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.story-number {
  font-size: 120px;
  font-weight: 700;
  color: rgba(102, 126, 234, 0.15);
  line-height: 1;
  flex-shrink: 0;
}

.story-content {
  flex: 1;
}

.story-content h2 {
  font-size: 32px;
  color: #ffffff;
  margin-bottom: 24px;
}

.story-content p {
  font-size: 16px;
  color: #8b949e;
  line-height: 1.8;
  margin-bottom: 16px;
}

.story-visual {
  flex-shrink: 0;
}

.visual-box {
  width: 150px;
  height: 150px;
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.2) 0%, rgba(118, 75, 162, 0.2) 100%);
  border-radius: 30px;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 1px solid rgba(102, 126, 234, 0.3);
  transition: all 0.3s ease;
}

.visual-box:hover {
  transform: scale(1.1) rotate(5deg);
  box-shadow: 0 20px 40px rgba(102, 126, 234, 0.3);
}

.visual-icon {
  font-size: 64px;
}

/* 团队展示 */
.team-section {
  background: linear-gradient(180deg, #0d1117 0%, #161b22 100%);
  padding: 100px 24px;
}

.team-grid {
  max-width: 1200px;
  margin: 0 auto;
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 30px;
}

.team-member {
  background: rgba(22, 27, 34, 0.8);
  border: 1px solid #30363d;
  border-radius: 20px;
  padding: 40px 24px;
  text-align: center;
  transition: all 0.4s ease;
}

.team-member:hover {
  transform: translateY(-10px);
  border-color: #667eea;
  box-shadow: 0 20px 40px rgba(102, 126, 234, 0.2);
}

.member-avatar {
  width: 100px;
  height: 100px;
  background: linear-gradient(135deg, #21262d 0%, #161b22 100%);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 20px;
  border: 3px solid #667eea;
}

.member-avatar span {
  font-size: 48px;
}

.team-member h3 {
  font-size: 20px;
  color: #ffffff;
  margin-bottom: 8px;
}

.member-role {
  color: #667eea;
  font-size: 14px;
  margin-bottom: 16px;
}

.member-desc {
  color: #8b949e;
  font-size: 14px;
  line-height: 1.6;
}

/* 发展历程 */
.timeline-section {
  background: #0d1117;
  padding: 100px 24px;
}

.timeline {
  max-width: 800px;
  margin: 0 auto;
  position: relative;
}

.timeline::before {
  content: '';
  position: absolute;
  left: 50%;
  transform: translateX(-50%);
  width: 2px;
  height: 100%;
  background: linear-gradient(180deg, #667eea 0%, #764ba2 100%);
}

.timeline-item {
  display: flex;
  justify-content: flex-end;
  padding-right: calc(50% + 40px);
  position: relative;
  margin-bottom: 50px;
}

.timeline-item:nth-child(even) {
  justify-content: flex-start;
  padding-right: 0;
  padding-left: calc(50% + 40px);
}

.timeline-dot {
  position: absolute;
  left: 50%;
  transform: translateX(-50%);
  width: 20px;
  height: 20px;
  background: #667eea;
  border-radius: 50%;
  border: 4px solid #0d1117;
  z-index: 1;
}

.timeline-content {
  background: rgba(22, 27, 34, 0.8);
  border: 1px solid #30363d;
  border-radius: 12px;
  padding: 24px;
  max-width: 350px;
}

.timeline-year {
  display: inline-block;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 14px;
  margin-bottom: 12px;
}

.timeline-content h3 {
  font-size: 18px;
  color: #ffffff;
  margin-bottom: 8px;
}

.timeline-content p {
  font-size: 14px;
  color: #8b949e;
  line-height: 1.6;
}

/* 价值观 */
.values-section {
  background: linear-gradient(180deg, #161b22 0%, #0d1117 100%);
  padding: 100px 24px;
}

.values-grid {
  max-width: 1200px;
  margin: 0 auto;
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 30px;
}

.value-card {
  background: rgba(22, 27, 34, 0.6);
  border: 1px solid #30363d;
  border-radius: 16px;
  padding: 40px 24px;
  text-align: center;
  transition: all 0.3s ease;
}

.value-card:hover {
  transform: translateY(-8px);
  border-color: #667eea;
}

.value-icon {
  font-size: 48px;
  margin-bottom: 20px;
}

.value-card h3 {
  font-size: 20px;
  color: #ffffff;
  margin-bottom: 12px;
}

.value-card p {
  font-size: 14px;
  color: #8b949e;
  line-height: 1.6;
}

/* 响应式 */
@media (max-width: 1024px) {
  .team-grid,
  .values-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .story-card,
  .story-card.reverse {
    flex-direction: column;
    text-align: center;
  }
  
  .story-number {
    font-size: 80px;
  }
  
  .timeline::before {
    left: 20px;
  }
  
  .timeline-item,
  .timeline-item:nth-child(even) {
    padding-left: 60px;
    padding-right: 0;
    justify-content: flex-start;
  }
  
  .timeline-dot {
    left: 20px;
  }
}

@media (max-width: 768px) {
  .hero-title {
    font-size: 42px;
  }
  
  .hero-subtitle {
    font-size: 18px;
  }
  
  .section-header h2 {
    font-size: 28px;
  }
  
  .team-grid,
  .values-grid {
    grid-template-columns: 1fr;
  }
  
  .story-number {
    font-size: 60px;
  }
}
</style>