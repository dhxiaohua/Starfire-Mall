package com.starfire.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.starfire.entity.CustomerMessage;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CustomerMessageMapper extends BaseMapper<CustomerMessage> {
}
