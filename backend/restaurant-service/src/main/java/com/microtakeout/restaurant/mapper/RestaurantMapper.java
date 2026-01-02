package com.microtakeout.restaurant.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.microtakeout.restaurant.entity.Restaurant;
import org.apache.ibatis.annotations.Mapper;

/**
 * 餐厅Mapper
 */
@Mapper
public interface RestaurantMapper extends BaseMapper<Restaurant> {
}

