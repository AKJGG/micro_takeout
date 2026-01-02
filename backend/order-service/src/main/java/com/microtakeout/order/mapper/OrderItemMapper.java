package com.microtakeout.order.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.microtakeout.order.entity.OrderItem;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单项Mapper
 */
@Mapper
public interface OrderItemMapper extends BaseMapper<OrderItem> {
}

