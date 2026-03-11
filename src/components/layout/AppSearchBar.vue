<template>
  <div class="search-bar">
    <div class="search-wrapper">
      <input 
        type="text" 
        v-model="searchText" 
        placeholder="搜索内容..." 
        class="search-input"
        @keyup.enter="handleSearch"
      />
      <button class="search-btn" @click="handleSearch">搜索</button>
    </div>
  </div>
</template>

<script>
import { ref } from 'vue'

export default {
  name: 'AppSearchBar',
  setup() {
    const searchText = ref('')

    const handleSearch = () => {
      if (searchText.value.trim()) {
        console.log('搜索:', searchText.value)
        // 可以添加实际的搜索逻辑
      }
    }

    return {
      searchText,
      handleSearch
    }
  }
}
</script>

<style scoped>
.search-bar {
  position: fixed;
  top: 70px;
  left: 0;
  width: 100%;
  padding: 15px 0;
  background: rgba(191, 219, 254, 0.95);
  backdrop-filter: blur(20px);
  z-index: 999;
}

.search-wrapper {
  max-width: 600px;
  margin: 0 auto;
  display: flex;
  background: rgba(255, 255, 255, 0.9);
  border-radius: 50px;
  padding: 5px;
  box-shadow: 0 5px 20px rgba(30, 64, 175, 0.1);
}

.search-input {
  flex: 1;
  border: none;
  background: transparent;
  padding: 12px 20px;
  font-size: 15px;
  outline: none;
}

.search-btn {
  background: #1e40af;
  color: white;
  border: none;
  padding: 12px 25px;
  border-radius: 25px;
  cursor: pointer;
  font-size: 14px;
  transition: transform 0.3s;
}

.search-btn:hover {
  transform: scale(1.05);
}
</style>
