<p align="center">
    <a href="https://github.com/xiaoleng-ros/Ice_server" target="_blank">
        <img width="100" src="https://bu.dusays.com/2024/11/17/6739adf188f64.png" alt="Ice Blog logo" style="width:100px" />
    </a>
</p>

<p align="center" style="font-size:20px; font-weight:700;">Ice Server</p>

<p align="center" style="margin-bottom:10px">基于 ThriveX 定制开发的现代化博客后端服务</p>

# 🎉 Ice Server

`Ice Server` 是基于优秀的开源博客系统 ThriveX 进行定制开发的后端服务，采用 `Spring Boot` 技术栈构建，为现代化博客系统提供强大的后端支持。

## 🛠️ 技术架构

**后端技术栈：**
- **Spring Boot** - 主要框架
- **Mybatis Plus** - ORM框架
- **Redis** - 缓存服务
- **MySQL** - 数据库
- **Docker** - 容器化部署
- **X File Storage** - 文件存储
- **Swagger** - API文档

## 📁 项目结构

```
ThriveX-Server/
├── blog/                   # 博客核心模块
│   ├── src/main/
│   │   ├── java/          # Java源码
│   │   └── resources/     # 配置文件
│   └── pom.xml
├── model/                  # 数据模型模块
│   ├── src/main/java/
│   └── pom.xml
├── Dockerfile             # Docker构建文件
├── ThriveX.sql           # 数据库脚本
└── pom.xml               # 主POM文件
```

## 🚀 快速开始

### 环境要求

- Java 8+
- Maven 3.6+
- MySQL 5.7+
- Redis 3.0+

### 本地开发

1. **克隆项目**
```bash
git clone https://github.com/xiaoleng-ros/Ice_server.git
cd Ice_server
```

2. **配置数据库**
```bash
# 创建数据库
mysql -u root -p
CREATE DATABASE thrivex;

# 导入数据库脚本
mysql -u root -p thrivex < ThriveX.sql
```

3. **修改配置**
编辑 `blog/src/main/resources/application.yml` 文件，配置数据库连接信息：
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/thrivex
    username: your_username
    password: your_password
```

4. **启动项目**
```bash
mvn clean install
cd blog
mvn spring-boot:run
```

### Docker 部署

1. **构建镜像**
```bash
docker build -t ice-server .
```

2. **运行容器**
```bash
docker run -d \
  --name ice-blog-server \
  -p 9003:9003 \
  -e MYSQL_HOST=your_mysql_host \
  -e MYSQL_PORT=3306 \
  -e MYSQL_DATABASE=thrivex \
  -e MYSQL_USERNAME=your_username \
  -e MYSQL_PASSWORD=your_password \
  ice-server
```

## 📖 API 文档

项目集成了 Swagger，启动后可访问：
- Swagger UI: `http://localhost:9003/swagger-ui.html`
- API Docs: `http://localhost:9003/v2/api-docs`

## 🔧 配置说明

主要配置文件位于 `blog/src/main/resources/application.yml`，包含：

- 数据库连接配置
- Redis 配置
- 文件存储配置
- 日志配置
- 其他业务配置

## 🐛 问题反馈

如果您在使用过程中遇到问题，请通过以下方式反馈：

1. 提交 [GitHub Issue](https://github.com/xiaoleng-ros/Ice_server/issues)
2. 发送邮件至项目维护者

## 🤝 贡献指南

欢迎提交 Pull Request 来改进项目：

1. Fork 本仓库
2. 创建您的特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交您的修改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 打开一个 Pull Request

## 📄 开源协议

本项目遵循 **AGPL-3.0 license** 开源协议。

## 致谢

本项目基于 [ThriveX](https://github.com/LiuYuYang01/ThriveX) 开源博客系统进行开发和定制。

**原作者**: 宇阳  
**原项目地址**: https://github.com/LiuYuYang01/ThriveX

感谢原作者的开源贡献，为我们提供了优秀的博客系统基础架构。ThriveX 是一个年轻、高颜值、全开源、永不收费的现代化博客管理系统，采用 NextJS + Spring Boot 技术栈开发。

### 原项目相关链接

- **项目官网**: https://thrivex.liuyuyang.net/
- **项目文档**: https://docs.liuyuyang.net/
- **项目演示**: https://liuyuyang.net/

### ThriveX 项目组成

- **前端**: [ThriveX-Blog](https://github.com/LiuYuYang01/ThriveX-Blog)
- **控制端**: [ThriveX-Admin](https://github.com/LiuYuYang01/ThriveX-Admin)  
- **后端**: [ThriveX-Server](https://github.com/LiuYuYang01/ThriveX-Server)

感谢 ThriveX 团队为开源社区做出的贡献！

---

<p align="center">
  <sub>Built with ❤️ based on ThriveX</sub>
</p>