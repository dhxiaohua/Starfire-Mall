package com.starfire.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.starfire.entity.Product;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ProductMapper extends BaseMapper<Product> {
}
