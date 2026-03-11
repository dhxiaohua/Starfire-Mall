<template>
  <WebsiteLayout current-page="contact">
    <div class="page-header">
      <h1>页面六</h1>
      <p>欢迎来到页面六</p>
    </div>
    
    <div class="page-content">
      <div class="content-card" v-for="i in 4" :key="i">
        <div class="card-icon">{{ i }}</div>
        <h3>功能模块 {{ i }}</h3>
        <p>这里可以放置自定义内容，可以是文章、图片、商品或其他任何内容。</p>
      </div>
    </div>
  </WebsiteLayout>
</template>

<script>
import WebsiteLayout from '../components/WebsiteLayout.vue'

export default {
  name: 'Page5',
  components: {
    WebsiteLayout
  }
}
</script>

<style scoped>
.page-header {
  margin-bottom: 60px;
}

.page-header h1 {
  font-size: 36px;
  color: #333;
  margin-bottom: 10px;
}

.page-header p {
  color: #666;
  font-size: 18px;
}

.page-content {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 30px;
}

.content-card {
  background: white;
  border-radius: 12px;
  padding: 40px;
  text-align: center;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  transition: transform 0.3s;
}

.content-card:hover {
  transform: translateY(-5px);
}

.card-icon {
  width: 70px;
  height: 70px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 28px;
  font-weight: bold;
  margin: 0 auto 20px;
}

.content-card h3 {
  margin-bottom: 15px;
  color: #333;
}

.content-card p {
  color: #666;
  line-height: 1.6;
}

@media (max-width: 768px) {
  .page-content {
    grid-template-columns: 1fr;
  }
}
</style>