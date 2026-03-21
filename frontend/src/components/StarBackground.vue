<template>
  <canvas ref="canvas" class="star-canvas"></canvas>
</template>

<script>
import { ref, onMounted, onUnmounted } from 'vue'

export default {
  name: 'StarBackground',
  setup() {
    const canvas = ref(null)
    let ctx = null
    let animationId = null
    let particles = []
    
    const initParticles = () => {
      const cvs = canvas.value
      if (!cvs) return
      
      cvs.width = window.innerWidth
      cvs.height = window.innerHeight
      
      const particleCount = Math.floor((cvs.width * cvs.height) / 8000)
      particles = []
      
      for (let i = 0; i < particleCount; i++) {
        particles.push({
          x: Math.random() * cvs.width,
          y: Math.random() * cvs.height,
          radius: Math.random() * 1.5 + 0.5,
          alpha: Math.random() * 0.8 + 0.2,
          speed: Math.random() * 0.3 + 0.1,
          direction: Math.random() > 0.5 ? 1 : -1
        })
      }
    }

    const animate = () => {
      const cvs = canvas.value
      if (!cvs) return
      
      ctx = cvs.getContext('2d')
      ctx.clearRect(0, 0, cvs.width, cvs.height)
      
      // 绘制星空背景渐变
      const gradient = ctx.createRadialGradient(
        cvs.width / 2, cvs.height / 2, 0,
        cvs.width / 2, cvs.height / 2, cvs.width
      )
      gradient.addColorStop(0, '#0a0a1a')
      gradient.addColorStop(0.5, '#0d1117')
      gradient.addColorStop(1, '#161b22')
      ctx.fillStyle = gradient
      ctx.fillRect(0, 0, cvs.width, cvs.height)
      
      // 绘制星星
      particles.forEach(p => {
        // 更新位置
        p.y += p.speed * p.direction
        if (p.y < 0) p.y = cvs.height
        if (p.y > cvs.height) p.y = 0
        
        // 闪烁效果
        p.alpha += Math.random() * 0.02 - 0.01
        if (p.alpha > 1) p.alpha = 1
        if (p.alpha < 0.2) p.alpha = 0.2
        
        // 绘制星星
        ctx.beginPath()
        ctx.arc(p.x, p.y, p.radius, 0, Math.PI * 2)
        ctx.fillStyle = `rgba(255, 255, 255, ${p.alpha})`
        ctx.fill()
        
        // 添加光晕
        if (p.radius > 1) {
          ctx.beginPath()
          ctx.arc(p.x, p.y, p.radius * 2, 0, Math.PI * 2)
          const glowGradient = ctx.createRadialGradient(p.x, p.y, 0, p.x, p.y, p.radius * 2)
          glowGradient.addColorStop(0, `rgba(100, 150, 255, ${p.alpha * 0.3})`)
          glowGradient.addColorStop(1, 'transparent')
          ctx.fillStyle = glowGradient
          ctx.fill()
        }
      })
      
      animationId = requestAnimationFrame(animate)
    }

    const handleResize = () => {
      initParticles()
    }

    onMounted(() => {
      initParticles()
      animate()
      window.addEventListener('resize', handleResize)
    })

    onUnmounted(() => {
      if (animationId) {
        cancelAnimationFrame(animationId)
      }
      window.removeEventListener('resize', handleResize)
    })

    return {
      canvas
    }
  }
}
</script>

<style scoped>
.star-canvas {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: -1;
  pointer-events: none;
}
</style>
