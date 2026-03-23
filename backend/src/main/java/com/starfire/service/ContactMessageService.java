package com.starfire.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.starfire.entity.ContactMessage;

public interface ContactMessageService extends IService<ContactMessage> {

    IPage<ContactMessage> getMessagesPage(int page, int size, String keyword, Integer status);

    boolean updateStatus(Long id, Integer status);

    boolean updateReply(Long id, String reply);
}