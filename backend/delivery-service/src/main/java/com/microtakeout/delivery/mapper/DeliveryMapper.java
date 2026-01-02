package com.microtakeout.delivery.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.microtakeout.delivery.entity.Delivery;
import org.apache.ibatis.annotations.Mapper;

/**
 * 配送Mapper
 */
@Mapper
public interface DeliveryMapper extends BaseMapper<Delivery> {
}

