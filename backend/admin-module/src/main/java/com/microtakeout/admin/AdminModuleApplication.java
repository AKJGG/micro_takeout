package com.microtakeout.admin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * 管理员模块
 */
@SpringBootApplication
@EnableEurekaClient
@EnableFeignClients
public class AdminModuleApplication {
    public static void main(String[] args) {
        SpringApplication.run(AdminModuleApplication.class, args);
    }
}

