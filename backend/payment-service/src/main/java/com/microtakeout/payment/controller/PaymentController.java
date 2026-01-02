package com.microtakeout.payment.controller;

import com.microtakeout.common.dto.ApiResponse;
import com.microtakeout.payment.entity.Payment;
import com.microtakeout.payment.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

/**
 * 支付控制器
 */
@RestController
@RequestMapping("/payments")
public class PaymentController {
    @Autowired
    private PaymentService paymentService;

    @PostMapping("/process")
    public ApiResponse<Payment> processPayment(
        @RequestParam Long orderId,
        @RequestParam BigDecimal amount,
        @RequestParam String paymentMethod) {
        Payment payment = paymentService.processPayment(orderId, amount, paymentMethod);
        return ApiResponse.success("支付处理中", payment);
    }

    @GetMapping("/{id}")
    public ApiResponse<Payment> getPaymentById(@PathVariable Long id) {
        Payment payment = paymentService.getPaymentById(id);
        return ApiResponse.success(payment);
    }

    @PostMapping("/{id}/refund")
    public ApiResponse<Object> refund(@PathVariable Long id) {
        paymentService.refund(id);
        return ApiResponse.success("退款成功");
    }
}

