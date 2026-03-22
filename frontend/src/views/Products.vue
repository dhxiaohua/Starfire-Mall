<template>
  <WebsiteLayout current-page="product">
    <!-- 广告轮播图 -->
    <div class="ad-carousel">
      <div class="carousel-track" :style="{ transform: `translateX(-${adCurrentSlide * 100}%)` }">
        <div class="carousel-slide" v-for="(ad, index) in adBanners" :key="index">
          <div class="slide-wrapper">
            <div class="slide-image">
              <img :src="ad.image" :alt="ad.title" />
            </div>
            <div class="slide-content">
              <h2>{{ ad.title }}</h2>
              <p>{{ ad.desc }}</p>
              <button class="slide-btn" @click="goToCategory(ad.category)">立即选购</button>
            </div>
          </div>
        </div>
      </div>
      <div class="carousel-indicators">
        <span
          v-for="(ad, index) in adBanners"
          :key="index"
          class="indicator"
          :class="{ active: adCurrentSlide === index }"
          @click="handleIndicatorClick(index)"
        ></span>
      </div>
    </div>

    <!-- 分类标签 -->
    <div class="category-tabs">
      <div 
        class="tab-item" 
        :class="{ active: activeCategory === 'all' }"
        @click="activeCategory = 'all'"
      >
        <span class="tab-icon">🎮</span>
        <span>全部</span>
      </div>
      <div 
        class="tab-item" 
        :class="{ active: activeCategory === 'mouse' }"
        @click="activeCategory = 'mouse'"
      >
        <span class="tab-icon">🖱️</span>
        <span>电竞鼠标</span>
      </div>
      <div 
        class="tab-item" 
        :class="{ active: activeCategory === 'keyboard' }"
        @click="activeCategory = 'keyboard'"
      >
        <span class="tab-icon">⌨️</span>
        <span>机械键盘</span>
      </div>
      <div 
        class="tab-item" 
        :class="{ active: activeCategory === 'headset' }"
        @click="activeCategory = 'headset'"
      >
        <span class="tab-icon">🎧</span>
        <span>游戏耳机</span>
      </div>
      <div 
        class="tab-item" 
        :class="{ active: activeCategory === 'controller' }"
        @click="activeCategory = 'controller'"
      >
        <span class="tab-icon">🎮</span>
        <span>游戏手柄</span>
      </div>
    </div>

    <!-- 推荐标题 -->
    <div class="section-header" v-if="activeCategory === 'all'">
      <h2>热门推荐</h2>
      <p>精选游戏装备，为你带来极致游戏体验</p>
    </div>

    <!-- 商品列表 -->
    <div class="products-grid">
      <div 
        class="product-card" 
        v-for="product in filteredProducts" 
        :key="product.id"
        @click="openProductDetail(product)"
      >
        <div class="product-image">
          <img :src="product.image" :alt="product.name" />
          <div class="product-overlay">
            <span class="view-detail">查看详情</span>
          </div>
        </div>
        <div class="product-info">
          <h3>{{ product.name }}</h3>
          <p class="product-desc">{{ product.description }}</p>
          <div class="product-price">
            <span class="price">￥{{ product.price.toFixed(2) }}</span>
          </div>
          <div class="product-actions">
            <button class="buy-btn" @click.stop="buyNow(product)">立即购买</button>
            <button class="cart-btn" @click.stop="addToCart(product, $event)">
              <svg viewBox="0 0 20 20" fill="currentColor">
                <path d="M3 1a1 1 0 000 2h1.22l.305 1.222a.997.997 0 00.01.042l1.358 5.43-.893.892C3.74 11.846 4.632 14 6.414 14H15a1 1 0 000-2H6.414l1-1H14a1 1 0 00.894-.553l3-6A1 1 0 0017 3H6.28l-.31-1.243A1 1 0 005 1H3zM16 16.5a1.5 1.5 0 11-3 0 1.5 1.5 0 013 0zM6.5 18a1.5 1.5 0 100-3 1.5 1.5 0 000 3z" />
              </svg>
              加入购物车
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- 购物车悬浮按钮 -->
    <div class="cart-float" @click="showCart = !showCart">
      <svg viewBox="0 0 20 20" fill="currentColor">
        <path d="M3 1a1 1 0 000 2h1.22l.305 1.222a.997.997 0 00.01.042l1.358 5.43-.893.892C3.74 11.846 4.632 14 6.414 14H15a1 1 0 000-2H6.414l1-1H14a1 1 0 00.894-.553l3-6A1 1 0 0017 3H6.28l-.31-1.243A1 1 0 005 1H3zM16 16.5a1.5 1.5 0 11-3 0 1.5 1.5 0 013 0zM6.5 18a1.5 1.5 0 100-3 1.5 1.5 0 000 3z" />
      </svg>
      <span class="cart-badge" v-if="cartItems.length > 0">{{ cartItems.length }}</span>
    </div>

    <!-- 购物车面板 -->
    <div class="cart-panel" :class="{ open: showCart }">
      <div class="cart-header">
        <h3>购物车</h3>
        <button class="close-cart" @click="showCart = false">
          <svg viewBox="0 0 20 20" fill="currentColor">
            <path fill-rule="evenodd" d="M4.293 4.293a1 1 0 011.414 0L10 8.586l4.293-4.293a1 1 0 111.414 1.414L11.414 10l4.293 4.293a1 1 0 01-1.414 1.414L10 11.414l-4.293 4.293a1 1 0 01-1.414-1.414L8.586 10 4.293 5.707a1 1 0 010-1.414z" clip-rule="evenodd" />
          </svg>
        </button>
      </div>
      
      <div class="cart-items" v-if="cartItems.length > 0">
        <div class="cart-item" v-for="(item, index) in cartItems" :key="index">
          <input 
            type="checkbox" 
            :checked="item.selected" 
            @change="toggleCartItem(index)"
            class="item-checkbox"
          />
          <img :src="item.image" :alt="item.name" class="item-image" />
          <div class="item-info">
            <h4>{{ item.name }}</h4>
            <span class="item-price">￥{{ item.price.toFixed(2) }}</span>
          </div>
          <button class="remove-item" @click="removeFromCart(index)">
            <svg viewBox="0 0 20 20" fill="currentColor">
              <path fill-rule="evenodd" d="M9 2a1 1 0 00-.894.553L7.382 4H4a1 1 0 000 2v10a2 2 0 002 2h8a2 2 0 002-2V6a1 1 0 100-2h-3.382l-.724-1.447A1 1 0 0011 2H9zM7 8a1 1 0 012 0v6a1 1 0 11-2 0V8zm5-1a1 1 0 00-1 1v6a1 1 0 102 0V8a1 1 0 00-1-1z" clip-rule="evenodd" />
            </svg>
          </button>
        </div>
      </div>
      
      <div class="cart-empty" v-else>
        <span>🛒</span>
        <p>购物车是空的</p>
      </div>
      
      <div class="cart-footer" v-if="cartItems.length > 0">
        <div class="cart-total">
          <span>总计：</span>
          <span class="total-price">￥{{ cartTotal.toFixed(2) }}</span>
        </div>
        <button class="checkout-btn" @click="checkout">结算</button>
      </div>
    </div>

    <!-- 商品详情弹窗 -->
    <div class="product-modal" v-if="selectedProduct" @click.self="closeProductDetail">
      <div class="modal-content">
        <button class="close-modal" @click="closeProductDetail">
          <svg viewBox="0 0 20 20" fill="currentColor">
            <path fill-rule="evenodd" d="M4.293 4.293a1 1 0 011.414 0L10 8.586l4.293-4.293a1 1 0 111.414 1.414L11.414 10l4.293 4.293a1 1 0 01-1.414 1.414L10 11.414l-4.293 4.293a1 1 0 01-1.414-1.414L8.586 10 4.293 5.707a1 1 0 010-1.414z" clip-rule="evenodd" />
          </svg>
        </button>
        
        <div class="modal-product">
          <!-- 商品图片轮播 -->
          <div class="modal-gallery">
            <div class="gallery-main" @click="openImageViewer">
              <img :src="currentGalleryImage" :alt="selectedProduct.name" />
              <div class="discount-badge" v-if="selectedProduct.originalPrice && selectedProduct.originalPrice > selectedProduct.price">
                {{ Math.round((1 - selectedProduct.price / selectedProduct.originalPrice) * 100) }}% OFF
              </div>
              <div class="view-hint">
                <svg viewBox="0 0 20 20" fill="currentColor">
                  <path d="M10 12a2 2 0 100-4 2 2 0 000 4z" />
                  <path fill-rule="evenodd" d="M.458 10C1.732 5.943 5.522 3 10 3s8.268 2.943 9.542 7c-1.274 4.057-5.064 7-9.542 7S1.732 14.057.458 10zM14 10a4 4 0 11-8 0 4 4 0 018 0z" clip-rule="evenodd" />
                </svg>
                点击查看大图
              </div>
            </div>
            <div class="gallery-thumbs" v-if="productImages.length > 1">
              <div 
                v-for="(img, i) in productImages" 
                :key="i"
                class="thumb-item"
                :class="{ active: currentGalleryIndex === i }"
                @click="currentGalleryIndex = i"
              >
                <img :src="img" :alt="`${selectedProduct.name} ${i + 1}`" />
              </div>
            </div>
          </div>
          
          <div class="modal-info">
            <!-- 商品分类标签 -->
            <div class="product-category">
              <span class="category-tag">{{ getCategoryName(selectedProduct.category) }}</span>
            </div>
            
            <h2>{{ selectedProduct.name }}</h2>
            
            <!-- 评分和销量 -->
            <div class="product-stats">
              <div class="rating-box">
                <div class="stars">
                  <span v-for="n in 5" :key="n" :class="{ filled: n <= Math.round(selectedProduct.rating || 4.5) }">★</span>
                </div>
                <span class="rating-value">{{ selectedProduct.rating || 4.5 }}</span>
                <span class="review-count">({{ selectedProduct.reviewCount || 0 }} 条评价)</span>
              </div>
              <div class="sales-info">
                <span>已售 {{ selectedProduct.sales || 0 }} 件</span>
              </div>
            </div>
            
            <p class="modal-desc">{{ selectedProduct.description }}</p>
            
            <!-- 价格信息 -->
            <div class="modal-price">
              <span class="current-price">￥{{ selectedProduct.price.toFixed(2) }}</span>
              <span class="original-price" v-if="selectedProduct.originalPrice">
                ￥{{ selectedProduct.originalPrice.toFixed(2) }}
              </span>
              <span class="save-money" v-if="selectedProduct.originalPrice && selectedProduct.originalPrice > selectedProduct.price">
                省 ￥{{ (selectedProduct.originalPrice - selectedProduct.price).toFixed(2) }}
              </span>
            </div>
            
            <!-- 库存信息 -->
            <div class="stock-info">
              <span class="stock-label">库存：</span>
              <span :class="{ 'low-stock': selectedProduct.stock && selectedProduct.stock < 10, 'out-of-stock': selectedProduct.stock === 0 }">
                {{ selectedProduct.stock === 0 ? '暂时缺货' : selectedProduct.stock + ' 件' }}
              </span>
            </div>
            
            <!-- 商品特性 -->
            <div class="modal-features" v-if="selectedProduct.features && selectedProduct.features.length > 0">
              <span class="feature" v-for="(feat, i) in selectedProduct.features" :key="i">
                {{ feat }}
              </span>
            </div>
            
            <!-- 上架时间 -->
            <div class="product-date" v-if="selectedProduct.createTime">
              <span>上架时间：{{ formatDate(selectedProduct.createTime) }}</span>
            </div>
            
            <!-- 购买按钮 -->
            <div class="modal-actions">
              <button 
                class="buy-now-btn" 
                @click="buyNow(selectedProduct)"
                :disabled="selectedProduct.stock === 0"
              >
                {{ selectedProduct.stock === 0 ? '暂时缺货' : '立即购买' }}
              </button>
              <button 
                class="add-cart-btn" 
                @click="addToCart(selectedProduct)"
                :disabled="selectedProduct.stock === 0"
              >
                <svg viewBox="0 0 20 20" fill="currentColor">
                  <path d="M3 1a1 1 0 000 2h1.22l.305 1.222a.997.997 0 00.01.042l1.358 5.43-.893.892C3.74 11.846 4.632 14 6.414 14H15a1 1 0 000-2H6.414l1-1H14a1 1 0 00.894-.553l3-6A1 1 0 0017 3H6.28l-.31-1.243A1 1 0 005 1H3zM16 16.5a1.5 1.5 0 11-3 0 1.5 1.5 0 013 0zM6.5 18a1.5 1.5 0 100-3 1.5 1.5 0 000 3z" />
                </svg>
                {{ selectedProduct.stock === 0 ? '暂时缺货' : '加入购物车' }}
              </button>
            </div>
          </div>
        </div>
        
        <!-- 商品详细信息 -->
        <div class="modal-details">
          <div class="detail-tabs">
            <div 
              class="detail-tab" 
              :class="{ active: activeDetailTab === 'desc' }"
              @click="activeDetailTab = 'desc'"
            >
              商品详情
            </div>
            <div 
              class="detail-tab" 
              :class="{ active: activeDetailTab === 'specs' }"
              @click="activeDetailTab = 'specs'"
            >
              规格参数
            </div>
            <div 
              class="detail-tab" 
              :class="{ active: activeDetailTab === 'reviews' }"
              @click="activeDetailTab = 'reviews'"
            >
              用户评价 ({{ selectedProduct.reviewCount || 0 }})
            </div>
          </div>
          
          <!-- 商品详情内容 -->
          <div class="detail-content" v-if="activeDetailTab === 'desc'">
            <div class="desc-content">
              <h3>{{ selectedProduct.name }}</h3>
              <p>{{ selectedProduct.description }}</p>
              <div class="detail-features" v-if="selectedProduct.features && selectedProduct.features.length > 0">
                <h4>产品特点：</h4>
                <ul>
                  <li v-for="(feat, i) in selectedProduct.features" :key="i">{{ feat }}</li>
                </ul>
              </div>
            </div>
          </div>
          
          <!-- 规格参数 -->
          <div class="detail-content" v-if="activeDetailTab === 'specs'">
            <div class="specs-table">
              <div class="spec-row">
                <span class="spec-label">商品名称</span>
                <span class="spec-value">{{ selectedProduct.name }}</span>
              </div>
              <div class="spec-row">
                <span class="spec-label">商品分类</span>
                <span class="spec-value">{{ getCategoryName(selectedProduct.category) }}</span>
              </div>
              <div class="spec-row">
                <span class="spec-label">商品价格</span>
                <span class="spec-value">￥{{ selectedProduct.price.toFixed(2) }}</span>
              </div>
              <div class="spec-row" v-if="selectedProduct.originalPrice">
                <span class="spec-label">原价</span>
                <span class="spec-value">￥{{ selectedProduct.originalPrice.toFixed(2) }}</span>
              </div>
              <div class="spec-row">
                <span class="spec-label">库存</span>
                <span class="spec-value">{{ selectedProduct.stock || 0 }} 件</span>
              </div>
              <div class="spec-row">
                <span class="spec-label">销量</span>
                <span class="spec-value">{{ selectedProduct.sales || 0 }} 件</span>
              </div>
              <div class="spec-row">
                <span class="spec-label">评分</span>
                <span class="spec-value">{{ selectedProduct.rating || 4.5 }} 分</span>
              </div>
              <div class="spec-row">
                <span class="spec-label">评价数</span>
                <span class="spec-value">{{ selectedProduct.reviewCount || 0 }} 条</span>
              </div>
              <div class="spec-row" v-if="selectedProduct.createTime">
                <span class="spec-label">上架时间</span>
                <span class="spec-value">{{ formatDate(selectedProduct.createTime) }}</span>
              </div>
            </div>
          </div>
          
          <!-- 用户评价 -->
          <div class="detail-content" v-if="activeDetailTab === 'reviews'">
            <div class="reviews-summary">
              <div class="summary-rating">
                <div class="rating-score">{{ selectedProduct.rating || 4.5 }}</div>
                <div class="rating-stars">
                  <span v-for="n in 5" :key="n" :class="{ filled: n <= Math.round(selectedProduct.rating || 4.5) }">★</span>
                </div>
                <div class="rating-total">{{ selectedProduct.reviewCount || 0 }} 条评价</div>
              </div>
            </div>
            
            <!-- 评论表单 -->
            <div class="review-form">
              <h4>发表评价</h4>
              <div class="rating-input">
                <span>您的评分：</span>
                <div class="star-rating-input">
                  <span 
                    v-for="n in 5" 
                    :key="n" 
                    :class="{ filled: n <= newReview.rating }"
                    @click="newReview.rating = n"
                  >
                    ★
                  </span>
                </div>
              </div>
              <textarea 
                v-model="newReview.content"
                class="review-textarea"
                placeholder="请输入您的评价内容..."
                rows="4"
                maxlength="500"
              ></textarea>
              <div class="review-actions">
                <span class="char-count">{{ newReview.content.length }}/500</span>
                <button class="submit-review-btn" @click="submitReview" :disabled="!newReview.rating || !newReview.content.trim()">
                  提交评价
                </button>
              </div>
            </div>
            
            <!-- 评论列表 -->
            <div class="reviews-list">
              <div class="review-item" v-for="(review, i) in selectedProduct.reviews" :key="i">
                <div class="review-header">
                  <span class="review-user">{{ review.user }}</span>
                  <div class="review-stars">
                    <span v-for="n in 5" :key="n" :class="{ filled: n <= review.rating }">★</span>
                  </div>
                  <span class="review-date">{{ review.date }}</span>
                </div>
                <p class="review-content">{{ review.content }}</p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 购买订单弹窗 -->
    <div class="order-modal" v-if="showOrderModal" @click.self="closeOrderModal">
      <div class="order-content">
        <h2>确认订单</h2>
        <div class="order-items">
          <div class="order-item" v-for="(item, index) in orderItems" :key="index">
            <img :src="item.image" :alt="item.name" />
            <div class="order-item-info">
              <h4>{{ item.name }}</h4>
              <span>× {{ item.quantity }}</span>
              <span class="item-total">￥{{ (item.price * item.quantity).toFixed(2) }}</span>
            </div>
          </div>
        </div>
        <div class="order-summary">
          <div class="summary-row">
            <span>商品总价：</span>
            <span>￥{{ orderTotal.toFixed(2) }}</span>
          </div>
          <div class="summary-row">
            <span>运费：</span>
            <span>￥0.00</span>
          </div>
          <div class="summary-row total">
            <span>应付总额：</span>
            <span>￥{{ orderTotal.toFixed(2) }}</span>
          </div>
        </div>
        <div class="order-payment">
          <h4>支付方式</h4>
          <div class="payment-options">
            <label class="payment-option">
              <input type="radio" name="payment" value="alipay" checked />
              <span>支付宝</span>
            </label>
            <label class="payment-option">
              <input type="radio" name="payment" value="wechat" />
              <span>微信支付</span>
            </label>
            <label class="payment-option">
              <input type="radio" name="payment" value="card" />
              <span>银行卡</span>
            </label>
          </div>
        </div>
        <button class="pay-btn" @click="processPayment">确认支付</button>
      </div>
    </div>

    <!-- 加入购物车动画元素 -->
    <div 
      v-for="(anim, index) in cartAnimations" 
      :key="index"
      class="cart-animation"
      :style="{ left: anim.x + 'px', top: anim.y + 'px' }"
    >
      <img :src="anim.image" alt="" />
    </div>
    
    <!-- 图片查看器 -->
    <div class="image-viewer" v-if="showImageViewer" @click.self="closeImageViewer">
      <button class="close-viewer" @click="closeImageViewer">
        <svg viewBox="0 0 20 20" fill="currentColor">
          <path fill-rule="evenodd" d="M4.293 4.293a1 1 0 011.414 0L10 8.586l4.293-4.293a1 1 0 111.414 1.414L11.414 10l4.293 4.293a1 1 0 01-1.414 1.414L10 11.414l-4.293 4.293a1 1 0 01-1.414-1.414L8.586 10 4.293 5.707a1 1 0 010-1.414z" clip-rule="evenodd" />
        </svg>
      </button>
      <button class="prev-image" @click="prevImage" v-if="productImages.length > 1">
        <svg viewBox="0 0 20 20" fill="currentColor">
          <path fill-rule="evenodd" d="M12.707 5.293a1 1 0 010 1.414L9.414 10l3.293 3.293a1 1 0 01-1.414 1.414l-4-4a1 1 0 010-1.414l4-4a1 1 0 011.414 0z" clip-rule="evenodd" />
        </svg>
      </button>
      <button class="next-image" @click="nextImage" v-if="productImages.length > 1">
        <svg viewBox="0 0 20 20" fill="currentColor">
          <path fill-rule="evenodd" d="M7.293 14.707a1 1 0 010-1.414L10.586 10 7.293 6.707a1 1 0 011.414-1.414l4 4a1 1 0 010 1.414l-4 4a1 1 0 01-1.414 0z" clip-rule="evenodd" />
        </svg>
      </button>
      <div class="viewer-image-container">
        <img :src="currentGalleryImage" :alt="selectedProduct?.name" />
      </div>
      <div class="viewer-indicators" v-if="productImages.length > 1">
        <span 
          v-for="(img, i) in productImages" 
          :key="i"
          :class="{ active: currentGalleryIndex === i }"
          @click="currentGalleryIndex = i"
        ></span>
      </div>
    </div>
  </WebsiteLayout>
</template>

<script>
import { ref, computed, onMounted, onUnmounted, watch } from 'vue'
import { useRoute } from 'vue-router'
import WebsiteLayout from '../components/WebsiteLayout.vue'
import { getProducts, getProductReviews, addProductReview } from '../api/index'

// 从后端获取产品数据
const productsFromBackend = ref([])

// 获取产品列表
const fetchProducts = async () => {
  try {
    const result = await getProducts()
    if (result.success && result.data) {
      productsFromBackend.value = result.data.products || []
    }
  } catch (error) {
    console.error('获取产品失败:', error)
  }
}

// 产品数据（保留本地数据作为后备）
const allProducts = [
  // 鼠标
  { id: 1, category: 'mouse', name: '星火RGB电竞鼠标', description: '16000DPI光学传感器，支持RGB灯效，专为FPS游戏玩家打造', price: 299, originalPrice: 399, image: '/images/mouse/1.jpg', images: ['/images/mouse/1.jpg', '/images/mouse/2.jpg', '/images/mouse/3.jpg'], features: ['16000 DPI', 'RGB灯效', '轻量化'], stock: 150, sales: 328, rating: 4.8, reviewCount: 89 },
  { id: 2, category: 'mouse', name: '星火无线游戏鼠标', description: '低延迟无线连接，超长续航，告别线缆束缚', price: 399, originalPrice: 499, image: '/images/mouse/2.jpg', images: ['/images/mouse/2.jpg', '/images/mouse/1.jpg'], features: ['无线连接', '72小时续航', '人体工学'], stock: 85, sales: 256, rating: 4.7, reviewCount: 67 },
  { id: 3, category: 'mouse', name: '星火轻量化鼠标', description: '58克超轻设计，FPS神器，极速响应', price: 349, image: '/images/mouse/3.jpg', images: ['/images/mouse/3.jpg', '/images/mouse/4.jpg'], features: ['58克', 'PMW3389', '伞绳线'], stock: 120, sales: 412, rating: 4.9, reviewCount: 124 },
  { id: 4, category: 'mouse', name: '星火对称游戏鼠标', description: '对称设计，左右手通用，适合所有玩家', price: 249, originalPrice: 329, image: '/images/mouse/4.jpg', images: ['/images/mouse/4.jpg', '/images/mouse/5.jpg'], features: ['对称设计', '16000 DPI', '耐用的'], stock: 200, sales: 189, rating: 4.5, reviewCount: 45 },
  { id: 5, category: 'mouse', name: '星火有线电竞鼠标', description: '1ms响应时间，职业选手选择', price: 199, image: '/images/mouse/5.jpg', images: ['/images/mouse/5.jpg'], features: ['1ms响应', '欧姆龙微动', '防滑涂层'], stock: 300, sales: 567, rating: 4.6, reviewCount: 98 },
  { id: 6, category: 'mouse', name: '星火蓝牙鼠标', description: '支持蓝牙5.0，办公游戏两用', price: 179, image: '/images/mouse/6.jpg', images: ['/images/mouse/6.jpg'], features: ['蓝牙5.0', '多设备切换', '静音'], stock: 180, sales: 234, rating: 4.4, reviewCount: 56 },
  { id: 7, category: 'mouse', name: '星火宏定义鼠标', description: '可编程按键，支持宏定义', price: 299, image: '/images/mouse/7.jpg', images: ['/images/mouse/7.jpg'], features: ['可编程', '宏定义', '板载内存'], stock: 95, sales: 178, rating: 4.7, reviewCount: 72 },
  // 键盘
  { id: 8, category: 'keyboard', name: '星火青轴机械键盘', description: '经典青轴，段落感强，打字声音清脆', price: 399, originalPrice: 499, image: '/images/keyboard/1.jpg', images: ['/images/keyboard/1.jpg', '/images/keyboard/2.jpg'], features: ['青轴', 'RGB背光', 'PBT键帽'], stock: 130, sales: 456, rating: 4.8, reviewCount: 112 },
  { id: 9, category: 'keyboard', name: '星火红轴机械键盘', description: '线性手感，快速触发，游戏玩家首选', price: 449, originalPrice: 599, image: '/images/keyboard/2.jpg', images: ['/images/keyboard/2.jpg', '/images/keyboard/3.jpg'], features: ['红轴', '热插拔', '铝合金'], stock: 90, sales: 345, rating: 4.9, reviewCount: 87 },
  { id: 10, category: 'keyboard', name: '星火茶轴机械键盘', description: '轻微段落感，办公游戏兼顾', price: 429, image: '/images/keyboard/3.jpg', images: ['/images/keyboard/3.jpg', '/images/keyboard/4.jpg'], features: ['茶轴', '全键无冲', '多媒体'], stock: 110, sales: 289, rating: 4.6, reviewCount: 68 },
  { id: 11, category: 'keyboard', name: '星火87键机械键盘', description: '紧凑布局，节省桌面空间', price: 349, originalPrice: 449, image: '/images/keyboard/4.jpg', images: ['/images/keyboard/4.jpg'], features: ['87键', '紧凑', 'RGB'], stock: 160, sales: 398, rating: 4.7, reviewCount: 94 },
  { id: 12, category: 'keyboard', name: '星火104键机械键盘', description: '全尺寸布局，数字键盘', price: 499, image: '/images/keyboard/5.jpg', images: ['/images/keyboard/5.jpg'], features: ['104键', '数字键盘', 'USB-C'], stock: 85, sales: 267, rating: 4.5, reviewCount: 53 },
  { id: 13, category: 'keyboard', name: '星火无线机械键盘', description: '三模连接，无线自由', price: 599, originalPrice: 799, image: '/images/keyboard/6.jpg', images: ['/images/keyboard/6.jpg', '/images/keyboard/7.jpg'], features: ['无线', '蓝牙/2.4G', '4000mAh'], stock: 70, sales: 189, rating: 4.8, reviewCount: 76 },
  { id: 14, category: 'keyboard', name: '星火透明机械键盘', description: '透明外壳，RGB内透', price: 699, image: '/images/keyboard/7.jpg', images: ['/images/keyboard/7.jpg', '/images/keyboard/8.jpg'], features: ['透明外壳', '下灯位', '硅胶垫'], stock: 50, sales: 145, rating: 4.9, reviewCount: 48 },
  { id: 15, category: 'keyboard', name: '星火游戏键盘', description: '游戏专用，宏按键', price: 549, image: '/images/keyboard/8.jpg', images: ['/images/keyboard/8.jpg'], features: ['游戏模式', '宏按键', '掌托'], stock: 100, sales: 234, rating: 4.6, reviewCount: 61 },
  // 耳机
  { id: 16, category: 'headset', name: '星火7.1游戏耳机', description: '虚拟7.1环绕声，听声辨位', price: 299, originalPrice: 399, image: '/images/headset/1.jpg', images: ['/images/headset/1.jpg', '/images/headset/2.jpg'], features: ['7.1声道', '降噪麦', 'RGB'], stock: 140, sales: 512, rating: 4.7, reviewCount: 118 },
  { id: 17, category: 'headset', name: '星火无线游戏耳机', description: '低延迟无线，震撼低音', price: 499, originalPrice: 699, image: '/images/headset/2.jpg', images: ['/images/headset/2.jpg', '/images/headset/3.jpg'], features: ['无线', '2.4G', '30小时'], stock: 80, sales: 345, rating: 4.8, reviewCount: 89 },
  { id: 18, category: 'headset', name: '星火头戴式游戏耳机', description: '50mm大单元，舒适佩戴', price: 249, originalPrice: 349, image: '/images/headset/3.jpg', images: ['/images/headset/3.jpg', '/images/headset/4.jpg'], features: ['50mm单元', '记忆棉', '可调头梁'], stock: 170, sales: 456, rating: 4.6, reviewCount: 97 },
  { id: 19, category: 'headset', name: '星火入耳式游戏耳机', description: '小巧便携，手机游戏', price: 149, originalPrice: 199, image: '/images/headset/4.jpg', images: ['/images/headset/4.jpg'], features: ['入耳式', '麦克风', '线控'], stock: 250, sales: 678, rating: 4.5, reviewCount: 134 },
  { id: 20, category: 'headset', name: '星火专业电竞耳机', description: 'ES8.0声卡，职业级', price: 799, image: '/images/headset/5.jpg', images: ['/images/headset/5.jpg', '/images/headset/6.jpg'], features: ['ES8.0', '声卡', 'Hi-Res'], stock: 45, sales: 123, rating: 4.9, reviewCount: 67 },
  { id: 21, category: 'headset', name: '星火蓝牙游戏耳机', description: '蓝牙5.0，超低延迟', price: 349, image: '/images/headset/6.jpg', images: ['/images/headset/6.jpg'], features: ['蓝牙5.0', '游戏模式', '充电盒'], stock: 120, sales: 289, rating: 4.7, reviewCount: 76 },
  { id: 22, category: 'headset', name: '星火降噪游戏耳机', description: '主动降噪，沉浸体验', price: 599, image: '/images/headset/7.jpg', images: ['/images/headset/7.jpg', '/images/headset/8.jpg'], features: ['ANC降噪', '通透模式', '触控'], stock: 75, sales: 198, rating: 4.8, reviewCount: 58 },
  { id: 23, category: 'headset', name: '星火立体声游戏耳机', description: '立体声效，脚步声增强', price: 199, image: '/images/headset/8.jpg', images: ['/images/headset/8.jpg'], features: ['立体声', '脚步声增强', '轻量化'], stock: 200, sales: 423, rating: 4.4, reviewCount: 89 },
  // 手柄
  { id: 24, category: 'controller', name: '星火无线游戏手柄', description: '多平台兼容，震动反馈', price: 299, originalPrice: 399, image: '/images/controller/1.jpg', images: ['/images/controller/1.jpg', '/images/controller/2.jpg'], features: ['无线', '双震动', '多平台'], stock: 130, sales: 389, rating: 4.7, reviewCount: 103 },
  { id: 25, category: 'controller', name: '星火有线游戏手柄', description: '0延迟，竞技首选', price: 199, originalPrice: 299, image: '/images/controller/2.jpg', images: ['/images/controller/2.jpg', '/images/controller/3.jpg'], features: ['有线', '0延迟', '线性扳机'], stock: 180, sales: 456, rating: 4.6, reviewCount: 87 },
  { id: 26, category: 'controller', name: '星火精英游戏手柄', description: '自定义宏，4背键', price: 499, image: '/images/controller/3.jpg', images: ['/images/controller/3.jpg', '/images/controller/4.jpg'], features: ['4背键', '自定义', '摇杆'], stock: 65, sales: 167, rating: 4.9, reviewCount: 72 },
  { id: 27, category: 'controller', name: '星火蓝牙手柄', description: '蓝牙连接，手机也能玩', price: 249, originalPrice: 349, image: '/images/controller/4.jpg', images: ['/images/controller/4.jpg'], features: ['蓝牙', '安卓/iOS', '体感'], stock: 150, sales: 312, rating: 4.5, reviewCount: 68 },
  { id: 28, category: 'controller', name: '星火手柄套装', description: '含底座和耳机，一站式', price: 599, image: '/images/controller/5.jpg', images: ['/images/controller/5.jpg'], features: ['套装', '充电底座', '耳机'], stock: 55, sales: 134, rating: 4.8, reviewCount: 49 },
  { id: 29, category: 'controller', name: '星火复古手柄', description: '经典造型，怀旧体验', price: 179, image: '/images/controller/6.jpg', images: ['/images/controller/6.jpg'], features: ['复古', 'USB-C', '震动'], stock: 110, sales: 223, rating: 4.4, reviewCount: 56 },
  { id: 30, category: 'controller', name: '星火云游戏手柄', description: '专属云游戏优化', price: 349, image: '/images/controller/7.jpg', images: ['/images/controller/7.jpg'], features: ['云游戏', '低延迟', '映射'], stock: 85, sales: 178, rating: 4.7, reviewCount: 64 }
]

// 为每个产品添加评价
allProducts.forEach(product => {
  // 确保features是数组
  if (typeof product.features === 'string') {
    try {
      product.features = JSON.parse(product.features)
    } catch (e) {
      product.features = [product.features]
    }
  }
  
  // 确保images是数组
  if (typeof product.images === 'string') {
    try {
      product.images = JSON.parse(product.images)
    } catch (e) {
      product.images = [product.images]
    }
  }
  
  // 确保reviews存在
  if (!product.reviews || product.reviews.length === 0) {
    product.reviews = [
      { user: '游戏玩家' + Math.floor(Math.random() * 1000), rating: 5, content: '产品质量很好，推荐购买！', date: '2026-0' + Math.floor(Math.random() * 3 + 1) + '-' + Math.floor(Math.random() * 28 + 1) },
      { user: '电竞爱好者' + Math.floor(Math.random() * 1000), rating: 4, content: '性价比很高，会推荐给朋友。', date: '2026-0' + Math.floor(Math.random() * 3 + 1) + '-' + Math.floor(Math.random() * 28 + 1) },
      { user: '资深玩家' + Math.floor(Math.random() * 1000), rating: 5, content: '用了一段时间，感觉非常棒！', date: '2026-0' + Math.floor(Math.random() * 3 + 1) + '-' + Math.floor(Math.random() * 28 + 1) }
    ]
  }
  
  // 确保必要字段存在
  product.stock = product.stock || 0
  product.sales = product.sales || 0
  product.rating = product.rating || 4.5
  product.reviewCount = product.reviewCount || 0
})

export default {
  name: 'Products',
  components: {
    WebsiteLayout
  },
  setup() {
    const route = useRoute()
    const activeCategory = ref(route.query.category || 'all')
    const adCurrentSlide = ref(0)
    const showCart = ref(false)
    const selectedProduct = ref(null)
    const showOrderModal = ref(false)
    const orderItems = ref([])
    const cartItems = ref([])
    const cartAnimations = ref([])
    const products = ref([])
    const loading = ref(true)
    const currentGalleryIndex = ref(0)
    const activeDetailTab = ref('desc')
    const showImageViewer = ref(false)
    const newReview = ref({
      rating: 0,
      content: ''
    })
    
    // 页面加载时获取产品
    onMounted(async () => {
      await fetchProducts()
      // 优先使用后端数据，如果没有则使用本地数据
      if (productsFromBackend.value.length > 0) {
        products.value = productsFromBackend.value
      } else {
        products.value = allProducts
      }
      loading.value = false
      startAdCarousel()
    })
    
    // 监听分类变化，重新获取产品
    watch(activeCategory, async (newCategory) => {
      loading.value = true
      try {
        const result = await getProducts(newCategory === 'all' ? null : newCategory)
        if (result.success && result.data) {
          products.value = result.data.products || []
        } else {
          // 如果API失败，使用本地数据过滤
          products.value = newCategory === 'all' ? allProducts : allProducts.filter(p => p.category === newCategory)
        }
      } catch (error) {
        console.error('获取产品失败:', error)
        products.value = newCategory === 'all' ? allProducts : allProducts.filter(p => p.category === newCategory)
      }
      loading.value = false
    })
    
    // 广告轮播图数据
    const adBanners = [
      { image: '/images/mouse/6.jpg', title: '星火RGB电竞鼠标', desc: '16000DPI专业级游戏鼠标', category: 'mouse' },
      { image: '/images/keyboard/9.jpg', title: '星火机械键盘', desc: '青轴红轴可选，畅快敲击体验', category: 'keyboard' },
      { image: '/images/headset/4.jpg', title: '星火7.1游戏耳机', desc: '虚拟环绕声，听声辨位先发制人', category: 'headset' },
      { image: '/images/controller/1.jpg', title: '星火无线游戏手柄', desc: '多平台兼容，沉浸式游戏体验', category: 'controller' }
    ]
    
    // 轮播图自动播放
    let adCarouselInterval = null

    const startAdCarousel = () => {
      // 先清除现有的计时器
      stopAdCarousel()
      adCarouselInterval = setInterval(() => {
        adCurrentSlide.value = (adCurrentSlide.value + 1) % adBanners.length
      }, 6000) // 改为6秒，让用户有足够时间查看
    }

    const stopAdCarousel = () => {
      if (adCarouselInterval) {
        clearInterval(adCarouselInterval)
        adCarouselInterval = null
      }
    }

    // 重置轮播图计时器（用于手动切换时）
    const resetAdCarousel = () => {
      stopAdCarousel()
      startAdCarousel()
    }

    // 处理指示器点击
    const handleIndicatorClick = (index) => {
      adCurrentSlide.value = index
      resetAdCarousel() // 重置计时器，避免自动播放冲突
    }
    
    // 过滤产品
    const filteredProducts = computed(() => {
      const productList = products.value.length > 0 ? products.value : allProducts
      if (activeCategory.value === 'all') {
        return productList
      }
      return productList.filter(p => p.category === activeCategory.value)
    })
    
    // 购物车计算
    const cartTotal = computed(() => {
      return cartItems.value
        .filter(item => item.selected)
        .reduce((sum, item) => sum + item.price * item.quantity, 0)
    })
    
    // 订单总额
    const orderTotal = computed(() => {
      return orderItems.value.reduce((sum, item) => sum + item.price * item.quantity, 0)
    })
    
    // 商品图片数组
    const productImages = computed(() => {
      if (!selectedProduct.value) return []
      
      // 如果images字段存在且是数组
      if (selectedProduct.value.images && Array.isArray(selectedProduct.value.images) && selectedProduct.value.images.length > 0) {
        return selectedProduct.value.images
      }
      
      // 如果images字段是字符串，尝试解析
      if (typeof selectedProduct.value.images === 'string') {
        try {
          const parsed = JSON.parse(selectedProduct.value.images)
          if (Array.isArray(parsed) && parsed.length > 0) {
            return parsed
          }
        } catch (e) {
          // 解析失败，忽略
        }
      }
      
      // 返回主图
      return [selectedProduct.value.image || '']
    })
    
    // 当前显示的图片
    const currentGalleryImage = computed(() => {
      return productImages.value[currentGalleryIndex.value] || ''
    })
    
    // 获取分类名称
    const getCategoryName = (category) => {
      const categoryMap = {
        'mouse': '电竞鼠标',
        'keyboard': '机械键盘',
        'headset': '游戏耳机',
        'controller': '游戏手柄'
      }
      return categoryMap[category] || category
    }
    
    // 格式化日期
    const formatDate = (dateStr) => {
      if (!dateStr) return ''
      const date = new Date(dateStr)
      return date.toLocaleDateString('zh-CN', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit'
      })
    }
    
    // 导航到分类
    const goToCategory = (category) => {
      activeCategory.value = category
    }
    
    // 打开商品详情
    const openProductDetail = async (product) => {
      // 创建商品副本，避免修改原数据
      const productCopy = JSON.parse(JSON.stringify(product))
      
      // 确保features是数组
      if (typeof productCopy.features === 'string') {
        try {
          productCopy.features = JSON.parse(productCopy.features)
        } catch (e) {
          productCopy.features = [productCopy.features]
        }
      }
      
      // 确保images是数组
      if (typeof productCopy.images === 'string') {
        try {
          productCopy.images = JSON.parse(productCopy.images)
        } catch (e) {
          productCopy.images = [productCopy.images]
        }
      }
      
      // 确保reviews是数组
      if (!productCopy.reviews || productCopy.reviews.length === 0) {
        productCopy.reviews = []
      }
      
      selectedProduct.value = productCopy
      currentGalleryIndex.value = 0
      activeDetailTab.value = 'desc'
      
      // 从后端加载评论
      await loadProductReviews(productCopy.id)
    }
    
    const closeProductDetail = () => {
      selectedProduct.value = null
      currentGalleryIndex.value = 0
      activeDetailTab.value = 'desc'
    }
    
    // 立即购买
    const buyNow = (product) => {
      orderItems.value = [{ ...product, quantity: 1 }]
      showOrderModal.value = true
      showCart.value = false
    }
    
    // 加入购物车
    const addToCart = (product, event) => {
      // 动画效果
      if (event) {
        const rect = event.target.getBoundingClientRect()
        cartAnimations.value.push({
          x: rect.left + rect.width / 2,
          y: rect.top,
          image: product.image
        })
        setTimeout(() => {
          cartAnimations.value.shift()
        }, 800)
      }
      
      const existingItem = cartItems.value.find(item => item.id === product.id)
      if (existingItem) {
        existingItem.quantity++
      } else {
        cartItems.value.push({
          ...product,
          quantity: 1,
          selected: true
        })
      }
    }
    
    // 切换购物车选项
    const toggleCartItem = (index) => {
      cartItems.value[index].selected = !cartItems.value[index].selected
    }
    
    // 移除购物车商品
    const removeFromCart = (index) => {
      cartItems.value.splice(index, 1)
    }
    
    // 结算
    const checkout = () => {
      const selectedItems = cartItems.value.filter(item => item.selected)
      if (selectedItems.length === 0) {
        alert('请选择要结算的商品')
        return
      }
      orderItems.value = [...selectedItems]
      showOrderModal.value = true
    }
    
    // 关闭订单弹窗
    const closeOrderModal = () => {
      showOrderModal.value = false
    }
    
    // 处理支付
    const processPayment = () => {
      alert('支付成功！感谢您的购买')
      // 清空购物车
      const selectedIds = orderItems.value.map(item => item.id)
      cartItems.value = cartItems.value.filter(item => !selectedIds.includes(item.id))
      showOrderModal.value = false
    }
    
    // 图片查看器相关方法
    const openImageViewer = () => {
      showImageViewer.value = true
    }
    
    const closeImageViewer = () => {
      showImageViewer.value = false
    }
    
    const prevImage = () => {
      if (currentGalleryIndex.value > 0) {
        currentGalleryIndex.value--
      } else {
        currentGalleryIndex.value = productImages.value.length - 1
      }
    }
    
    const nextImage = () => {
      if (currentGalleryIndex.value < productImages.value.length - 1) {
        currentGalleryIndex.value++
      } else {
        currentGalleryIndex.value = 0
      }
    }
    
    // 评论相关方法
    // 从后端加载商品评论
    const loadProductReviews = async (productId) => {
      try {
        const result = await getProductReviews(productId)
        if (result.success && result.data && result.data.reviews) {
          // 将后端返回的评论格式转换为前端需要的格式
          selectedProduct.value.reviews = result.data.reviews.map(review => ({
            user: review.username,
            rating: review.rating,
            content: review.content,
            date: review.createTime ? formatDate(review.createTime) : review.createTime
          }))
          
          // 更新评论数
          selectedProduct.value.reviewCount = result.data.reviews.length
          
          // 计算平均评分
          if (result.data.reviews.length > 0) {
            const totalRating = result.data.reviews.reduce((sum, r) => sum + r.rating, 0)
            selectedProduct.value.rating = (totalRating / result.data.reviews.length).toFixed(1)
          }
        }
      } catch (error) {
        console.error('加载商品评论失败:', error)
      }
    }
    
    // 提交评论
    const submitReview = async () => {
      if (!newReview.value.rating || !newReview.value.content.trim()) {
        alert('请选择评分并填写评价内容')
        return
      }
      
      // 获取当前登录用户信息
      const currentUser = JSON.parse(sessionStorage.getItem('currentUser') || '{}')
      const username = currentUser.username || currentUser.nickname || '匿名用户'
      
      // 调用后端API添加评论
      try {
        const result = await addProductReview(
          selectedProduct.value.id,
          username,
          newReview.value.rating,
          newReview.value.content.trim()
        )
        
        if (result.success) {
          // 重新加载评论列表
          await loadProductReviews(selectedProduct.value.id)
          
          // 重置评论表单
          newReview.value.rating = 0
          newReview.value.content = ''
          
          alert('评价提交成功！')
        } else {
          alert(result.message || '评价提交失败')
        }
      } catch (error) {
        console.error('提交评价失败:', error)
        alert('评价提交失败，请稍后重试')
      }
    }
    
    onMounted(() => {
      startAdCarousel()
    })
    
    onUnmounted(() => {
      stopAdCarousel()
    })
    
    return {
      activeCategory,
      adCurrentSlide,
      adBanners,
      filteredProducts,
      showCart,
      cartItems,
      cartTotal,
      selectedProduct,
      showOrderModal,
      orderItems,
      orderTotal,
      cartAnimations,
      currentGalleryIndex,
      activeDetailTab,
      productImages,
      currentGalleryImage,
      showImageViewer,
      newReview,
      goToCategory,
      openProductDetail,
      closeProductDetail,
      buyNow,
      addToCart,
      toggleCartItem,
      removeFromCart,
      checkout,
      closeOrderModal,
      processPayment,
      handleIndicatorClick,
      resetAdCarousel,
      getCategoryName,
      formatDate,
      openImageViewer,
      closeImageViewer,
      prevImage,
      nextImage,
      submitReview
    }
  }
}
</script>

<style scoped>
/* 广告轮播图 */
.ad-carousel {
  position: relative;
  width: 100%;
  max-width: 1200px;
  margin: 0 auto 40px;
  overflow: hidden;
}

.carousel-track {
  display: flex;
  transition: transform 0.5s ease;
}

.carousel-slide {
  min-width: 100%;
  background: #161b22;
}

.slide-wrapper {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 40px 60px;
  min-height: 400px;
}

.slide-image {
  flex: 0 0 55%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #21262d;
  border-radius: 16px;
  overflow: hidden;
  max-height: 400px;
  height: 400px;
}

.slide-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
}

.slide-content {
  flex: 0 0 40%;
  color: white;
  padding-left: 40px;
}

.slide-content h2 {
  font-size: 42px;
  font-weight: 700;
  margin-bottom: 16px;
  text-shadow: 0 2px 10px rgba(0, 0, 0, 0.5);
}

.slide-content p {
  font-size: 20px;
  margin-bottom: 24px;
  opacity: 0.9;
  line-height: 1.6;
}

.slide-btn {
  padding: 14px 36px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  border-radius: 30px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: transform 0.3s, box-shadow 0.3s;
}

.slide-btn:hover {
  transform: scale(1.05);
  box-shadow: 0 8px 25px rgba(102, 126, 234, 0.5);
}

.carousel-indicators {
  position: absolute;
  bottom: 20px;
  left: 50%;
  transform: translateX(-50%);
  display: flex;
  gap: 10px;
  z-index: 20;
}

.indicator {
  width: 12px;
  height: 12px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.5);
  cursor: pointer;
  transition: all 0.3s;
}

.indicator.active {
  background: #667eea;
  transform: scale(1.2);
}

/* 分类标签 */
.category-tabs {
  display: flex;
  justify-content: center;
  gap: 20px;
  margin-bottom: 40px;
  flex-wrap: wrap;
}

.tab-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 14px 28px;
  background: #161b22;
  border: 1px solid #30363d;
  border-radius: 30px;
  color: #c9d1d9;
  cursor: pointer;
  transition: all 0.3s;
}

.tab-item:hover {
  background: #21262d;
  border-color: #667eea;
}

.tab-item.active {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-color: transparent;
  color: white;
}

.tab-icon {
  font-size: 20px;
}

/* 标题 */
.section-header {
  text-align: center;
  margin-bottom: 40px;
  padding: 0 20px;
}

.section-header h2 {
  font-size: 32px;
  color: #ffffff;
  margin-bottom: 12px;
}

.section-header p {
  font-size: 16px;
  color: #8b949e;
}

/* 商品列表 */
.products-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 40px;
  padding: 0 60px 60px;
  max-width: 1400px;
  margin: 0 auto;
}

.product-card {
  background: #161b22;
  border-radius: 16px;
  overflow: hidden;
  border: 1px solid #30363d;
  cursor: pointer;
  transition: all 0.3s;
  aspect-ratio: 1;
  display: flex;
  flex-direction: column;
}

.product-card:hover {
  transform: translateY(-8px);
  border-color: #667eea;
  box-shadow: 0 12px 40px rgba(102, 126, 234, 0.2);
}

.product-image {
  position: relative;
  aspect-ratio: 1;
  overflow: hidden;
}

.product-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.4s;
}

.product-card:hover .product-image img {
  transform: scale(1.1);
}

.product-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.6);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.3s;
}

.product-card:hover .product-overlay {
  opacity: 1;
}

.view-detail {
  padding: 12px 24px;
  background: #667eea;
  color: white;
  border-radius: 25px;
  font-weight: 500;
}

.product-info {
  padding: 20px;
  flex: 1;
  display: flex;
  flex-direction: column;
}

.product-info h3 {
  font-size: 18px;
  color: #ffffff;
  margin-bottom: 8px;
}

.product-desc {
  font-size: 14px;
  color: #8b949e;
  margin-bottom: 16px;
  line-height: 1.5;
  height: 42px;
  overflow: hidden;
}

.product-price {
  margin-bottom: 16px;
}

.price {
  font-size: 24px;
  font-weight: 700;
  color: #ff6b6b;
}

.product-actions {
  display: flex;
  gap: 10px;
}

.buy-btn, .cart-btn {
  flex: 1;
  padding: 12px;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
}

.buy-btn {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
}

.buy-btn:hover {
  transform: scale(1.02);
  box-shadow: 0 4px 15px rgba(102, 126, 234, 0.4);
}

.cart-btn {
  background: transparent;
  color: #667eea;
  border: 1px solid #667eea;
}

.cart-btn:hover {
  background: rgba(102, 126, 234, 0.1);
}

.cart-btn svg {
  width: 18px;
  height: 18px;
}

/* 购物车悬浮按钮 */
.cart-float {
  position: fixed;
  bottom: 30px;
  right: 30px;
  width: 60px;
  height: 60px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  box-shadow: 0 4px 20px rgba(102, 126, 234, 0.5);
  z-index: 999;
  transition: transform 0.3s;
}

.cart-float:hover {
  transform: scale(1.1);
}

.cart-float svg {
  width: 28px;
  height: 28px;
  color: white;
}

.cart-badge {
  position: absolute;
  top: -5px;
  right: -5px;
  width: 22px;
  height: 22px;
  background: #ff6b6b;
  border-radius: 50%;
  color: white;
  font-size: 12px;
  font-weight: 600;
  display: flex;
  align-items: center;
  justify-content: center;
}

/* 购物车面板 */
.cart-panel {
  position: fixed;
  top: 0;
  right: -400px;
  width: 400px;
  height: 100vh;
  background: #161b22;
  border-left: 1px solid #30363d;
  z-index: 1000;
  transition: right 0.3s ease;
  display: flex;
  flex-direction: column;
}

.cart-panel.open {
  right: 0;
}

.cart-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  border-bottom: 1px solid #30363d;
}

.cart-header h3 {
  color: #ffffff;
  font-size: 20px;
}

.close-cart {
  background: none;
  border: none;
  color: #8b949e;
  cursor: pointer;
  padding: 5px;
}

.close-cart svg {
  width: 24px;
  height: 24px;
}

.cart-items {
  flex: 1;
  overflow-y: auto;
  padding: 15px;
}

.cart-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 15px;
  background: #21262d;
  border-radius: 10px;
  margin-bottom: 10px;
}

.item-checkbox {
  width: 18px;
  height: 18px;
  accent-color: #667eea;
}

.item-image {
  width: 60px;
  height: 60px;
  border-radius: 8px;
  object-fit: cover;
}

.item-info {
  flex: 1;
}

.item-info h4 {
  color: #ffffff;
  font-size: 14px;
  margin-bottom: 6px;
}

.item-price {
  color: #ff6b6b;
  font-weight: 600;
}

.remove-item {
  background: none;
  border: none;
  color: #8b949e;
  cursor: pointer;
  padding: 5px;
}

.remove-item:hover {
  color: #ff6b6b;
}

.remove-item svg {
  width: 20px;
  height: 20px;
}

.cart-empty {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #8b949e;
}

.cart-empty span {
  font-size: 60px;
  margin-bottom: 20px;
}

.cart-footer {
  padding: 20px;
  border-top: 1px solid #30363d;
}

.cart-total {
  display: flex;
  justify-content: space-between;
  margin-bottom: 16px;
  color: #ffffff;
  font-size: 16px;
}

.total-price {
  color: #ff6b6b;
  font-size: 24px;
  font-weight: 700;
}

.checkout-btn {
  width: 100%;
  padding: 16px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  border-radius: 10px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
}

.checkout-btn:hover {
  transform: scale(1.02);
  box-shadow: 0 4px 15px rgba(102, 126, 234, 0.4);
}

/* 滚动条样式 */
.modal-content::-webkit-scrollbar,
.gallery-thumbs::-webkit-scrollbar,
.detail-tabs::-webkit-scrollbar,
.detail-content::-webkit-scrollbar,
.reviews-list::-webkit-scrollbar {
  width: 6px;
  height: 6px;
}

.modal-content::-webkit-scrollbar-track,
.gallery-thumbs::-webkit-scrollbar-track,
.detail-tabs::-webkit-scrollbar-track,
.detail-content::-webkit-scrollbar-track,
.reviews-list::-webkit-scrollbar-track {
  background: #21262d;
}

.modal-content::-webkit-scrollbar-thumb,
.gallery-thumbs::-webkit-scrollbar-thumb,
.detail-tabs::-webkit-scrollbar-thumb,
.detail-content::-webkit-scrollbar-thumb,
.reviews-list::-webkit-scrollbar-thumb {
  background: #30363d;
  border-radius: 3px;
}

.modal-content::-webkit-scrollbar-thumb:hover,
.gallery-thumbs::-webkit-scrollbar-thumb:hover,
.detail-tabs::-webkit-scrollbar-thumb:hover,
.detail-content::-webkit-scrollbar-thumb:hover,
.reviews-list::-webkit-scrollbar-thumb:hover {
  background: #484f58;
}

/* 商品详情弹窗 */
.product-modal {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.8);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1001;
  padding: 20px;
  overflow-y: auto;
  animation: fadeIn 0.3s ease;
}

.modal-content {
  background: #161b22;
  border-radius: 20px;
  max-width: 1000px;
  width: 100%;
  max-height: calc(100vh - 40px);
  overflow-y: auto;
  position: relative;
  border: 1px solid #30363d;
  display: flex;
  flex-direction: column;
  animation: slideUp 0.3s ease;
}

@keyframes fadeIn {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

@keyframes slideUp {
  from {
    transform: translateY(20px);
    opacity: 0;
  }
  to {
    transform: translateY(0);
    opacity: 1;
  }
}

.close-modal {
  position: fixed;
  top: 30px;
  right: 30px;
  background: rgba(255, 255, 255, 0.1);
  border: none;
  color: #ffffff;
  width: 40px;
  height: 40px;
  border-radius: 50%;
  cursor: pointer;
  z-index: 1002;
  transition: all 0.3s;
  display: flex;
  align-items: center;
  justify-content: center;
}

.close-modal:hover {
  background: rgba(255, 255, 255, 0.2);
}

.close-modal svg {
  width: 24px;
  height: 24px;
}

.modal-product {
  display: flex;
  gap: 40px;
  padding: 40px;
  flex-shrink: 0;
}

/* 商品图片轮播 */
.modal-gallery {
  width: 400px;
  flex-shrink: 0;
}

.gallery-main {
  position: relative;
  width: 100%;
  height: 400px;
  border-radius: 16px;
  overflow: hidden;
  background: #21262d;
  margin-bottom: 16px;
}

.gallery-main img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  cursor: pointer;
  transition: transform 0.3s;
}

.gallery-main:hover img {
  transform: scale(1.05);
}

.discount-badge {
  position: absolute;
  top: 16px;
  left: 16px;
  padding: 8px 16px;
  background: linear-gradient(135deg, #ff6b6b 0%, #ff8e53 100%);
  color: white;
  font-weight: 600;
  font-size: 14px;
  border-radius: 8px;
  z-index: 5;
}

.view-hint {
  position: absolute;
  bottom: 16px;
  left: 50%;
  transform: translateX(-50%);
  background: rgba(0, 0, 0, 0.6);
  color: white;
  padding: 8px 16px;
  border-radius: 20px;
  font-size: 13px;
  display: flex;
  align-items: center;
  gap: 6px;
  opacity: 0;
  transition: opacity 0.3s;
  z-index: 5;
}

.gallery-main:hover .view-hint {
  opacity: 1;
}

.view-hint svg {
  width: 16px;
  height: 16px;
}

.gallery-thumbs {
  display: flex;
  gap: 12px;
  overflow-x: auto;
  padding: 4px;
}

.thumb-item {
  width: 80px;
  height: 80px;
  border-radius: 8px;
  overflow: hidden;
  cursor: pointer;
  border: 2px solid transparent;
  transition: all 0.3s;
  flex-shrink: 0;
}

.thumb-item:hover {
  border-color: #667eea;
}

.thumb-item.active {
  border-color: #667eea;
}

.thumb-item img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.modal-info {
  flex: 1;
  min-width: 0;
}

/* 商品分类标签 */
.product-category {
  margin-bottom: 12px;
}

.category-tag {
  padding: 6px 16px;
  background: rgba(102, 126, 234, 0.2);
  color: #667eea;
  border-radius: 20px;
  font-size: 14px;
  font-weight: 500;
  display: inline-block;
}

.modal-info h2 {
  font-size: 28px;
  color: #ffffff;
  margin-bottom: 16px;
  line-height: 1.4;
  word-wrap: break-word;
}

/* 评分和销量 */
.product-stats {
  display: flex;
  gap: 24px;
  margin-bottom: 16px;
  padding: 12px 0;
  border-bottom: 1px solid #30363d;
  flex-wrap: wrap;
}

.rating-box {
  display: flex;
  align-items: center;
  gap: 8px;
}

.rating-box .stars {
  display: flex;
  gap: 2px;
}

.rating-box .stars span {
  color: #30363d;
  font-size: 16px;
  line-height: 16px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 16px;
  height: 16px;
  text-align: center;
  vertical-align: middle;
  box-sizing: border-box;
}

.rating-box .stars span.filled {
  color: #ffc107;
}

.rating-value {
  color: #ffc107;
  font-weight: 600;
  font-size: 16px;
}

.review-count {
  color: #8b949e;
  font-size: 14px;
}

.sales-info {
  color: #8b949e;
  font-size: 14px;
}

.modal-desc {
  font-size: 16px;
  color: #8b949e;
  line-height: 1.6;
  margin-bottom: 20px;
  word-wrap: break-word;
}

/* 价格信息 */
.modal-price {
  display: flex;
  align-items: baseline;
  gap: 16px;
  margin-bottom: 20px;
  flex-wrap: wrap;
}

.current-price {
  font-size: 36px;
  font-weight: 700;
  color: #ff6b6b;
}

.original-price {
  font-size: 20px;
  color: #8b949e;
  text-decoration: line-through;
}

.save-money {
  padding: 4px 12px;
  background: rgba(255, 107, 107, 0.2);
  color: #ff6b6b;
  border-radius: 4px;
  font-size: 14px;
  font-weight: 500;
}

/* 库存信息 */
.stock-info {
  margin-bottom: 20px;
  font-size: 14px;
}

.stock-label {
  color: #8b949e;
}

.stock-info span:last-child {
  color: #ffffff;
  font-weight: 500;
}

.stock-info .low-stock {
  color: #ffc107;
}

.stock-info .out-of-stock {
  color: #ff6b6b;
}

.modal-features {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-bottom: 20px;
}

.feature {
  padding: 8px 16px;
  background: #21262d;
  border-radius: 20px;
  color: #c9d1d9;
  font-size: 14px;
}

/* 上架时间 */
.product-date {
  margin-bottom: 24px;
  font-size: 13px;
  color: #8b949e;
}

.modal-actions {
  display: flex;
  gap: 16px;
}

.buy-now-btn, .add-cart-btn {
  flex: 1;
  padding: 16px 24px;
  border-radius: 10px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  min-width: 0;
}

.buy-now-btn {
  background: linear-gradient(135deg, #ff6b6b 0%, #ff8e53 100%);
  color: white;
  border: none;
}

.buy-now-btn:hover:not(:disabled) {
  transform: scale(1.02);
  box-shadow: 0 4px 15px rgba(255, 107, 107, 0.4);
}

.buy-now-btn:disabled {
  background: #30363d;
  color: #8b949e;
  cursor: not-allowed;
}

.add-cart-btn {
  background: transparent;
  color: #667eea;
  border: 2px solid #667eea;
}

.add-cart-btn:hover:not(:disabled) {
  background: rgba(102, 126, 234, 0.1);
}

.add-cart-btn:disabled {
  border-color: #30363d;
  color: #8b949e;
  cursor: not-allowed;
}

.add-cart-btn svg {
  width: 22px;
  height: 22px;
  flex-shrink: 0;
}

/* 商品详细信息 */
.modal-details {
  padding: 0 40px 40px;
  border-top: 1px solid #30363d;
  flex: 1;
  min-height: 0;
}

.detail-tabs {
  display: flex;
  gap: 0;
  border-bottom: 2px solid #30363d;
  margin: 32px 0 24px 0;
  overflow-x: auto;
  flex-shrink: 0;
}

.detail-tab {
  padding: 16px 24px;
  color: #8b949e;
  cursor: pointer;
  transition: all 0.3s;
  font-size: 15px;
  font-weight: 500;
  border-bottom: 2px solid transparent;
  margin-bottom: -2px;
  white-space: nowrap;
  flex-shrink: 0;
}

.detail-tab:hover {
  color: #ffffff;
}

.detail-tab.active {
  color: #667eea;
  border-bottom-color: #667eea;
}

.detail-content {
  color: #c9d1d9;
  line-height: 1.8;
  max-height: 500px;
  overflow-y: auto;
  padding-right: 10px;
}

/* 商品详情内容 */
.desc-content h3 {
  color: #ffffff;
  font-size: 20px;
  margin-bottom: 16px;
}

.desc-content p {
  margin-bottom: 24px;
  word-wrap: break-word;
}

.detail-features h4 {
  color: #ffffff;
  font-size: 16px;
  margin-bottom: 12px;
}

.detail-features ul {
  list-style: none;
  padding: 0;
}

.detail-features li {
  padding: 8px 0;
  padding-left: 24px;
  position: relative;
  word-wrap: break-word;
}

.detail-features li::before {
  content: "?";
  position: absolute;
  left: 0;
  color: #667eea;
  font-weight: 600;
}

/* 规格参数表格 */
.specs-table {
  display: flex;
  flex-direction: column;
}

.spec-row {
  display: flex;
  padding: 16px;
  border-bottom: 1px solid #30363d;
}

.spec-row:last-child {
  border-bottom: none;
}

.spec-label {
  width: 120px;
  color: #8b949e;
  font-size: 14px;
  flex-shrink: 0;
}

.spec-value {
  color: #ffffff;
  font-size: 14px;
  flex: 1;
  word-wrap: break-word;
}

/* 评价摘要 */
.reviews-summary {
  display: flex;
  justify-content: center;
  padding: 32px;
  background: #21262d;
  border-radius: 12px;
  margin-bottom: 24px;
}

.summary-rating {
  text-align: center;
}

.rating-score {
  font-size: 48px;
  font-weight: 700;
  color: #ffc107;
  line-height: 1;
  margin-bottom: 8px;
}

.rating-stars {
  display: flex;
  justify-content: center;
  gap: 4px;
  margin-bottom: 8px;
}

.rating-stars span {
  color: #30363d;
  font-size: 24px;
  line-height: 24px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 24px;
  height: 24px;
  text-align: center;
  vertical-align: middle;
  box-sizing: border-box;
}

.rating-stars span.filled {
  color: #ffc107;
}

.rating-total {
  color: #8b949e;
  font-size: 14px;
}

/* 评论表单 */
.review-form {
  background: #21262d;
  border-radius: 12px;
  padding: 24px;
  margin-bottom: 24px;
  flex-shrink: 0;
}

.review-form h4 {
  color: #ffffff;
  font-size: 18px;
  margin-bottom: 16px;
}

.rating-input {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 16px;
}

.rating-input > span:first-child {
  color: #c9d1d9;
  font-size: 14px;
}

.star-rating-input {
  display: flex;
  gap: 4px;
  align-items: center;
}

.star-rating-input span {
  color: #30363d;
  font-size: 28px !important;
  line-height: 28px;
  cursor: pointer;
  transition: all 0.2s;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 28px;
  height: 28px;
  text-align: center;
  vertical-align: middle;
  box-sizing: border-box;
}

.star-rating-input span:hover {
  transform: scale(1.2);
}

.star-rating-input span.filled {
  color: #ffc107;
}

.review-textarea {
  width: 100%;
  background: #161b22;
  border: 1px solid #30363d;
  border-radius: 8px;
  padding: 12px;
  color: #c9d1d9;
  font-size: 14px;
  line-height: 1.6;
  resize: none;
  margin-bottom: 12px;
  font-family: inherit;
}

.review-textarea:focus {
  outline: none;
  border-color: #667eea;
}

.review-textarea::placeholder {
  color: #8b949e;
}

.review-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.char-count {
  color: #8b949e;
  font-size: 13px;
}

.submit-review-btn {
  padding: 10px 24px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
}

.submit-review-btn:hover:not(:disabled) {
  transform: scale(1.02);
  box-shadow: 0 4px 15px rgba(102, 126, 234, 0.4);
}

.submit-review-btn:disabled {
  background: #30363d;
  color: #8b949e;
  cursor: not-allowed;
}

/* 评论列表 */
.reviews-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
  max-height: 400px;
  overflow-y: auto;
  padding-right: 10px;
}

.review-item {
  padding: 20px;
  background: #21262d;
  border-radius: 12px;
}

.review-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
  flex-wrap: wrap;
  gap: 8px;
}

.review-user {
  color: #ffffff;
  font-weight: 500;
}

.review-stars {
  display: flex;
  gap: 4px;
}

.review-stars span {
  color: #30363d;
  font-size: 16px;
  line-height: 16px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 16px;
  height: 16px;
  text-align: center;
  vertical-align: middle;
  box-sizing: border-box;
}

.review-stars span.filled {
  color: #ffc107;
}

.review-content {
  color: #c9d1d9;
  line-height: 1.6;
  margin-bottom: 8px;
  word-wrap: break-word;
}

.review-date {
  font-size: 13px;
  color: #8b949e;
}

/* 订单弹窗 */
.order-modal {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.8);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1002;
}

.order-content {
  background: #161b22;
  border-radius: 20px;
  padding: 40px;
  max-width: 500px;
  width: 100%;
  border: 1px solid #30363d;
}

.order-content h2 {
  font-size: 24px;
  color: #ffffff;
  margin-bottom: 24px;
  text-align: center;
}

.order-items {
  margin-bottom: 24px;
}

.order-item {
  display: flex;
  gap: 16px;
  padding: 16px;
  background: #21262d;
  border-radius: 12px;
  margin-bottom: 12px;
}

.order-item img {
  width: 80px;
  height: 80px;
  border-radius: 8px;
  object-fit: cover;
}

.order-item-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.order-item-info h4 {
  color: #ffffff;
  font-size: 16px;
  margin-bottom: 8px;
}

.order-item-info span {
  color: #8b949e;
  font-size: 14px;
}

.item-total {
  color: #ff6b6b !important;
  font-weight: 600;
}

.order-summary {
  margin-bottom: 24px;
  padding: 20px;
  background: #21262d;
  border-radius: 12px;
}

.summary-row {
  display: flex;
  justify-content: space-between;
  color: #c9d1d9;
  margin-bottom: 12px;
}

.summary-row.total {
  margin-top: 16px;
  padding-top: 16px;
  border-top: 1px solid #30363d;
  font-size: 18px;
  font-weight: 600;
  color: #ffffff;
}

.summary-row.total span:last-child {
  color: #ff6b6b;
  font-size: 24px;
}

.order-payment {
  margin-bottom: 24px;
}

.order-payment h4 {
  color: #ffffff;
  margin-bottom: 16px;
}

.payment-options {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.payment-option {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 14px;
  background: #21262d;
  border-radius: 10px;
  cursor: pointer;
  transition: all 0.3s;
  color: #c9d1d9;
}

.payment-option:hover {
  background: #30363d;
}

.payment-option input {
  accent-color: #667eea;
}

.pay-btn {
  width: 100%;
  padding: 18px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  border-radius: 12px;
  font-size: 18px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
}

.pay-btn:hover {
  transform: scale(1.02);
  box-shadow: 0 4px 20px rgba(102, 126, 234, 0.5);
}

/* 购物车动画 */
.cart-animation {
  position: fixed;
  width: 60px;
  height: 60px;
  border-radius: 50%;
  overflow: hidden;
  z-index: 2000;
  pointer-events: none;
  animation: flyToCart 0.8s ease-in-out forwards;
}

.cart-animation img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

@keyframes flyToCart {
  0% {
    opacity: 1;
    transform: scale(1);
  }
  100% {
    opacity: 0;
    transform: scale(0.3) translate(200px, 400px);
  }
}

</style>
