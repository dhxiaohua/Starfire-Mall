<template>
  <div class="ai-customer-service">
    <WebsiteLayout currentPage="ai-service" />
    <div class="content-wrapper">
      <div class="chat-container">
        <div class="chat-header">
          <h2>🤖 AI 智能客服</h2>
          <button class="clear-btn" @click="clearContext" title="清除对话历史">
            🔄 清除上下文
          </button>
        </div>

        <div class="chat-messages" ref="messagesContainer">
          <div
            v-for="(msg, index) in messages"
            :key="index"
            class="message"
            :class="{ 'is-user': msg.isUser }"
          >
            <div class="message-avatar" :class="{ 'is-user': msg.isUser }">
              <img v-if="msg.isUser && userStore.avatar" :src="userStore.avatar" alt="用户头像" class="avatar-image" />
              <span v-else-if="msg.isUser">{{ getUserInitials() }}</span>
              <span v-else>🤖</span>
            </div>
            <div class="message-content">
              <div class="message-text">{{ msg.text }}</div>
              <div class="message-time">{{ msg.time }}</div>
            </div>
          </div>

          <div v-if="messages.length === 0" class="empty-state">
            <span class="empty-icon">💬</span>
            <p>您好！我是 AI 智能客服，有什么可以帮您的吗？</p>
          </div>
        </div>

        <div class="chat-input">
          <textarea
            ref="inputRef"
            v-model="inputMessage"
            placeholder="请输入您的问题..."
            rows="1"
            @keydown.ctrl.enter="sendMessage"
            @input="autoResize"
          ></textarea>
          <button
            class="send-btn"
            @click="sendMessage"
            :disabled="!inputMessage.trim() || isLoading"
          >
            {{ isLoading ? '...' : '发送' }}
          </button>
        </div>
      </div>
    </div>

    <!-- 客服弹窗按钮 -->
    <CustomerService />
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick, onUnmounted } from 'vue'
import { userStore } from '../stores/userStore'
import WebsiteLayout from '../components/WebsiteLayout.vue'
import CustomerService from '../components/CustomerService.vue'
import { chatAI, clearSession as clearSessionApi } from '../api'

const messages = ref([])
const inputMessage = ref('')
const isLoading = ref(false)
const sessionId = ref(null)
const inputRef = ref(null)
const messagesContainer = ref(null)

// 获取用户首字母
const getUserInitials = () => {
  const username = userStore.value?.username || ''
  return username.charAt(0).toUpperCase()
}

// 自动调整文本框高度
const autoResize = () => {
  if (inputRef.value) {
    inputRef.value.style.height = 'auto'
    inputRef.value.style.height = Math.min(inputRef.value.scrollHeight, 120) + 'px'
  }
}

// 获取当前时间
const getCurrentTime = () => {
  const now = new Date()
  return now.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })
}

// 发送消息
const sendMessage = async () => {
  const message = inputMessage.value.trim()
  if (!message || isLoading.value) return

  // 添加用户消息
  messages.value.push({
    text: message,
    time: getCurrentTime(),
    isUser: true
  })

  inputMessage.value = ''
  autoResize()
  await scrollToBottom()

  isLoading.value = true

  try {
    const result = await chatAI({
      username: userStore.value?.username || 'guest',
      sessionId: sessionId.value,
      message: message
    })

    if (result.success && result.data && result.data.reply) {
      messages.value.push({
        text: result.data.reply,
        time: getCurrentTime(),
        isUser: false
      })
      sessionId.value = result.data.sessionId
    } else {
      messages.value.push({
        text: result.error || result.message || '抱歉，我暂时无法回答您的问题。',
        time: getCurrentTime(),
        isUser: false
      })
    }
  } catch (error) {
    console.error('发送消息失败:', error)
    messages.value.push({
      text: '抱歉，系统出现错误，请稍后重试。',
      time: getCurrentTime(),
      isUser: false
    })
  }

  isLoading.value = false
  await scrollToBottom()
}

// 清除上下文
const clearContext = async () => {
  if (!confirm('确定要清除对话历史吗？')) return

  try {
    const result = await clearSessionApi({
      username: userStore.value?.username || 'guest'
    })

    if (result.success) {
      messages.value = []
      sessionId.value = result.data
      alert('对话历史已清除')
    } else {
      alert(result.message || '清除失败')
    }
  } catch (error) {
    console.error('清除上下文失败:', error)
    alert('清除失败，请重试')
  }
}

// 滚动到底部
const scrollToBottom = async () => {
  await nextTick()
  if (messagesContainer.value) {
    messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
  }
}

// 自动聚焦输入框
onMounted(() => {
  if (inputRef.value) {
    inputRef.value.focus()
  }
})

onUnmounted(() => {
  // 清理工作
})
</script>

<style scoped>
.ai-customer-service {
  min-height: 100vh;
  background: #0d1117;
}

.content-wrapper {
  max-width: 1200px;
  margin: 0 auto;
  padding: 40px 20px;
}

.chat-container {
  background: #161b22;
  border-radius: 20px;
  overflow: hidden;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
  border: 1px solid #30363d;
}

.chat-header {
  background: #21262d;
  color: #c9d1d9;
  padding: 20px 30px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-bottom: 1px solid #30363d;
}

.chat-header h2 {
  margin: 0;
  font-size: 24px;
  font-weight: 600;
  color: #ffffff;
}

.clear-btn {
  padding: 8px 16px;
  background: #21262d;
  color: #58a6ff;
  border: 1px solid #30363d;
  border-radius: 8px;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.3s ease;
}

.clear-btn:hover {
  background: #30363d;
  border-color: #58a6ff;
  transform: translateY(-1px);
}

.clear-btn:active {
  transform: translateY(0);
}

.chat-messages {
  height: 500px;
  overflow-y: auto;
  padding: 30px;
  background: #0d1117;
}

.chat-messages::-webkit-scrollbar {
  width: 8px;
}

.chat-messages::-webkit-scrollbar-track {
  background: #161b22;
}

.chat-messages::-webkit-scrollbar-thumb {
  background: #30363d;
  border-radius: 4px;
}

.chat-messages::-webkit-scrollbar-thumb:hover {
  background: #484f58;
}

.message {
  display: flex;
  margin-bottom: 20px;
  animation: slideIn 0.3s ease;
}

@keyframes slideIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.message.is-user {
  flex-direction: row-reverse;
}

.message-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: #58a6ff;
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 600;
  font-size: 16px;
  flex-shrink: 0;
}

.message-avatar.is-user {
  background: #238636;
}

.avatar-image {
  width: 100%;
  height: 100%;
  border-radius: 50%;
  object-fit: cover;
}

.message-content {
  max-width: 70%;
  margin: 0 15px;
}

.message.is-user .message-content {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
}

.message-text {
  padding: 15px 20px;
  background: #161b22;
  border-radius: 16px;
  border: 1px solid #30363d;
  color: #c9d1d9;
  line-height: 1.6;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.3);
}

.message.is-user .message-text {
  background: #238636;
  color: white;
  border: none;
  border-radius: 16px 16px 4px 16px;
}

.message-time {
  font-size: 12px;
  color: #8b949e;
  margin-top: 8px;
}

.empty-state {
  text-align: center;
  padding: 60px 20px;
  color: #8b949e;
}

.empty-icon {
  font-size: 48px;
  margin-bottom: 10px;
  display: block;
}

.chat-input {
  padding: 20px 30px;
  background: #161b22;
  border-top: 1px solid #30363d;
  display: flex;
  gap: 15px;
  align-items: flex-end;
}

.chat-input textarea {
  flex: 1;
  padding: 12px 16px;
  border: 1px solid #30363d;
  border-radius: 10px;
  resize: none;
  background: #0d1117;
  color: #c9d1d9;
  font-size: 15px;
  line-height: 1.5;
  min-height: 44px;
  max-height: 120px;
  transition: all 0.3s ease;
}

.chat-input textarea:focus {
  outline: none;
  border-color: #58a6ff;
  box-shadow: 0 0 0 3px rgba(88, 166, 255, 0.1);
}

.chat-input textarea::placeholder {
  color: #8b949e;
}

.send-btn {
  padding: 12px 30px;
  background: #238636;
  color: white;
  border: none;
  border-radius: 10px;
  cursor: pointer;
  font-size: 15px;
  font-weight: 500;
  transition: all 0.3s ease;
  white-space: nowrap;
}

.send-btn:hover:not(:disabled) {
  background: #2ea043;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(35, 134, 54, 0.4);
}

.send-btn:active:not(:disabled) {
  transform: translateY(0);
}

.send-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
  background: #484f58;
}

@media (max-width: 768px) {
  .content-wrapper {
    padding: 20px 10px;
  }

  .chat-messages {
    height: 400px;
    padding: 20px 15px;
  }

  .message-content {
    max-width: 85%;
  }
}
</style>