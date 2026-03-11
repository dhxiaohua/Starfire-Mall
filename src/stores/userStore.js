/**
 * 用户状态管理
 */

import { ref } from 'vue'

export const userStore = ref({
  isLoggedIn: false,
  username: '',
  nickname: '',
  avatar: '',
  isAdmin: false,
  adminStatus: 'none',
  canApply: false,
  isAdminMode: false
})

// 初始化用户状态 - 使用sessionStorage支持多标签页同时登录
export const initUserStore = () => {
  const storedUser = sessionStorage.getItem('currentUser')
  if (storedUser) {
    try {
      const user = JSON.parse(storedUser)
      userStore.value.isLoggedIn = true
      userStore.value.username = user.username
      userStore.value.nickname = user.nickname || ''
      userStore.value.avatar = user.avatar || ''
      userStore.value.isAdmin = user.isAdmin || false
      userStore.value.adminStatus = user.adminStatus || 'none'
      userStore.value.canApply = user.canApply !== undefined ? user.canApply : false
      userStore.value.isAdminMode = user.isAdminMode || false
    } catch (e) {
      console.error('解析用户数据失败:', e)
      sessionStorage.removeItem('currentUser')
    }
  }
}

// 设置用户登录
export const setUserLogin = (user) => {
  userStore.value.isLoggedIn = true
  userStore.value.username = user.username
  userStore.value.nickname = user.nickname || user.username
  userStore.value.avatar = user.avatar || ''
  userStore.value.isAdmin = user.isAdmin || false
  userStore.value.adminStatus = user.adminStatus || 'none'
  userStore.value.canApply = user.canApply !== undefined ? user.canApply : false
  userStore.value.isAdminMode = user.isAdminMode || false
  sessionStorage.setItem('currentUser', JSON.stringify(userStore.value))
}

// 设置用户登出
export const setUserLogout = () => {
  userStore.value.isLoggedIn = false
  userStore.value.username = ''
  userStore.value.nickname = ''
  userStore.value.avatar = ''
  userStore.value.isAdmin = false
  userStore.value.adminStatus = 'none'
  userStore.value.canApply = false
  userStore.value.isAdminMode = false
  sessionStorage.removeItem('currentUser')
}

// 切换管理员模式
export const toggleAdminMode = () => {
  if (userStore.value.isAdmin) {
    userStore.value.isAdminMode = !userStore.value.isAdminMode
    const user = JSON.parse(sessionStorage.getItem('currentUser') || '{}')
    user.isAdminMode = userStore.value.isAdminMode
    sessionStorage.setItem('currentUser', JSON.stringify(user))
  }
}

// 更新管理员状态
export const updateAdminStatus = (isAdmin, adminStatus, canApply) => {
  userStore.value.isAdmin = isAdmin
  userStore.value.adminStatus = adminStatus
  userStore.value.canApply = canApply
  const user = JSON.parse(sessionStorage.getItem('currentUser') || '{}')
  user.isAdmin = isAdmin
  user.adminStatus = adminStatus
  user.canApply = canApply
  sessionStorage.setItem('currentUser', JSON.stringify(user))
}

// 更新当前用户信息
export const updateCurrentUser = (updates) => {
  Object.assign(userStore.value, updates)
  const user = JSON.parse(sessionStorage.getItem('currentUser') || '{}')
  Object.assign(user, updates)
  sessionStorage.setItem('currentUser', JSON.stringify(user))
}
