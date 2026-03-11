import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import { initUserStore } from './stores/userStore'

const app = createApp(App)

// 初始化用户状态
initUserStore()

app.use(router)
app.mount('#app')