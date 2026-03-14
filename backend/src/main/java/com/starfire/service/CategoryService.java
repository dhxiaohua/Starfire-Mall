package com.starfire.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.starfire.entity.Category;
import com.starfire.entity.Product;
import com.starfire.mapper.CategoryMapper;
import com.starfire.mapper.ProductMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {
    
    private final CategoryMapper categoryMapper;
    private final ProductMapper productMapper;
    
    public CategoryService(CategoryMapper categoryMapper, ProductMapper productMapper) {
        this.categoryMapper = categoryMapper;
        this.productMapper = productMapper;
    }
    
    // 获取所有启用的分类
    public List<Category> getAllCategories() {
        QueryWrapper<Category> wrapper = new QueryWrapper<>();
        wrapper.eq("status", 1);
        wrapper.orderByAsc("sort_order");
        return categoryMapper.selectList(wrapper);
    }
    
    // 获取分类及其商品数量
    public List<Category> getCategoriesWithCount() {
        List<Category> categories = getAllCategories();
        for (Category category : categories) {
            QueryWrapper<Product> wrapper = new QueryWrapper<>();
            wrapper.eq("category_id", category.getId());
            wrapper.eq("status", 1);
            Long count = productMapper.selectCount(wrapper);
            category.setSortOrder(count.intValue()); // 临时存储商品数量
        }
        return categories;
    }
    
    // 根据slug获取分类
    public Category getCategoryBySlug(String slug) {
        QueryWrapper<Category> wrapper = new QueryWrapper<>();
        wrapper.eq("slug", slug);
        return categoryMapper.selectOne(wrapper);
    }
    
    // 根据ID获取分类
    public Category getCategoryById(Long id) {
        return categoryMapper.selectById(id);
    }
}
