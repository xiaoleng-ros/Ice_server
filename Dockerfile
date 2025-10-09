# ================================
# ThriveX 博客系统 - 生产环境 Dockerfile
# 适用于 Render/Koyeb 等免费云平台部署
# ================================

# 第一阶段：构建 Maven 项目
FROM maven:3.8.6-openjdk-8-slim AS maven_builder

# 设置工作目录
WORKDIR /build

# 复制 Maven 配置文件
COPY pom.xml .
COPY model/pom.xml ./model/
COPY blog/pom.xml ./blog/

# 下载依赖（利用 Docker 缓存层）
RUN mvn dependency:go-offline -B

# 复制源代码
COPY model/ ./model/
COPY blog/ ./blog/

# 构建项目
RUN mvn clean package -DskipTests -B

# 第二阶段：运行时镜像
FROM openjdk:8-jre-alpine

# 安装必要的运行时依赖
RUN apk add --no-cache \
    bash \
    curl \
    netcat-openbsd \
    tzdata && \
    cp /usr/share/zoneinfo/Asia/Shanghai /etc/localtime && \
    echo "Asia/Shanghai" > /etc/timezone

# 创建非 root 用户
RUN addgroup -g 1000 thrivex && \
    adduser -D -s /bin/bash -u 1000 -G thrivex thrivex

# 设置工作目录
WORKDIR /app

# 复制构建产物
COPY --from=maven_builder /build/blog/target/*.jar ./app.jar

# 设置文件权限
RUN chown -R thrivex:thrivex /app

# 切换到非 root 用户
USER thrivex

# 环境变量配置
ENV JAVA_OPTS="-Xms256m -Xmx512m -Djava.security.egd=file:/dev/./urandom" \
    SPRING_PROFILES_ACTIVE=pro \
    SERVER_PORT=8080

# 数据库连接配置（InfinityFree）
ENV DB_HOST=sql102.infinityfree.com \
    DB_PORT=3306 \
    DB_NAME=if0_40132299_thrivex \
    DB_USERNAME=if0_40132299 \
    DB_PASSWORD=LENGleng5201314

# 邮件服务配置（QQ邮箱SMTP）
ENV EMAIL_HOST=smtp.qq.com \
    EMAIL_PORT=587 \
    EMAIL_USERNAME=1873048956@qq.com \
    EMAIL_PASSWORD=mrflyntajdjagehf \
    EMAIL_PROTOCOL=smtp \
    EMAIL_DEFAULT_ENCODING=UTF-8

# 应用配置
ENV SPRING_DATASOURCE_URL="jdbc:mysql://\${DB_HOST}:\${DB_PORT}/\${DB_NAME}?useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true&useUnicode=true&characterEncoding=utf8" \
    SPRING_DATASOURCE_USERNAME="\${DB_USERNAME}" \
    SPRING_DATASOURCE_PASSWORD="\${DB_PASSWORD}" \
    SPRING_DATASOURCE_DRIVER_CLASS_NAME=com.mysql.cj.jdbc.Driver

# 健康检查
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:$SERVER_PORT/actuator/health || exit 1

# 暴露端口
EXPOSE $SERVER_PORT

# 启动命令（直接启动 Java 应用）
ENTRYPOINT ["sh", "-c", "\
echo '=== ThriveX 博客系统启动中 ===' && \
echo '当前时间:' $(date) && \
echo '服务端口: '$SERVER_PORT && \
echo '数据库地址: '$DB_HOST':'$DB_PORT'/'$DB_NAME && \
echo '邮件服务器: '$EMAIL_HOST':'$EMAIL_PORT && \
echo '正在测试数据库连接...' && \
nc -z $DB_HOST $DB_PORT && echo '数据库连接测试成功' || echo '数据库连接测试失败' && \
exec java $JAVA_OPTS -jar \
-Dserver.port=$SERVER_PORT \
-Dspring.profiles.active=$SPRING_PROFILES_ACTIVE \
-Dspring.datasource.url=jdbc:mysql://$DB_HOST:$DB_PORT/$DB_NAME?useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true&useUnicode=true&characterEncoding=utf8 \
-Dspring.datasource.username=$DB_USERNAME \
-Dspring.datasource.password=$DB_PASSWORD \
-Dspring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver \
-Dspring.mail.host=$EMAIL_HOST \
-Dspring.mail.port=$EMAIL_PORT \
-Dspring.mail.username=$EMAIL_USERNAME \
-Dspring.mail.password=$EMAIL_PASSWORD \
-Dspring.mail.protocol=$EMAIL_PROTOCOL \
-Dspring.mail.default-encoding=$EMAIL_DEFAULT_ENCODING \
-Dspring.mail.properties.mail.smtp.auth=true \
-Dspring.mail.properties.mail.smtp.starttls.enable=true \
./app.jar"]
