package com.starfire.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.starfire.dto.ApiResponse;
import com.starfire.entity.ContactMessage;
import com.starfire.service.ContactMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/contact-messages")
public class ContactMessageController {

    @Autowired
    private ContactMessageService contactMessageService;

    // 提交留言
    @PostMapping
    public ApiResponse<Void> submitMessage(@RequestBody Map<String, Object> request) {
        try {
            ContactMessage message = new ContactMessage();

            if (request.containsKey("name")) {
                message.setName((String) request.get("name"));
            }
            if (request.containsKey("email")) {
                message.setEmail((String) request.get("email"));
            }
            if (request.containsKey("phone")) {
                message.setPhone((String) request.get("phone"));
            }
            if (request.containsKey("type")) {
                message.setType((String) request.get("type"));
            }
            if (request.containsKey("message")) {
                message.setMessage((String) request.get("message"));
            }

            message.setStatus(0); // 0-待处理 1-已处理

            boolean success = contactMessageService.save(message);
            if (success) {
                return ApiResponse.success(null);
            } else {
                return ApiResponse.error("提交失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error("提交失败：" + e.getMessage());
        }
    }

    // 获取留言列表（管理员）
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<IPage<ContactMessage>> getMessages(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status) {
        try {
            IPage<ContactMessage> messages = contactMessageService.getMessagesPage(page, size, keyword, status);
            return ApiResponse.success(messages);
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error("获取留言失败：" + e.getMessage());
        }
    }

    // 更新留言状态（管理员）
    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        try {
            boolean success = contactMessageService.updateStatus(id, status);
            if (success) {
                return ApiResponse.success(null);
            } else {
                return ApiResponse.error("更新失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error("更新失败：" + e.getMessage());
        }
    }

    // 回复留言（管理员）
    @PutMapping("/{id}/reply")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> replyMessage(@PathVariable Long id, @RequestBody Map<String, String> request) {
        try {
            String reply = request.get("reply");
            boolean success = contactMessageService.updateReply(id, reply);
            if (success) {
                return ApiResponse.success(null);
            } else {
                return ApiResponse.error("回复失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error("回复失败：" + e.getMessage());
        }
    }

    // 删除留言（管理员）
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> deleteMessage(@PathVariable Long id) {
        try {
            boolean success = contactMessageService.removeById(id);
            if (success) {
                return ApiResponse.success(null);
            } else {
                return ApiResponse.error("删除失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error("删除失败：" + e.getMessage());
        }
    }
}