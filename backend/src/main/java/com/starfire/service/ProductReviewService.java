package com.starfire.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.starfire.entity.ProductReview;
import com.starfire.mapper.ProductReviewMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ProductReviewService {
    
    @Autowired
    private ProductReviewMapper reviewMapper;
    
    // 获取产品评论
    public Page<ProductReview> getProductReviews(Long productId, int page, int size) {
        Page<ProductReview> pageParam = new Page<>(page, size);
        QueryWrapper<ProductReview> wrapper = new QueryWrapper<>();
        wrapper.eq("product_id", productId).orderByDesc("create_time");
        return reviewMapper.selectPage(pageParam, wrapper);
    }
    
    // 添加评论
    public boolean addReview(Long productId, String username, Integer rating, String content) {
        ProductReview review = new ProductReview();
        review.setProductId(productId);
        review.setUsername(username);
        review.setRating(rating);
        review.setContent(content);
        review.setCreateTime(LocalDateTime.now());
        review.setUpdateTime(LocalDateTime.now());
        return reviewMapper.insert(review) > 0;
    }
    
    // 更新评论
    public boolean updateReview(Long reviewId, String username, Integer rating, String content) {
        ProductReview review = reviewMapper.selectById(reviewId);
        if (review == null || !review.getUsername().equals(username)) {
            return false;
        }
        review.setRating(rating);
        review.setContent(content);
        review.setUpdateTime(LocalDateTime.now());
        return reviewMapper.updateById(review) > 0;
    }
    
    // 删除评论
    public boolean deleteReview(Long reviewId, String username, boolean isAdmin) {
        ProductReview review = reviewMapper.selectById(reviewId);
        if (review == null) {
            return false;
        }
        // 只有评论者本人或管理员可以删除
        if (!review.getUsername().equals(username) && !isAdmin) {
            return false;
        }
        return reviewMapper.deleteById(reviewId) > 0;
    }
}
