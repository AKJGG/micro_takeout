package com.microtakeout.admin.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 管理员控制器
 */
@Controller
@RequestMapping("/admin")
public class AdminController {

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("title", "仪表板");
        return "dashboard";
    }

    @GetMapping("/users")
    public String users(Model model) {
        model.addAttribute("title", "用户管理");
        return "users";
    }

    @GetMapping("/restaurants")
    public String restaurants(Model model) {
        model.addAttribute("title", "餐厅管理");
        return "restaurants";
    }

    @GetMapping("/orders")
    public String orders(Model model) {
        model.addAttribute("title", "订单管理");
        return "orders";
    }

    @GetMapping("/analytics")
    public String analytics(Model model) {
        model.addAttribute("title", "数据分析");
        return "analytics";
    }
}

