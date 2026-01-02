package com.microtakeout.order.controller;

import com.microtakeout.common.dto.ApiResponse;
import com.microtakeout.order.dto.CreateOrderRequest;
import com.microtakeout.order.entity.Order;
import com.microtakeout.order.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 订单控制器
 */
@RestController
@RequestMapping("/orders")
public class OrderController {
    @Autowired
    private OrderService orderService;

    @PostMapping
    public ApiResponse<Order> createOrder(
        @Valid @RequestBody CreateOrderRequest request,
        @RequestHeader("X-User-Id") Long userId) {
        Order order = orderService.createOrder(request, userId);
        return ApiResponse.success("订单创建成功", order);
    }

    @GetMapping("/{id}")
    public ApiResponse<Order> getOrderById(@PathVariable Long id) {
        Order order = orderService.getOrderById(id);
        return ApiResponse.success(order);
    }

    @GetMapping("/my-orders")
    public ApiResponse<List<Order>> getMyOrders(@RequestHeader("X-User-Id") Long userId) {
        List<Order> orders = orderService.getOrdersByUserId(userId);
        return ApiResponse.success(orders);
    }

    @PostMapping("/{id}/cancel")
    public ApiResponse<Object> cancelOrder(
        @PathVariable Long id,
        @RequestHeader("X-User-Id") Long userId) {
        orderService.cancelOrder(id, userId);
        return ApiResponse.success("订单取消成功");
    }
}

