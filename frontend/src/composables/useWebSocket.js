import { ref, onMounted, onUnmounted } from 'vue'

export function useWebSocket(username) {
  const socket = ref(null)
  const connected = ref(false)
  const messages = ref([])
  const notifications = ref([])
  
  const connect = () => {
    if (!username) return
    
    const wsUrl = `ws://localhost:8080/ws/notifications?username=${encodeURIComponent(username)}`
    socket.value = new WebSocket(wsUrl)
    
    socket.value.onopen = () => {
      console.log('WebSocket连接已建立')
      connected.value = true
    }
    
    socket.value.onmessage = (event) => {
      try {
        const data = JSON.parse(event.data)
        messages.value.push(data)
        
        // 触发自定义事件通知
        window.dispatchEvent(new CustomEvent('websocket-notification', { detail: data }))
        
        // 对于重要通知，显示浏览器通知
        if (data.type === 'new_admin_request' || data.type === 'request_approved' || data.type === 'request_rejected') {
          notifications.value.push(data)
          
          // 显示浏览器通知
          if ('Notification' in window && Notification.permission === 'granted') {
            new Notification('星火商城通知', {
              body: data.message,
              icon: '/images/logo.jpg'
            })
          }
        }
      } catch (e) {
        console.error('解析WebSocket消息失败:', e)
      }
    }
    
    socket.value.onclose = () => {
      console.log('WebSocket连接已关闭')
      connected.value = false
      
      // 5秒后自动重连
      setTimeout(() => {
        if (username) {
          connect()
        }
      }, 5000)
    }
    
    socket.value.onerror = (error) => {
      console.error('WebSocket错误:', error)
    }
  }
  
  const disconnect = () => {
    if (socket.value) {
      socket.value.close()
      socket.value = null
      connected.value = false
    }
  }
  
  const sendMessage = (message) => {
    if (socket.value && connected.value) {
      socket.value.send(JSON.stringify(message))
    }
  }
  
  const clearNotifications = () => {
    notifications.value = []
  }
  
  // 请求浏览器通知权限
  const requestNotificationPermission = async () => {
    if ('Notification' in window) {
      if (Notification.permission === 'default') {
        await Notification.requestPermission()
      }
    }
  }
  
  onMounted(() => {
    requestNotificationPermission()
    connect()
  })
  
  onUnmounted(() => {
    disconnect()
  })
  
  return {
    connected,
    messages,
    notifications,
    connect,
    disconnect,
    sendMessage,
    clearNotifications
  }
}
