package com.microtakeout.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.microtakeout.user.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户Mapper
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
}

