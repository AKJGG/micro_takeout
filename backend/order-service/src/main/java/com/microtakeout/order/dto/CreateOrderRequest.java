package com.microtakeout.order.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

/**
 * 创建订单请求DTO
 */
public record CreateOrderRequest(
    @NotNull(message = "餐厅ID不能为空")
    Long restaurantId,
    
    @NotEmpty(message = "订单项不能为空")
    List<OrderItemRequest> items,
    
    @NotNull(message = "配送地址不能为空")
    String deliveryAddress,
    
    String notes
) {
    public record OrderItemRequest(
        @NotNull(message = "菜单项ID不能为空")
        Long menuItemId,
        
        @NotNull(message = "数量不能为空")
        Integer quantity
    ) {
    }
}

