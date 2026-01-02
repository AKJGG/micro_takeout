package com.microtakeout.payment.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 支付实体
 */
@Data
@TableName("payments")
public class Payment {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String paymentNumber;
    private Long orderId;
    private BigDecimal amount;
    private String status; // PENDING, PROCESSING, SUCCESS, FAILED, REFUNDED
    private String paymentMethod; // CREDIT_CARD, ALIPAY, WECHAT
    private String transactionId;
    private String failureReason;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}

