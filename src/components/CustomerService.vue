<template>
  <div class="customer-service-wrapper">
    <!-- 聊天弹窗 -->
    <transition name="slide-up">
      <div class="chat-panel" v-if="isExpanded">
        <div class="chat-header">
          <div class="chat-title">
            <span class="chat-icon">🎧</span>
            <span>客服中心</span>
          </div>
          <button class="close-btn" @click="isExpanded = false">×</button>
        </div>

        <div class="chat-messages" ref="messagesContainer">
          <div v-if="messages.length === 0" class="empty-chat">
            <span class="empty-icon">💬</span>
            <p>暂无消息，开始对话吧</p>
          </div>
          <div 
            v-for="msg in messages" 
            :key="msg.id" 
            class="message-item"
            :class="{ 'is-mine': msg.isMine }"
          >
            <!-- 客服消息：头像在左，内容在右 -->
            <template v-if="!msg.isMine">
              <div class="message-avatar customer">
                <span>服</span>
              </div>
              <div class="message-content">
                <div class="message-text">{{ msg.content }}</div>
                <div class="message-time">{{ msg.time }}</div>
              </div>
            </template>
            <!-- 用户消息：内容在左，头像在右 -->
            <template v-else>
              <div class="message-content">
                <div class="message-text">{{ msg.content }}</div>
                <div class="message-time">{{ msg.time }}</div>
              </div>
              <div class="message-avatar user">
                <img v-if="userStore.avatar" :src="userStore.avatar" alt="头像" />
                <span v-else>{{ userStore.username?.charAt(0).toUpperCase() }}</span>
              </div>
            </template>
          </div>
        </div>

        <div class="chat-input-area">
          <textarea 
            v-model="inputMessage" 
            placeholder="请输入您的问题..." 
            @keydown.enter.exact.prevent="sendMessage"
            rows="2"
          ></textarea>
          <button class="send-btn" @click="sendMessage" :disabled="!inputMessage.trim()">
            发送
          </button>
        </div>
      </div>
    </transition>

    <!-- 悬浮按钮 - 始终可见 -->
    <button class="service-toggle-btn" @click="toggleChat" :class="{ expanded: isExpanded }">
      <span class="btn-icon">{{ isExpanded ? '❌' : '💬' }}</span>
    </button>
  </div>
</template>

<script>
import { ref, nextTick, onMounted, onUnmounted, watch } from 'vue'
import { userStore } from '../stores/userStore'
import { getCustomerMessages, sendCustomerMessage } from '../api'

export default {
  name: 'CustomerService',
  setup() {
    const isExpanded = ref(false)
    const inputMessage = ref('')
    const messages = ref([])
    let messageRefreshTimer = null  // 消息自动刷新定时器
    const messagesContainer = ref(null)

    // 加载历史消息
    const loadMessages = async () => {
      if (!userStore.value.isLoggedIn) return
      
      const result = await getCustomerMessages(userStore.value.username)
      if (result.success) {
        messages.value = result.messages || []
      }
    }

    // 发送消息
    const sendMessage = async () => {
      if (!inputMessage.value.trim() || !userStore.value.isLoggedIn) return

      const content = inputMessage.value.trim()
      const now = new Date()
      const time = `${now.getHours().toString().padStart(2, '0')}:${now.getMinutes().toString().padStart(2, '0')}`

      // 先添加到本地显示
      messages.value.push({
        id: Date.now(),
        content: content,
        time: time,
        isMine: true
      })

      inputMessage.value = ''
      
      // 滚动到底部
      await nextTick()
      scrollToBottom()

      // 发送到服务器
      await sendCustomerMessage({
        username: userStore.value.username,
        content: content
      })

      // 模拟客服回复
      setTimeout(() => {
        const replyTime = `${now.getHours().toString().padStart(2, '0')}:${now.getMinutes().toString().padStart(2, '0')}`
        messages.value.push({
          id: Date.now() + 1,
          content: '感谢您的消息！我们的客服人员会尽快回复您。请保持在线，也可以拨打客服热线：400-123-4567',
          time: replyTime,
          isMine: false
        })
        nextTick(() => scrollToBottom())
      }, 1000)
    }

    // 滚动到底部
    const scrollToBottom = () => {
      if (messagesContainer.value) {
        messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
      }
    }

    // 切换聊天面板
    const toggleChat = () => {
      isExpanded.value = !isExpanded.value
      if (isExpanded.value) {
        loadMessages()
      }
    }

    onMounted(() => {
      if (userStore.value.isLoggedIn) {
        loadMessages()
        // 自动刷新消息，每秒刷新一次
        messageRefreshTimer = setInterval(() => {
          if (userStore.value.isLoggedIn) {
            loadMessages()
          }
        }, 1000)
      }
    })

    onUnmounted(() => {
      // 清除定时器
      if (messageRefreshTimer) {
        clearInterval(messageRefreshTimer)
      }
    })

    return {
      isExpanded,
      inputMessage,
      messages,
      messagesContainer,
      userStore,
      sendMessage,
      toggleChat
    }
  }
}
</script>

<style scoped>
.customer-service-wrapper {
  position: fixed;
  bottom: 30px;
  left: 30px;
  z-index: 99999;
}

.service-toggle-btn {
  width: 56px;
  height: 56px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  border-radius: 50%;
  color: white;
  cursor: pointer;
  box-shadow: 0 4px 20px rgba(102, 126, 234, 0.5);
  transition: all 0.3s ease;
}

.service-toggle-btn:hover {
  transform: scale(1.1);
  box-shadow: 0 6px 30px rgba(102, 126, 234, 0.7);
}

.service-toggle-btn.expanded {
  border-radius: 50%;
  background: linear-gradient(135deg, #f5576c 0%, #f093fb 100%);
}

.btn-icon {
  font-size: 24px;
}

/* 聊天面板 */
.chat-panel {
  position: absolute;
  bottom: 60px;
  left: 0;
  width: 380px;
  height: 500px;
  background: #1a1a2e;
  border-radius: 16px 16px 0 0;
  box-shadow: 0 -4px 30px rgba(0, 0, 0, 0.3);
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.chat-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.chat-title {
  display: flex;
  align-items: center;
  gap: 10px;
  color: white;
  font-weight: 600;
  font-size: 16px;
}

.chat-icon {
  font-size: 20px;
}

.close-btn {
  width: 28px;
  height: 28px;
  background: rgba(255, 255, 255, 0.2);
  border: none;
  border-radius: 50%;
  color: white;
  font-size: 18px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: background 0.2s;
}

.close-btn:hover {
  background: rgba(255, 255, 255, 0.3);
}

.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: 16px;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.empty-chat {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #8b949e;
}

.empty-icon {
  font-size: 48px;
  margin-bottom: 12px;
}

.message-item {
  display: flex;
  gap: 10px;
  max-width: 85%;
  margin-bottom: 12px;
}

.message-item.is-mine {
  justify-content: flex-end;
}

.message-avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 14px;
  font-weight: 600;
  flex-shrink: 0;
  overflow: hidden;
}

.message-avatar.customer {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.message-avatar.user {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
}

.message-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.message-content {
  background: #252540;
  padding: 12px 16px;
  border-radius: 16px;
  border-top-left-radius: 4px;
}

.message-item.is-mine .message-content {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-top-left-radius: 16px;
  border-top-right-radius: 4px;
}

.message-text {
  color: #e0e0e0;
  font-size: 14px;
  line-height: 1.5;
  word-break: break-word;
}

.message-time {
  color: #8b949e;
  font-size: 11px;
  margin-top: 6px;
}

.message-item.is-mine .message-time {
  color: rgba(255, 255, 255, 0.7);
  text-align: right;
}

.chat-input-area {
  padding: 16px;
  background: #16162a;
  border-top: 1px solid #2a2a4a;
}

.chat-input-area textarea {
  width: 100%;
  padding: 12px;
  background: #252540;
  border: 1px solid #3a3a5a;
  border-radius: 8px;
  color: #e0e0e0;
  font-size: 14px;
  resize: none;
  margin-bottom: 12px;
}

.chat-input-area textarea:focus {
  outline: none;
  border-color: #667eea;
}

.chat-input-area textarea::placeholder {
  color: #6b7280;
}

.send-btn {
  width: 100%;
  padding: 12px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  border-radius: 8px;
  color: white;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: opacity 0.2s;
}

.send-btn:hover:not(:disabled) {
  opacity: 0.9;
}

.send-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

/* 过渡动画 */
.slide-up-enter-active,
.slide-up-leave-active {
  transition: all 0.3s ease;
}

.slide-up-enter-from,
.slide-up-leave-to {
  opacity: 0;
  transform: translateY(20px);
}
</style>
