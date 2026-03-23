package com.starfire.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.starfire.entity.ContactMessage;
import com.starfire.mapper.ContactMessageMapper;
import com.starfire.service.ContactMessageService;
import org.springframework.stereotype.Service;

@Service
public class ContactMessageServiceImpl extends ServiceImpl<ContactMessageMapper, ContactMessage> implements ContactMessageService {

    @Override
    public IPage<ContactMessage> getMessagesPage(int page, int size, String keyword, Integer status) {
        Page<ContactMessage> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<ContactMessage> wrapper = new LambdaQueryWrapper<ContactMessage>()
                .orderByDesc(ContactMessage::getCreateTime);

        if (keyword != null && !keyword.isEmpty()) {
            wrapper.and(w -> w.like(ContactMessage::getName, keyword)
                    .or().like(ContactMessage::getEmail, keyword)
                    .or().like(ContactMessage::getMessage, keyword));
        }

        if (status != null) {
            wrapper.eq(ContactMessage::getStatus, status);
        }

        return page(pageParam, wrapper);
    }

    @Override
    public boolean updateStatus(Long id, Integer status) {
        ContactMessage message = new ContactMessage();
        message.setId(id);
        message.setStatus(status);
        return updateById(message);
    }

    @Override
    public boolean updateReply(Long id, String reply) {
        ContactMessage message = new ContactMessage();
        message.setId(id);
        message.setReply(reply);
        message.setStatus(1); // 已回复
        return updateById(message);
    }
}