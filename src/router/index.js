import { createRouter, createWebHistory } from 'vue-router'
import Search from '../views/Search.vue'
import Home from '../views/Home.vue'
import Login from '../views/Login.vue'
import Register from '../views/Register.vue'
import Settings from '../views/Settings.vue'
import Page1 from '../views/Page1.vue'
import Page2 from '../views/Page2.vue'
import Page3 from '../views/Page3.vue'
import Admin from '../views/Admin.vue'
import { userStore } from '../stores/userStore'

const routes = [
  {
    path: '/',
    name: 'Home',
    component: Home,
    meta: { title: '首页' }
  },
  {
    path: '/search',
    name: 'Search',
    component: Search,
    meta: { title: '搜索' }
  },
  {
    path: '/login',
    name: 'Login',
    component: Login,
    meta: { guest: true }
  },
  {
    path: '/register',
    name: 'Register',
    component: Register,
    meta: { guest: true }
  },
  {
    path: '/settings',
    name: 'Settings',
    component: Settings,
    meta: { requiresAuth: true }
  },
  {
    path: '/page1',
    name: 'Page1',
    component: Page1,
    meta: { title: '产品列表' }
  },
  {
    path: '/page2',
    name: 'Page2',
    component: Page2,
    meta: { title: '关于我们' }
  },
  {
    path: '/page3',
    name: 'Page3',
    component: Page3,
    meta: { title: '联系我们' }
  },
  {
    path: '/admin',
    name: 'Admin',
    component: Admin,
    meta: { title: '管理端', requiresAdmin: true }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  // 检查是否需要管理员权限
  if (to.meta.requiresAdmin) {
    if (!userStore.value.isLoggedIn) {
      next({ name: 'Login', query: { redirect: to.fullPath } })
      return
    }
    if (!userStore.value.isAdmin) {
      next({ name: 'Home' })
      return
    }
  }
  
  // 检查是否需要登录（个人设置页）
  if (to.meta.requiresAuth) {
    if (!userStore.value.isLoggedIn) {
      next({ name: 'Login', query: { redirect: to.fullPath } })
      return
    }
  }
  
  // 检查是否是访客（如已登录不能访问登录页）
  if (to.meta.guest) {
    if (userStore.value.isLoggedIn) {
      next({ name: 'Home' })
      return
    }
  }
  
  next()
})

export default router