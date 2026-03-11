<template>
  <WebsiteLayout current-page="search">
    <div class="search-page">
      <div class="search-container">
        <h2 class="search-title">搜索游戏设备</h2>
        <div class="search-box">
          <input 
            type="text" 
            v-model="searchText" 
            placeholder="输入关键词搜索..." 
            class="search-input" 
            @keyup.enter="handleSearch"
          />
          <button class="search-btn" @click="handleSearch">搜索</button>
        </div>
        
        <div class="search-results" v-if="searchText">
          <p class="results-title">搜索结果: "{{ searchText }}"</p>
          <div class="results-grid">
            <div class="result-card" v-for="i in 4" :key="i">
              <div class="result-image">
                <span>🎮</span>
              </div>
              <div class="result-info">
                <h3>游戏设备 {{ i }}</h3>
                <p>专业级游戏设备，为您带来极致游戏体验</p>
                <span class="result-price">¥{{ (999 + i * 100).toFixed(2) }}</span>
              </div>
            </div>
          </div>
        </div>
        
        <div class="hot-searches" v-else>
          <h3>热门搜索</h3>
          <div class="hot-tags">
            <span class="hot-tag" v-for="tag in hotTags" :key="tag" @click="searchText = tag; handleSearch()">{{ tag }}</span>
          </div>
        </div>
      </div>
    </div>
  </WebsiteLayout>
</template>

<script>
import { ref } from 'vue'
import WebsiteLayout from '../components/WebsiteLayout.vue'

export default {
  name: 'Search',
  components: {
    WebsiteLayout
  },
  setup() {
    const searchText = ref('')
    const hotTags = ['电竞鼠标', '机械键盘', '游戏耳机', '游戏手柄', '显示器', '游戏椅']
    
    const handleSearch = () => {
      if (searchText.value.trim()) {
        console.log('搜索:', searchText.value)
      }
    }
    
    return {
      searchText,
      hotTags,
      handleSearch
    }
  }
}
</script>

<style scoped>
.search-page {
  min-height: calc(100vh - 64px);
  padding: 60px 20px;
  background: #0d1117;
}

.search-container {
  max-width: 900px;
  margin: 0 auto;
}

.search-title {
  text-align: center;
  color: #ffffff;
  font-size: 32px;
  margin-bottom: 40px;
}

.search-box {
  display: flex;
  background: #161b22;
  border: 1px solid #30363d;
  border-radius: 50px;
  overflow: hidden;
  margin-bottom: 40px;
}

.search-input {
  flex: 1;
  border: none;
  background: transparent;
  padding: 16px 24px;
  font-size: 16px;
  color: #ffffff;
  outline: none;
}

.search-input::placeholder {
  color: #8b949e;
}

.search-btn {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  padding: 16px 36px;
  cursor: pointer;
  font-size: 16px;
  font-weight: 500;
  transition: all 0.3s ease;
}

.search-btn:hover {
  background: linear-gradient(135deg, #5a6fd6 0%, #6a4190 100%);
}

.results-title {
  color: #8b949e;
  margin-bottom: 24px;
  font-size: 16px;
}

.results-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20px;
}

.result-card {
  display: flex;
  background: #161b22;
  border: 1px solid #30363d;
  border-radius: 12px;
  overflow: hidden;
  transition: all 0.3s ease;
}

.result-card:hover {
  border-color: #667eea;
  transform: translateY(-4px);
}

.result-image {
  width: 120px;
  background: #21262d;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 40px;
}

.result-info {
  padding: 20px;
  flex: 1;
}

.result-info h3 {
  color: #ffffff;
  font-size: 18px;
  margin-bottom: 8px;
}

.result-info p {
  color: #8b949e;
  font-size: 14px;
  margin-bottom: 12px;
}

.result-price {
  color: #667eea;
  font-size: 20px;
  font-weight: 600;
}

.hot-searches {
  text-align: center;
}

.hot-searches h3 {
  color: #ffffff;
  font-size: 20px;
  margin-bottom: 20px;
}

.hot-tags {
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
  gap: 12px;
}

.hot-tag {
  background: #161b22;
  border: 1px solid #30363d;
  color: #8b949e;
  padding: 10px 20px;
  border-radius: 20px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.hot-tag:hover {
  background: #667eea;
  color: #ffffff;
  border-color: #667eea;
}

@media (max-width: 768px) {
  .results-grid {
    grid-template-columns: 1fr;
  }
}
</style>