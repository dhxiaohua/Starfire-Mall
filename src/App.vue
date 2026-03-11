<template>
  <div id="app">
    <router-view v-slot="{ Component }">
      <transition name="fade" mode="out-in">
        <component :is="Component" />
      </transition>
    </router-view>
    
    <!-- 客服悬浮按钮 - 在所有页面显示 -->
    <CustomerService v-if="showCustomerService" />
  </div>
</template>

<script>
import { computed } from 'vue'
import CustomerService from './components/CustomerService.vue'
import { userStore } from './stores/userStore'

export default {
  name: 'App',
  components: {
    CustomerService
  },
  setup() {
    // 仅普通用户显示客服按钮
    const showCustomerService = computed(() => {
      return userStore.value.isLoggedIn && !userStore.value.isAdmin && !userStore.value.isAdminMode
    })
    
    return {
      showCustomerService
    }
  }
}
</script>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

html, body {
  overflow-x: hidden;
  background: #0d0d0d;
}

#app {
  min-height: 100vh;
  overflow-x: hidden;
  background: #0d0d0d;
}

/* 页面切换淡入淡出效果 */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.25s ease;
}

.fade-enter-from {
  opacity: 0;
  transform: translateX(20px);
}

.fade-leave-to {
  opacity: 0;
  transform: translateX(-20px);
}
</style>
