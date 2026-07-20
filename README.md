# book-trade — 学生二手书籍交易平台

校园二手书籍买卖信息管理系统，支持书籍发布、分类浏览、留言互动、完整订单流转（下单→确认→付款→完成）、通知系统、管理员广播，中/英/韩三语国际化。

---

## 🚀 本地快速启动

### 环境要求：JDK 17+ / Maven 3.6+

### 一步启动（零配置）
```bash
mvn spring-boot:run
```
默认使用 H2 内存数据库，表结构和种子数据自动创建，**无需安装 MySQL**。

访问 `http://localhost:8080`

测试账号：`admin/123456`（管理员） / `student1/123456`（学生）

### 使用本地 MySQL（可选）
```bash
# 1. 导入数据库
mysql -u root -p < sql/init.sql

# 2. 设置环境变量启动
$env:DB_DRIVER='com.mysql.cj.jdbc.Driver'
$env:DB_URL='jdbc:mysql://localhost:3306/book_trade?useUnicode=true&characterEncoding=utf8mb4&serverTimezone=Asia/Shanghai'
$env:DB_USERNAME='root'
$env:DB_PASSWORD='你的密码'
$env:SCHEMA_FILE='schema.sql'
$env:DATA_FILE='data.sql'
$env:H2_CONSOLE='false'
mvn spring-boot:run
```

---

## ☁️ 云端部署 (Railway)

### Railway 环境变量配置

| 变量名 | 值 | 说明 |
|--------|-----|------|
| `PORT` | `8080` | 应用端口 |
| `DB_DRIVER` | `com.mysql.cj.jdbc.Driver` | MySQL 驱动 |
| `DB_URL` | `jdbc:mysql://${MYSQLHOST}:${MYSQLPORT}/${MYSQLDATABASE}?useUnicode=true&characterEncoding=utf8mb4&serverTimezone=UTC` | Railway MySQL 插件自动注入 MYSQLHOST 等 |
| `DB_USERNAME` | `${MYSQLUSER}` | 数据库用户 |
| `DB_PASSWORD` | `${MYSQLPASSWORD}` | 数据库密码 |
| `SCHEMA_FILE` | `schema.sql` | MySQL 建表脚本 |
| `DATA_FILE` | `data.sql` | 种子数据脚本 |
| `H2_CONSOLE` | `false` | 关闭 H2 控制台 |
| `UPLOAD_PATH` | `/app/uploads` | 上传目录 |

### 首次部署建表

Railway MySQL 插件初始为空库，需要通过 CLI 手动导入：

```bash
railway connect mysql
# 进入 MySQL 命令行后：
source sql/init.sql;
```

### 部署步骤
1. 推送代码到 GitHub
2. Railway → New Project → Deploy from GitHub repo
3. 添加 MySQL 插件
4. 配置上述环境变量
5. 执行 `railway connect mysql` + `source sql/init.sql` 建表
6. 部署自动触发

---

## 🗺️ 项目代码地图

### 安全防线
| 文件 | 职责 |
|------|------|
| `config/LoginInterceptor.java` | Session 登录校验 |
| `config/InterceptorConfig.java` | 拦截路径白/黑名单 |
| `config/WebConfig.java` | BCrypt 加密 + 多语言 + 上传资源映射 |
| `config/GlobalModelAdvice.java` | 全局注入未读通知数 |
| `service/UserService.java` | 登录验证 + 注册加密 |

### 核心业务
| 文件 | 职责 |
|------|------|
| `controller/HomeController.java` | 首页搜索 + 分类浏览 |
| `controller/BookController.java` | 书籍 CRUD + 封面上传 + 留言/回复 |
| `controller/OrderController.java` | 下单/确认/付款/完成/取消 |
| `controller/AdminController.java` | 管理后台 + 全站广播 |
| `controller/ProfileController.java` | 个人中心 + 卖家主页 |
| `controller/NotificationController.java` | 通知列表 + 管理员广播 |
| `service/OrderService.java` | 订单创建 + 状态机 + 通知触发 |
| `service/CommentService.java` | 留言 + 通知触发 |
| `service/NotificationService.java` | 通知 CRUD + 全站广播 |

### 数据层
| 路径 | 说明 |
|------|------|
| `entity/*.java` | 8 个实体（含 Notification） |
| `mapper/*.java` | MyBatis-Plus BaseMapper |
| `sql/init.sql` | 完整 MySQL 建表 + 种子数据 |
| `schema.sql / data.sql` | MySQL 自动初始化脚本 |
| `schema-h2.sql / data-h2.sql` | H2 自动初始化脚本 |

---

## 📦 技术栈

Spring Boot 3.2.5 + MyBatis-Plus 3.5.6 + Thymeleaf + Bootstrap 5 + MySQL/H2 + BCrypt + Docker

---

## 🏗️ 订单状态机

```
0(待确认) → 1(已确认) → 4(已付款) → 2(已完成)
0 → 3(已取消)
```