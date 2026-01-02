# 微外卖平台后端系统

一个基于Spring Cloud微服务架构的完整外卖送餐平台后端系统，使用Java 25和Spring Cloud 2025.1.0构建。

## 技术栈

- **Java**: 25 (使用最新特性：虚拟线程、Records、Sealed Classes等)
- **Spring Boot**: 4.0.1
- **Spring Cloud**: 2025.1.0
- **数据库**: PostgreSQL 16
- **缓存**: Redis 7
- **消息队列**: Apache Kafka
- **搜索引擎**: Elasticsearch 8.11
- **对象存储**: MinIO
- **ORM**: MyBatis-Plus 3.5.9
- **服务发现**: Eureka
- **API网关**: Spring Cloud Gateway
- **配置中心**: Spring Cloud Config
- **熔断器**: Resilience4j
- **数据库迁移**: Flyway

## 微服务架构

系统包含以下微服务：

1. **discovery-server** (8761) - Eureka服务发现服务器
2. **config-server** (8888) - 配置服务器
3. **api-gateway** (8080) - API网关
4. **user-service** (8081) - 用户服务
5. **restaurant-service** (8082) - 餐厅服务
6. **order-service** (8083) - 订单服务
7. **payment-service** (8084) - 支付服务
8. **delivery-service** (8085) - 配送服务
9. **search-service** (8087) - 搜索服务
10. **notification-service** (8086) - 通知服务
11. **analytics-service** (8088) - 分析服务
12. **admin-module** - 管理员仪表板

## 快速开始

### 前置要求

- JDK 25
- Maven 3.8+
- Docker & Docker Compose

### 1. 启动基础设施

```bash
docker-compose up -d
```

这将启动：
- PostgreSQL (5432)
- Redis (6379)
- Kafka (9092)
- Elasticsearch (9200)
- MinIO (9000, 9001)

### 2. 构建项目

```bash
mvn clean install
```

### 3. 启动服务

按以下顺序启动服务：

```bash
# 1. 启动服务发现
cd discovery-server
mvn spring-boot:run

# 2. 启动配置服务器
cd ../config-server
mvn spring-boot:run

# 3. 启动各个微服务（新终端窗口）
cd ../user-service
mvn spring-boot:run

cd ../restaurant-service
mvn spring-boot:run

cd ../order-service
mvn spring-boot:run

cd ../payment-service
mvn spring-boot:run

cd ../delivery-service
mvn spring-boot:run

cd ../search-service
mvn spring-boot:run

cd ../notification-service
mvn spring-boot:run

cd ../analytics-service
mvn spring-boot:run

# 4. 启动API网关
cd ../api-gateway
mvn spring-boot:run
```

### 4. 访问服务

- **Eureka Dashboard**: http://localhost:8761
- **API Gateway**: http://localhost:8080
- **Swagger UI**: http://localhost:8080/swagger-ui.html

## API端点

### 认证
- `POST /api/auth/register` - 用户注册
- `POST /api/auth/login` - 用户登录
- `POST /api/auth/verify-email` - 邮箱验证
- `POST /api/auth/reset-password` - 密码重置

### 用户
- `GET /api/users/{id}` - 获取用户信息
- `GET /api/users/username/{username}` - 根据用户名获取用户

### 餐厅
- `GET /api/restaurants` - 获取餐厅列表
- `GET /api/restaurants/{id}` - 获取餐厅详情
- `POST /api/restaurants` - 创建餐厅
- `PUT /api/restaurants/{id}` - 更新餐厅
- `GET /api/restaurants/{id}/menu` - 获取菜单

### 订单
- `POST /api/orders` - 创建订单
- `GET /api/orders/{id}` - 获取订单详情
- `GET /api/orders/my-orders` - 获取我的订单
- `POST /api/orders/{id}/cancel` - 取消订单

### 支付
- `POST /api/payments/process` - 处理支付
- `GET /api/payments/{id}` - 获取支付信息
- `POST /api/payments/{id}/refund` - 退款

### 配送
- `GET /api/deliveries/{id}` - 获取配送信息
- `PUT /api/deliveries/{id}/location` - 更新位置
- `PUT /api/deliveries/{id}/status` - 更新状态

### 搜索
- `GET /api/search/restaurants` - 搜索餐厅

### 分析
- `GET /api/analytics/sales-report` - 获取销售报告

## 架构图

```
                    ┌─────────────┐
                    │   Client    │
                    └──────┬──────┘
                           │
                    ┌──────▼──────┐
                    │ API Gateway │
                    └──────┬──────┘
                           │
        ┌──────────────────┼──────────────────┐
        │                  │                  │
   ┌────▼────┐      ┌──────▼──────┐    ┌─────▼─────┐
   │  User   │      │ Restaurant  │    │   Order   │
   │ Service │      │  Service    │    │  Service   │
   └────┬────┘      └──────┬──────┘    └─────┬─────┘
        │                  │                  │
   ┌────▼────┐      ┌──────▼──────┐    ┌─────▼─────┐
   │Payment │      │  Delivery   │    │Search     │
   │Service │      │  Service     │    │Service    │
   └────┬────┘      └──────┬──────┘    └─────┬─────┘
        │                  │                  │
        └──────────────────┼──────────────────┘
                           │
                    ┌──────▼──────┐
                    │   Kafka     │
                    └──────┬──────┘
                           │
        ┌──────────────────┼──────────────────┐
        │                  │                  │
   ┌────▼────┐      ┌──────▼──────┐    ┌─────▼─────┐
   │Notification│   │  Ingestion  │    │ Analytics │
   │  Service    │   │  Service    │    │  Service  │
   └────────────┘   └─────────────┘    └───────────┘
```

## 数据库Schema

每个服务使用独立的PostgreSQL schema：
- `user_service` - 用户服务
- `restaurant_service` - 餐厅服务
- `order_service` - 订单服务
- `payment_service` - 支付服务
- `delivery_service` - 配送服务

## 特性

- ✅ 微服务架构
- ✅ 服务发现与注册
- ✅ API网关路由与认证
- ✅ 分布式配置管理
- ✅ 事件驱动架构（Kafka）
- ✅ 数据库迁移（Flyway）
- ✅ JWT认证
- ✅ 熔断器（Resilience4j）
- ✅ 分布式追踪
- ✅ 监控与指标（Prometheus）

## 开发

### 代码规范

- 使用Java 25特性（Records、Sealed Classes、虚拟线程）
- 遵循DDD设计原则
- 使用MyBatis-Plus进行数据库操作
- 使用Kafka进行异步事件处理

### 测试

```bash
mvn test
```

## 许可证

MIT License

## 贡献

欢迎提交Issue和Pull Request！

