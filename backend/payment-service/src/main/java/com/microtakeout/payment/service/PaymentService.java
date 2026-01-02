package com.microtakeout.payment.service;

import com.microtakeout.common.exception.NotFoundException;
import com.microtakeout.payment.entity.Payment;
import com.microtakeout.payment.mapper.PaymentMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

/**
 * 支付服务
 */
@Service
public class PaymentService {
    private static final Logger log = LoggerFactory.getLogger(PaymentService.class);
    
    @Autowired
    private PaymentMapper paymentMapper;
    
    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Transactional
    public Payment processPayment(Long orderId, BigDecimal amount, String paymentMethod) {
        Payment payment = new Payment();
        payment.setPaymentNumber(generatePaymentNumber());
        payment.setOrderId(orderId);
        payment.setAmount(amount);
        payment.setPaymentMethod(paymentMethod);
        payment.setStatus("PROCESSING");
        
        paymentMapper.insert(payment);
        
        // 模拟支付处理（异步）
        processPaymentAsync(payment);
        
        return payment;
    }

    private void processPaymentAsync(Payment payment) {
        // 模拟支付网关调用
        new Thread(() -> {
            try {
                Thread.sleep(2000); // 模拟网络延迟
                
                // 模拟支付成功（90%成功率）
                boolean success = Math.random() > 0.1;
                payment.setStatus(success ? "SUCCESS" : "FAILED");
                payment.setTransactionId(UUID.randomUUID().toString());
                if (!success) {
                    payment.setFailureReason("支付网关返回失败");
                }
                
                paymentMapper.updateById(payment);
                
                // 发送支付结果事件
                kafkaTemplate.send("payment-events", Map.of(
                    "eventType", success ? "PAYMENT_SUCCESS" : "PAYMENT_FAILED",
                    "paymentId", payment.getId(),
                    "orderId", payment.getOrderId(),
                    "status", payment.getStatus()
                ));
                
                log.info("支付处理完成: paymentId={}, status={}", payment.getId(), payment.getStatus());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
    }

    public Payment getPaymentById(Long id) {
        Payment payment = paymentMapper.selectById(id);
        if (payment == null) {
            throw new NotFoundException("支付", id);
        }
        return payment;
    }

    @Transactional
    public void refund(Long paymentId) {
        Payment payment = getPaymentById(paymentId);
        if (!"SUCCESS".equals(payment.getStatus())) {
            throw new RuntimeException("只能退款已成功的支付");
        }
        
        payment.setStatus("REFUNDED");
        paymentMapper.updateById(payment);
        
        // 发送退款事件
        kafkaTemplate.send("payment-events", Map.of(
            "eventType", "PAYMENT_REFUNDED",
            "paymentId", paymentId,
            "orderId", payment.getOrderId()
        ));
        
        log.info("退款成功: paymentId={}", paymentId);
    }

    private String generatePaymentNumber() {
        return "PAY" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}

