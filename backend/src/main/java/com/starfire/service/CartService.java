package com.starfire.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.starfire.entity.Cart;
import com.starfire.entity.Product;
import com.starfire.mapper.CartMapper;
import com.starfire.mapper.ProductMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CartService {
    
    private final CartMapper cartMapper;
    private final ProductMapper productMapper;
    
    public CartService(CartMapper cartMapper, ProductMapper productMapper) {
        this.cartMapper = cartMapper;
        this.productMapper = productMapper;
    }
    
    // 获取用户购物车列表（私有 - 仅用户自己可见）
    public List<Cart> getUserCart(Long userId) {
        QueryWrapper<Cart> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        wrapper.orderByDesc("create_time");
        
        List<Cart> carts = cartMapper.selectList(wrapper);
        
        // 填充商品信息
        for (Cart cart : carts) {
            Product product = productMapper.selectById(cart.getProductId());
            cart.setProduct(product);
        }
        
        return carts;
    }
    
    // 添加商品到购物车
    public boolean addToCart(Long userId, Long productId, Integer quantity) {
        // 检查商品是否存在
        Product product = productMapper.selectById(productId);
        if (product == null || product.getStatus() != 1) {
            return false;
        }
        
        // 检查购物车是否已有该商品
        QueryWrapper<Cart> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        wrapper.eq("product_id", productId);
        Cart existingCart = cartMapper.selectOne(wrapper);
        
        if (existingCart != null) {
            // 更新数量
            existingCart.setQuantity(existingCart.getQuantity() + quantity);
            return cartMapper.updateById(existingCart) > 0;
        } else {
            // 新增购物车项
            Cart cart = new Cart();
            cart.setUserId(userId);
            cart.setProductId(productId);
            cart.setQuantity(quantity);
            return cartMapper.insert(cart) > 0;
        }
    }
    
    // 更新购物车商品数量
    public boolean updateCartQuantity(Long cartId, Long userId, Integer quantity) {
        QueryWrapper<Cart> wrapper = new QueryWrapper<>();
        wrapper.eq("id", cartId);
        wrapper.eq("user_id", userId);
        
        Cart cart = cartMapper.selectOne(wrapper);
        if (cart == null) {
            return false;
        }
        
        cart.setQuantity(quantity);
        return cartMapper.updateById(cart) > 0;
    }
    
    // 删除购物车项
    public boolean removeFromCart(Long cartId, Long userId) {
        QueryWrapper<Cart> wrapper = new QueryWrapper<>();
        wrapper.eq("id", cartId);
        wrapper.eq("user_id", userId);
        
        return cartMapper.delete(wrapper) > 0;
    }
    
    // 清空用户购物车
    public boolean clearCart(Long userId) {
        QueryWrapper<Cart> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        return cartMapper.delete(wrapper) > 0;
    }
    
    // 获取购物车商品数量
    public int getCartCount(Long userId) {
        QueryWrapper<Cart> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        Long count = cartMapper.selectCount(wrapper);
        return count != null ? count.intValue() : 0;
    }
}
