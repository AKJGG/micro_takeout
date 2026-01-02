package com.microtakeout.order.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.microtakeout.common.exception.NotFoundException;
import com.microtakeout.order.dto.CreateOrderRequest;
import com.microtakeout.order.entity.Order;
import com.microtakeout.order.entity.OrderItem;
import com.microtakeout.order.mapper.OrderItemMapper;
import com.microtakeout.order.mapper.OrderMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 订单服务
 */
@Service
public class OrderService {
    private static final Logger log = LoggerFactory.getLogger(OrderService.class);
    
    @Autowired
    private OrderMapper orderMapper;
    
    @Autowired
    private OrderItemMapper orderItemMapper;
    
    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Transactional
    public Order createOrder(CreateOrderRequest request, Long userId) {
        // 创建订单
        Order order = new Order();
        order.setOrderNumber(generateOrderNumber());
        order.setUserId(userId);
        order.setRestaurantId(request.restaurantId());
        order.setDeliveryAddress(request.deliveryAddress());
        order.setNotes(request.notes());
        order.setStatus("PENDING");
        order.setTotalAmount(BigDecimal.ZERO);
        
        orderMapper.insert(order);
        
        // 创建订单项并计算总金额
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (CreateOrderRequest.OrderItemRequest itemRequest : request.items()) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrderId(order.getId());
            orderItem.setMenuItemId(itemRequest.menuItemId());
            orderItem.setQuantity(itemRequest.quantity());
            // 这里应该从Restaurant Service获取价格，简化处理
            orderItem.setPrice(BigDecimal.valueOf(50)); // 临时价格
            totalAmount = totalAmount.add(orderItem.getPrice().multiply(BigDecimal.valueOf(itemRequest.quantity())));
            orderItemMapper.insert(orderItem);
        }
        
        order.setTotalAmount(totalAmount);
        orderMapper.updateById(order);
        
        // 发送订单创建事件（触发Saga）
        kafkaTemplate.send("order-events", Map.of(
            "eventType", "ORDER_CREATED",
            "orderId", order.getId(),
            "orderNumber", order.getOrderNumber(),
            "userId", userId,
            "restaurantId", request.restaurantId(),
            "totalAmount", totalAmount
        ));
        
        log.info("订单创建成功: orderNumber={}", order.getOrderNumber());
        return order;
    }

    public Order getOrderById(Long id) {
        Order order = orderMapper.selectById(id);
        if (order == null) {
            throw new NotFoundException("订单", id);
        }
        return order;
    }

    public List<Order> getOrdersByUserId(Long userId) {
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Order::getUserId, userId);
        wrapper.orderByDesc(Order::getCreatedAt);
        return orderMapper.selectList(wrapper);
    }

    @Transactional
    public void updateOrderStatus(Long orderId, String status) {
        Order order = getOrderById(orderId);
        order.setStatus(status);
        orderMapper.updateById(order);
        
        // 发送状态更新事件
        kafkaTemplate.send("order-events", Map.of(
            "eventType", "ORDER_STATUS_UPDATED",
            "orderId", orderId,
            "status", status
        ));
    }

    @Transactional
    public void cancelOrder(Long orderId, Long userId) {
        Order order = getOrderById(orderId);
        if (!order.getUserId().equals(userId)) {
            throw new RuntimeException("无权取消此订单");
        }
        if (!"PENDING".equals(order.getStatus()) && !"PAID".equals(order.getStatus())) {
            throw new RuntimeException("订单状态不允许取消");
        }
        
        order.setStatus("CANCELLED");
        orderMapper.updateById(order);
        
        // 发送取消事件
        kafkaTemplate.send("order-events", Map.of(
            "eventType", "ORDER_CANCELLED",
            "orderId", orderId
        ));
    }

    private String generateOrderNumber() {
        return "ORD" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}

