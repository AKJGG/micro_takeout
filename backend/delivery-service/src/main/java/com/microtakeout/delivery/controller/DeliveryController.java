package com.microtakeout.delivery.controller;

import com.microtakeout.common.dto.ApiResponse;
import com.microtakeout.delivery.entity.Delivery;
import com.microtakeout.delivery.service.DeliveryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

/**
 * 配送控制器
 */
@RestController
@RequestMapping("/deliveries")
public class DeliveryController {
    @Autowired
    private DeliveryService deliveryService;

    @PostMapping
    public ApiResponse<Delivery> createDelivery(
        @RequestParam Long orderId,
        @RequestParam String pickupAddress,
        @RequestParam String deliveryAddress) {
        Delivery delivery = deliveryService.createDelivery(orderId, pickupAddress, deliveryAddress);
        return ApiResponse.success("配送创建成功", delivery);
    }

    @GetMapping("/{id}")
    public ApiResponse<Delivery> getDeliveryById(@PathVariable Long id) {
        Delivery delivery = deliveryService.getDeliveryById(id);
        return ApiResponse.success(delivery);
    }

    @PostMapping("/{id}/assign")
    public ApiResponse<Object> assignDeliveryPerson(
        @PathVariable Long id,
        @RequestParam Long deliveryPersonId) {
        deliveryService.assignDeliveryPerson(id, deliveryPersonId);
        return ApiResponse.success("配送员分配成功");
    }

    @PutMapping("/{id}/location")
    public ApiResponse<Object> updateLocation(
        @PathVariable Long id,
        @RequestParam BigDecimal latitude,
        @RequestParam BigDecimal longitude) {
        deliveryService.updateLocation(id, latitude, longitude);
        return ApiResponse.success("位置更新成功");
    }

    @PutMapping("/{id}/status")
    public ApiResponse<Object> updateStatus(
        @PathVariable Long id,
        @RequestParam String status) {
        deliveryService.updateStatus(id, status);
        return ApiResponse.success("状态更新成功");
    }
}

