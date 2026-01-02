# 启动教程

本文档提供详细的启动步骤，帮助您快速启动微外卖平台后端系统。

## 快速开始

### 前置检查清单

- [ ] JDK 25 已安装并配置
- [ ] Maven 3.8+ 已安装
- [ ] Docker 和 Docker Compose 已安装
- [ ] 至少 8GB 可用内存
- [ ] 端口 8761, 8080-8090 未被占用

## 第一步：启动基础设施

### 1.1 启动Docker容器

```bash
# Linux/Mac
docker-compose -f docker-compose.prod.yml up -d

# Windows PowerShell
docker-compose -f docker-compose.prod.yml up -d
```

### 1.2 验证基础设施

```bash
# 检查所有容器状态
docker-compose -f docker-compose.prod.yml ps

# 应该看到以下服务运行中：
# - postgres (5432)
# - redis (6379)
# - zookeeper (2181)
# - kafka (9092)
# - elasticsearch (9200)
# - minio (9000, 9001)
```

### 1.3 等待服务就绪

```bash
# 等待PostgreSQL就绪（约10-20秒）
docker logs micro-takeout-postgres

# 等待Kafka就绪（约30秒）
docker logs micro-takeout-kafka

# 等待Elasticsearch就绪（约1分钟）
curl http://localhost:9200
```

## 第二步：构建项目

### 2.1 清理并构建

```bash
# 在项目根目录执行
mvn clean install -DskipTests

# 如果构建失败，尝试：
mvn clean install -DskipTests -U
```

### 2.2 验证构建

```bash
# 检查JAR文件是否生成
ls */target/*.jar

# 应该看到所有服务的JAR文件
```

## 第三步：启动服务（按顺序）

### 3.1 启动服务发现（必须第一个）

```bash
cd discovery-server
mvn spring-boot:run
```

**等待提示：** 看到 "Started DiscoveryServerApplication" 后，等待10秒再启动下一个服务。

**验证：** 访问 http://localhost:8761 应该能看到Eureka Dashboard。

### 3.2 启动配置服务器（必须第二个）

```bash
# 新开终端窗口
cd config-server
mvn spring-boot:run
```

**等待提示：** 看到 "Started ConfigServerApplication" 后，等待10秒。

### 3.3 启动核心服务

按以下顺序启动，每个服务在新终端窗口启动：

```bash
# 终端3 - 用户服务
cd user-service
mvn spring-boot:run

# 终端4 - 餐厅服务
cd restaurant-service
mvn spring-boot:run

# 终端5 - 订单服务
cd order-service
mvn spring-boot:run

# 终端6 - 支付服务
cd payment-service
mvn spring-boot:run

# 终端7 - 配送服务
cd delivery-service
mvn spring-boot:run
```

### 3.4 启动支持服务

```bash
# 终端8 - 搜索服务
cd search-service
mvn spring-boot:run

# 终端9 - 通知服务
cd notification-service
mvn spring-boot:run

# 终端10 - 分析服务
cd analytics-service
mvn spring-boot:run

# 终端11 - 数据摄入服务
cd ingestion-service
mvn spring-boot:run
```

### 3.5 启动网关和管理模块

```bash
# 终端12 - API网关（最后启动）
cd api-gateway
mvn spring-boot:run

# 终端13 - 管理员模块
cd admin-module
mvn spring-boot:run
```

## 第四步：验证启动

### 4.1 检查Eureka注册情况

访问 http://localhost:8761，应该看到以下服务已注册：

- CONFIG-SERVER
- USER-SERVICE
- RESTAURANT-SERVICE
- ORDER-SERVICE
- PAYMENT-SERVICE
- DELIVERY-SERVICE
- SEARCH-SERVICE
- NOTIFICATION-SERVICE
- ANALYTICS-SERVICE
- INGESTION-SERVICE
- API-GATEWAY
- ADMIN-MODULE

### 4.2 测试API端点

```bash
# 测试API Gateway健康检查
curl http://localhost:8080/actuator/health

# 测试用户注册（应该返回401，因为需要认证）
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "email": "test@example.com",
    "password": "password123",
    "role": "CUSTOMER"
  }'
```

### 4.3 检查服务日志

每个服务的控制台应该显示：
- ✅ "Started [ServiceName]Application"
- ✅ "Registered with Eureka"
- ❌ 没有ERROR级别的错误

## 启动脚本（可选）

### Linux/Mac启动脚本

创建 `start-all.sh`：

```bash
#!/bin/bash

echo "启动基础设施..."
docker-compose -f docker-compose.prod.yml up -d
sleep 30

echo "启动服务发现..."
cd discovery-server
mvn spring-boot:run > ../logs/discovery.log 2>&1 &
DISCOVERY_PID=$!
cd ..
sleep 30

echo "启动配置服务器..."
cd config-server
mvn spring-boot:run > ../logs/config.log 2>&1 &
CONFIG_PID=$!
cd ..
sleep 30

echo "启动用户服务..."
cd user-service
mvn spring-boot:run > ../logs/user.log 2>&1 &
cd ..
sleep 10

echo "启动餐厅服务..."
cd restaurant-service
mvn spring-boot:run > ../logs/restaurant.log 2>&1 &
cd ..
sleep 10

echo "启动订单服务..."
cd order-service
mvn spring-boot:run > ../logs/order.log 2>&1 &
cd ..
sleep 10

echo "启动支付服务..."
cd payment-service
mvn spring-boot:run > ../logs/payment.log 2>&1 &
cd ..
sleep 10

echo "启动配送服务..."
cd delivery-service
mvn spring-boot:run > ../logs/delivery.log 2>&1 &
cd ..
sleep 10

echo "启动搜索服务..."
cd search-service
mvn spring-boot:run > ../logs/search.log 2>&1 &
cd ..
sleep 10

echo "启动通知服务..."
cd notification-service
mvn spring-boot:run > ../logs/notification.log 2>&1 &
cd ..
sleep 10

echo "启动分析服务..."
cd analytics-service
mvn spring-boot:run > ../logs/analytics.log 2>&1 &
cd ..
sleep 10

echo "启动API网关..."
cd api-gateway
mvn spring-boot:run > ../logs/gateway.log 2>&1 &
cd ..

echo "所有服务启动完成！"
echo "Eureka Dashboard: http://localhost:8761"
echo "API Gateway: http://localhost:8080"
```

使用：
```bash
chmod +x start-all.sh
./start-all.sh
```

### Windows启动脚本

创建 `start-all.bat`：

```batch
@echo off
echo 启动基础设施...
docker-compose -f docker-compose.prod.yml up -d
timeout /t 30

echo 启动服务发现...
start "Discovery Server" cmd /k "cd discovery-server && mvn spring-boot:run"
timeout /t 30

echo 启动配置服务器...
start "Config Server" cmd /k "cd config-server && mvn spring-boot:run"
timeout /t 30

echo 启动用户服务...
start "User Service" cmd /k "cd user-service && mvn spring-boot:run"
timeout /t 10

echo 启动餐厅服务...
start "Restaurant Service" cmd /k "cd restaurant-service && mvn spring-boot:run"
timeout /t 10

echo 启动订单服务...
start "Order Service" cmd /k "cd order-service && mvn spring-boot:run"
timeout /t 10

echo 启动支付服务...
start "Payment Service" cmd /k "cd payment-service && mvn spring-boot:run"
timeout /t 10

echo 启动配送服务...
start "Delivery Service" cmd /k "cd delivery-service && mvn spring-boot:run"
timeout /t 10

echo 启动搜索服务...
start "Search Service" cmd /k "cd search-service && mvn spring-boot:run"
timeout /t 10

echo 启动通知服务...
start "Notification Service" cmd /k "cd notification-service && mvn spring-boot:run"
timeout /t 10

echo 启动分析服务...
start "Analytics Service" cmd /k "cd analytics-service && mvn spring-boot:run"
timeout /t 10

echo 启动API网关...
start "API Gateway" cmd /k "cd api-gateway && mvn spring-boot:run"

echo 所有服务启动完成！
echo Eureka Dashboard: http://localhost:8761
echo API Gateway: http://localhost:8080
pause
```

## 停止服务

### Linux/Mac

```bash
# 停止所有Java进程
pkill -f spring-boot:run

# 停止Docker容器
docker-compose -f docker-compose.prod.yml down
```

### Windows

```powershell
# 停止所有Java进程
Get-Process java | Where-Object {$_.Path -like "*maven*"} | Stop-Process

# 停止Docker容器
docker-compose -f docker-compose.prod.yml down
```

## 常见启动问题

### 问题1：端口被占用

```bash
# Linux/Mac - 查找占用端口的进程
lsof -i :8080

# Windows
netstat -ano | findstr :8080

# 杀死进程
kill -9 <PID>  # Linux/Mac
taskkill /PID <PID> /F  # Windows
```

### 问题2：服务无法连接到数据库

1. 检查PostgreSQL是否运行：`docker ps | grep postgres`
2. 检查数据库密码是否正确
3. 检查网络连接：`telnet localhost 5432`

### 问题3：服务无法注册到Eureka

1. 确保Eureka Server已启动并运行
2. 检查服务配置中的Eureka地址
3. 查看服务日志中的错误信息

### 问题4：内存不足

```bash
# 增加Maven内存
export MAVEN_OPTS="-Xmx2048m -Xms1024m"

# 或修改JVM参数
java -Xmx2g -Xms1g -jar app.jar
```

## 性能优化建议

1. **使用JVM参数优化**：
   ```bash
   -XX:+UseG1GC -XX:MaxGCPauseMillis=200
   ```

2. **调整连接池大小**：在 `application.yml` 中配置

3. **启用Redis缓存**：减少数据库查询

4. **使用虚拟线程**（Java 25特性）：提高并发性能

## 下一步

- 查看 [README.md](README.md) 了解API端点
- 查看 [DEPLOYMENT.md](DEPLOYMENT.md) 了解生产环境部署
- 查看 [PROJECT_STRUCTURE.md](PROJECT_STRUCTURE.md) 了解项目结构

