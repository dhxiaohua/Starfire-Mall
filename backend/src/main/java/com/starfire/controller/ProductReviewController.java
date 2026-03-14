package com.starfire.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.starfire.dto.ApiResponse;
import com.starfire.entity.ProductReview;
import com.starfire.service.ProductReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/reviews")
public class ProductReviewController {
    
    @Autowired
    private ProductReviewService reviewService;
    
    // 获取产品评论列表
    @GetMapping("/product/{productId}")
    public ApiResponse<Map<String, Object>> getProductReviews(
            @PathVariable Long productId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Page<ProductReview> pageResult = reviewService.getProductReviews(productId, page, size);
        
        Map<String, Object> data = new HashMap<>();
        data.put("reviews", pageResult.getRecords());
        data.put("total", pageResult.getTotal());
        
        return ApiResponse.success(data);
    }
    
    // 添加评论
    @PostMapping("/add")
    public ApiResponse<String> addReview(@RequestBody Map<String, Object> request) {
        Long productId = Long.valueOf(request.get("productId").toString());
        String username = (String) request.get("username");
        Integer rating = (Integer) request.get("rating");
        String content = (String) request.get("content");
        
        if (username == null || content == null || content.isEmpty()) {
            return ApiResponse.error("请填写评论内容");
        }
        
        if (rating == null || rating < 1 || rating > 5) {
            rating = 5;
        }
        
        boolean success = reviewService.addReview(productId, username, rating, content);
        
        if (success) {
            return ApiResponse.success("评论添加成功", null);
        } else {
            return ApiResponse.error("评论添加失败");
        }
    }
    
    // 更新评论
    @PutMapping("/update")
    public ApiResponse<String> updateReview(@RequestBody Map<String, Object> request) {
        Long reviewId = Long.valueOf(request.get("reviewId").toString());
        String username = (String) request.get("username");
        Integer rating = (Integer) request.get("rating");
        String content = (String) request.get("content");
        
        if (username == null || content == null || content.isEmpty()) {
            return ApiResponse.error("请填写评论内容");
        }
        
        boolean success = reviewService.updateReview(reviewId, username, rating, content);
        
        if (success) {
            return ApiResponse.success("评论更新成功", null);
        } else {
            return ApiResponse.error("评论更新失败，您只能修改自己的评论");
        }
    }
    
    // 删除评论
    @DeleteMapping("/delete")
    public ApiResponse<String> deleteReview(@RequestBody Map<String, Object> request) {
        Long reviewId = Long.valueOf(request.get("reviewId").toString());
        String username = (String) request.get("username");
        Boolean isAdmin = (Boolean) request.get("isAdmin");
        
        boolean success = reviewService.deleteReview(reviewId, username, isAdmin != null && isAdmin);
        
        if (success) {
            return ApiResponse.success("评论删除成功", null);
        } else {
            return ApiResponse.error("评论删除失败，您只能删除自己的评论");
        }
    }
}
