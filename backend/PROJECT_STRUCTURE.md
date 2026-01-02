# 项目结构

```
micro-takeout-backend/
├── pom.xml                          # 父POM
├── docker-compose.yml               # Docker Compose配置
├── README.md                        # 项目说明
├── .gitignore                      # Git忽略文件
│
├── common/                         # 共享模块
│   ├── pom.xml
│   └── src/main/java/com/microtakeout/common/
│       ├── dto/                    # 数据传输对象
│       │   ├── ApiResponse.java
│       │   └── PageResult.java
│       ├── exception/               # 异常类
│       │   ├── BusinessException.java
│       │   ├── NotFoundException.java
│       │   ├── ValidationException.java
│       │   ├── UnauthorizedException.java
│       │   ├── ForbiddenException.java
│       │   └── GlobalExceptionHandler.java
│       ├── constant/               # 常量
│       │   └── UserRole.java
│       └── util/                    # 工具类
│           └── JwtUtil.java
│
├── discovery-server/               # Eureka服务发现
│   ├── pom.xml
│   └── src/main/java/com/microtakeout/discovery/
│       └── DiscoveryServerApplication.java
│
├── config-server/                  # 配置服务器
│   ├── pom.xml
│   └── src/main/java/com/microtakeout/config/
│       └── ConfigServerApplication.java
│
├── api-gateway/                    # API网关
│   ├── pom.xml
│   └── src/main/java/com/microtakeout/gateway/
│       ├── GatewayApplication.java
│       └── filter/
│           └── JwtAuthenticationFilter.java
│
├── user-service/                   # 用户服务
│   ├── pom.xml
│   └── src/main/java/com/microtakeout/user/
│       ├── UserServiceApplication.java
│       ├── entity/
│       │   └── User.java
│       ├── mapper/
│       │   └── UserMapper.java
│       ├── service/
│       │   └── UserService.java
│       ├── controller/
│       │   ├── AuthController.java
│       │   └── UserController.java
│       ├── dto/
│       │   ├── RegisterRequest.java
│       │   ├── LoginRequest.java
│       │   └── LoginResponse.java
│       └── config/
│           ├── SecurityConfig.java
│           └── MyBatisPlusConfig.java
│
├── restaurant-service/             # 餐厅服务
│   ├── pom.xml
│   └── src/main/java/com/microtakeout/restaurant/
│       ├── RestaurantServiceApplication.java
│       ├── entity/
│       │   ├── Restaurant.java
│       │   └── MenuItem.java
│       ├── mapper/
│       │   ├── RestaurantMapper.java
│       │   └── MenuItemMapper.java
│       ├── service/
│       │   └── RestaurantService.java
│       └── controller/
│           └── RestaurantController.java
│
├── order-service/                  # 订单服务
│   ├── pom.xml
│   └── src/main/java/com/microtakeout/order/
│       ├── OrderServiceApplication.java
│       ├── entity/
│       │   ├── Order.java
│       │   └── OrderItem.java
│       ├── mapper/
│       │   ├── OrderMapper.java
│       │   └── OrderItemMapper.java
│       ├── service/
│       │   └── OrderService.java
│       ├── controller/
│       │   └── OrderController.java
│       └── dto/
│           └── CreateOrderRequest.java
│
├── payment-service/                # 支付服务
│   ├── pom.xml
│   └── src/main/java/com/microtakeout/payment/
│       ├── PaymentServiceApplication.java
│       ├── entity/
│       │   └── Payment.java
│       ├── mapper/
│       │   └── PaymentMapper.java
│       ├── service/
│       │   └── PaymentService.java
│       └── controller/
│           └── PaymentController.java
│
├── delivery-service/               # 配送服务
│   ├── pom.xml
│   └── src/main/java/com/microtakeout/delivery/
│       ├── DeliveryServiceApplication.java
│       ├── entity/
│       │   └── Delivery.java
│       ├── mapper/
│       │   └── DeliveryMapper.java
│       ├── service/
│       │   └── DeliveryService.java
│       └── controller/
│           └── DeliveryController.java
│
├── search-service/                 # 搜索服务
│   ├── pom.xml
│   └── src/main/java/com/microtakeout/search/
│       ├── SearchServiceApplication.java
│       ├── service/
│       │   └── SearchService.java
│       └── controller/
│           └── SearchController.java
│
├── notification-service/           # 通知服务
│   ├── pom.xml
│   └── src/main/java/com/microtakeout/notification/
│       ├── NotificationServiceApplication.java
│       └── service/
│           └── NotificationService.java
│
├── ingestion-service/              # 数据摄入服务
│   ├── pom.xml
│   └── src/main/java/com/microtakeout/ingestion/
│       ├── IngestionServiceApplication.java
│       └── service/
│           └── CdcService.java
│
├── analytics-service/              # 分析服务
│   ├── pom.xml
│   └── src/main/java/com/microtakeout/analytics/
│       ├── AnalyticsServiceApplication.java
│       ├── service/
│       │   └── AnalyticsService.java
│       └── controller/
│           └── AnalyticsController.java
│
└── admin-module/                   # 管理员模块
    ├── pom.xml
    └── src/main/java/com/microtakeout/admin/
        ├── AdminModuleApplication.java
        ├── controller/
        │   └── AdminController.java
        └── resources/
            └── templates/
                └── dashboard.html
```

## 数据库Schema

每个服务使用独立的PostgreSQL schema：

- `user_service` - 用户表
- `restaurant_service` - 餐厅和菜单表
- `order_service` - 订单和订单项表
- `payment_service` - 支付表
- `delivery_service` - 配送表

## 服务端口

- discovery-server: 8761
- config-server: 8888
- api-gateway: 8080
- user-service: 8081
- restaurant-service: 8082
- order-service: 8083
- payment-service: 8084
- delivery-service: 8085
- notification-service: 8086
- search-service: 8087
- analytics-service: 8088
- ingestion-service: 8089
- admin-module: 8090

