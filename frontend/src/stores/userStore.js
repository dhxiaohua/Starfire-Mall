/**
 * 用户状态管理
 */

import { ref } from 'vue'
import { getUserInfo } from '../api/index'

export const userStore = ref({
  isLoggedIn: false,
  id: null,
  username: '',
  nickname: '',
  avatar: '',
  isAdmin: false,
  adminStatus: 'none',
  canApply: false,
  isAdminMode: false
})

// 初始化用户状态 - 使用sessionStorage支持多标签页同时登录
export const initUserStore = async () => {
  const storedUser = sessionStorage.getItem('currentUser')
  if (storedUser) {
    try {
      const user = JSON.parse(storedUser)
      userStore.value.isLoggedIn = true
      userStore.value.username = user.username || ''
      userStore.value.nickname = user.nickname || ''
      userStore.value.avatar = user.avatar || ''
      userStore.value.isAdmin = user.isAdmin || false
      userStore.value.adminStatus = user.adminStatus || 'none'
      userStore.value.canApply = user.canApply !== undefined ? user.canApply : false
      userStore.value.isAdminMode = user.isAdminMode || false
      
      // 尝试获取用户ID
      if (user.id) {
        userStore.value.id = user.id
      } else if (user.user && user.user.id) {
        // 兼容旧数据格式
        userStore.value.id = user.user.id
      } else {
        // 如果没有id，尝试从后端获取完整的用户信息
        if (user.username) {
          try {
            const userInfo = await getUserInfo(user.username)
            if (userInfo.success && userInfo.data && userInfo.data.id) {
              userStore.value.id = userInfo.data.id
              // 更新sessionStorage中的数据
              user.id = userInfo.data.id
              sessionStorage.setItem('currentUser', JSON.stringify({ ...user, id: userInfo.data.id }))
            }
          } catch (e) {
            console.error('获取用户信息失败:', e)
          }
        }
      }
      
      // 保存token
      if (user.token) {
        userStore.value.token = user.token
      }
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
  // 保存用户ID
  if (user.id) {
    userStore.value.id = user.id
  }
  // 保存token
  if (user.token) {
    userStore.value.token = user.token
  }
  // 保存完整用户数据到sessionStorage
  const userData = { 
    ...userStore.value, 
    token: user.token,
    id: user.id  // 确保保存id
  }
  sessionStorage.setItem('currentUser', JSON.stringify(userData))
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
