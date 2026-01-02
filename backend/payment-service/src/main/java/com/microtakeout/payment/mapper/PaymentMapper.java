package com.microtakeout.payment.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.microtakeout.payment.entity.Payment;
import org.apache.ibatis.annotations.Mapper;

/**
 * 支付Mapper
 */
@Mapper
public interface PaymentMapper extends BaseMapper<Payment> {
}

