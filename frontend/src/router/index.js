import { createRouter, createWebHistory } from 'vue-router'
import Search from '../views/Search.vue'
import Home from '../views/Home.vue'
import Settings from '../views/Settings.vue'
import Products from '../views/Products.vue'
import About from '../views/About.vue'
import Contact from '../views/Contact.vue'
import Admin from '../views/Admin.vue'
import UserOrders from '../views/UserOrders.vue'
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
    path: '/settings',
    name: 'Settings',
    component: Settings,
    meta: { requiresAuth: true }
  },
  {
    path: '/products',
    name: 'Products',
    component: Products,
    meta: { title: '产品列表' }
  },
  {
    path: '/about',
    name: 'About',
    component: About,
    meta: { title: '关于我们' }
  },
  {
    path: '/contact',
    name: 'Contact',
    component: Contact,
    meta: { title: '联系我们' }
  },
  {
    path: '/admin',
    name: 'Admin',
    component: Admin,
    meta: { title: '管理端', requiresAdmin: true }
  },
  {
    path: '/orders',
    name: 'UserOrders',
    component: UserOrders,
    meta: { title: '我的订单', requiresAuth: true }
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