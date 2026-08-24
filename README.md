# 🎓 AI 智能学习平台

> Java + AI 全栈实战项目：JWT 登录鉴权 · 刷题管理 · Redis 缓存 · AI 出题/讲题

一个面向学生的智能学习平台，支持注册登录、刷题练习、错题记录，并通过大模型实现 **AI 自动出题** 和 **AI 错题讲解**。

## ✨ 功能特性

- 🔐 **JWT 登录鉴权**：BCrypt 密码加密 + JWT 无状态 token + 拦截器统一校验（白名单放行）
- 📚 **题库管理**：题目的增删改查、分页查询、条件筛选
- ✍️ **刷题练习**：答题判分、错题本、学习统计
- 🏆 **排行榜**：Redis ZSet 实现刷题排行榜
- 🤖 **AI 能力**：DeepSeek 大模型接入，AI 按知识点出题、AI 错题讲解（Prompt 三段式设计 + 输出容错解析）
- 🌐 **前端（开发中）**：Vue3 + Element Plus 前后端分离

## 🛠 技术栈

| 分类 | 技术 |
|------|------|
| 后端 | Java 17 · Spring Boot 3.5 · MyBatis-Plus |
| 数据库 | MySQL 8 · Redis |
| 安全 | JWT · BCrypt |
| AI | DeepSeek API（大模型接入） |
| 前端 | Vue3 · Element Plus · Pinia |
| 工具 | Maven · Git · 宝塔/Nginx 部署 |

## 📁 项目结构

```
ai-smart-learning-platform
├── src/main/java/com/ai/learning
│   ├── controller    # 接口层（Auth / User / Question）
│   ├── service       # 业务层（接口 + 实现）
│   ├── mapper        # 数据访问层（MyBatis-Plus）
│   ├── entity        # 实体类
│   ├── config        # 配置（拦截器 / 跨域）
│   ├── common        # 公共类（统一返回 / 全局异常）
│   └── util          # 工具类（JWT）
├── src/main/resources
│   ├── application.yml   # 应用配置
│   └── db/init.sql       # 数据库初始化脚本
└── src/test/java         # 单元测试
```

## 🚀 快速开始

### 环境要求
- JDK 17+ · Maven 3.6+ · MySQL 8 · Redis（模块四后需要）

### 1. 初始化数据库
依次执行 `src/main/resources/db/` 下的脚本：
- `init.sql`：建库 + 用户表
- `init-module3.sql`：答题记录表

### 2. 本地配置
复制 `src/main/resources/application-local.example.yml` 为 **`application-local.yml`**，填入你的 MySQL 密码和 DeepSeek API Key。
（该文件已被 .gitignore 排除，不会上传 GitHub——**这是公开仓库的安全规范**）

### 3. 启动项目
```bash
mvn spring-boot:run
```
或 IDEA 中运行 `LearningApplication`，看到 `🚀 启动成功` 即可

### 4. 快速验证
```bash
# 注册
curl -X POST http://localhost:8081/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"zhangsan","password":"123456"}'

# 登录（返回 token）
curl -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"zhangsan","password":"123456"}'

# 携带 token 访问受保护接口
curl http://localhost:8081/api/user/info \
  -H "Authorization: Bearer <上面返回的token>"
```

## 📖 API 文档

项目集成了 Swagger（springdoc-openapi），启动后访问：
- Swagger UI：`http://localhost:8081/swagger-ui.html`
- OpenAPI JSON：`http://localhost:8081/v3/api-docs`

## 🧪 测试

```bash
mvn test
```
包含 JwtUtil 单元测试（生成/解析/防篡改）等。

## 📌 开发计划

- [x] 模块一：JWT 登录鉴权
- [x] 模块二：题库管理（CRUD + 分页）
- [x] 模块三：刷题 + 错题本
- [x] 模块四：Redis 缓存 + 排行榜
- [x] 模块五：AI 出题 / 讲题（DeepSeek）
- [x] 模块六：Vue3 前端 + 部署上线 ✅

## 🚀 部署

生产环境：Ubuntu 24.04 + Nginx + systemd，手动部署（无宝塔）。

- 前端：`npm run build` → `dist/` 放到 Nginx 根目录
- 后端：`mvn package` → jar + `application-prod.yml`（外部配置覆盖，无需重新打包）
- Nginx：静态文件 + `/api` 反代 8081 + SSE 关缓冲（`proxy_buffering off`）
- 后端守护：systemd 服务（开机自启 + 崩溃自动重启）

## 📄 License

MIT
