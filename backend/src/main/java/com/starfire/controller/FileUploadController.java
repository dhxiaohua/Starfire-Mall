package com.starfire.controller;

import com.starfire.service.FileUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/upload")
public class FileUploadController {

    @Autowired
    private FileUploadService fileUploadService;

    /**
     * 上传文件
     */
    @PostMapping("/file")
    public ResponseEntity<Map<String, Object>> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "userId", required = false) Long userId) {
        Map<String, Object> response = new HashMap<>();

        try {
            // 如果没有提供 userId，使用默认值 0
            if (userId == null) {
                userId = 0L;
            }

            String filePath = fileUploadService.uploadFile(file, "avatars", userId);

            response.put("success", true);
            response.put("message", "文件上传成功");
            response.put("data", Map.of(
                "url", filePath,
                "fullUrl", "http://localhost:8080" + filePath
            ));

            return ResponseEntity.ok(response);
        } catch (IOException e) {
            response.put("success", false);
            response.put("message", "文件上传失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * 上传 Base64 图片
     */
    @PostMapping("/base64")
    public ResponseEntity<Map<String, Object>> uploadBase64Image(@RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();
        String base64Data = request.get("image");
        String userIdStr = request.get("userId");

        if (base64Data == null || base64Data.isEmpty()) {
            response.put("success", false);
            response.put("message", "图片数据不能为空");
            return ResponseEntity.badRequest().body(response);
        }

        try {
            // 如果没有提供 userId，使用默认值 0
            Long userId = userIdStr != null ? Long.parseLong(userIdStr) : 0L;

            String filePath = fileUploadService.uploadBase64Image(base64Data, userId);

            response.put("success", true);
            response.put("message", "图片上传成功");
            response.put("data", Map.of(
                "url", filePath,
                "fullUrl", "http://localhost:8080" + filePath
            ));

            return ResponseEntity.ok(response);
        } catch (IOException e) {
            response.put("success", false);
            response.put("message", "图片上传失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        } catch (NumberFormatException e) {
            response.put("success", false);
            response.put("message", "用户ID格式错误");
            return ResponseEntity.badRequest().body(response);
        }
    }
}