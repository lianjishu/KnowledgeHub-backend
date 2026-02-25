# Blog Backend - Java Spring Boot

这是云原生博客系统的 Java Spring Boot 后端实现，完全兼容原 Python FastAPI 版本的 API 接口。

## 技术栈

- **Java 17**
- **Spring Boot 3.2**
- **Spring Data MongoDB**
- **Spring Security + JWT**
- **Maven**

## 项目结构

```
backend-java/
├── src/main/java/com/blog/
│   ├── BlogApplication.java          # 主入口
│   ├── config/                       # 配置类
│   │   ├── SecurityConfig.java       # Spring Security 配置
│   │   ├── WebConfig.java            # Web MVC 配置
│   │   └── MongoConfig.java          # MongoDB 索引配置
│   ├── controller/                   # 控制器
│   │   ├── AuthController.java       # 认证接口
│   │   ├── ArticleController.java    # 文章接口
│   │   ├── CategoryController.java   # 分类接口
│   │   ├── CommentController.java    # 评论接口
│   │   ├── UploadController.java     # 文件上传接口
│   │   └── HealthController.java     # 健康检查
│   ├── service/                      # 服务层
│   │   ├── AuthService.java
│   │   ├── ArticleService.java
│   │   ├── CategoryService.java
│   │   └── CommentService.java
│   ├── repository/                   # 数据访问
│   │   ├── UserRepository.java
│   │   ├── ArticleRepository.java
│   │   ├── CategoryRepository.java
│   │   └── CommentRepository.java
│   ├── entity/                       # 实体类
│   │   ├── User.java
│   │   ├── Article.java
│   │   ├── Category.java
│   │   └── Comment.java
│   ├── dto/                          # 数据传输对象
│   │   ├── request/                  # 请求 DTO
│   │   └── response/                 # 响应 DTO
│   ├── common/                       # 公共类
│   │   ├── ApiResponse.java          # 统一响应
│   │   └── PageResult.java           # 分页结果
│   ├── exception/                    # 异常处理
│   │   ├── BusinessException.java
│   │   └── GlobalExceptionHandler.java
│   ├── security/                     # 安全认证
│   │   ├── JwtUtil.java
│   │   ├── JwtAuthenticationFilter.java
│   │   └── UserDetailsServiceImpl.java
│   └── util/                         # 工具类
│       └── FileUtil.java
├── src/main/resources/
│   └── application.yml               # 配置文件
├── uploads/                           # 文件上传目录
├── pom.xml                            # Maven 配置
└── Dockerfile                         # Docker 配置
```

## API 接口

### 认证接口 `/api/v1/auth`

| 方法 | 路径 | 说明 | 认证 |
|------|------|------|------|
| POST | /login | 用户登录 | 否 |
| POST | /register | 用户注册 | 否 |
| GET | /info | 获取当前用户信息 | 是 |
| POST | /change-password | 修改密码 | 是 |
| POST | /logout | 退出登录 | 是 |

### 文章接口 `/api/v1/articles`

| 方法 | 路径 | 说明 | 认证 |
|------|------|------|------|
| POST | /create | 创建文章 | 是 |
| PUT | /{articleId} | 更新文章 | 是 |
| DELETE | /{articleId} | 删除文章 | 是 |
| GET | /{articleId} | 获取文章详情 | 否 |
| GET | /list | 获取文章列表 | 否 |
| GET | /hot | 获取热门文章 | 否 |
| POST | /{articleId}/like | 点赞文章 | 否 |
| GET | /statistics | 获取统计信息 | 是 |

### 分类接口 `/api/v1/categories`

| 方法 | 路径 | 说明 | 认证 |
|------|------|------|------|
| POST | /create | 创建分类 | 是 |
| PUT | /{categoryId} | 更新分类 | 是 |
| DELETE | /{categoryId} | 删除分类 | 是 |
| GET | /{categoryId} | 获取分类详情 | 否 |
| GET | /list | 获取分类列表 | 否 |
| GET | /tree | 获取分类树 | 否 |

### 评论接口 `/api/v1/comments`

| 方法 | 路径 | 说明 | 认证 |
|------|------|------|------|
| POST | /create | 创建评论 | 否 |
| POST | /{commentId}/check | 审核评论 | 是 |
| DELETE | /{commentId} | 删除评论 | 是 |
| GET | /list | 获取评论列表 | 否 |
| POST | /{commentId}/reply | 回复评论 | 是 |
| GET | /statistics | 获取统计信息 | 是 |

### 文件上传接口 `/api/v1/upload`

| 方法 | 路径 | 说明 | 认证 |
|------|------|------|------|
| POST | /upload | 上传单文件 | 是 |
| POST | /upload-multiple | 批量上传 | 是 |
| POST | /delete | 删除文件 | 是 |
| GET | /info | 获取文件信息 | 否 |

## 响应格式

所有 API 响应保持与 Python 版本一致：

```json
{
  "code": 200,
  "message": "操作成功",
  "data": { ... },
  "timestamp": 1708684800000
}
```

分页响应：

```json
{
  "code": 200,
  "message": "获取成功",
  "data": {
    "list": [...],
    "pagination": {
      "page": 1,
      "page_size": 10,
      "total": 100,
      "total_pages": 10
    }
  },
  "timestamp": 1708684800000
}
```

## 本地开发

### 前置要求

- Java 17+
- Maven 3.8+
- MongoDB 7.0+

### 环境变量配置

复制 `.env.example` 为 `.env` 并根据需要修改：

```bash
cp .env.example .env
```

### 运行

```bash
# 编译
mvn clean package -DskipTests

# 运行
mvn spring-boot:run
```

## Docker 部署

```bash
# 构建并启动（从项目根目录）
docker-compose up -d
```

## 与 Python 版本的切换

在 `docker-compose.yml` 中可以方便地切换后端：

- 默认使用 **Java 版本**
- 如需使用 **Python 版本**，注释掉 Java 后端部分，取消 Python 后端部分的注释
