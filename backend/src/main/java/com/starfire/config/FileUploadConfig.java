package com.starfire.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;

@Configuration
public class FileUploadConfig implements WebMvcConfigurer {

    @Value("${file.upload.path:D:/娱乐/桌面/Starfire-Mall/frontend/public/images/avatars}")
    private String uploadPath;

    public String getUploadPath() {
        return uploadPath;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 确保上传目录存在
        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        // 配置静态资源映射（支持用户子目录）
        registry.addResourceHandler("/images/avatars/**")
                .addResourceLocations("file:" + uploadPath + "/");
    }
}