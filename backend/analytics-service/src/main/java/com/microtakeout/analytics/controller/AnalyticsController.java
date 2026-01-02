package com.microtakeout.analytics.controller;

import com.microtakeout.common.dto.ApiResponse;
import com.microtakeout.analytics.service.AnalyticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 分析控制器
 */
@RestController
@RequestMapping("/analytics")
public class AnalyticsController {
    @Autowired
    private AnalyticsService analyticsService;

    @GetMapping("/sales-report")
    public ApiResponse<Map<String, Object>> getSalesReport(@RequestParam(defaultValue = "daily") String period) {
        Map<String, Object> report = analyticsService.getSalesReport(period);
        return ApiResponse.success(report);
    }
}

