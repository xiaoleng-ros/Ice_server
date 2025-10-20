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

# 环境变量配置（默认值，可被 Render 环境变量覆盖）
ENV LANG=C.UTF-8 \
    LC_ALL=C.UTF-8
ENV JAVA_TOOL_OPTIONS="-Dfile.encoding=UTF-8"
ENV JAVA_OPTS="-Xms256m -Xmx512m -Djava.security.egd=file:/dev/./urandom" \
    SPRING_PROFILES_ACTIVE=pro \
    SERVER_PORT=9003 \
    PORT=9003

# 健康检查
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:$SERVER_PORT/actuator/health || exit 1

# 暴露端口
EXPOSE $SERVER_PORT

# 启动命令（内嵌启动逻辑）
ENTRYPOINT ["/bin/bash", "-c", "\
echo '=== ThriveX 博客系统启动中 ===' && \
echo '当前时间: '$(date) && \
echo '服务端口: '$SERVER_PORT && \
echo '数据库地址: '$DB_HOST':'$DB_PORT'/'$DB_NAME && \
echo '邮件服务器: '$EMAIL_HOST':'$EMAIL_PORT && \
echo '正在测试数据库连接...' && \
if nc -z $DB_HOST $DB_PORT; then \
    echo '数据库连接测试成功'; \
else \
    echo '数据库连接测试失败'; \
fi && \
echo '启动 Java 应用...' && \
exec java -Dfile.encoding=UTF-8 $JAVA_OPTS -jar ./app.jar \
    --server.port=$SERVER_PORT \
    --spring.profiles.active=$SPRING_PROFILES_ACTIVE \
    --spring.datasource.url=\"jdbc:mysql://$DB_HOST:$DB_PORT/$DB_NAME?sslmode=REQUIRED&useSSL=true&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true&useUnicode=true&characterEncoding=utf8\" \
    --spring.datasource.username=\"$DB_USERNAME\" \
    --spring.datasource.password=\"$DB_PASSWORD\" \
    --spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver \
    --spring.mail.host=\"$EMAIL_HOST\" \
    --spring.mail.port=\"$EMAIL_PORT\" \
    --spring.mail.username=\"$EMAIL_USERNAME\" \
    --spring.mail.password=\"$EMAIL_PASSWORD\" \
    --spring.mail.protocol=\"$EMAIL_PROTOCOL\" \
    --spring.mail.default-encoding=\"${EMAIL_DEFAULT_ENCODING:-UTF-8}\" \
    --spring.mail.properties.mail.smtp.auth=true \
    --spring.mail.properties.mail.smtp.starttls.enable=true"]
