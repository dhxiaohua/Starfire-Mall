package com.starfire.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.starfire.entity.AIChatMessage;
import org.apache.ibatis.annotations.Mapper;

/**
 * AI 聊天消息 Mapper
 */
@Mapper
public interface AIChatMessageMapper extends BaseMapper<AIChatMessage> {
}