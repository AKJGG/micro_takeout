# 部署教程

本文档介绍如何在Linux和Windows系统上部署微外卖平台后端系统。

## 前置要求

### 通用要求
- JDK 25
- Maven 3.8+
- Docker & Docker Compose（用于基础设施）
- Git

### Linux系统要求
- Ubuntu 20.04+ / CentOS 8+ / 其他主流Linux发行版
- 至少 8GB RAM
- 至少 20GB 可用磁盘空间

### Windows系统要求
- Windows 10/11 或 Windows Server 2019+
- WSL2（推荐，用于Docker）或 Docker Desktop
- 至少 8GB RAM
- 至少 20GB 可用磁盘空间

## Linux部署

### 1. 安装JDK 25

```bash
# Ubuntu/Debian
sudo apt update
sudo apt install openjdk-25-jdk

# CentOS/RHEL
sudo yum install java-25-openjdk-devel

# 验证安装
java -version
```

### 2. 安装Maven

```bash
# Ubuntu/Debian
sudo apt install maven

# CentOS/RHEL
sudo yum install maven

# 验证安装
mvn -version
```

### 3. 安装Docker和Docker Compose

```bash
# 安装Docker
curl -fsSL https://get.docker.com -o get-docker.sh
sudo sh get-docker.sh

# 安装Docker Compose
sudo curl -L "https://github.com/docker/compose/releases/latest/download/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
sudo chmod +x /usr/local/bin/docker-compose

# 验证安装
docker --version
docker-compose --version
```

### 4. 克隆项目

```bash
git clone <your-repo-url>
cd micro-takeout-backend
```

### 5. 启动基础设施

```bash
# 启动PostgreSQL、Redis、Kafka等
docker-compose -f docker-compose.prod.yml up -d

# 检查服务状态
docker-compose -f docker-compose.prod.yml ps
```

### 6. 配置数据库密码

编辑各服务的 `application.yml` 文件，将数据库密码修改为实际密码：

```bash
# 修改所有服务的数据库密码
find . -name "application.yml" -type f -exec sed -i 's/password: postgres/password: YOUR_PASSWORD/g' {} \;
```

### 7. 构建项目

```bash
# 清理并构建所有模块
mvn clean install -DskipTests

# 如果遇到内存不足，增加Maven内存
export MAVEN_OPTS="-Xmx2048m"
mvn clean install -DskipTests
```

### 8. 启动服务

#### 方式一：手动启动（开发环境）

```bash
# 终端1 - 启动服务发现
cd discovery-server
mvn spring-boot:run

# 终端2 - 启动配置服务器
cd config-server
mvn spring-boot:run

# 等待30秒后，启动其他服务
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

# 终端12 - API网关
cd api-gateway
mvn spring-boot:run

# 终端13 - 管理员模块
cd admin-module
mvn spring-boot:run
```

#### 方式二：使用systemd服务（生产环境）

创建systemd服务文件：

```bash
sudo nano /etc/systemd/system/micro-takeout-discovery.service
```

```ini
[Unit]
Description=Micro Takeout Discovery Server
After=network.target

[Service]
Type=simple
User=your-user
WorkingDirectory=/path/to/micro-takeout-backend/discovery-server
ExecStart=/usr/bin/java -jar target/discovery-server-1.0.0-SNAPSHOT.jar
Restart=always
RestartSec=10

[Install]
WantedBy=multi-user.target
```

为每个服务创建类似的systemd文件，然后：

```bash
# 重新加载systemd
sudo systemctl daemon-reload

# 启动服务
sudo systemctl start micro-takeout-discovery
sudo systemctl enable micro-takeout-discovery

# 检查状态
sudo systemctl status micro-takeout-discovery
```

#### 方式三：使用Docker部署

为每个服务创建Dockerfile（已提供），然后使用docker-compose编排：

```bash
# 构建镜像
docker-compose build

# 启动所有服务
docker-compose up -d
```

### 9. 验证部署

```bash
# 检查Eureka Dashboard
curl http://localhost:8761

# 检查API Gateway
curl http://localhost:8080/actuator/health

# 检查服务注册情况
curl http://localhost:8761/eureka/apps
```

## Windows部署

### 1. 安装JDK 25

1. 下载JDK 25安装包：https://jdk.java.net/25/
2. 运行安装程序
3. 配置环境变量：
   - 新建 `JAVA_HOME` = `C:\Program Files\Java\jdk-25`
   - 编辑 `Path`，添加 `%JAVA_HOME%\bin`
4. 验证：打开PowerShell，运行 `java -version`

### 2. 安装Maven

1. 下载Maven：https://maven.apache.org/download.cgi
2. 解压到 `C:\Program Files\Apache\maven`
3. 配置环境变量：
   - 新建 `MAVEN_HOME` = `C:\Program Files\Apache\maven`
   - 编辑 `Path`，添加 `%MAVEN_HOME%\bin`
4. 验证：运行 `mvn -version`

### 3. 安装Docker Desktop

1. 下载Docker Desktop：https://www.docker.com/products/docker-desktop
2. 安装并启动Docker Desktop
3. 确保WSL2已启用（Windows 10/11）

### 4. 克隆项目

```powershell
git clone <your-repo-url>
cd micro-takeout-backend
```

### 5. 启动基础设施

```powershell
# 启动Docker容器
docker-compose -f docker-compose.prod.yml up -d

# 检查状态
docker-compose -f docker-compose.prod.yml ps
```

### 6. 配置数据库密码

使用文本编辑器（如VS Code）打开各服务的 `application.yml`，修改数据库密码。

### 7. 构建项目

```powershell
# 在项目根目录
mvn clean install -DskipTests
```

### 8. 启动服务

#### 方式一：使用PowerShell脚本

创建 `start-services.ps1`：

```powershell
# 启动服务发现
Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd discovery-server; mvn spring-boot:run"
Start-Sleep -Seconds 30

# 启动配置服务器
Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd config-server; mvn spring-boot:run"
Start-Sleep -Seconds 30

# 启动其他服务...
```

运行：
```powershell
.\start-services.ps1
```

#### 方式二：使用批处理文件

创建 `start-services.bat`：

```batch
@echo off
start "Discovery Server" cmd /k "cd discovery-server && mvn spring-boot:run"
timeout /t 30
start "Config Server" cmd /k "cd config-server && mvn spring-boot:run"
timeout /t 30
start "User Service" cmd /k "cd user-service && mvn spring-boot:run"
start "Restaurant Service" cmd /k "cd restaurant-service && mvn spring-boot:run"
start "Order Service" cmd /k "cd order-service && mvn spring-boot:run"
start "Payment Service" cmd /k "cd payment-service && mvn spring-boot:run"
start "Delivery Service" cmd /k "cd delivery-service && mvn spring-boot:run"
start "Search Service" cmd /k "cd search-service && mvn spring-boot:run"
start "Notification Service" cmd /k "cd notification-service && mvn spring-boot:run"
start "Analytics Service" cmd /k "cd analytics-service && mvn spring-boot:run"
start "API Gateway" cmd /k "cd api-gateway && mvn spring-boot:run"
start "Admin Module" cmd /k "cd admin-module && mvn spring-boot:run"
```

#### 方式三：使用Docker Desktop

1. 在Docker Desktop中打开项目目录
2. 使用docker-compose启动所有服务

### 9. 验证部署

在浏览器中访问：
- Eureka Dashboard: http://localhost:8761
- API Gateway: http://localhost:8080/actuator/health

## 生产环境优化建议

### 1. 使用Nginx反向代理

```nginx
upstream api_gateway {
    server localhost:8080;
}

server {
    listen 80;
    server_name api.yourdomain.com;

    location / {
        proxy_pass http://api_gateway;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }
}
```

### 2. 配置HTTPS

使用Let's Encrypt或商业SSL证书配置HTTPS。

### 3. 监控和日志

- 集成Prometheus + Grafana进行监控
- 使用ELK Stack进行日志聚合
- 配置告警规则

### 4. 数据库备份

```bash
# 定期备份PostgreSQL
pg_dump -U postgres micro_takeout > backup_$(date +%Y%m%d).sql
```

### 5. 性能调优

- 调整JVM参数（堆内存、GC策略）
- 配置连接池大小
- 启用Redis缓存
- 使用CDN加速静态资源

## 故障排查

### 服务无法启动

1. 检查端口是否被占用：`netstat -tulpn | grep <port>`
2. 检查日志文件：`tail -f logs/application.log`
3. 检查数据库连接：`psql -U postgres -d micro_takeout`

### 服务无法注册到Eureka

1. 确保Eureka Server已启动
2. 检查网络连接
3. 查看服务日志中的注册错误

### 数据库连接失败

1. 检查PostgreSQL是否运行：`docker ps | grep postgres`
2. 验证数据库密码是否正确
3. 检查防火墙规则

## 常见问题

**Q: 内存不足怎么办？**
A: 增加JVM堆内存：`java -Xmx2g -jar app.jar`

**Q: 如何查看服务日志？**
A: 日志文件位于各服务的 `logs/` 目录，或使用 `journalctl -u service-name`（systemd）

**Q: 如何重启单个服务？**
A: 使用systemd：`sudo systemctl restart service-name`，或直接停止并重新启动进程

