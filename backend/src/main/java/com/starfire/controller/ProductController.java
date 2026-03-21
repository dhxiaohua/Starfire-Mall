package com.starfire.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.starfire.dto.ApiResponse;
import com.starfire.dto.ProductVO;
import com.starfire.entity.Category;
import com.starfire.entity.Product;
import com.starfire.service.CategoryService;
import com.starfire.service.ProductService;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    
    private final ProductService productService;
    private final CategoryService categoryService;
    
    public ProductController(ProductService productService, CategoryService categoryService) {
        this.productService = productService;
        this.categoryService = categoryService;
    }
    
    // 获取商品列表
    @GetMapping
    public ApiResponse<Map<String, Object>> getProducts(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "12") int size) {
        
        Page<Product> pageResult = productService.getProductList(category, keyword, page, size);
        
        Map<String, Object> data = new HashMap<>();
        data.put("products", pageResult.getRecords().stream().map(this::toVO).collect(Collectors.toList()));
        data.put("total", pageResult.getTotal());
        data.put("pages", pageResult.getPages());
        data.put("current", pageResult.getCurrent());
        
        return ApiResponse.success(data);
    }
    
    // 获取商品详情
    @GetMapping("/{id}")
    public ApiResponse<ProductVO> getProduct(@PathVariable Long id) {
        Product product = productService.getProductById(id);
        
        if (product == null) {
            return ApiResponse.error("商品不存在");
        }
        
        ProductVO vo = toVO(product);
        // 填充分类信息
        if (product.getCategoryId() != null) {
            Category category = categoryService.getCategoryById(product.getCategoryId());
            if (category != null) {
                vo.setCategory(category.getName());
            }
        }
        
        return ApiResponse.success(vo);
    }
    
    // 获取商品分类列表（公开 - 所有用户可见）
    @GetMapping("/categories")
    public ApiResponse<List<Category>> getCategories() {
        List<Category> categories = categoryService.getAllCategories();
        return ApiResponse.success(categories);
    }
    
    // 添加商品（管理员）
    @PostMapping
    public ApiResponse<Product> addProduct(@RequestBody Product product) {
        System.out.println("ProductController: 收到添加商品请求，商品名称: " + product.getName());
        
        Product savedProduct = productService.addProduct(product);
        
        if (savedProduct != null) {
            System.out.println("ProductController: 商品添加成功，ID: " + savedProduct.getId());
            return ApiResponse.success(savedProduct);
        } else {
            System.out.println("ProductController: 商品添加失败");
            return ApiResponse.error("商品添加失败");
        }
    }
    
    // 更新商品（管理员）
    @PutMapping("/{id}")
    public ApiResponse<String> updateProduct(@PathVariable Long id, @RequestBody Product product) {
        product.setId(id);
        boolean success = productService.updateProduct(product);
        
        if (success) {
            return ApiResponse.success("商品更新成功", null);
        } else {
            return ApiResponse.error("商品更新失败");
        }
    }
    
    // 删除商品（管理员）
    @DeleteMapping("/{id}")
    public ApiResponse<String> deleteProduct(@PathVariable Long id) {
        boolean success = productService.deleteProduct(id);
        
        if (success) {
            return ApiResponse.success("商品删除成功", null);
        } else {
            return ApiResponse.error("商品删除失败");
        }
    }
    
    // 更新库存（管理员）
    @PutMapping("/{id}/stock")
    public ApiResponse<String> updateStock(@PathVariable Long id, @RequestBody Map<String, Integer> request) {
        Integer stock = request.get("stock");
        
        if (stock == null || stock < 0) {
            return ApiResponse.error("库存数量无效");
        }
        
        boolean success = productService.updateStock(id, stock);
        
        if (success) {
            return ApiResponse.success("库存更新成功", null);
        } else {
            return ApiResponse.error("库存更新失败");
        }
    }
    
    private ProductVO toVO(Product product) {
        ProductVO vo = new ProductVO();
        BeanUtils.copyProperties(product, vo);
        return vo;
    }
    
    // 获取销售统计
    @GetMapping("/stats")
    public ApiResponse<Map<String, Object>> getSalesStats() {
        Map<String, Object> stats = productService.getSalesStats();
        return ApiResponse.success(stats);
    }
}
