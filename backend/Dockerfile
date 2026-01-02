# 多阶段构建 - 构建阶段
FROM maven:3.9-eclipse-temurin-25 AS build
WORKDIR /app

# 复制POM文件
COPY pom.xml .
COPY */pom.xml ./

# 下载依赖（利用Docker缓存层）
RUN mvn dependency:go-offline -B

# 复制源代码
COPY . .

# 构建项目
RUN mvn clean package -DskipTests

# 运行阶段
FROM eclipse-temurin:25-jre-alpine
WORKDIR /app

# 创建非root用户
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# 从构建阶段复制JAR文件
COPY --from=build --chown=spring:spring /app/*/target/*.jar app.jar

# 暴露端口（根据服务不同，运行时指定）
EXPOSE 8080

# 健康检查
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

# 启动应用
ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75.0", "-jar", "app.jar"]

