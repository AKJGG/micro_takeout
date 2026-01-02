package com.microtakeout.delivery.service;

import com.microtakeout.common.exception.NotFoundException;
import com.microtakeout.delivery.entity.Delivery;
import com.microtakeout.delivery.mapper.DeliveryMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 配送服务
 */
@Service
public class DeliveryService {
    private static final Logger log = LoggerFactory.getLogger(DeliveryService.class);
    
    @Autowired
    private DeliveryMapper deliveryMapper;
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Transactional
    public Delivery createDelivery(Long orderId, String pickupAddress, String deliveryAddress) {
        Delivery delivery = new Delivery();
        delivery.setDeliveryNumber(generateDeliveryNumber());
        delivery.setOrderId(orderId);
        delivery.setPickupAddress(pickupAddress);
        delivery.setDeliveryAddress(deliveryAddress);
        delivery.setStatus("PENDING");
        
        deliveryMapper.insert(delivery);
        
        // 发送配送创建事件，触发骑手分配
        kafkaTemplate.send("delivery-events", Map.of(
            "eventType", "DELIVERY_CREATED",
            "deliveryId", delivery.getId(),
            "orderId", orderId
        ));
        
        log.info("配送创建成功: deliveryNumber={}", delivery.getDeliveryNumber());
        return delivery;
    }

    @Transactional
    public void assignDeliveryPerson(Long deliveryId, Long deliveryPersonId) {
        Delivery delivery = getDeliveryById(deliveryId);
        delivery.setDeliveryPersonId(deliveryPersonId);
        delivery.setStatus("ASSIGNED");
        deliveryMapper.updateById(delivery);
        
        // 将配送员位置信息存储到Redis（用于实时跟踪）
        String key = "delivery:location:" + deliveryId;
        redisTemplate.opsForValue().set(key, Map.of(
            "deliveryPersonId", deliveryPersonId,
            "status", "ASSIGNED"
        ), 2, TimeUnit.HOURS);
        
        // 发送分配事件
        kafkaTemplate.send("delivery-events", Map.of(
            "eventType", "DELIVERY_ASSIGNED",
            "deliveryId", deliveryId,
            "deliveryPersonId", deliveryPersonId
        ));
    }

    public Delivery getDeliveryById(Long id) {
        Delivery delivery = deliveryMapper.selectById(id);
        if (delivery == null) {
            throw new NotFoundException("配送", id);
        }
        return delivery;
    }

    @Transactional
    public void updateLocation(Long deliveryId, BigDecimal latitude, BigDecimal longitude) {
        Delivery delivery = getDeliveryById(deliveryId);
        delivery.setCurrentLatitude(latitude);
        delivery.setCurrentLongitude(longitude);
        deliveryMapper.updateById(delivery);
        
        // 更新Redis中的位置信息
        String key = "delivery:location:" + deliveryId;
        redisTemplate.opsForValue().set(key, Map.of(
            "latitude", latitude,
            "longitude", longitude,
            "timestamp", System.currentTimeMillis()
        ), 2, TimeUnit.HOURS);
    }

    @Transactional
    public void updateStatus(Long deliveryId, String status) {
        Delivery delivery = getDeliveryById(deliveryId);
        delivery.setStatus(status);
        if ("DELIVERED".equals(status)) {
            delivery.setEstimatedArrivalTime(LocalDateTime.now());
        }
        deliveryMapper.updateById(delivery);
        
        // 发送状态更新事件
        kafkaTemplate.send("delivery-events", Map.of(
            "eventType", "DELIVERY_STATUS_UPDATED",
            "deliveryId", deliveryId,
            "status", status
        ));
    }

    private String generateDeliveryNumber() {
        return "DEL" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}

