package com.starfire.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileUploadService {

    @Autowired
    private com.starfire.config.FileUploadConfig fileUploadConfig;

    @Value("${server.port:8080}")
    private String serverPort;

    /**
     * 上传文件
     * @param file 文件
     * @param subPath 子路径（如：avatars）
     * @param userId 用户ID（用于分目录存储）
     * @return 文件访问路径
     */
    public String uploadFile(MultipartFile file, String subPath, Long userId) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("文件不能为空");
        }

        // 获取原始文件名和扩展名
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));

        // 生成唯一文件名
        String newFilename = UUID.randomUUID().toString() + extension;

        // 创建目标目录（按用户ID分目录）
        String uploadPath = fileUploadConfig.getUploadPath();
        String userDir = String.valueOf(userId);
        File targetDir = new File(uploadPath, userDir);
        if (!targetDir.exists()) {
            targetDir.mkdirs();
        }

        // 保存文件
        Path targetPath = Paths.get(uploadPath, userDir, newFilename);
        Files.copy(file.getInputStream(), targetPath);

        // 返回访问路径
        return "/images/avatars/" + userDir + "/" + newFilename;
    }

    /**
     * 删除文件
     * @param filePath 文件路径
     */
    public void deleteFile(String filePath) {
        if (filePath == null || filePath.isEmpty()) {
            return;
        }

        try {
            // 从访问路径中提取用户ID和文件名
            // 格式：/images/avatars/{userId}/{filename}
            String pathPart = filePath.replace("/images/avatars/", "");
            File file = new File(fileUploadConfig.getUploadPath(), pathPart);

            if (file.exists()) {
                file.delete();
            }
        } catch (Exception e) {
            // 删除失败不影响主流程
            System.err.println("删除文件失败: " + filePath + ", 错误: " + e.getMessage());
        }
    }

    /**
     * 删除用户的所有文件
     * @param userId 用户ID
     */
    public void deleteUserFiles(Long userId) {
        if (userId == null) {
            return;
        }

        try {
            String userDirPath = fileUploadConfig.getUploadPath() + File.separator + userId;
            File userDir = new File(userDirPath);

            if (userDir.exists() && userDir.isDirectory()) {
                // 递归删除目录及所有文件
                deleteDirectory(userDir);
                System.out.println("已删除用户目录: " + userDirPath);
            }
        } catch (Exception e) {
            System.err.println("删除用户目录失败: " + userId + ", 错误: " + e.getMessage());
        }
    }

    /**
     * 递归删除目录
     * @param directory 目录
     */
    private void deleteDirectory(File directory) {
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    deleteDirectory(file);
                } else {
                    file.delete();
                }
            }
        }
        directory.delete();
    }

    /**
     * 处理 Base64 图片并保存
     * @param base64Data Base64 图片数据
     * @param userId 用户ID（用于分目录存储）
     * @return 文件访问路径
     */
    public String uploadBase64Image(String base64Data, Long userId) throws IOException {
        if (base64Data == null || base64Data.isEmpty()) {
            return null;
        }

        // 解析 Base64 数据
        String[] parts = base64Data.split(",");
        if (parts.length < 2) {
            throw new IllegalArgumentException("无效的 Base64 图片数据");
        }

        // 获取图片类型（data:image/jpeg;base64,）
        String mimeType = parts[0];
        String extension;
        if (mimeType.contains("jpeg") || mimeType.contains("jpg")) {
            extension = ".jpg";
        } else if (mimeType.contains("png")) {
            extension = ".png";
        } else if (mimeType.contains("gif")) {
            extension = ".gif";
        } else if (mimeType.contains("webp")) {
            extension = ".webp";
        } else {
            extension = ".jpg"; // 默认
        }

        // 解码 Base64 数据
        byte[] imageBytes = java.util.Base64.getDecoder().decode(parts[1]);

        // 生成唯一文件名
        String newFilename = UUID.randomUUID().toString() + extension;

        // 保存文件（按用户ID分目录）
        String uploadPath = fileUploadConfig.getUploadPath();
        String userDir = String.valueOf(userId);
        File targetDir = new File(uploadPath, userDir);
        if (!targetDir.exists()) {
            targetDir.mkdirs();
        }

        Path targetPath = Paths.get(uploadPath, userDir, newFilename);
        Files.write(targetPath, imageBytes);

        // 返回访问路径
        return "/images/avatars/" + userDir + "/" + newFilename;
    }
}