package com.microtakeout.order.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单实体
 */
@Data
@TableName("orders")
public class Order {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String orderNumber;
    private Long userId;
    private Long restaurantId;
    private BigDecimal totalAmount;
    private String status; // PENDING, PAID, CONFIRMED, PREPARING, READY, DELIVERING, DELIVERED, CANCELLED
    private String deliveryAddress;
    private String paymentId;
    private String deliveryId;
    private String notes;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
    
    @Version
    private Integer version;
}

