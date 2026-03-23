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
        
        <!-- 收货地址 -->
        <div class="order-section order-address">
          <div class="section-header">
            <h3>收货地址</h3>
            <button class="edit-address-btn" @click="openAddressModal">
              <svg viewBox="0 0 20 20" fill="currentColor">
                <path d="M13.586 3.586a2 2 0 112.828 2.828l-.793.793-2.828-2.828.793-.793zM11.379 5.793L3 14.172V17h2.828l8.38-8.379-2.83-2.828z" />
              </svg>
              管理/添加地址
            </button>
          </div>
          <div v-if="selectedAddress" class="address-info">
            <div class="address-item">
              <span class="address-label">收货人：</span>
              <span>{{ selectedAddress.name }}</span>
              <span class="default-badge" v-if="selectedAddress.isDefault">默认</span>
            </div>
            <div class="address-item">
              <span class="address-label">联系电话：</span>
              <span>{{ selectedAddress.phone }}</span>
            </div>
            <div class="address-item">
              <span class="address-label">收货地址：</span>
              <span>{{ selectedAddress.province }} {{ selectedAddress.city }} {{ selectedAddress.district }} {{ selectedAddress.detail }}</span>
            </div>
          </div>
          <div v-else class="address-empty">
            <span>📍</span>
            <p>请选择或添加收货地址</p>
          </div>
        </div>
        
        <!-- 商品清单 -->
        <div class="order-section order-items">
          <h3>商品清单</h3>
          <div class="order-header">
            <span class="col-product">商品信息</span>
            <span class="col-price">单价</span>
            <span class="col-quantity">数量</span>
            <span class="col-total">小计</span>
          </div>
          <div class="order-item" v-for="(item, index) in orderItems" :key="index">
            <div class="item-product">
              <img :src="item.image" :alt="item.name" />
              <div class="item-details">
                <h4>{{ item.name }}</h4>
                <span class="item-category">{{ getCategoryName(item.category) }}</span>
                <div class="item-features" v-if="item.features && item.features.length > 0">
                  <span v-for="(feat, i) in item.features.slice(0, 3)" :key="i" class="feature-tag">{{ feat }}</span>
                </div>
              </div>
            </div>
            <span class="item-price">￥{{ item.price.toFixed(2) }}</span>
            <span class="item-quantity">× {{ item.quantity }}</span>
            <span class="item-total">￥{{ (item.price * item.quantity).toFixed(2) }}</span>
          </div>
        </div>
        
        <!-- 配送方式 -->
        <div class="order-section order-delivery">
          <h3>配送方式</h3>
          <div class="delivery-options">
            <label class="delivery-option">
              <input type="radio" name="delivery" value="express" checked />
              <div class="delivery-info">
                <span class="delivery-name">顺丰快递</span>
                <span class="delivery-time">预计3-5天送达</span>
                <span class="delivery-fee">免运费</span>
              </div>
            </label>
            <label class="delivery-option">
              <input type="radio" name="delivery" value="standard" />
              <div class="delivery-info">
                <span class="delivery-name">标准快递</span>
                <span class="delivery-time">预计5-7天送达</span>
                <span class="delivery-fee">免运费</span>
              </div>
            </label>
          </div>
        </div>
        
        <!-- 订单备注 -->
        <div class="order-section order-remark">
          <h3>订单备注</h3>
          <textarea 
            v-model="orderRemark"
            class="remark-input"
            placeholder="选填，可以告诉商家您对订单的特殊要求..."
            rows="3"
            maxlength="200"
          ></textarea>
          <span class="char-count">{{ orderRemark.length }}/200</span>
        </div>
        
        <!-- 费用明细 -->
        <div class="order-section order-summary">
          <h3>费用明细</h3>
          <div class="summary-row">
            <span>商品总价：</span>
            <span>￥{{ orderTotal.toFixed(2) }}</span>
          </div>
          <div class="summary-row">
            <span>运费：</span>
            <span class="free-shipping">免运费</span>
          </div>
          <div class="summary-row" v-if="discount > 0">
            <span>优惠金额：</span>
            <span class="discount">-￥{{ discount.toFixed(2) }}</span>
          </div>
          <div class="summary-row total">
            <span>应付总额：</span>
            <span class="total-price">￥{{ (orderTotal - discount).toFixed(2) }}</span>
          </div>
        </div>
        
        <!-- 支付方式 -->
        <div class="order-section order-payment">
          <h3>支付方式</h3>
          <div class="payment-options">
            <label class="payment-option" :class="{ active: selectedPayment === 'alipay' }">
              <input type="radio" name="payment" value="alipay" v-model="selectedPayment" />
              <div class="payment-info">
                <span class="payment-icon">💳</span>
                <div class="payment-details">
                  <span class="payment-name">支付宝</span>
                  <span class="payment-desc">推荐使用支付宝支付</span>
                </div>
              </div>
            </label>
            <label class="payment-option" :class="{ active: selectedPayment === 'wechat' }">
              <input type="radio" name="payment" value="wechat" v-model="selectedPayment" />
              <div class="payment-info">
                <span class="payment-icon">💚</span>
                <div class="payment-details">
                  <span class="payment-name">微信支付</span>
                  <span class="payment-desc">便捷安全的微信支付</span>
                </div>
              </div>
            </label>
            <label class="payment-option" :class="{ active: selectedPayment === 'card' }">
              <input type="radio" name="payment" value="card" v-model="selectedPayment" />
              <div class="payment-info">
                <span class="payment-icon">🏦</span>
                <div class="payment-details">
                  <span class="payment-name">银行卡</span>
                  <span class="payment-desc">支持各大银行储蓄卡</span>
                </div>
              </div>
            </label>
          </div>
        </div>
        
        <!-- 底部操作 -->
        <div class="order-footer">
          <div class="footer-total">
            <span>应付总额：</span>
            <span class="total-amount">￥{{ (orderTotal - discount).toFixed(2) }}</span>
          </div>
          <div class="footer-actions">
            <button class="cancel-pay-btn" @click="cancelPayment">取消支付</button>
            <button class="pay-btn" @click="processPayment">确认支付</button>
          </div>
        </div>
      </div>
    </div>

    <!-- 地址管理弹窗 -->
    <div class="address-modal" v-if="showAddressModal" @click.self="closeAddressModal">
      <div class="address-modal-content">
        <div class="modal-header">
          <h2>{{ editingAddress ? '编辑收货地址' : '添加收货地址' }}</h2>
          <button class="close-modal" @click="closeAddressModal">
            <svg viewBox="0 0 20 20" fill="currentColor">
              <path fill-rule="evenodd" d="M4.293 4.293a1 1 0 011.414 0L10 8.586l4.293-4.293a1 1 0 111.414 1.414L11.414 10l4.293 4.293a1 1 0 01-1.414 1.414L10 11.414l-4.293 4.293a1 1 0 01-1.414-1.414L8.586 10 4.293 5.707a1 1 0 010-1.414z" clip-rule="evenodd" />
            </svg>
          </button>
        </div>

        <!-- 地址列表 -->
        <div class="address-list-wrapper" v-if="!showAddressForm">
          <div class="address-list">
            <div class="address-item-card" v-for="address in addresses" :key="address.id" :class="{ active: selectedAddress?.id === address.id }">
              <div class="address-card-info">
                <div class="address-card-header">
                  <span class="address-name">{{ address.name }}</span>
                  <span class="address-phone">{{ address.phone }}</span>
                  <span class="default-badge" v-if="address.isDefault">默认</span>
                </div>
                <div class="address-card-detail">
                  {{ address.province }} {{ address.city }} {{ address.district }} {{ address.detail }}
                </div>
              </div>
              <div class="address-card-actions">
                <button class="action-btn" @click="selectAddress(address)" v-if="selectedAddress?.id !== address.id">
                  选择
                </button>
                <button class="action-btn" @click="editAddress(address)">
                  <svg viewBox="0 0 20 20" fill="currentColor">
                    <path d="M13.586 3.586a2 2 0 112.828 2.828l-.793.793-2.828-2.828.793-.793zM11.379 5.793L3 14.172V17h2.828l8.38-8.379-2.83-2.828z" />
                  </svg>
                </button>
                <button class="action-btn danger" @click="deleteAddress(address.id)" v-if="addresses.length > 1">
                  <svg viewBox="0 0 20 20" fill="currentColor">
                    <path fill-rule="evenodd" d="M9 2a1 1 0 00-.894.553L7.382 4H4a1 1 0 000 2v10a2 2 0 002 2h8a2 2 0 002-2V6a1 1 0 100-2h-3.382l-.724-1.447A1 1 0 0011 2H9zM7 8a1 1 0 012 0v6a1 1 0 11-2 0V8zm5-1a1 1 0 00-1 1v6a1 1 0 102 0V8a1 1 0 00-1-1z" clip-rule="evenodd" />
                  </svg>
                </button>
              </div>
            </div>
          </div>
          <button class="add-address-btn" @click="startAddAddress" type="button" style="cursor: pointer !important; pointer-events: auto !important;">
            <svg viewBox="0 0 20 20" fill="currentColor">
              <path fill-rule="evenodd" d="M10 3a1 1 0 011 1v5h5a1 1 0 110 2h-5v5a1 1 0 11-2 0v-5H4a1 1 0 110-2h5V4a1 1 0 011-1z" clip-rule="evenodd" />
            </svg>
            添加新地址
          </button>
        </div>

        <!-- 地址表单 -->
        <div class="address-form" v-else>
          <div class="form-group">
            <label>收货人 *</label>
            <input
              type="text"
              v-model="addressForm.name"
              placeholder="请输入收货人姓名"
              maxlength="20"
            />
          </div>
          <div class="form-group">
            <label>联系电话 *</label>
            <input
              type="tel"
              v-model="addressForm.phone"
              placeholder="请输入手机号码"
              maxlength="11"
            />
          </div>
          <div class="form-row">
            <div class="form-group">
              <label>省份 *</label>
              <select v-model="addressForm.province">
                <option value="上海市">上海市</option>
                <option value="北京市">北京市</option>
                <option value="广东省">广东省</option>
                <option value="浙江省">浙江省</option>
                <option value="江苏省">江苏省</option>
                <option value="四川省">四川省</option>
              </select>
            </div>
            <div class="form-group">
              <label>城市 *</label>
              <select v-model="addressForm.city">
                <option value="上海市">上海市</option>
                <option value="北京市">北京市</option>
                <option value="广州市">广州市</option>
                <option value="深圳市">深圳市</option>
                <option value="杭州市">杭州市</option>
                <option value="南京市">南京市</option>
                <option value="成都市">成都市</option>
              </select>
            </div>
          </div>
          <div class="form-group">
            <label>区/县 *</label>
            <select v-model="addressForm.district">
              <option value="浦东新区">浦东新区</option>
              <option value="黄浦区">黄浦区</option>
              <option value="徐汇区">徐汇区</option>
              <option value="长宁区">长宁区</option>
              <option value="静安区">静安区</option>
              <option value="普陀区">普陀区</option>
              <option value="虹口区">虹口区</option>
              <option value="杨浦区">杨浦区</option>
              <option value="闵行区">闵行区</option>
              <option value="宝山区">宝山区</option>
              <option value="嘉定区">嘉定区</option>
              <option value="松江区">松江区</option>
              <option value="青浦区">青浦区</option>
              <option value="奉贤区">奉贤区</option>
              <option value="金山区">金山区</option>
              <option value="崇明区">崇明区</option>
            </select>
          </div>
          <div class="form-group">
            <label>详细地址 *</label>
            <textarea
              v-model="addressForm.detail"
              placeholder="请输入详细地址，如街道、门牌号等"
              rows="3"
              maxlength="100"
            ></textarea>
          </div>
          <div class="form-group checkbox-group">
            <label class="checkbox-label">
              <input type="checkbox" v-model="addressForm.isDefault" />
              <span>设为默认地址</span>
            </label>
          </div>
          <div class="form-actions">
            <button class="cancel-btn" @click="cancelEditAddress">取消</button>
            <button class="save-btn" @click="saveAddress">保存地址</button>
          </div>
        </div>
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
import { getProducts, getProductReviews, addProductReview, createOrder, completePayment, cancelOrder, getUserOrders, getOrderDetail } from '../api/index'

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
    const selectedPayment = ref('alipay')
    const orderRemark = ref('')
    const discount = ref(0)
    const cartItems = ref([])

    // 地址管理
    const addresses = ref([
      {
        id: 1,
        name: '张三',
        phone: '138****8888',
        province: '上海市',
        city: '上海市',
        district: '浦东新区',
        detail: '张江高科技园区科苑路88号',
        isDefault: true
      }
    ])
    const selectedAddress = ref(null)
    const showAddressModal = ref(false)
    const showAddressForm = ref(false)
    const editingAddress = ref(null)
    const addressForm = ref({
      name: '',
      phone: '',
      province: '上海市',
      city: '上海市',
      district: '浦东新区',
      detail: '',
      isDefault: false
    })
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

      // 初始化选中地址
      if (addresses.value.length > 0) {
        selectedAddress.value = addresses.value.find(addr => addr.isDefault) || addresses.value[0]
      }
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
      // 自动选中默认地址
      if (!selectedAddress.value && addresses.value.length > 0) {
        selectedAddress.value = addresses.value.find(addr => addr.isDefault) || addresses.value[0]
      }
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
      // 自动选中默认地址
      if (!selectedAddress.value && addresses.value.length > 0) {
        selectedAddress.value = addresses.value.find(addr => addr.isDefault) || addresses.value[0]
      }
      showOrderModal.value = true
    }
    
    // 关闭订单弹窗
    const closeOrderModal = () => {
      showOrderModal.value = false
    }
    
    // 处理支付（两步流程：先创建待支付订单，然后完成支付）
    const processPayment = async () => {
      if (!selectedAddress.value) {
        alert('请选择收货地址')
        return
      }

      // 从sessionStorage获取当前用户信息
      const userData = sessionStorage.getItem('currentUser')
      if (!userData) {
        alert('请先登录后再购买')
        return
      }

      let userId = null
      let username = 'guest'

      try {
        const user = JSON.parse(userData)
        console.log('从sessionStorage解析的用户数据：', user)

        userId = user.id
        username = user.username || user.username

        console.log('提取的userId：', userId)
        console.log('提取的username：', username)

        if (!userId) {
          alert('用户信息不完整，请重新登录')
          return
        }
      } catch (e) {
        console.error('解析用户数据失败', e)
        alert('用户信息错误，请重新登录')
        return
      }

      const paymentName = {
        alipay: '支付宝',
        wechat: '微信支付',
        card: '银行卡'
      }[selectedPayment.value] || '未知'

      // 准备订单项数据
      const items = orderItems.value.map(item => ({
        productId: item.id,
        productName: item.name,
        productImage: item.image || '',
        quantity: item.quantity,
        price: parseFloat(item.price.toFixed(2)),
        subtotal: parseFloat((item.price * item.quantity).toFixed(2))
      }))

      // 准备订单数据（后端会自动创建为pending状态）
      const orderData = {
        userId: parseInt(userId),
        username: username,
        receiverName: selectedAddress.value.name,
        receiverPhone: selectedAddress.value.phone,
        receiverAddress: `${selectedAddress.value.province} ${selectedAddress.value.city} ${selectedAddress.value.district} ${selectedAddress.value.detail}`,
        totalAmount: parseFloat(orderTotal.value.toFixed(2)),
        discountAmount: parseFloat(discount.value.toFixed(2)),
        payAmount: parseFloat((orderTotal.value - discount.value).toFixed(2)),
        payMethod: selectedPayment.value,
        remark: orderRemark.value || '',
        items: items
      }

      console.log('准备创建订单，数据：', orderData)

      // 第一步：创建订单（后端会创建为pending状态）
      try {
        const result = await createOrder(orderData)
        console.log('创建订单结果：', result)

        if (result.success) {
          const orderNo = result.data.order.orderNo
          const orderId = result.data.order.id

          // 第二步：完成支付
          const payResult = await completePayment(orderId)
          console.log('完成支付结果：', payResult)

          if (payResult.success) {
            alert(`支付成功！\n\n订单号：${orderNo}\n收货人：${selectedAddress.value.name}\n联系电话：${selectedAddress.value.phone}\n收货地址：${selectedAddress.value.province} ${selectedAddress.value.city} ${selectedAddress.value.district} ${selectedAddress.value.detail}\n\n支付方式：${paymentName}\n支付金额：￥${(orderTotal.value - discount.value).toFixed(2)}\n\n感谢您的购买，我们会尽快为您发货！`)

            // 清空购物车
            const selectedIds = orderItems.value.map(item => item.id)
            cartItems.value = cartItems.value.filter(item => !selectedIds.includes(item.id))

            // 重置订单状态
            showOrderModal.value = false
            selectedPayment.value = 'alipay'
            orderRemark.value = ''
            discount.value = 0
          } else {
            // 支付失败，但订单已创建（pending状态），提示用户可以去订单页面完成支付
            alert(`订单已创建，但支付失败！\n\n订单号：${orderNo}\n订单状态：待支付\n\n您可以在"我的订单"页面继续完成支付或取消订单。`)

            // 关闭订单弹窗，不清空购物车（用户可以重新选择）
            showOrderModal.value = false
          }
        } else {
          alert('订单创建失败：' + (result.message || '未知错误'))
        }
      } catch (error) {
        console.error('创建订单出错：', error)
        alert('订单创建失败：' + error.message)
      }
    }

    // 取消支付（创建待支付订单后）
    const cancelPayment = async () => {
      if (!selectedAddress.value) {
        alert('请选择收货地址')
        return
      }

      // 从sessionStorage获取当前用户信息
      const userData = sessionStorage.getItem('currentUser')
      if (!userData) {
        alert('请先登录后再购买')
        return
      }

      let userId = null
      let username = 'guest'

      try {
        const user = JSON.parse(userData)
        userId = user.id
        username = user.username || user.username

        if (!userId) {
          alert('用户信息不完整，请重新登录')
          return
        }
      } catch (e) {
        console.error('解析用户数据失败', e)
        alert('用户信息错误，请重新登录')
        return
      }

      // 准备订单项数据
      const items = orderItems.value.map(item => ({
        productId: item.id,
        productName: item.name,
        productImage: item.image || '',
        quantity: item.quantity,
        price: parseFloat(item.price.toFixed(2)),
        subtotal: parseFloat((item.price * item.quantity).toFixed(2))
      }))

      // 准备订单数据（后端会创建为pending状态）
      const orderData = {
        userId: parseInt(userId),
        username: username,
        receiverName: selectedAddress.value.name,
        receiverPhone: selectedAddress.value.phone,
        receiverAddress: `${selectedAddress.value.province} ${selectedAddress.value.city} ${selectedAddress.value.district} ${selectedAddress.value.detail}`,
        totalAmount: parseFloat(orderTotal.value.toFixed(2)),
        discountAmount: parseFloat(discount.value.toFixed(2)),
        payAmount: parseFloat((orderTotal.value - discount.value).toFixed(2)),
        payMethod: selectedPayment.value,
        remark: orderRemark.value || '',
        items: items
      }

      console.log('准备创建待支付订单，数据：', orderData)

      try {
        const result = await createOrder(orderData)
        console.log('创建订单结果：', result)

        if (result.success) {
          const orderNo = result.data.order.orderNo

          alert(`订单已创建！\n\n订单号：${orderNo}\n订单状态：待支付\n\n您可以在"我的订单"页面继续完成支付或取消订单。`)

          // 清空购物车中参与本次订单的商品
          const selectedIds = orderItems.value.map(item => item.id)
          cartItems.value = cartItems.value.filter(item => !selectedIds.includes(item.id))

          // 关闭订单弹窗
          showOrderModal.value = false
          selectedPayment.value = 'alipay'
          orderRemark.value = ''
          discount.value = 0
        } else {
          alert('订单创建失败：' + (result.message || '未知错误'))
        }
      } catch (error) {
        console.error('创建订单出错：', error)
        alert('订单创建失败：' + error.message)
      }
    }

    // 地址管理方法
    const openAddressModal = () => {
      // 如果没有选中地址且地址列表不为空，默认选择第一个
      if (!selectedAddress.value && addresses.value.length > 0) {
        selectedAddress.value = addresses.value.find(addr => addr.isDefault) || addresses.value[0]
      }
      showAddressModal.value = true
      editingAddress.value = null
    }

    const closeAddressModal = () => {
      showAddressModal.value = false
      editingAddress.value = null
      showAddressForm.value = false
      resetAddressForm()
    }

    const resetAddressForm = () => {
      addressForm.value = {
        name: '',
        phone: '',
        province: '上海市',
        city: '上海市',
        district: '浦东新区',
        detail: '',
        isDefault: false
      }
    }

    const selectAddress = (address) => {
      selectedAddress.value = address
      showAddressModal.value = false
    }

    const startAddAddress = () => {
      try {
        console.log('=== startAddAddress 开始执行 ===')
        console.log('当前 editingAddress:', editingAddress.value)
        console.log('当前 showAddressForm:', showAddressForm.value)
        
        // 清除编辑状态（表示是添加新地址，不是编辑）
        editingAddress.value = null
        
        // 重置表单
        resetAddressForm()
        
        // 显示表单
        showAddressForm.value = true
        
        console.log('设置后 showAddressForm:', showAddressForm.value)
        console.log('设置后 editingAddress:', editingAddress.value)
        console.log('=== startAddAddress 执行完成 ===')
      } catch (error) {
        console.error('startAddAddress 出错:', error)
        alert('打开添加地址表单时出错：' + error.message)
      }
    }

    const editAddress = (address) => {
      editingAddress.value = address
      addressForm.value = {
        name: address.name,
        phone: address.phone,
        province: address.province,
        city: address.city,
        district: address.district,
        detail: address.detail,
        isDefault: address.isDefault
      }
      // 显示表单
      showAddressForm.value = true
    }

    const cancelEditAddress = () => {
      editingAddress.value = null
      resetAddressForm()
      showAddressForm.value = false
    }

    const saveAddress = () => {
      // 验证表单
      if (!addressForm.value.name.trim()) {
        alert('请输入收货人姓名')
        return
      }
      if (!addressForm.value.phone.trim()) {
        alert('请输入联系电话')
        return
      }
      if (!/^1[3-9]\d{9}$/.test(addressForm.value.phone)) {
        alert('请输入正确的手机号码')
        return
      }
      if (!addressForm.value.detail.trim()) {
        alert('请输入详细地址')
        return
      }

      // 如果设置为默认地址，先取消所有其他地址的默认状态
      if (addressForm.value.isDefault) {
        addresses.value.forEach(addr => addr.isDefault = false)
      }

      if (editingAddress.value) {
        // 编辑现有地址
        const index = addresses.value.findIndex(addr => addr.id === editingAddress.value.id)
        if (index !== -1) {
          addresses.value[index] = {
            ...editingAddress.value,
            name: addressForm.value.name,
            phone: addressForm.value.phone,
            province: addressForm.value.province,
            city: addressForm.value.city,
            district: addressForm.value.district,
            detail: addressForm.value.detail,
            isDefault: addressForm.value.isDefault
          }
        }
        alert('地址修改成功')
      } else {
        // 添加新地址
        const newId = Math.max(...addresses.value.map(addr => addr.id), 0) + 1
        const newAddress = {
          id: newId,
          name: addressForm.value.name,
          phone: addressForm.value.phone,
          province: addressForm.value.province,
          city: addressForm.value.city,
          district: addressForm.value.district,
          detail: addressForm.value.detail,
          isDefault: addressForm.value.isDefault
        }

        addresses.value.push(newAddress)
        alert('地址添加成功')
      }

      // 如果这是第一个地址或设为默认，自动选中
      if (addresses.value.length === 1 || addressForm.value.isDefault) {
        selectedAddress.value = addresses.value.find(addr => addr.isDefault) || addresses.value[addresses.value.length - 1]
      }

      editingAddress.value = null
      resetAddressForm()
      // 返回列表视图
      showAddressForm.value = false
    }

    const deleteAddress = (id) => {
      if (!confirm('确定要删除这个地址吗？')) {
        return
      }

      const index = addresses.value.findIndex(addr => addr.id === id)
      if (index !== -1) {
        addresses.value.splice(index, 1)

        // 如果删除的是选中的地址，重新选择
        if (selectedAddress.value?.id === id) {
          selectedAddress.value = addresses.value.length > 0 ? addresses.value[0] : null
        }

        alert('地址删除成功')
      }
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
      selectedPayment,
      orderRemark,
      discount,
      addresses,
      selectedAddress,
      showAddressModal,
      showAddressForm,
      editingAddress,
      addressForm,
      openAddressModal,
      closeAddressModal,
      selectAddress,
      startAddAddress,
      editAddress,
      cancelEditAddress,
      saveAddress,
      deleteAddress,
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
      cancelPayment,
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
  overflow-y: auto;
  padding: 20px;
}

.order-content {
  background: #161b22;
  border-radius: 20px;
  padding: 40px;
  max-width: 700px;
  width: 100%;
  max-height: 90vh;
  overflow-y: auto;
  border: 1px solid #30363d;
}

.order-content h2 {
  font-size: 28px;
  color: #ffffff;
  margin-bottom: 32px;
  text-align: center;
  font-weight: 700;
}

.order-section {
  margin-bottom: 24px;
  padding: 20px;
  background: #21262d;
  border-radius: 12px;
}

.order-section h3 {
  color: #ffffff;
  font-size: 18px;
  margin-bottom: 16px;
  font-weight: 600;
  display: flex;
  align-items: center;
  gap: 8px;
}

.order-section h3::before {
  content: '';
  width: 4px;
  height: 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 2px;
}

/* 收货地址 */
.order-address .address-info {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.address-item {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #c9d1d9;
}

.address-label {
  color: #8b949e;
  font-size: 14px;
  min-width: 80px;
}

/* 商品清单 */
.order-header {
  display: flex;
  padding: 12px 16px;
  background: #30363d;
  border-radius: 8px;
  margin-bottom: 12px;
  font-size: 14px;
  color: #8b949e;
  font-weight: 600;
}

.col-product {
  flex: 2;
}

.col-price,
.col-quantity,
.col-total {
  flex: 1;
  text-align: center;
}

.order-item {
  display: flex;
  align-items: center;
  padding: 16px;
  background: #30363d;
  border-radius: 8px;
  margin-bottom: 12px;
  gap: 16px;
}

.item-product {
  flex: 2;
  display: flex;
  gap: 12px;
  align-items: center;
}

.item-product img {
  width: 60px;
  height: 60px;
  border-radius: 8px;
  object-fit: cover;
}

.item-details {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.item-details h4 {
  color: #ffffff;
  font-size: 15px;
  margin: 0;
  line-height: 1.4;
}

.item-category {
  color: #8b949e;
  font-size: 13px;
}

.item-features {
  display: flex;
  gap: 6px;
  flex-wrap: wrap;
}

.feature-tag {
  background: #484f58;
  color: #c9d1d9;
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 12px;
}

.item-price,
.item-quantity,
.item-total {
  flex: 1;
  text-align: center;
  color: #c9d1d9;
  font-size: 14px;
}

.item-total {
  color: #ff6b6b;
  font-weight: 600;
  font-size: 16px;
}

/* 配送方式 */
.delivery-options {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.delivery-option {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px;
  background: #30363d;
  border-radius: 10px;
  cursor: pointer;
  transition: all 0.3s;
  border: 2px solid transparent;
}

.delivery-option:hover {
  background: #3d444d;
}

.delivery-option input:checked + .delivery-info {
  border-color: #667eea;
}

.delivery-info {
  flex: 1;
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 8px;
  border-radius: 8px;
  border: 2px solid transparent;
  transition: all 0.3s;
}

.delivery-name {
  font-weight: 600;
  color: #ffffff;
}

.delivery-time,
.delivery-fee {
  color: #8b949e;
  font-size: 13px;
}

/* 订单备注 */
.order-remark {
  position: relative;
}

.remark-input {
  width: 100%;
  background: #30363d;
  border: 1px solid #484f58;
  border-radius: 8px;
  padding: 12px;
  color: #c9d1d9;
  font-size: 14px;
  resize: none;
  font-family: inherit;
  transition: all 0.3s;
}

.remark-input:focus {
  outline: none;
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

.remark-input::placeholder {
  color: #8b949e;
}

.char-count {
  position: absolute;
  bottom: 12px;
  right: 12px;
  color: #8b949e;
  font-size: 12px;
}

/* 费用明细 */
.summary-row {
  display: flex;
  justify-content: space-between;
  color: #c9d1d9;
  margin-bottom: 12px;
  font-size: 14px;
}

.summary-row.total {
  margin-top: 16px;
  padding-top: 16px;
  border-top: 1px solid #484f58;
  font-size: 18px;
  font-weight: 600;
  color: #ffffff;
}

.summary-row.total .total-price {
  color: #ff6b6b;
  font-size: 24px;
  font-weight: 700;
}

.free-shipping {
  color: #3fb950;
}

.discount {
  color: #3fb950;
}

/* 支付方式 */
.payment-options {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.payment-option {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px;
  background: #30363d;
  border-radius: 10px;
  cursor: pointer;
  transition: all 0.3s;
  border: 2px solid transparent;
}

.payment-option:hover {
  background: #3d444d;
}

.payment-option.active {
  border-color: #667eea;
  background: #3d444d;
}

.payment-option input {
  display: none;
}

.payment-info {
  flex: 1;
  display: flex;
  align-items: center;
  gap: 12px;
}

.payment-icon {
  font-size: 24px;
}

.payment-details {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.payment-name {
  font-weight: 600;
  color: #ffffff;
}

.payment-desc {
  color: #8b949e;
  font-size: 13px;
}

/* 底部操作 */
.order-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 20px;
  padding: 24px 0 0;
  border-top: 1px solid #30363d;
  flex-wrap: wrap;
}

.footer-total {
  font-size: 16px;
  color: #c9d1d9;
  flex-shrink: 0;
}

.total-amount {
  font-size: 28px;
  color: #ff6b6b;
  font-weight: 700;
  margin-left: 8px;
}

.footer-actions {
  display: flex;
  gap: 12px;
  flex: 1;
  justify-content: flex-end;
}

.cancel-pay-btn {
  padding: 16px 24px;
  background: #30363d;
  color: #c9d1d9;
  border: 1px solid #484f58;
  border-radius: 12px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
  white-space: nowrap;
}

.cancel-pay-btn:hover {
  background: #3d444d;
  color: #ffffff;
  border-color: #8b949e;
}

.pay-btn {
  padding: 16px 24px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  border-radius: 12px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
  white-space: nowrap;
}

.pay-btn:hover {
  transform: scale(1.02);
  box-shadow: 0 4px 20px rgba(102, 126, 234, 0.5);
}

/* 地址管理弹窗 */
.address-modal {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.8);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1003;
  overflow-y: auto;
  padding: 20px;
  pointer-events: auto;
}

.address-modal-content {
  background: #161b22;
  border-radius: 20px;
  padding: 40px;
  max-width: 600px;
  width: 100%;
  max-height: 90vh;
  overflow-y: auto;
  overflow-x: hidden;
  border: 1px solid #30363d;
  position: relative;
  pointer-events: auto;
}

.modal-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 24px;
}

.modal-header h2 {
  font-size: 24px;
  color: #ffffff;
  margin: 0;
  font-weight: 700;
}

.close-modal {
  background: transparent;
  border: none;
  color: #8b949e;
  cursor: pointer;
  padding: 8px;
  border-radius: 8px;
  transition: all 0.3s;
  display: flex;
  align-items: center;
  justify-content: center;
}

.close-modal:hover {
  background: #30363d;
  color: #ffffff;
}

.close-modal svg {
  width: 24px;
  height: 24px;
}

/* 地址列表 */
.address-list-wrapper {
  display: flex;
  flex-direction: column;
  gap: 12px;
  pointer-events: auto;
  position: relative;
  z-index: 10;
  width: 100%;
  height: auto;
}

.address-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
  max-height: 400px;
  overflow-y: auto;
  overflow-x: hidden;
  padding-right: 8px;
  pointer-events: auto;
  width: 100%;
}

.address-item-card {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 20px;
  background: #21262d;
  border-radius: 12px;
  border: 2px solid transparent;
  transition: all 0.3s;
}

.address-item-card.active {
  border-color: #667eea;
  background: #30363d;
}

.address-item-card:hover {
  border-color: #484f58;
}

.address-card-info {
  flex: 1;
}

.address-card-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 8px;
}

.address-name {
  font-weight: 600;
  color: #ffffff;
  font-size: 16px;
}

.address-phone {
  color: #8b949e;
  font-size: 14px;
}

.address-card-detail {
  color: #c9d1d9;
  font-size: 14px;
  line-height: 1.6;
}

.address-card-actions {
  display: flex;
  gap: 8px;
}

.action-btn {
  background: #30363d;
  border: 1px solid #484f58;
  color: #c9d1d9;
  padding: 8px 12px;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
}

.action-btn:hover {
  background: #3d444d;
  color: #ffffff;
}

.action-btn.danger:hover {
  background: #da3633;
  border-color: #da3633;
  color: #ffffff;
}

.action-btn svg {
  width: 16px;
  height: 16px;
}

.add-address-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  padding: 14px 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  border-radius: 10px;
  font-size: 15px;
  font-weight: 600;
  cursor: pointer !important;
  transition: all 0.3s;
  position: relative;
  z-index: 100;
  pointer-events: auto !important;
  margin-top: 8px;
  user-select: none;
  -webkit-user-select: none;
  -moz-user-select: none;
  -ms-user-select: none;
}

.add-address-btn:hover {
  transform: scale(1.02);
  box-shadow: 0 4px 20px rgba(102, 126, 234, 0.5);
}

.add-address-btn svg {
  width: 20px;
  height: 20px;
}

/* 地址表单 */
.address-form {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.form-group label {
  color: #c9d1d9;
  font-size: 14px;
  font-weight: 600;
}

.form-group input,
.form-group select,
.form-group textarea {
  background: #30363d;
  border: 1px solid #484f58;
  border-radius: 8px;
  padding: 12px;
  color: #c9d1d9;
  font-size: 14px;
  font-family: inherit;
  transition: all 0.3s;
}

.form-group input:focus,
.form-group select:focus,
.form-group textarea:focus {
  outline: none;
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

.form-group textarea {
  resize: vertical;
  min-height: 80px;
}

.form-row {
  display: flex;
  gap: 16px;
}

.form-row .form-group {
  flex: 1;
}

.checkbox-group {
  flex-direction: row;
  align-items: center;
  justify-content: space-between;
}

.checkbox-label {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #c9d1d9;
  font-size: 14px;
  cursor: pointer;
  margin: 0;
}

.checkbox-label input[type="checkbox"] {
  width: 18px;
  height: 18px;
  accent-color: #667eea;
}

.form-actions {
  display: flex;
  gap: 12px;
  margin-top: 8px;
}

.cancel-btn,
.save-btn {
  flex: 1;
  padding: 14px;
  border: none;
  border-radius: 10px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
}

.cancel-btn {
  background: #30363d;
  color: #c9d1d9;
  border: 1px solid #484f58;
}

.cancel-btn:hover {
  background: #3d444d;
  color: #ffffff;
}

.save-btn {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.save-btn:hover {
  transform: scale(1.02);
  box-shadow: 0 4px 20px rgba(102, 126, 234, 0.5);
}

/* 收货地址编辑按钮 */
.edit-address-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 16px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
}

.edit-address-btn:hover {
  transform: scale(1.02);
  box-shadow: 0 2px 10px rgba(102, 126, 234, 0.4);
}

.edit-address-btn svg {
  width: 16px;
  height: 16px;
}

/* 默认地址标签 */
.default-badge {
  background: linear-gradient(135deg, #3fb950 0%, #2ea043 100%);
  color: white;
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 600;
}

/* 地址为空状态 */
.address-empty {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px 20px;
  color: #8b949e;
}

.address-empty span {
  font-size: 48px;
  margin-bottom: 16px;
}

.address-empty p {
  font-size: 14px;
  margin: 0;
}

/* 收货地址部分的头部 */
.order-address .section-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
}

.order-address .section-header h3 {
  margin: 0;
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
