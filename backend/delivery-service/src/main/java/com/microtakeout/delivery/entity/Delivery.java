package com.microtakeout.delivery.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 配送实体
 */
@Data
@TableName("deliveries")
public class Delivery {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String deliveryNumber;
    private Long orderId;
    private Long deliveryPersonId;
    private String status; // PENDING, ASSIGNED, PICKED_UP, DELIVERING, DELIVERED, CANCELLED
    private String pickupAddress;
    private String deliveryAddress;
    private BigDecimal currentLatitude;
    private BigDecimal currentLongitude;
    private LocalDateTime estimatedArrivalTime;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}

