package com.microtakeout.restaurant.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.microtakeout.restaurant.entity.MenuItem;
import org.apache.ibatis.annotations.Mapper;

/**
 * 菜单项Mapper
 */
@Mapper
public interface MenuItemMapper extends BaseMapper<MenuItem> {
}

