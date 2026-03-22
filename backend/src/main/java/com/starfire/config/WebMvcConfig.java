package com.starfire.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC 配置类
 * 用于配置字符编码过滤器，解决中文乱码问题
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    
    /**
     * 字符编码过滤器
     * 强制请求和响应使用 UTF-8 编码，防止中文乱码
     */
    @Bean
    public CharacterEncodingFilter characterEncodingFilter() {
        CharacterEncodingFilter filter = new CharacterEncodingFilter();
        // 设置请求编码
        filter.setEncoding("UTF-8");
        // 设置响应编码
        filter.setForceEncoding(true);
        // 强制请求编码
        filter.setForceRequestEncoding(true);
        // 强制响应编码
        filter.setForceResponseEncoding(true);
        return filter;
    }
}