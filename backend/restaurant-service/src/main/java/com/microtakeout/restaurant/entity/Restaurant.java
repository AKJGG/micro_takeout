package com.microtakeout.restaurant.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 餐厅实体
 */
@Data
@TableName("restaurants")
public class Restaurant {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String name;
    private String description;
    private String address;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private String phone;
    private String imageUrl;
    private Long ownerId;
    private String status; // PENDING, APPROVED, REJECTED
    private BigDecimal rating;
    private Integer totalReviews;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
    
    @Version
    private Integer version;
}

