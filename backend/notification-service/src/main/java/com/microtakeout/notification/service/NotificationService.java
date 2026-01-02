package com.microtakeout.notification.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 通知服务
 */
@Service
public class NotificationService {
    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);
    
    @Autowired(required = false)
    private JavaMailSender mailSender;

    @KafkaListener(topics = "user-events", groupId = "notification-service-group")
    public void handleUserEvents(Map<String, Object> event) {
        String eventType = (String) event.get("eventType");
        
        switch (eventType) {
            case "USER_REGISTERED" -> {
                String email = (String) event.get("email");
                String token = (String) event.get("verificationToken");
                sendEmailVerification(email, token);
            }
            default -> log.info("未处理的事件类型: {}", eventType);
        }
    }

    @KafkaListener(topics = "order-events", groupId = "notification-service-group")
    public void handleOrderEvents(Map<String, Object> event) {
        String eventType = (String) event.get("eventType");
        log.info("处理订单事件: {}", eventType);
        // 发送订单状态通知
    }

    @KafkaListener(topics = "payment-events", groupId = "notification-service-group")
    public void handlePaymentEvents(Map<String, Object> event) {
        String eventType = (String) event.get("eventType");
        log.info("处理支付事件: {}", eventType);
        // 发送支付通知
    }

    @KafkaListener(topics = "delivery-events", groupId = "notification-service-group")
    public void handleDeliveryEvents(Map<String, Object> event) {
        String eventType = (String) event.get("eventType");
        log.info("处理配送事件: {}", eventType);
        // 发送配送通知
    }

    private void sendEmailVerification(String email, String token) {
        if (mailSender == null) {
            log.warn("邮件服务未配置，跳过发送验证邮件: {}", email);
            return;
        }
        
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setSubject("邮箱验证");
            message.setText("请点击以下链接验证您的邮箱: http://localhost:8080/api/auth/verify-email?token=" + token);
            mailSender.send(message);
            log.info("验证邮件发送成功: {}", email);
        } catch (Exception e) {
            log.error("发送验证邮件失败: {}", email, e);
        }
    }
}

