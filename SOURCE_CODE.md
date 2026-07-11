# 二手书交易系统 - 完整源代码

## 一、Maven配置 - pom.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.2.5</version>
        <relativePath/>
    </parent>

    <groupId>com.booktrade</groupId>
    <artifactId>book-trade</artifactId>
    <version>1.0.0</version>
    <name>book-trade</name>
    <description>学生二手书籍买卖信息管理系统</description>

    <properties>
        <java.version>17</java.version>
        <mybatis-plus.version>3.5.6</mybatis-plus.version>
    </properties>

    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-thymeleaf</artifactId>
        </dependency>
        <dependency>
            <groupId>com.baomidou</groupId>
            <artifactId>mybatis-plus-spring-boot3-starter</artifactId>
            <version>${mybatis-plus.version}</version>
        </dependency>
        <dependency>
            <groupId>com.mysql</groupId>
            <artifactId>mysql-connector-j</artifactId>
            <scope>runtime</scope>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>
</project>
```

## 二、应用配置 - application.yml

```yaml
server:
  port: 8080

spring:
  application:
    name: book-trade
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/book_trade?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai
    username: 2192767721@qq.com
    password: '@303303LUo'
  thymeleaf:
    cache: false
    prefix: classpath:/templates/
    suffix: .html
  messages:
    basename: i18n/messages
    encoding: UTF-8
  servlet:
    multipart:
      max-file-size: 5MB
      max-request-size: 10MB

mybatis-plus:
  configuration:
    map-underscore-to-camel-case: true
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl
  global-config:
    db-config:
      id-type: auto
      logic-delete-field: deleted
      logic-delete-value: 1
      logic-not-delete-value: 0

book-trade:
  upload-path: uploads/
```

## 三、数据库初始化 - sql/init.sql

```sql
CREATE DATABASE IF NOT EXISTS book_trade DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE book_trade;

CREATE TABLE IF NOT EXISTS `user` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `username`    VARCHAR(50)  NOT NULL COMMENT '用户名',
    `password`    VARCHAR(100) NOT NULL COMMENT '密码',
    `nickname`    VARCHAR(50)  DEFAULT NULL COMMENT '昵称',
    `phone`       VARCHAR(20)  DEFAULT NULL COMMENT '手机号',
    `role`        TINYINT      NOT NULL DEFAULT 0 COMMENT '角色：0-学生 1-管理员',
    `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `deleted`     TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

CREATE TABLE IF NOT EXISTS `category` (
    `id`   BIGINT      NOT NULL AUTO_INCREMENT COMMENT '主键',
    `name` VARCHAR(50) NOT NULL COMMENT '分类名称',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='书籍分类表';

CREATE TABLE IF NOT EXISTS `book` (
    `id`          BIGINT        NOT NULL AUTO_INCREMENT COMMENT '主键',
    `title`       VARCHAR(100)  NOT NULL COMMENT '书名',
    `author`      VARCHAR(50)   DEFAULT NULL COMMENT '作者',
    `isbn`        VARCHAR(20)   DEFAULT NULL COMMENT 'ISBN',
    `price`       DECIMAL(10,2) NOT NULL COMMENT '售价',
    `description` TEXT          DEFAULT NULL COMMENT '描述',
    `cover_image` VARCHAR(255)  DEFAULT NULL COMMENT '封面图片路径',
    `category_id` BIGINT        DEFAULT NULL COMMENT '分类ID',
    `seller_id`   BIGINT        NOT NULL COMMENT '卖家ID',
    `status`      TINYINT       NOT NULL DEFAULT 1 COMMENT '状态：0-下架 1-在售 2-已售',
    `create_time` DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '发布时间',
    `update_time` DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`     TINYINT       NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    `condition`   TINYINT       NOT NULL DEFAULT 0 COMMENT '书籍状态：0-全新 1-良好 2-一般',
    PRIMARY KEY (`id`),
    KEY `idx_seller_id` (`seller_id`),
    KEY `idx_category_id` (`category_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='书籍表';

CREATE TABLE IF NOT EXISTS `trade_order` (
    `id`          BIGINT        NOT NULL AUTO_INCREMENT COMMENT '主键',
    `order_no`    VARCHAR(32)   NOT NULL COMMENT '订单编号',
    `book_id`     BIGINT        NOT NULL COMMENT '书籍ID',
    `buyer_id`    BIGINT        NOT NULL COMMENT '买家ID',
    `seller_id`   BIGINT        NOT NULL COMMENT '卖家ID',
    `price`       DECIMAL(10,2) NOT NULL COMMENT '成交价格',
    `status`      TINYINT       NOT NULL DEFAULT 0 COMMENT '状态：0-待确认 1-已确认 2-已完成 3-已取消',
    `create_time` DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '下单时间',
    `update_time` DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_order_no` (`order_no`),
    KEY `idx_buyer_id` (`buyer_id`),
    KEY `idx_seller_id` (`seller_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表';

CREATE TABLE IF NOT EXISTS `payment` (
    `id`          BIGINT        NOT NULL AUTO_INCREMENT COMMENT '主键',
    `order_id`    BIGINT        NOT NULL COMMENT '订单ID',
    `amount`      DECIMAL(10,2) NOT NULL COMMENT '付款金额',
    `pay_method`  VARCHAR(50)   DEFAULT 'card' COMMENT '支付方式',
    `pay_time`    DATETIME      DEFAULT NULL COMMENT '付款时间',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='付款记录表';

CREATE TABLE IF NOT EXISTS `comment` (
    `id`          BIGINT        NOT NULL AUTO_INCREMENT COMMENT '主键',
    `book_id`     BIGINT        NOT NULL COMMENT '书籍ID',
    `user_id`     BIGINT        NOT NULL COMMENT '用户ID',
    `parent_id`   BIGINT        DEFAULT NULL COMMENT '父留言ID',
    `content`     TEXT          NOT NULL COMMENT '留言内容',
    `create_time` DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_book_id` (`book_id`),
    KEY `idx_parent_id` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='留言表';

CREATE TABLE IF NOT EXISTS `order_log` (
    `id`            BIGINT      NOT NULL AUTO_INCREMENT COMMENT '主键',
    `order_id`      BIGINT      NOT NULL COMMENT '订单ID',
    `operator_id`   BIGINT      DEFAULT NULL COMMENT '操作人ID',
    `operator_name` VARCHAR(50) DEFAULT NULL COMMENT '操作人名称',
    `action`        VARCHAR(100) DEFAULT NULL COMMENT '操作内容',
    `create_time`   DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单操作日志';

INSERT INTO `category` (`name`) VALUES
    ('教材'), ('考研'), ('文学'), ('计算机'), ('其他');

INSERT INTO `user` (`username`, `password`, `nickname`, `phone`, `role`) VALUES
    ('admin', '123456', '管理员', '13800000000', 1),
    ('student1', '123456', '张三', '13800000001', 0),
    ('student2', '123456', '李四', '13800000002', 0);

INSERT INTO `book` (`title`, `author`, `isbn`, `price`, `description`, `category_id`, `seller_id`, `status`) VALUES
    ('Java程序设计', '张三', '978-7-111-12345-6', 35.00, '九成新，无笔记', 4, 2, 1),
    ('高等数学（上册）', '同济大学', '978-7-111-23456-7', 20.00, '有少量笔记，不影响阅读', 1, 2, 1),
    ('考研英语词汇', '朱伟', '978-7-111-34567-8', 15.00, '几乎全新', 2, 3, 1);
```

## 四、Java源代码

### 4.1 启动类 - BookTradeApplication.java

```java
package com.booktrade;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.booktrade.mapper")
public class BookTradeApplication {

    public static void main(String[] args) {
        SpringApplication.run(BookTradeApplication.class, args);
    }
}
```

### 4.2 配置类

#### LoginInterceptor.java

```java
package com.booktrade.config;

import com.booktrade.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class LoginInterceptor implements HandlerInterceptor {

    public static final String SESSION_USER = "loginUser";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute(SESSION_USER);
        if (user == null) {
            response.sendRedirect("/login");
            return false;
        }
        return true;
    }
}
```

#### InterceptorConfig.java

```java
package com.booktrade.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class InterceptorConfig implements WebMvcConfigurer {

    private final LoginInterceptor loginInterceptor;

    public InterceptorConfig(LoginInterceptor loginInterceptor) {
        this.loginInterceptor = loginInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginInterceptor)
                .addPathPatterns("/", "/book", "/book/publish", "/book/edit/**", "/book/off/**",
                        "/book/my", "/order/**", "/admin/**")
                .excludePathPatterns("/login", "/register", "/book/detail/**",
                        "/css/**", "/js/**", "/uploads/**");
    }
}
```

#### WebConfig.java

```java
package com.booktrade.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;

import java.nio.file.Paths;
import java.util.Locale;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${book-trade.upload-path}")
    private String uploadPath;

    @Bean
    public LocaleResolver localeResolver() {
        CookieLocaleResolver resolver = new CookieLocaleResolver();
        resolver.setDefaultLocale(Locale.CHINA);
        resolver.setCookieName("lang");
        resolver.setCookieMaxAge(3600);
        return resolver;
    }

    @Bean
    public LocaleChangeInterceptor localeChangeInterceptor() {
        LocaleChangeInterceptor interceptor = new LocaleChangeInterceptor();
        interceptor.setParamName("lang");
        return interceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(localeChangeInterceptor());
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String absolutePath = Paths.get(uploadPath).toAbsolutePath().toUri().toString();
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(absolutePath);
    }
}
```

### 4.3 实体类

#### User.java

```java
package com.booktrade.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;

import java.time.LocalDateTime;

@TableName("user")
public class User {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String username;
    private String password;
    private String nickname;
    private String phone;
    private Integer role;
    private LocalDateTime createTime;
    @TableLogic
    private Integer deleted;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public Integer getRole() { return role; }
    public void setRole(Integer role) { this.role = role; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
    public Integer getDeleted() { return deleted; }
    public void setDeleted(Integer deleted) { this.deleted = deleted; }
}
```

#### Book.java

```java
package com.booktrade.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@TableName("book")
public class Book {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String title;
    private String author;
    private String isbn;
    private BigDecimal price;
    private String description;
    private String coverImage;
    private Long categoryId;
    private Long sellerId;
    private Integer status;
    @TableField("`condition`")
    private Integer condition;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    @TableLogic
    private Integer deleted;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getCoverImage() { return coverImage; }
    public void setCoverImage(String coverImage) { this.coverImage = coverImage; }
    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }
    public Long getSellerId() { return sellerId; }
    public void setSellerId(Long sellerId) { this.sellerId = sellerId; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    public Integer getCondition() { return condition; }
    public void setCondition(Integer condition) { this.condition = condition; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
    public LocalDateTime getUpdateTime() { return updateTime; }
    public void setUpdateTime(LocalDateTime updateTime) { this.updateTime = updateTime; }
    public Integer getDeleted() { return deleted; }
    public void setDeleted(Integer deleted) { this.deleted = deleted; }
}
```

#### Category.java

```java
package com.booktrade.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

@TableName("category")
public class Category {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}
```

#### TradeOrder.java

```java
package com.booktrade.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@TableName("trade_order")
public class TradeOrder {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String orderNo;
    private Long bookId;
    private Long buyerId;
    private Long sellerId;
    private BigDecimal price;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getOrderNo() { return orderNo; }
    public void setOrderNo(String orderNo) { this.orderNo = orderNo; }
    public Long getBookId() { return bookId; }
    public void setBookId(Long bookId) { this.bookId = bookId; }
    public Long getBuyerId() { return buyerId; }
    public void setBuyerId(Long buyerId) { this.buyerId = buyerId; }
    public Long getSellerId() { return sellerId; }
    public void setSellerId(Long sellerId) { this.sellerId = sellerId; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
    public LocalDateTime getUpdateTime() { return updateTime; }
    public void setUpdateTime(LocalDateTime updateTime) { this.updateTime = updateTime; }
}
```

#### Payment.java

```java
package com.booktrade.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@TableName("payment")
public class Payment {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long orderId;
    private BigDecimal amount;
    private String payMethod;
    private LocalDateTime payTime;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public String getPayMethod() { return payMethod; }
    public void setPayMethod(String payMethod) { this.payMethod = payMethod; }
    public LocalDateTime getPayTime() { return payTime; }
    public void setPayTime(LocalDateTime payTime) { this.payTime = payTime; }
}
```

#### Comment.java

```java
package com.booktrade.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.time.LocalDateTime;
import java.util.List;

@TableName("comment")
public class Comment {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long bookId;
    private Long userId;
    private Long parentId;
    private String content;
    private LocalDateTime createTime;

    @com.baomidou.mybatisplus.annotation.TableField(exist = false)
    private String userNickname;

    @com.baomidou.mybatisplus.annotation.TableField(exist = false)
    private List<Comment> replies;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getBookId() { return bookId; }
    public void setBookId(Long bookId) { this.bookId = bookId; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Long getParentId() { return parentId; }
    public void setParentId(Long parentId) { this.parentId = parentId; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
    public String getUserNickname() { return userNickname; }
    public void setUserNickname(String userNickname) { this.userNickname = userNickname; }
    public List<Comment> getReplies() { return replies; }
    public void setReplies(List<Comment> replies) { this.replies = replies; }
}
```

#### OrderLog.java

```java
package com.booktrade.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.time.LocalDateTime;

@TableName("order_log")
public class OrderLog {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long orderId;
    private Long operatorId;
    private String operatorName;
    private String action;
    private LocalDateTime createTime;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }
    public Long getOperatorId() { return operatorId; }
    public void setOperatorId(Long operatorId) { this.operatorId = operatorId; }
    public String getOperatorName() { return operatorName; }
    public void setOperatorName(String operatorName) { this.operatorName = operatorName; }
    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
}
```

### 4.4 Mapper接口

#### UserMapper.java

```java
package com.booktrade.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.booktrade.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}
```

#### BookMapper.java

```java
package com.booktrade.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.booktrade.entity.Book;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface BookMapper extends BaseMapper<Book> {
}
```

#### CategoryMapper.java

```java
package com.booktrade.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.booktrade.entity.Category;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CategoryMapper extends BaseMapper<Category> {
}
```

#### OrderMapper.java

```java
package com.booktrade.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.booktrade.entity.TradeOrder;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderMapper extends BaseMapper<TradeOrder> {
}
```

#### PaymentMapper.java

```java
package com.booktrade.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.booktrade.entity.Payment;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PaymentMapper extends BaseMapper<Payment> {
}
```

#### CommentMapper.java

```java
package com.booktrade.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.booktrade.entity.Comment;

public interface CommentMapper extends BaseMapper<Comment> {
}
```

#### OrderLogMapper.java

```java
package com.booktrade.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.booktrade.entity.OrderLog;

public interface OrderLogMapper extends BaseMapper<OrderLog> {
}
```

### 4.5 Service类

#### UserService.java

```java
package com.booktrade.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.booktrade.entity.User;
import com.booktrade.mapper.UserMapper;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserMapper userMapper;

    public UserService(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    public User login(String username, String password) {
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getUsername, username));
        if (user != null && user.getPassword().equals(password)) {
            return user;
        }
        return null;
    }

    public boolean register(User user) {
        Long count = userMapper.selectCount(new LambdaQueryWrapper<User>().eq(User::getUsername, user.getUsername()));
        if (count > 0) return false;
        user.setRole(0);
        return userMapper.insert(user) > 0;
    }

    public User getById(Long id) {
        return userMapper.selectById(id);
    }
}
```

#### BookService.java

```java
package com.booktrade.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.booktrade.entity.Book;
import com.booktrade.mapper.BookMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class BookService {
    private final BookMapper bookMapper;

    public BookService(BookMapper bookMapper) {
        this.bookMapper = bookMapper;
    }

    public List<Book> listOnSale(String keyword, Long categoryId) {
        LambdaQueryWrapper<Book> wrapper = new LambdaQueryWrapper<Book>()
                .eq(Book::getStatus, 1)
                .orderByDesc(Book::getCreateTime);
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(Book::getTitle, keyword)
                    .or().like(Book::getAuthor, keyword)
                    .or().like(Book::getDescription, keyword));
        }
        if (categoryId != null) {
            wrapper.eq(Book::getCategoryId, categoryId);
        }
        return bookMapper.selectList(wrapper);
    }

    public List<Book> listBySeller(Long sellerId) {
        return bookMapper.selectList(new LambdaQueryWrapper<Book>()
                .eq(Book::getSellerId, sellerId)
                .orderByDesc(Book::getCreateTime));
    }

    public List<Book> listAll() {
        return bookMapper.selectList(new LambdaQueryWrapper<Book>().orderByDesc(Book::getCreateTime));
    }

    public Book getById(Long id) {
        return bookMapper.selectById(id);
    }

    public boolean save(Book book) {
        book.setStatus(1);
        return bookMapper.insert(book) > 0;
    }

    public boolean update(Book book) {
        return bookMapper.updateById(book) > 0;
    }

    public boolean offShelf(Long id) {
        Book book = new Book();
        book.setId(id);
        book.setStatus(0);
        return bookMapper.updateById(book) > 0;
    }

    public boolean markSold(Long id) {
        Book book = new Book();
        book.setId(id);
        book.setStatus(2);
        return bookMapper.updateById(book) > 0;
    }

    public boolean delete(Long id) {
        return bookMapper.deleteById(id) > 0;
    }
}
```

#### CategoryService.java

```java
package com.booktrade.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.booktrade.entity.Category;
import com.booktrade.mapper.CategoryMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {
    private final CategoryMapper categoryMapper;

    public CategoryService(CategoryMapper categoryMapper) {
        this.categoryMapper = categoryMapper;
    }

    public List<Category> listAll() {
        return categoryMapper.selectList(null);
    }

    public Category getById(Long id) {
        return categoryMapper.selectById(id);
    }
}
```

#### OrderService.java

```java
package com.booktrade.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.booktrade.entity.OrderLog;
import com.booktrade.entity.TradeOrder;
import com.booktrade.mapper.OrderLogMapper;
import com.booktrade.mapper.OrderMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class OrderService {
    private final OrderMapper orderMapper;
    private final OrderLogMapper orderLogMapper;

    public OrderService(OrderMapper orderMapper, OrderLogMapper orderLogMapper) {
        this.orderMapper = orderMapper;
        this.orderLogMapper = orderLogMapper;
    }

    public List<TradeOrder> listByBuyer(Long buyerId) {
        return orderMapper.selectList(new LambdaQueryWrapper<TradeOrder>()
                .eq(TradeOrder::getBuyerId, buyerId)
                .orderByDesc(TradeOrder::getCreateTime));
    }

    public List<TradeOrder> listBySeller(Long sellerId) {
        return orderMapper.selectList(new LambdaQueryWrapper<TradeOrder>()
                .eq(TradeOrder::getSellerId, sellerId)
                .orderByDesc(TradeOrder::getCreateTime));
    }

    public List<TradeOrder> listAll() {
        return orderMapper.selectList(new LambdaQueryWrapper<TradeOrder>().orderByDesc(TradeOrder::getCreateTime));
    }

    public List<TradeOrder> listByStatus(Integer status) {
        return orderMapper.selectList(new LambdaQueryWrapper<TradeOrder>()
                .eq(TradeOrder::getStatus, status)
                .orderByDesc(TradeOrder::getCreateTime));
    }

    public List<TradeOrder> listByStatusAndTimeRange(Integer status, LocalDateTime startTime, LocalDateTime endTime) {
        LambdaQueryWrapper<TradeOrder> wrapper = new LambdaQueryWrapper<>();
        if (status != null) wrapper.eq(TradeOrder::getStatus, status);
        if (startTime != null) wrapper.ge(TradeOrder::getCreateTime, startTime);
        if (endTime != null) wrapper.le(TradeOrder::getCreateTime, endTime);
        wrapper.orderByDesc(TradeOrder::getCreateTime);
        return orderMapper.selectList(wrapper);
    }

    public TradeOrder getById(Long id) {
        return orderMapper.selectById(id);
    }

    public boolean create(TradeOrder order) {
        order.setOrderNo(generateOrderNo());
        order.setStatus(0);
        boolean result = orderMapper.insert(order) > 0;
        if (result) addLog(order.getId(), null, null, "订单创建");
        return result;
    }

    public boolean updateStatus(Long id, Integer status, Long operatorId, String operatorName) {
        TradeOrder order = new TradeOrder();
        order.setId(id);
        order.setStatus(status);
        boolean result = orderMapper.updateById(order) > 0;
        if (result && operatorId != null) {
            String action = switch (status) {
                case 0 -> "订单重置为待确认";
                case 1 -> "管理员确认订单";
                case 2 -> "订单完成";
                case 3 -> "订单取消";
                default -> "";
            };
            addLog(id, operatorId, operatorName, action);
        }
        return result;
    }

    public boolean updateStatus(Long id, Integer status) {
        return updateStatus(id, status, null, null);
    }

    public List<OrderLog> getLogsByOrderId(Long orderId) {
        return orderLogMapper.selectList(new LambdaQueryWrapper<OrderLog>()
                .eq(OrderLog::getOrderId, orderId)
                .orderByDesc(OrderLog::getCreateTime));
    }

    private void addLog(Long orderId, Long operatorId, String operatorName, String action) {
        OrderLog log = new OrderLog();
        log.setOrderId(orderId);
        log.setOperatorId(operatorId);
        log.setOperatorName(operatorName);
        log.setAction(action);
        log.setCreateTime(LocalDateTime.now());
        orderLogMapper.insert(log);
    }

    private String generateOrderNo() {
        String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        int random = ThreadLocalRandom.current().nextInt(1000, 9999);
        return "ORD" + time + random;
    }
}
```

#### PaymentService.java

```java
package com.booktrade.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.booktrade.entity.Payment;
import com.booktrade.entity.TradeOrder;
import com.booktrade.mapper.PaymentMapper;
import com.booktrade.mapper.OrderMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class PaymentService {
    private final PaymentMapper paymentMapper;
    private final OrderMapper orderMapper;

    public PaymentService(PaymentMapper paymentMapper, OrderMapper orderMapper) {
        this.paymentMapper = paymentMapper;
        this.orderMapper = orderMapper;
    }

    @Transactional
    public boolean pay(Long orderId, BigDecimal amount) {
        return pay(orderId, amount, "card");
    }

    @Transactional
    public boolean pay(Long orderId, BigDecimal amount, String payMethod) {
        TradeOrder order = orderMapper.selectById(orderId);
        if (order == null || order.getStatus() != 1) return false;

        Payment payment = new Payment();
        payment.setOrderId(orderId);
        payment.setAmount(amount);
        payment.setPayMethod(payMethod);
        payment.setPayTime(LocalDateTime.now());
        paymentMapper.insert(payment);

        TradeOrder updateOrder = new TradeOrder();
        updateOrder.setId(orderId);
        updateOrder.setStatus(4);
        orderMapper.updateById(updateOrder);

        return true;
    }

    public Payment getByOrderId(Long orderId) {
        return paymentMapper.selectOne(new LambdaQueryWrapper<Payment>().eq(Payment::getOrderId, orderId));
    }

    public boolean isPaid(Long orderId) {
        return paymentMapper.selectCount(new LambdaQueryWrapper<Payment>().eq(Payment::getOrderId, orderId)) > 0;
    }
}
```

#### CommentService.java

```java
package com.booktrade.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.booktrade.entity.Comment;
import com.booktrade.entity.User;
import com.booktrade.mapper.CommentMapper;
import com.booktrade.mapper.UserMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CommentService {
    private final CommentMapper commentMapper;
    private final UserMapper userMapper;

    public CommentService(CommentMapper commentMapper, UserMapper userMapper) {
        this.commentMapper = commentMapper;
        this.userMapper = userMapper;
    }

    public List<Comment> listByBookId(Long bookId) {
        List<Comment> comments = commentMapper.selectList(new LambdaQueryWrapper<Comment>()
                .eq(Comment::getBookId, bookId)
                .isNull(Comment::getParentId)
                .orderByDesc(Comment::getCreateTime));

        for (Comment comment : comments) {
            User user = userMapper.selectById(comment.getUserId());
            if (user != null) comment.setUserNickname(user.getNickname());
            comment.setReplies(getRepliesByParentId(comment.getId()));
        }
        return comments;
    }

    public List<Comment> getRepliesByParentId(Long parentId) {
        List<Comment> replies = commentMapper.selectList(new LambdaQueryWrapper<Comment>()
                .eq(Comment::getParentId, parentId)
                .orderByAsc(Comment::getCreateTime));

        for (Comment reply : replies) {
            User user = userMapper.selectById(reply.getUserId());
            if (user != null) reply.setUserNickname(user.getNickname());
        }
        return replies;
    }

    public boolean create(Comment comment) {
        comment.setCreateTime(LocalDateTime.now());
        return commentMapper.insert(comment) > 0;
    }

    public Comment getById(Long id) {
        return commentMapper.selectById(id);
    }

    public long countByBookId(Long bookId) {
        return commentMapper.selectCount(new LambdaQueryWrapper<Comment>().eq(Comment::getBookId, bookId));
    }
}
```

### 4.6 Controller类

#### UserController.java

```java
package com.booktrade.controller;

import com.booktrade.config.LoginInterceptor;
import com.booktrade.entity.User;
import com.booktrade.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public String login(@RequestParam String username,
                        @RequestParam String password,
                        HttpSession session, Model model) {
        User user = userService.login(username, password);
        if (user == null) {
            model.addAttribute("error", "用户名或密码错误");
            return "login";
        }
        session.setAttribute(LoginInterceptor.SESSION_USER, user);
        return "redirect:/";
    }

    @PostMapping("/register")
    public String register(@RequestParam String username,
                           @RequestParam String password,
                           @RequestParam String nickname,
                           @RequestParam(required = false) String phone,
                           Model model) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setNickname(nickname);
        user.setPhone(phone);

        if (!userService.register(user)) {
            model.addAttribute("error", "用户名已存在");
            return "register";
        }
        model.addAttribute("success", "注册成功，请登录");
        return "login";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
}
```

#### HomeController.java

```java
package com.booktrade.controller;

import com.booktrade.config.LoginInterceptor;
import com.booktrade.entity.Book;
import com.booktrade.entity.Category;
import com.booktrade.entity.User;
import com.booktrade.service.BookService;
import com.booktrade.service.CategoryService;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

@Controller
public class HomeController {
    private static final Logger logger = LoggerFactory.getLogger(HomeController.class);
    private final BookService bookService;
    private final CategoryService categoryService;

    public HomeController(BookService bookService, CategoryService categoryService) {
        this.bookService = bookService;
        this.categoryService = categoryService;
    }

    @GetMapping("/")
    public String index(@RequestParam(required = false) String keyword,
                        @RequestParam(required = false) Long categoryId,
                        HttpSession session, Model model) {
        List<Book> books = new ArrayList<>();
        List<Category> categories = new ArrayList<>();
        
        try {
            books = bookService.listOnSale(keyword, categoryId);
        } catch (Exception e) {
            logger.error("Failed to fetch books: {}", e.getMessage());
        }
        
        try {
            categories = categoryService.listAll();
        } catch (Exception e) {
            logger.error("Failed to fetch categories: {}", e.getMessage());
        }
        
        User user = (User) session.getAttribute(LoginInterceptor.SESSION_USER);
        model.addAttribute("books", books);
        model.addAttribute("categories", categories);
        model.addAttribute("keyword", keyword);
        model.addAttribute("categoryId", categoryId);
        model.addAttribute("loginUser", user);
        return "index";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }
}
```

#### BookController.java

```java
package com.booktrade.controller;

import com.booktrade.config.LoginInterceptor;
import com.booktrade.entity.Book;
import com.booktrade.entity.Category;
import com.booktrade.entity.Comment;
import com.booktrade.entity.User;
import com.booktrade.service.BookService;
import com.booktrade.service.CategoryService;
import com.booktrade.service.CommentService;
import com.booktrade.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/book")
public class BookController {
    private final BookService bookService;
    private final CategoryService categoryService;
    private final UserService userService;
    private final CommentService commentService;

    @Value("${book-trade.upload-path}")
    private String uploadPath;

    public BookController(BookService bookService, CategoryService categoryService, 
                          UserService userService, CommentService commentService) {
        this.bookService = bookService;
        this.categoryService = categoryService;
        this.userService = userService;
        this.commentService = commentService;
    }

    @GetMapping("/detail/{id}")
    public String detail(@PathVariable Long id, HttpSession session, Model model) {
        Book book = bookService.getById(id);
        if (book == null) return "redirect:/";
        Category category = categoryService.getById(book.getCategoryId());
        User seller = userService.getById(book.getSellerId());
        User loginUser = (User) session.getAttribute(LoginInterceptor.SESSION_USER);
        List<Comment> comments = commentService.listByBookId(id);

        model.addAttribute("book", book);
        model.addAttribute("category", category);
        model.addAttribute("seller", seller);
        model.addAttribute("loginUser", loginUser);
        model.addAttribute("comments", comments);
        return "book-detail";
    }

    @PostMapping("/comment/{bookId}")
    public String addComment(@PathVariable Long bookId,
                             @RequestParam String content,
                             HttpSession session,
                             RedirectAttributes redirectAttributes) {
        User user = (User) session.getAttribute(LoginInterceptor.SESSION_USER);
        if (user == null) {
            redirectAttributes.addFlashAttribute("error", "请先登录");
            return "redirect:/login";
        }

        Comment comment = new Comment();
        comment.setBookId(bookId);
        comment.setUserId(user.getId());
        comment.setContent(content);

        boolean success = commentService.create(comment);
        if (success) {
            redirectAttributes.addFlashAttribute("success", "留言成功");
        } else {
            redirectAttributes.addFlashAttribute("error", "留言失败");
        }
        return "redirect:/book/detail/" + bookId;
    }

    @PostMapping("/reply/{parentId}")
    public String addReply(@PathVariable Long parentId,
                           @RequestParam String content,
                           HttpSession session,
                           RedirectAttributes redirectAttributes) {
        User user = (User) session.getAttribute(LoginInterceptor.SESSION_USER);
        if (user == null) {
            redirectAttributes.addFlashAttribute("error", "请先登录");
            return "redirect:/login";
        }

        Comment parentComment = commentService.getById(parentId);
        if (parentComment == null) {
            redirectAttributes.addFlashAttribute("error", "留言不存在");
            return "redirect:/";
        }

        if (content == null || content.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "回复内容不能为空");
            return "redirect:/book/detail/" + parentComment.getBookId();
        }

        Comment reply = new Comment();
        reply.setBookId(parentComment.getBookId());
        reply.setUserId(user.getId());
        reply.setParentId(parentId);
        reply.setContent(content);

        boolean success = commentService.create(reply);
        if (success) {
            redirectAttributes.addFlashAttribute("success", "回复成功");
        } else {
            redirectAttributes.addFlashAttribute("error", "回复失败");
        }
        return "redirect:/book/detail/" + parentComment.getBookId();
    }

    @GetMapping("/publish")
    public String publishPage(Model model) {
        model.addAttribute("categories", categoryService.listAll());
        return "book-form";
    }

    @PostMapping("/publish")
    public String publish(@RequestParam String title,
                          @RequestParam(required = false) String author,
                          @RequestParam(required = false) String isbn,
                          @RequestParam BigDecimal price,
                          @RequestParam(required = false) String description,
                          @RequestParam Long categoryId,
                          @RequestParam(defaultValue = "0") Integer condition,
                          @RequestParam(required = false) MultipartFile coverImage,
                          HttpSession session,
                          RedirectAttributes redirectAttributes) throws IOException {
        User user = (User) session.getAttribute(LoginInterceptor.SESSION_USER);

        Book book = new Book();
        book.setTitle(title);
        book.setAuthor(author);
        book.setIsbn(isbn);
        book.setPrice(price);
        book.setDescription(description);
        book.setCategoryId(categoryId);
        book.setCondition(condition);
        book.setSellerId(user.getId());

        if (coverImage != null && !coverImage.isEmpty()) {
            book.setCoverImage(saveImage(coverImage));
        }

        bookService.save(book);
        redirectAttributes.addFlashAttribute("success", "发布成功");
        return "redirect:/book/my";
    }

    @GetMapping("/edit/{id}")
    public String editPage(@PathVariable Long id, HttpSession session, Model model) {
        User user = (User) session.getAttribute(LoginInterceptor.SESSION_USER);
        Book book = bookService.getById(id);
        if (book == null || !book.getSellerId().equals(user.getId())) {
            return "redirect:/book/my";
        }
        model.addAttribute("book", book);
        model.addAttribute("categories", categoryService.listAll());
        return "book-form";
    }

    @PostMapping("/edit/{id}")
    public String edit(@PathVariable Long id,
                       @RequestParam String title,
                       @RequestParam(required = false) String author,
                       @RequestParam(required = false) String isbn,
                       @RequestParam BigDecimal price,
                       @RequestParam(required = false) String description,
                       @RequestParam Long categoryId,
                       @RequestParam(defaultValue = "0") Integer condition,
                       @RequestParam(required = false) MultipartFile coverImage,
                       HttpSession session,
                       RedirectAttributes redirectAttributes) throws IOException {
        User user = (User) session.getAttribute(LoginInterceptor.SESSION_USER);
        Book existing = bookService.getById(id);
        if (existing == null || !existing.getSellerId().equals(user.getId())) {
            return "redirect:/book/my";
        }

        existing.setTitle(title);
        existing.setAuthor(author);
        existing.setIsbn(isbn);
        existing.setPrice(price);
        existing.setDescription(description);
        existing.setCategoryId(categoryId);
        existing.setCondition(condition);

        if (coverImage != null && !coverImage.isEmpty()) {
            existing.setCoverImage(saveImage(coverImage));
        }

        bookService.update(existing);
        redirectAttributes.addFlashAttribute("success", "修改成功");
        return "redirect:/book/my";
    }

    @GetMapping("/my")
    public String myBooks(HttpSession session, Model model) {
        User user = (User) session.getAttribute(LoginInterceptor.SESSION_USER);
        List<Book> books = bookService.listBySeller(user.getId());
        model.addAttribute("books", books);
        model.addAttribute("loginUser", user);
        return "my-books";
    }

    @GetMapping("/off/{id}")
    public String offShelf(@PathVariable Long id,
                           HttpSession session,
                           RedirectAttributes redirectAttributes) {
        User user = (User) session.getAttribute(LoginInterceptor.SESSION_USER);
        Book book = bookService.getById(id);
        if (book != null && book.getSellerId().equals(user.getId())) {
            bookService.offShelf(id);
            redirectAttributes.addFlashAttribute("success", "已下架");
        }
        return "redirect:/book/my";
    }

    private String saveImage(MultipartFile file) throws IOException {
        Path dir = Paths.get(uploadPath);
        if (!Files.exists(dir)) Files.createDirectories(dir);
        String originalName = file.getOriginalFilename();
        String ext = originalName != null && originalName.contains(".")
                ? originalName.substring(originalName.lastIndexOf(".")) : ".jpg";
        String fileName = UUID.randomUUID() + ext;
        Files.copy(file.getInputStream(), dir.resolve(fileName));
        return "/uploads/" + fileName;
    }
}
```

#### OrderController.java

```java
package com.booktrade.controller;

import com.booktrade.config.LoginInterceptor;
import com.booktrade.entity.Book;
import com.booktrade.entity.TradeOrder;
import com.booktrade.entity.User;
import com.booktrade.service.BookService;
import com.booktrade.service.OrderService;
import com.booktrade.service.PaymentService;
import com.booktrade.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/order")
public class OrderController {
    private final OrderService orderService;
    private final BookService bookService;
    private final PaymentService paymentService;
    private final UserService userService;

    public OrderController(OrderService orderService, BookService bookService, 
                           PaymentService paymentService, UserService userService) {
        this.orderService = orderService;
        this.bookService = bookService;
        this.paymentService = paymentService;
        this.userService = userService;
    }

    @PostMapping("/create/{bookId}")
    public String create(@PathVariable Long bookId,
                         HttpSession session,
                         RedirectAttributes redirectAttributes) {
        User user = (User) session.getAttribute(LoginInterceptor.SESSION_USER);
        Book book = bookService.getById(bookId);

        if (book == null || book.getStatus() != 1) {
            redirectAttributes.addFlashAttribute("error", "书籍不可购买");
            return "redirect:/";
        }
        if (book.getSellerId().equals(user.getId())) {
            redirectAttributes.addFlashAttribute("error", "不能购买自己发布的书籍");
            return "redirect:/book/detail/" + bookId;
        }

        TradeOrder order = new TradeOrder();
        order.setBookId(bookId);
        order.setBuyerId(user.getId());
        order.setSellerId(book.getSellerId());
        order.setPrice(book.getPrice());

        orderService.create(order);
        redirectAttributes.addFlashAttribute("success", "下单成功，等待卖家确认");
        return "redirect:/order/my";
    }

    @GetMapping("/my")
    public String myOrders(HttpSession session, Model model) {
        User user = (User) session.getAttribute(LoginInterceptor.SESSION_USER);
        List<TradeOrder> buyOrders = orderService.listByBuyer(user.getId());
        List<TradeOrder> sellOrders = orderService.listBySeller(user.getId());

        model.addAttribute("buyOrders", buyOrders);
        model.addAttribute("sellOrders", sellOrders);
        model.addAttribute("loginUser", user);
        return "my-orders";
    }

    @GetMapping("/confirm/{id}")
    public String confirm(@PathVariable Long id,
                          HttpSession session,
                          RedirectAttributes redirectAttributes) {
        User user = (User) session.getAttribute(LoginInterceptor.SESSION_USER);
        TradeOrder order = orderService.getById(id);
        if (order != null && order.getSellerId().equals(user.getId()) && order.getStatus() == 0) {
            orderService.updateStatus(id, 1);
            redirectAttributes.addFlashAttribute("success", "已确认订单");
        }
        return "redirect:/order/my";
    }

    @GetMapping("/detail/{id}")
    public String detail(@PathVariable Long id,
                         HttpSession session,
                         Model model) {
        User user = (User) session.getAttribute(LoginInterceptor.SESSION_USER);
        TradeOrder order = orderService.getById(id);
        if (order != null) {
            Book book = bookService.getById(order.getBookId());
            User buyer = userService.getById(order.getBuyerId());
            User seller = userService.getById(order.getSellerId());

            model.addAttribute("order", order);
            model.addAttribute("book", book);
            model.addAttribute("buyer", buyer);
            model.addAttribute("seller", seller);
            model.addAttribute("loginUser", user);
            return "trade-detail";
        }
        model.addAttribute("error", "订单不存在");
        return "trade-detail";
    }

    @GetMapping("/payment/{id}")
    public String payment(@PathVariable Long id,
                          HttpSession session,
                          Model model) {
        User user = (User) session.getAttribute(LoginInterceptor.SESSION_USER);
        TradeOrder order = orderService.getById(id);
        if (order != null && order.getBuyerId().equals(user.getId()) && order.getStatus() == 1) {
            Book book = bookService.getById(order.getBookId());
            User buyer = userService.getById(order.getBuyerId());
            User seller = userService.getById(order.getSellerId());

            model.addAttribute("order", order);
            model.addAttribute("book", book);
            model.addAttribute("buyer", buyer);
            model.addAttribute("seller", seller);
            model.addAttribute("loginUser", user);
            return "payment";
        }
        model.addAttribute("error", "订单不存在或无法付款");
        return "payment";
    }

    @PostMapping("/pay/{id}")
    public String pay(@PathVariable Long id,
                      @RequestParam(defaultValue = "card") String payMethod,
                      HttpSession session,
                      RedirectAttributes redirectAttributes) {
        User user = (User) session.getAttribute(LoginInterceptor.SESSION_USER);
        TradeOrder order = orderService.getById(id);
        if (order != null && order.getBuyerId().equals(user.getId()) && order.getStatus() == 1) {
            boolean success = paymentService.pay(id, order.getPrice(), payMethod);
            if (success) {
                redirectAttributes.addFlashAttribute("success", "付款成功");
            } else {
                redirectAttributes.addFlashAttribute("error", "付款失败");
            }
        }
        return "redirect:/order/detail/" + id;
    }

    @GetMapping("/complete/{id}")
    public String complete(@PathVariable Long id,
                           HttpSession session,
                           RedirectAttributes redirectAttributes) {
        User user = (User) session.getAttribute(LoginInterceptor.SESSION_USER);
        TradeOrder order = orderService.getById(id);
        if (order != null && (order.getStatus() == 1 || order.getStatus() == 4)
                && (order.getBuyerId().equals(user.getId()) || order.getSellerId().equals(user.getId()))) {
            orderService.updateStatus(id, 2);
            bookService.markSold(order.getBookId());
            redirectAttributes.addFlashAttribute("success", "交易已完成");
        }
        return "redirect:/order/my";
    }

    @GetMapping("/cancel/{id}")
    public String cancel(@PathVariable Long id,
                         HttpSession session,
                         RedirectAttributes redirectAttributes) {
        User user = (User) session.getAttribute(LoginInterceptor.SESSION_USER);
        TradeOrder order = orderService.getById(id);
        if (order != null && order.getStatus() == 0
                && (order.getBuyerId().equals(user.getId()) || order.getSellerId().equals(user.getId()))) {
            orderService.updateStatus(id, 3);
            redirectAttributes.addFlashAttribute("success", "订单已取消");
        }
        return "redirect:/order/my";
    }
}
```

#### AdminController.java

```java
package com.booktrade.controller;

import com.booktrade.config.LoginInterceptor;
import com.booktrade.entity.Book;
import com.booktrade.entity.OrderLog;
import com.booktrade.entity.TradeOrder;
import com.booktrade.entity.User;
import com.booktrade.service.BookService;
import com.booktrade.service.OrderService;
import com.booktrade.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {
    private final BookService bookService;
    private final OrderService orderService;
    private final UserService userService;

    public AdminController(BookService bookService, OrderService orderService, UserService userService) {
        this.bookService = bookService;
        this.orderService = orderService;
        this.userService = userService;
    }

    @GetMapping
    public String index(HttpSession session, Model model) {
        User user = (User) session.getAttribute(LoginInterceptor.SESSION_USER);
        if (user.getRole() != 1) return "redirect:/";
        List<Book> books = bookService.listAll();
        List<TradeOrder> orders = orderService.listAll();
        model.addAttribute("books", books);
        model.addAttribute("orders", orders);
        model.addAttribute("loginUser", user);
        return "admin";
    }

    @GetMapping("/orders")
    public String orders(HttpSession session, Model model,
                         @RequestParam(required = false) Integer status,
                         @RequestParam(required = false) String startDate,
                         @RequestParam(required = false) String endDate) {
        User user = (User) session.getAttribute(LoginInterceptor.SESSION_USER);
        if (user.getRole() != 1) return "redirect:/";

        LocalDateTime startTime = null;
        LocalDateTime endTime = null;
        if (startDate != null && !startDate.isEmpty()) {
            startTime = LocalDate.parse(startDate).atStartOfDay();
        }
        if (endDate != null && !endDate.isEmpty()) {
            endTime = LocalDate.parse(endDate).atTime(LocalTime.MAX);
        }

        List<TradeOrder> orders = orderService.listByStatusAndTimeRange(status, startTime, endTime);
        model.addAttribute("orders", orders);
        model.addAttribute("status", status);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        model.addAttribute("loginUser", user);
        return "admin-orders";
    }

    @GetMapping("/order/detail/{id}")
    public String orderDetail(@PathVariable Long id, HttpSession session, Model model) {
        User user = (User) session.getAttribute(LoginInterceptor.SESSION_USER);
        if (user.getRole() != 1) return "redirect:/";

        TradeOrder order = orderService.getById(id);
        if (order == null) return "redirect:/admin/orders";

        User buyer = userService.getById(order.getBuyerId());
        User seller = userService.getById(order.getSellerId());
        Book book = bookService.getById(order.getBookId());
        List<OrderLog> logs = orderService.getLogsByOrderId(id);

        model.addAttribute("order", order);
        model.addAttribute("buyer", buyer);
        model.addAttribute("seller", seller);
        model.addAttribute("book", book);
        model.addAttribute("logs", logs);
        model.addAttribute("loginUser", user);
        return "admin-order-detail";
    }

    @GetMapping("/order/confirm/{id}")
    public String confirmOrder(@PathVariable Long id, HttpSession session,
                               RedirectAttributes redirectAttributes) {
        User user = (User) session.getAttribute(LoginInterceptor.SESSION_USER);
        if (user.getRole() != 1) return "redirect:/";

        TradeOrder order = orderService.getById(id);
        if (order != null && order.getStatus() == 0) {
            orderService.updateStatus(id, 1, user.getId(), user.getNickname());
            redirectAttributes.addFlashAttribute("success", "订单已确认");
        } else {
            redirectAttributes.addFlashAttribute("error", "订单状态不正确");
        }
        return "redirect:/admin/order/detail/" + id;
    }

    @GetMapping("/order/complete/{id}")
    public String completeOrder(@PathVariable Long id, HttpSession session,
                                RedirectAttributes redirectAttributes) {
        User user = (User) session.getAttribute(LoginInterceptor.SESSION_USER);
        if (user.getRole() != 1) return "redirect:/";

        TradeOrder order = orderService.getById(id);
        if (order != null && order.getStatus() == 1) {
            orderService.updateStatus(id, 2, user.getId(), user.getNickname());
            bookService.markSold(order.getBookId());
            redirectAttributes.addFlashAttribute("success", "订单已完成");
        } else {
            redirectAttributes.addFlashAttribute("error", "订单状态不正确");
        }
        return "redirect:/admin/order/detail/" + id;
    }

    @GetMapping("/order/cancel/{id}")
    public String cancelOrder(@PathVariable Long id, HttpSession session,
                              RedirectAttributes redirectAttributes) {
        User user = (User) session.getAttribute(LoginInterceptor.SESSION_USER);
        if (user.getRole() != 1) return "redirect:/";

        TradeOrder order = orderService.getById(id);
        if (order != null && order.getStatus() == 0) {
            orderService.updateStatus(id, 3, user.getId(), user.getNickname());
            redirectAttributes.addFlashAttribute("success", "订单已取消");
        } else {
            redirectAttributes.addFlashAttribute("error", "订单状态不正确");
        }
        return "redirect:/admin/order/detail/" + id;
    }

    @GetMapping("/book/delete/{id}")
    public String deleteBook(@PathVariable Long id,
                             HttpSession session,
                             RedirectAttributes redirectAttributes) {
        User user = (User) session.getAttribute(LoginInterceptor.SESSION_USER);
        if (user.getRole() != 1) return "redirect:/";
        bookService.delete(id);
        redirectAttributes.addFlashAttribute("success", "书籍已删除");
        return "redirect:/admin";
    }
}
```

## 五、前端页面模板

### 5.1 布局组件 - fragments/layout.html

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head th:fragment="head(title)">
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title th:text="${title}">二手书交易平台</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <style>
        body { min-height: 100vh; display: flex; flex-direction: column; }
        .main-content { flex: 1; }
        .footer { background-color: #f8f9fa; padding: 20px 0; text-align: center; }
        .comment-item { margin-bottom: 15px; padding: 15px; border-radius: 8px; }
        .reply-item { margin-left: 30px; margin-bottom: 10px; padding: 10px; background: #f8f9fa; border-radius: 6px; }
    </style>
</head>

<body>
    <nav th:fragment="navbar" class="navbar navbar-expand-lg navbar-dark bg-primary">
        <div class="container">
            <a class="navbar-brand" th:href="@{/}" th:text="#{app.title}">二手书交易平台</a>
            <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav">
                <span class="navbar-toggler-icon"></span>
            </button>
            <div class="collapse navbar-collapse" id="navbarNav">
                <ul class="navbar-nav me-auto">
                    <li class="nav-item"><a class="nav-link" th:href="@{/}" th:text="#{nav.home}">首页</a></li>
                    <li class="nav-item"><a class="nav-link" th:href="@{/book/publish}" th:text="#{nav.publish}">发布书籍</a></li>
                    <li class="nav-item"><a class="nav-link" th:href="@{/book/my}" th:text="#{nav.mybooks}">我的发布</a></li>
                    <li class="nav-item"><a class="nav-link" th:href="@{/order/my}" th:text="#{nav.myorders}">我的订单</a></li>
                </ul>
                <ul class="navbar-nav">
                    <li th:if="${loginUser != null}" class="nav-item dropdown">
                        <a class="nav-link dropdown-toggle" href="#" role="button" data-bs-toggle="dropdown">
                            <span th:text="${loginUser.nickname}">用户</span>
                        </a>
                        <ul class="dropdown-menu">
                            <li><a class="dropdown-item" th:href="@{/logout}" th:text="#{nav.logout}">退出登录</a></li>
                            <li th:if="${loginUser.role == 1}">
                                <a class="dropdown-item" th:href="@{/admin}" th:text="#{nav.admin}">管理后台</a>
                            </li>
                        </ul>
                    </li>
                    <li th:if="${loginUser == null}" class="nav-item">
                        <a class="nav-link" th:href="@{/login}" th:text="#{nav.login}">登录</a>
                    </li>
                    <li th:if="${loginUser == null}" class="nav-item">
                        <a class="nav-link" th:href="@{/register}" th:text="#{nav.register}">注册</a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" th:href="@{/(lang=zh_CN)}">中文</a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" th:href="@{/(lang=en_US)}">English</a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" th:href="@{/(lang=ko_KR)}">한국어</a>
                    </li>
                </ul>
            </div>
        </div>
    </nav>

    <div th:fragment="alert" class="container mt-3">
        <div th:if="${success != null}" class="alert alert-success" th:text="${success}"></div>
    </div>

    <div th:fragment="alert-error" class="container mt-3">
        <div th:if="${error != null}" class="alert alert-danger" th:text="${error}"></div>
    </div>

    <div th:fragment="scripts">
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    </div>

    <footer th:fragment="footer" class="footer">
        <div class="container">
            <p th:text="#{footer.copyright}">© 2024 二手书交易平台</p>
        </div>
    </footer>
</body>
</html>
```

### 5.2 首页 - index.html

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org" lang="zh-CN">
<head th:replace="~{fragments/layout :: head(#{app.title})}"></head>
<body>
    <div th:replace="~{fragments/layout :: navbar}"></div>

    <div class="container mt-4 main-content">
        <div th:replace="~{fragments/layout :: alert}"></div>

        <div class="row mb-4">
            <div class="col-md-6">
                <form th:action="@{/}" method="get" class="input-group">
                    <input type="text" class="form-control" name="keyword" th:value="${keyword}" 
                           placeholder="#{search.placeholder}" />
                    <button type="submit" class="btn btn-primary" th:text="#{search.button}">搜索</button>
                </form>
            </div>
            <div class="col-md-6">
                <div class="btn-group">
                    <button type="button" class="btn btn-outline-secondary dropdown-toggle" data-bs-toggle="dropdown">
                        <span th:text="${categoryId != null ? #messages.msg('category.' + categoryId) : #{category.all}}">全部分类</span>
                    </button>
                    <ul class="dropdown-menu">
                        <li><a class="dropdown-item" th:href="@{/}" th:text="#{category.all}">全部分类</a></li>
                        <li th:each="category : ${categories}">
                            <a class="dropdown-item" th:href="@{/(categoryId=${category.id})}" th:text="${category.name}"></a>
                        </li>
                    </ul>
                </div>
            </div>
        </div>

        <div class="row">
            <div th:each="book : ${books}" class="col-md-4 mb-4">
                <div class="card h-100">
                    <div class="card-body">
                        <h5 class="card-title" th:text="${book.title}">书名</h5>
                        <p class="card-text text-muted" th:text="${book.author}">作者</p>
                        <p class="card-text text-muted small" th:text="${book.description}">描述</p>
                        <div class="d-flex justify-content-between align-items-center">
                            <span class="text-primary font-bold">₩<span th:text="${book.price}">0.00</span></span>
                            <a th:href="@{/book/detail/{id}(id=${book.id})}" class="btn btn-sm btn-outline-primary" th:text="#{book.detail}">查看详情</a>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <div th:if="${books == null or books.isEmpty()}" class="text-center text-muted py-8">
            <p th:text="#{home.nobooks}">暂无书籍</p>
        </div>
    </div>

    <div th:replace="~{fragments/layout :: footer}"></div>
    <div th:replace="~{fragments/layout :: scripts}"></div>
</body>
</html>
```

### 5.3 登录页 - login.html

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org" lang="zh-CN">
<head th:replace="~{fragments/layout :: head(#{login.title})}"></head>
<body>
    <div th:replace="~{fragments/layout :: navbar}"></div>

    <div class="container mt-5 main-content">
        <div class="row justify-content-center">
            <div class="col-md-6">
                <div class="card">
                    <div class="card-header bg-primary text-white text-center">
                        <h4 th:text="#{login.title}">用户登录</h4>
                    </div>
                    <div class="card-body">
                        <div th:if="${error != null}" class="alert alert-danger" th:text="${error}"></div>
                        <div th:if="${success != null}" class="alert alert-success" th:text="${success}"></div>
                        
                        <form th:action="@{/login}" method="post">
                            <div class="mb-3">
                                <label class="form-label" th:text="#{login.username}">用户名</label>
                                <input type="text" class="form-control" name="username" required />
                            </div>
                            <div class="mb-3">
                                <label class="form-label" th:text="#{login.password}">密码</label>
                                <input type="password" class="form-control" name="password" required />
                            </div>
                            <button type="submit" class="btn btn-primary w-100" th:text="#{login.button}">登录</button>
                        </form>
                        
                        <p class="text-center mt-3">
                            <a th:href="@{/register}" th:text="#{login.register}">还没有账号？点击注册</a>
                        </p>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <div th:replace="~{fragments/layout :: footer}"></div>
    <div th:replace="~{fragments/layout :: scripts}"></div>
</body>
</html>
```

### 5.4 注册页 - register.html

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org" lang="zh-CN">
<head th:replace="~{fragments/layout :: head(#{register.title})}"></head>
<body>
    <div th:replace="~{fragments/layout :: navbar}"></div>

    <div class="container mt-5 main-content">
        <div class="row justify-content-center">
            <div class="col-md-6">
                <div class="card">
                    <div class="card-header bg-primary text-white text-center">
                        <h4 th:text="#{register.title}">用户注册</h4>
                    </div>
                    <div class="card-body">
                        <div th:if="${error != null}" class="alert alert-danger" th:text="${error}"></div>
                        
                        <form th:action="@{/register}" method="post">
                            <div class="mb-3">
                                <label class="form-label" th:text="#{register.username}">用户名</label>
                                <input type="text" class="form-control" name="username" required />
                            </div>
                            <div class="mb-3">
                                <label class="form-label" th:text="#{register.password}">密码</label>
                                <input type="password" class="form-control" name="password" required />
                            </div>
                            <div class="mb-3">
                                <label class="form-label" th:text="#{register.nickname}">昵称</label>
                                <input type="text" class="form-control" name="nickname" required />
                            </div>
                            <div class="mb-3">
                                <label class="form-label" th:text="#{register.phone}">手机号（选填）</label>
                                <input type="tel" class="form-control" name="phone" />
                            </div>
                            <button type="submit" class="btn btn-primary w-100" th:text="#{register.button}">注册</button>
                        </form>
                        
                        <p class="text-center mt-3">
                            <a th:href="@{/login}" th:text="#{register.login}">已有账号？点击登录</a>
                        </p>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <div th:replace="~{fragments/layout :: footer}"></div>
    <div th:replace="~{fragments/layout :: scripts}"></div>
</body>
</html>
```

### 5.5 书籍详情页（含留言板）- book-detail.html

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org" lang="zh-CN">
<head th:replace="~{fragments/layout :: head(#{book.detail})}"></head>
<body>
    <div th:replace="~{fragments/layout :: navbar}"></div>

    <div class="container mt-4 main-content">
        <div th:replace="~{fragments/layout :: alert}"></div>
        <div th:replace="~{fragments/layout :: alert-error}"></div>

        <div class="row" th:if="${book != null}">
            <div class="col-md-8">
                <div class="card mb-4">
                    <div class="card-body">
                        <div class="row">
                            <div class="col-md-4">
                                <img th:if="${book.coverImage != null}" th:src="${book.coverImage}" 
                                     class="img-fluid rounded" alt="封面">
                                <div th:else class="bg-light rounded p-4 text-center text-muted">无封面</div>
                            </div>
                            <div class="col-md-8">
                                <h2 th:text="${book.title}">书名</h2>
                                <p class="text-muted" th:text="${book.author}">作者</p>
                                <p class="text-muted" th:text="${book.isbn}">ISBN</p>
                                <p class="text-primary font-bold text-xl">₩<span th:text="${book.price}">0.00</span></p>
                                <div class="mt-2">
                                    <span class="badge bg-secondary" th:text="#{book.category}">分类</span>
                                    <span th:text="${#messages.msg('category.' + book.categoryId)}">分类名</span>
                                </div>
                                <div class="mt-2">
                                    <span th:if="${book.condition == 0}" class="badge bg-success" th:text="#{book.condition.new}">全新</span>
                                    <span th:if="${book.condition == 1}" class="badge bg-info" th:text="#{book.condition.good}">良好</span>
                                    <span th:if="${book.condition == 2}" class="badge bg-warning" th:text="#{book.condition.fair}">一般</span>
                                </div>
                            </div>
                        </div>
                        <div class="mt-4">
                            <h5 th:text="#{book.description}">描述</h5>
                            <p th:text="${book.description}">暂无描述</p>
                        </div>
                    </div>
                </div>

                <div class="card mb-4">
                    <div class="card-header" th:text="#{book.seller}">卖家信息</div>
                    <div class="card-body">
                        <p><strong th:text="#{user.nickname}">昵称：</strong><span th:text="${seller.nickname}"></span></p>
                        <p><strong th:text="#{user.phone}">手机号：</strong><span th:text="${seller.phone != null ? seller.phone : '-'}"></span></p>
                    </div>
                </div>

                <div class="card mb-4">
                    <div class="card-header">
                        <span th:text="#{comment.title}">留言板</span>
                        <span class="badge bg-secondary ms-2" th:text="${comments != null ? comments.size() : 0}"></span>
                    </div>
                    <div class="card-body">
                        <form th:if="${loginUser != null}" th:action="@{/book/comment/{bookId}(bookId=${book.id})}" method="post">
                            <div class="mb-3">
                                <textarea class="form-control" name="content" rows="3" 
                                          placeholder="#{comment.placeholder}" required></textarea>
                            </div>
                            <button type="submit" class="btn btn-primary" th:text="#{comment.submit}">发表留言</button>
                        </form>
                        <div th:if="${loginUser == null}" class="alert alert-info" th:text="#{comment.login}"></div>

                        <div th:if="${comments != null && !comments.isEmpty()}" class="mt-4">
                            <div th:each="comment : ${comments}" class="list-group-item comment-item">
                                <div class="d-flex justify-content-between">
                                    <strong class="text-primary" th:text="${comment.userNickname}">用户</strong>
                                    <span class="text-muted small" th:text="${#temporals.format(comment.createTime, 'yyyy-MM-dd HH:mm')}"></span>
                                </div>
                                <p class="mt-2" th:text="${comment.content}">留言内容</p>
                                <button type="button" class="btn btn-sm btn-outline-secondary reply-btn mt-2" 
                                        th:attr="data-comment-id=${comment.id}" th:text="#{comment.reply}">回复</button>

                                <div th:if="${comment.replies != null && !comment.replies.isEmpty()}" class="replies-container mt-3">
                                    <div th:each="reply : ${comment.replies}" class="reply-item">
                                        <div class="d-flex justify-content-between">
                                            <strong class="text-primary" th:text="${reply.userNickname}">回复者</strong>
                                            <span class="text-muted small" th:text="${#temporals.format(reply.createTime, 'yyyy-MM-dd HH:mm')}"></span>
                                        </div>
                                        <p class="mt-1" th:text="${reply.content}">回复内容</p>
                                    </div>
                                </div>

                                <div class="reply-form-container mt-3" style="display: none;" th:id="|reply-form-${comment.id}|">
                                    <form th:action="@{/book/reply/{parentId}(parentId=${comment.id})}" method="post">
                                        <textarea name="content" class="form-control form-control-sm" rows="2" 
                                                  placeholder="#{comment.reply.placeholder}" required></textarea>
                                        <button type="submit" class="btn btn-sm btn-primary mt-2" th:text="#{comment.reply.submit}">发送回复</button>
                                    </form>
                                </div>
                            </div>
                        </div>
                        <div th:if="${comments == null || comments.isEmpty()}" class="text-center text-muted py-4" th:text="#{comment.empty}">暂无留言</div>
                    </div>
                </div>
            </div>

            <div class="col-md-4">
                <div class="card">
                    <div class="card-body">
                        <div th:if="${book.status == 1}">
                            <span th:if="${loginUser != null && loginUser.id != book.sellerId}">
                                <form th:action="@{/order/create/{bookId}(bookId=${book.id})}" method="post">
                                    <button type="submit" class="btn btn-primary w-100" th:text="#{book.buy}">立即购买</button>
                                </form>
                            </span>
                            <div th:if="${loginUser == null}" class="alert alert-warning" th:text="#{book.login.buy}">请登录后购买</div>
                            <div th:if="${loginUser != null && loginUser.id == book.sellerId}" class="alert alert-info" th:text="#{book.self}">不能购买自己发布的书籍</div>
                        </div>
                        <span th:if="${book.status == 2}" class="badge bg-secondary" th:text="#{book.detail.sold}">已售出</span>
                        <span th:if="${book.status == 0}" class="badge bg-secondary" th:text="#{book.detail.off}">已下架</span>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <div th:replace="~{fragments/layout :: footer}"></div>
    <div th:replace="~{fragments/layout :: scripts}"></div>
    <script>
        document.addEventListener('DOMContentLoaded', function() {
            const replyBtns = document.querySelectorAll('.reply-btn');
            replyBtns.forEach(btn => {
                btn.addEventListener('click', function() {
                    const commentId = this.getAttribute('data-comment-id');
                    const formContainer = document.getElementById('reply-form-' + commentId);
                    formContainer.style.display = formContainer.style.display === 'none' ? 'block' : 'none';
                });
            });
        });
    </script>
</body>
</html>
```

### 5.6 书籍发布/编辑页 - book-form.html

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org" lang="zh-CN">
<head th:replace="~{fragments/layout :: head(#{book.publish})}"></head>
<body>
    <div th:replace="~{fragments/layout :: navbar}"></div>

    <div class="container mt-4 main-content">
        <div th:replace="~{fragments/layout :: alert}"></div>

        <h4 th:text="${book != null ? #{book.edit} : #{book.publish}}">发布书籍</h4>

        <form th:action="${book != null ? @{/book/edit/{id}(id=${book.id})} : @{/book/publish}}" 
              method="post" enctype="multipart/form-data">
            <div class="row">
                <div class="col-md-6">
                    <div class="mb-3">
                        <label class="form-label" th:text="#{book.title}">书名 <span class="text-danger">*</span></label>
                        <input type="text" class="form-control" name="title" th:value="${book?.title}" required />
                    </div>
                    <div class="mb-3">
                        <label class="form-label" th:text="#{book.author}">作者</label>
                        <input type="text" class="form-control" name="author" th:value="${book?.author}" />
                    </div>
                    <div class="mb-3">
                        <label class="form-label" th:text="#{book.isbn}">ISBN</label>
                        <input type="text" class="form-control" name="isbn" th:value="${book?.isbn}" />
                    </div>
                    <div class="mb-3">
                        <label class="form-label" th:text="#{book.price}">价格 <span class="text-danger">*</span></label>
                        <input type="number" step="0.01" class="form-control" name="price" th:value="${book?.price}" required />
                    </div>
                    <div class="mb-3">
                        <label class="form-label" th:text="#{book.category}">分类 <span class="text-danger">*</span></label>
                        <select class="form-select" name="categoryId" required>
                            <option value="">请选择分类</option>
                            <option th:each="category : ${categories}" 
                                    th:value="${category.id}" 
                                    th:text="${category.name}"
                                    th:selected="${book?.categoryId == category.id}"></option>
                        </select>
                    </div>
                </div>
                <div class="col-md-6">
                    <div class="mb-3">
                        <label class="form-label" th:text="#{book.condition}">书籍状态</label>
                        <select class="form-select" name="condition">
                            <option value="0" th:selected="${book?.condition == 0}" th:text="#{book.condition.new}">全新</option>
                            <option value="1" th:selected="${book?.condition == 1}" th:text="#{book.condition.good}">良好</option>
                            <option value="2" th:selected="${book?.condition == 2}" th:text="#{book.condition.fair}">一般</option>
                        </select>
                    </div>
                    <div class="mb-3">
                        <label class="form-label" th:text="#{book.cover}">封面图片</label>
                        <input type="file" class="form-control" name="coverImage" accept="image/*" />
                        <img th:if="${book?.coverImage != null}" th:src="${book.coverImage}" 
                             class="img-fluid mt-2" style="max-height: 200px;" />
                    </div>
                    <div class="mb-3">
                        <label class="form-label" th:text="#{book.description}">描述</label>
                        <textarea class="form-control" name="description" rows="5" th:text="${book?.description}"></textarea>
                    </div>
                </div>
            </div>
            <button type="submit" class="btn btn-primary" th:text="${book != null ? #{book.update} : #{book.submit}}">提交</button>
            <a th:href="@{/book/my}" class="btn btn-secondary ms-2" th:text="#{book.cancel}">取消</a>
        </form>
    </div>

    <div th:replace="~{fragments/layout :: footer}"></div>
    <div th:replace="~{fragments/layout :: scripts}"></div>
</body>
</html>
```

### 5.7 我的发布页 - my-books.html

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org" lang="zh-CN">
<head th:replace="~{fragments/layout :: head(#{nav.mybooks})}"></head>
<body>
    <div th:replace="~{fragments/layout :: navbar}"></div>

    <div class="container mt-4 main-content">
        <div th:replace="~{fragments/layout :: alert}"></div>

        <h4 th:text="#{nav.mybooks}">我的发布</h4>

        <div class="row">
            <div th:each="book : ${books}" class="col-md-4 mb-4">
                <div class="card">
                    <div class="card-body">
                        <h5 class="card-title" th:text="${book.title}">书名</h5>
                        <p class="card-text text-muted" th:text="${book.author}">作者</p>
                        <p class="text-primary font-bold">₩<span th:text="${book.price}">0.00</span></p>
                        <div class="mt-2">
                            <span th:if="${book.status == 0}" class="badge bg-secondary" th:text="#{book.detail.off}">已下架</span>
                            <span th:if="${book.status == 1}" class="badge bg-success" th:text="#{book.status}">在售</span>
                            <span th:if="${book.status == 2}" class="badge bg-warning" th:text="#{book.detail.sold}">已售</span>
                        </div>
                        <div class="mt-3">
                            <a th:href="@{/book/detail/{id}(id=${book.id})}" class="btn btn-sm btn-outline-primary" th:text="#{book.detail}">详情</a>
                            <a th:href="@{/book/edit/{id}(id=${book.id})}" class="btn btn-sm btn-outline-secondary" th:text="#{book.edit}">编辑</a>
                            <a th:href="@{/book/off/{id}(id=${book.id})}" class="btn btn-sm btn-outline-danger" th:text="#{book.off}"
                               th:if="${book.status == 1}">下架</a>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <div th:if="${books == null || books.isEmpty()}" class="text-center text-muted py-8">
            <p th:text="#{mybooks.empty}">暂无发布书籍</p>
            <a th:href="@{/book/publish}" class="btn btn-primary mt-2" th:text="#{book.publish}">发布书籍</a>
        </div>
    </div>

    <div th:replace="~{fragments/layout :: footer}"></div>
    <div th:replace="~{fragments/layout :: scripts}"></div>
</body>
</html>
```

### 5.8 我的订单页 - my-orders.html

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org" lang="zh-CN">
<head th:replace="~{fragments/layout :: head(#{nav.myorders})}"></head>
<body>
    <div th:replace="~{fragments/layout :: navbar}"></div>

    <div class="container mt-4 main-content">
        <div th:replace="~{fragments/layout :: alert}"></div>

        <h4 th:text="#{myorders.title}">我的订单</h4>

        <div class="mb-4">
            <h5 th:text="#{myorders.buy}">我购买的</h5>
            <div class="table-responsive">
                <table class="table table-hover">
                    <thead>
                        <tr>
                            <th th:text="#{myorders.orderid}">订单号</th>
                            <th th:text="#{book.title}">书籍</th>
                            <th th:text="#{myorders.seller}">卖家</th>
                            <th th:text="#{myorders.amount}">价格</th>
                            <th th:text="#{myorders.status}">状态</th>
                            <th th:text="#{myorders.time}">时间</th>
                            <th th:text="#{myorders.action}">操作</th>
                        </tr>
                    </thead>
                    <tbody>
                        <tr th:each="order : ${buyOrders}">
                            <td><a th:href="@{/order/detail/{id}(id=${order.id})}" th:text="${order.orderNo}"></a></td>
                            <td th:text="${order.bookId}"></td>
                            <td th:text="${order.sellerId}"></td>
                            <td>₩<span th:text="${order.price}"></span></td>
                            <td>
                                <span th:if="${order.status == 0}" class="badge bg-warning text-dark" th:text="#{myorders.status.pending}"></span>
                                <span th:if="${order.status == 1}" class="badge bg-info" th:text="#{myorders.status.confirmed}"></span>
                                <span th:if="${order.status == 4}" class="badge bg-primary" th:text="#{myorders.status.paid}"></span>
                                <span th:if="${order.status == 2}" class="badge bg-success" th:text="#{myorders.status.completed}"></span>
                                <span th:if="${order.status == 3}" class="badge bg-secondary" th:text="#{myorders.status.cancelled}"></span>
                            </td>
                            <td th:text="${#temporals.format(order.createTime, 'yyyy-MM-dd HH:mm')}"></td>
                            <td>
                                <a th:href="@{/order/detail/{id}(id=${order.id})}" class="btn btn-sm btn-outline-primary" th:text="#{myorders.detail}"></a>
                                <a th:href="@{/order/cancel/{id}(id=${order.id})}" class="btn btn-sm btn-outline-danger" th:text="#{myorders.cancel}"
                                   th:if="${order.status == 0}"></a>
                                <a th:href="@{/order/payment/{id}(id=${order.id})}" class="btn btn-sm btn-primary" th:text="#{payment.title}"
                                   th:if="${order.status == 1}"></a>
                            </td>
                        </tr>
                    </tbody>
                </table>
            </div>
            <div th:if="${buyOrders == null || buyOrders.isEmpty()}" class="text-center text-muted py-4" th:text="#{myorders.empty}">暂无订单</div>
        </div>

        <div>
            <h5 th:text="#{myorders.sell}">我卖出的</h5>
            <div class="table-responsive">
                <table class="table table-hover">
                    <thead>
                        <tr>
                            <th th:text="#{myorders.orderid}">订单号</th>
                            <th th:text="#{book.title}">书籍</th>
                            <th th:text="#{myorders.buyer}">买家</th>
                            <th th:text="#{myorders.amount}">价格</th>
                            <th th:text="#{myorders.status}">状态</th>
                            <th th:text="#{myorders.time}">时间</th>
                            <th th:text="#{myorders.action}">操作</th>
                        </tr>
                    </thead>
                    <tbody>
                        <tr th:each="order : ${sellOrders}">
                            <td><a th:href="@{/order/detail/{id}(id=${order.id})}" th:text="${order.orderNo}"></a></td>
                            <td th:text="${order.bookId}"></td>
                            <td th:text="${order.buyerId}"></td>
                            <td>₩<span th:text="${order.price}"></span></td>
                            <td>
                                <span th:if="${order.status == 0}" class="badge bg-warning text-dark" th:text="#{myorders.status.pending}"></span>
                                <span th:if="${order.status == 1}" class="badge bg-info" th:text="#{myorders.status.confirmed}"></span>
                                <span th:if="${order.status == 4}" class="badge bg-primary" th:text="#{myorders.status.paid}"></span>
                                <span th:if="${order.status == 2}" class="badge bg-success" th:text="#{myorders.status.completed}"></span>
                                <span th:if="${order.status == 3}" class="badge bg-secondary" th:text="#{myorders.status.cancelled}"></span>
                            </td>
                            <td th:text="${#temporals.format(order.createTime, 'yyyy-MM-dd HH:mm')}"></td>
                            <td>
                                <a th:href="@{/order/detail/{id}(id=${order.id})}" class="btn btn-sm btn-outline-primary" th:text="#{myorders.detail}"></a>
                                <a th:href="@{/order/confirm/{id}(id=${order.id})}" class="btn btn-sm btn-primary" th:text="#{myorders.confirm}"
                                   th:if="${order.status == 0}"></a>
                                <a th:href="@{/order/complete/{id}(id=${order.id})}" class="btn btn-sm btn-success" th:text="#{myorders.complete}"
                                   th:if="${order.status == 1 || order.status == 4}"></a>
                            </td>
                        </tr>
                    </tbody>
                </table>
            </div>
            <div th:if="${sellOrders == null || sellOrders.isEmpty()}" class="text-center text-muted py-4" th:text="#{myorders.empty}">暂无订单</div>
        </div>
    </div>

    <div th:replace="~{fragments/layout :: footer}"></div>
    <div th:replace="~{fragments/layout :: scripts}"></div>
</body>
</html>
```

### 5.9 交易详情页 - trade-detail.html

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org" lang="zh-CN">
<head th:replace="~{fragments/layout :: head(#{myorders.title})}"></head>
<body>
    <div th:replace="~{fragments/layout :: navbar}"></div>

    <div class="container mt-4 main-content">
        <div th:replace="~{fragments/layout :: alert}"></div>
        <div th:replace="~{fragments/layout :: alert-error}"></div>

        <div th:if="${order != null}">
            <div class="d-flex justify-content-between align-items-center mb-4">
                <h4 th:text="#{myorders.title}">交易详情</h4>
                <a th:href="@{/order/my}" class="btn btn-sm btn-outline-primary" th:text="#{myorders.back}">返回订单列表</a>
            </div>

            <div class="row">
                <div class="col-md-8">
                    <div class="card mb-4">
                        <div class="card-header bg-primary text-white" th:text="#{myorders.orderinfo}">订单信息</div>
                        <div class="card-body">
                            <div class="row mb-3">
                                <div class="col-md-6">
                                    <strong th:text="#{myorders.orderid}">订单号：</strong>
                                    <span th:text="${order.orderNo}"></span>
                                </div>
                                <div class="col-md-6">
                                    <strong th:text="#{myorders.status}">订单状态：</strong>
                                    <span th:if="${order.status == 0}" class="badge bg-warning text-dark" th:text="#{myorders.status.pending}"></span>
                                    <span th:if="${order.status == 1}" class="badge bg-info" th:text="#{myorders.status.confirmed}"></span>
                                    <span th:if="${order.status == 4}" class="badge bg-primary" th:text="#{myorders.status.paid}"></span>
                                    <span th:if="${order.status == 2}" class="badge bg-success" th:text="#{myorders.status.completed}"></span>
                                    <span th:if="${order.status == 3}" class="badge bg-secondary" th:text="#{myorders.status.cancelled}"></span>
                                </div>
                            </div>
                            <div class="row mb-3">
                                <div class="col-md-6">
                                    <strong th:text="#{myorders.time}">下单时间：</strong>
                                    <span th:text="${#temporals.format(order.createTime, 'yyyy-MM-dd HH:mm:ss')}"></span>
                                </div>
                                <div class="col-md-6">
                                    <strong th:text="#{myorders.amount}">交易金额：</strong>
                                    <span class="text-primary fw-bold">₩<span th:text="${order.price}"></span></span>
                                </div>
                            </div>
                        </div>
                    </div>

                    <div class="card mb-4">
                        <div class="card-header bg-primary text-white" th:text="#{book.title}">商品详情</div>
                        <div class="card-body">
                            <div th:if="${book != null}">
                                <div class="row">
                                    <div class="col-md-4">
                                        <img th:if="${book.coverImage != null}" th:src="${book.coverImage}" 
                                             class="img-fluid rounded" alt="封面">
                                        <div th:else class="bg-light rounded p-4 text-center text-muted">无封面</div>
                                    </div>
                                    <div class="col-md-8">
                                        <h5 th:text="${book.title}"></h5>
                                        <p class="text-muted" th:text="${book.author}"></p>
                                        <p class="text-muted" th:text="${book.description}"></p>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

                <div class="col-md-4">
                    <div class="card mb-4">
                        <div class="card-header bg-primary text-white" th:text="#{myorders.buyer}">买家信息</div>
                        <div class="card-body">
                            <p th:if="${buyer != null}">
                                <strong th:text="#{user.nickname}">昵称：</strong><span th:text="${buyer.nickname}"></span>
                            </p>
                        </div>
                    </div>

                    <div class="card mb-4">
                        <div class="card-header bg-primary text-white" th:text="#{myorders.seller}">卖家信息</div>
                        <div class="card-body">
                            <p th:if="${seller != null}">
                                <strong th:text="#{user.nickname}">昵称：</strong><span th:text="${seller.nickname}"></span>
                            </p>
                        </div>
                    </div>

                    <div class="card">
                        <div class="card-header bg-primary text-white" th:text="#{myorders.action}">操作</div>
                        <div class="card-body">
                            <div th:if="${order.status == 0}">
                                <a th:href="@{/order/confirm/{id}(id=${order.id})}" class="btn btn-sm btn-primary w-100 mb-2"
                                   th:if="${loginUser.id == order.sellerId}" th:text="#{myorders.confirm}">确认订单</a>
                                <a th:href="@{/order/cancel/{id}(id=${order.id})}" class="btn btn-sm btn-outline-danger w-100"
                                   th:text="#{myorders.cancel}">取消订单</a>
                            </div>
                            <div th:if="${order.status == 1}">
                                <a th:href="@{/order/payment/{id}(id=${order.id})}" class="btn btn-sm btn-primary w-100 mb-2"
                                   th:if="${loginUser.id == order.buyerId}" th:text="#{payment.pay}">去付款</a>
                                <a th:href="@{/order/complete/{id}(id=${order.id})}" class="btn btn-sm btn-success w-100"
                                   th:if="${loginUser.id == order.sellerId}" th:text="#{myorders.complete}">确认完成</a>
                            </div>
                            <div th:if="${order.status == 4}">
                                <a th:href="@{/order/complete/{id}(id=${order.id})}" class="btn btn-sm btn-success w-100"
                                   th:text="#{myorders.complete}">确认完成</a>
                            </div>
                            <div th:if="${order.status == 2}" class="text-center text-success" th:text="#{myorders.status.completed}"></div>
                            <div th:if="${order.status == 3}" class="text-center text-secondary" th:text="#{myorders.status.cancelled}"></div>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <div th:if="${order == null}" class="text-center text-muted py-8">
            <p th:text="#{myorders.notfound}">订单不存在</p>
            <a th:href="@{/order/my}" class="btn btn-primary mt-2" th:text="#{myorders.back}">返回订单列表</a>
        </div>
    </div>

    <div th:replace="~{fragments/layout :: footer}"></div>
    <div th:replace="~{fragments/layout :: scripts}"></div>
</body>
</html>
```

### 5.10 付款页面 - payment.html

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org" lang="zh-CN">
<head th:replace="~{fragments/layout :: head(#{payment.title})}"></head>
<body>
    <div th:replace="~{fragments/layout :: navbar}"></div>

    <div class="container mt-4 main-content">
        <div th:replace="~{fragments/layout :: alert}"></div>
        <div th:replace="~{fragments/layout :: alert-error}"></div>

        <div th:if="${order != null}">
            <div class="d-flex justify-content-between align-items-center mb-4">
                <h4 th:text="#{payment.title}">付款页面</h4>
                <a th:href="@{/order/detail/{id}(id=${order.id})}" class="btn btn-sm btn-outline-primary" th:text="#{payment.back}">返回订单详情</a>
            </div>

            <div class="row">
                <div class="col-md-8">
                    <div class="card mb-4">
                        <div class="card-header bg-primary text-white" th:text="#{myorders.orderinfo}">订单信息</div>
                        <div class="card-body">
                            <div class="row mb-3">
                                <div class="col-md-6">
                                    <strong th:text="#{myorders.orderid}">订单号：</strong>
                                    <span th:text="${order.orderNo}"></span>
                                </div>
                                <div class="col-md-6">
                                    <strong th:text="#{myorders.status}">订单状态：</strong>
                                    <span class="badge bg-info" th:text="#{myorders.status.confirmed}"></span>
                                </div>
                            </div>
                        </div>
                    </div>

                    <div class="card mb-4">
                        <div class="card-header bg-primary text-white" th:text="#{book.title}">商品信息</div>
                        <div class="card-body">
                            <div th:if="${book != null}">
                                <h5 th:text="${book.title}"></h5>
                                <p class="text-muted" th:text="${book.author}"></p>
                            </div>
                        </div>
                    </div>

                    <div class="card mb-4">
                        <div class="card-header bg-primary text-white" th:text="#{payment.method}">支付方式</div>
                        <div class="card-body">
                            <form th:action="@{/order/pay/{id}(id=${order.id})}" method="post" id="payment-form">
                                <div class="mb-3">
                                    <div class="form-check">
                                        <input class="form-check-input" type="radio" name="payMethod" id="card" value="card" checked>
                                        <label class="form-check-label" for="card" th:text="#{payment.card}">信用卡</label>
                                    </div>
                                    <div class="form-check">
                                        <input class="form-check-input" type="radio" name="payMethod" id="bank" value="bank">
                                        <label class="form-check-label" for="bank" th:text="#{payment.bank}">银行转账</label>
                                    </div>
                                    <div class="form-check">
                                        <input class="form-check-input" type="radio" name="payMethod" id="cash" value="cash">
                                        <label class="form-check-label" for="cash" th:text="#{payment.cash}">现金</label>
                                    </div>
                                    <div class="form-check">
                                        <input class="form-check-input" type="radio" name="payMethod" id="wallet" value="wallet">
                                        <label class="form-check-label" for="wallet" th:text="#{payment.wallet}">电子钱包</label>
                                    </div>
                                </div>
                                <button type="submit" class="btn btn-primary w-100" id="pay-btn" th:text="#{payment.submit}">确认付款</button>
                            </form>
                        </div>
                    </div>
                </div>

                <div class="col-md-4">
                    <div class="card">
                        <div class="card-header bg-primary text-white" th:text="#{payment.summary}">付款信息</div>
                        <div class="card-body">
                            <div class="mb-3">
                                <strong th:text="#{payment.amount}">付款金额：</strong>
                                <span class="text-primary fw-bold text-xl">₩<span th:text="${order.price}"></span></span>
                            </div>
                            <div class="mb-3">
                                <strong th:text="#{myorders.buyer}">买家：</strong>
                                <span th:text="${buyer != null ? buyer.nickname : '-'}"></span>
                            </div>
                            <div class="mb-3">
                                <strong th:text="#{myorders.seller}">卖家：</strong>
                                <span th:text="${seller != null ? seller.nickname : '-'}"></span>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <div th:if="${order == null}" class="text-center text-muted py-8">
            <p th:text="${error != null ? error : #{payment.notfound}}">订单不存在或无法付款</p>
        </div>
    </div>

    <div th:replace="~{fragments/layout :: footer}"></div>
    <div th:replace="~{fragments/layout :: scripts}"></div>
    <script>
        document.getElementById('payment-form').addEventListener('submit', function(e) {
            const btn = document.getElementById('pay-btn');
            btn.disabled = true;
            btn.innerHTML = '<span class="spinner-border spinner-border-sm"></span> 处理中...';
        });
    </script>
</body>
</html>
```

### 5.11 管理后台 - admin.html

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org" lang="zh-CN">
<head th:replace="~{fragments/layout :: head(#{admin.title})}"></head>
<body>
    <div th:replace="~{fragments/layout :: navbar}"></div>

    <div class="container">
        <div th:replace="~{fragments/layout :: alert}"></div>

        <h4 class="mb-4" th:text="#{admin.title}">管理后台</h4>

        <h5 class="mb-3" th:text="#{admin.books}">全部书籍</h5>
        <div class="table-responsive mb-5">
            <table class="table table-hover">
                <thead>
                    <tr>
                        <th>ID</th>
                        <th th:text="#{book.title}">书名</th>
                        <th th:text="#{book.price}">价格</th>
                        <th th:text="#{admin.sellerid}">卖家ID</th>
                        <th th:text="#{book.condition}">状态</th>
                        <th th:text="#{admin.action}">操作</th>
                    </tr>
                </thead>
                <tbody>
                    <tr th:each="book : ${books}">
                        <td th:text="${book.id}"></td>
                        <td th:text="${book.title}"></td>
                        <td>₩<span th:text="${book.price}"></span></td>
                        <td th:text="${book.sellerId}"></td>
                        <td>
                            <span th:if="${book.status == 0}" class="badge bg-secondary" th:text="#{admin.off}"></span>
                            <span th:if="${book.status == 1}" class="badge bg-success" th:text="#{book.status}"></span>
                            <span th:if="${book.status == 2}" class="badge bg-warning text-dark" th:text="#{admin.sold}"></span>
                        </td>
                        <td>
                            <a th:href="@{/admin/book/delete/{id}(id=${book.id})}" 
                               class="btn btn-sm btn-outline-danger" th:text="#{admin.delete}">删除</a>
                        </td>
                    </tr>
                </tbody>
            </table>
        </div>

        <h5 class="mb-3" th:text="#{admin.orders}">全部订单</h5>
        <div class="table-responsive">
            <table class="table table-hover">
                <thead>
                    <tr>
                        <th th:text="#{myorders.orderid}">订单号</th>
                        <th th:text="#{book.title}">书籍ID</th>
                        <th th:text="#{myorders.buyer}">买家ID</th>
                        <th th:text="#{myorders.seller}">卖家ID</th>
                        <th th:text="#{myorders.amount}">价格</th>
                        <th th:text="#{myorders.status}">状态</th>
                        <th th:text="#{admin.time}">下单时间</th>
                    </tr>
                </thead>
                <tbody>
                    <tr th:each="order : ${orders}">
                        <td th:text="${order.orderNo}"></td>
                        <td th:text="${order.bookId}"></td>
                        <td th:text="${order.buyerId}"></td>
                        <td th:text="${order.sellerId}"></td>
                        <td>₩<span th:text="${order.price}"></span></td>
                        <td>
                            <span th:if="${order.status == 0}" class="badge bg-warning text-dark" th:text="#{myorders.status.pending}"></span>
                            <span th:if="${order.status == 1}" class="badge bg-info" th:text="#{myorders.status.confirmed}"></span>
                            <span th:if="${order.status == 2}" class="badge bg-success" th:text="#{myorders.status.completed}"></span>
                            <span th:if="${order.status == 3}" class="badge bg-secondary" th:text="#{myorders.status.cancelled}"></span>
                        </td>
                        <td th:text="${#temporals.format(order.createTime, 'yyyy-MM-dd HH:mm')}"></td>
                    </tr>
                </tbody>
            </table>
        </div>
    </div>

    <div th:replace="~{fragments/layout :: scripts}"></div>
</body>
</html>
```

### 5.12 订单管理页 - admin-orders.html

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org" lang="zh-CN">
<head th:replace="~{fragments/layout :: head(#{admin.orders.title})}"></head>
<body>
    <div th:replace="~{fragments/layout :: navbar}"></div>

    <div class="container">
        <div th:replace="~{fragments/layout :: alert}"></div>
        <div th:replace="~{fragments/layout :: alert-error}"></div>

        <div class="d-flex justify-content-between align-items-center mb-4">
            <h4 th:text="#{admin.orders.title}">订单管理</h4>
            <a th:href="@{/admin}" class="btn btn-sm btn-outline-primary" th:text="#{admin.back}">返回管理后台</a>
        </div>

        <div class="card mb-4">
            <div class="card-body">
                <form th:action="@{/admin/orders}" method="get" class="row g-3">
                    <div class="col-md-3">
                        <label class="form-label" th:text="#{myorders.status}">订单状态</label>
                        <select class="form-select form-select-sm" name="status">
                            <option value="" th:if="${status == null}" th:text="#{admin.all}">全部</option>
                            <option value="" th:else>全部</option>
                            <option value="0" th:if="${status == 0}" selected th:text="#{myorders.status.pending}"></option>
                            <option value="0" th:else th:text="#{myorders.status.pending}"></option>
                            <option value="1" th:if="${status == 1}" selected th:text="#{myorders.status.confirmed}"></option>
                            <option value="1" th:else th:text="#{myorders.status.confirmed}"></option>
                            <option value="2" th:if="${status == 2}" selected th:text="#{myorders.status.completed}"></option>
                            <option value="2" th:else th:text="#{myorders.status.completed}"></option>
                            <option value="3" th:if="${status == 3}" selected th:text="#{myorders.status.cancelled}"></option>
                            <option value="3" th:else th:text="#{myorders.status.cancelled}"></option>
                            <option value="4" th:if="${status == 4}" selected th:text="#{myorders.status.paid}"></option>
                            <option value="4" th:else th:text="#{myorders.status.paid}"></option>
                        </select>
                    </div>
                    <div class="col-md-3">
                        <label class="form-label" th:text="#{admin.startdate}">开始日期</label>
                        <input type="date" class="form-control form-control-sm" name="startDate" th:value="${startDate}">
                    </div>
                    <div class="col-md-3">
                        <label class="form-label" th:text="#{admin.enddate}">结束日期</label>
                        <input type="date" class="form-control form-control-sm" name="endDate" th:value="${endDate}">
                    </div>
                    <div class="col-md-3">
                        <label class="form-label">&nbsp;</label>
                        <div class="d-grid">
                            <button type="submit" class="btn btn-sm btn-primary" th:text="#{admin.search}">搜索</button>
                        </div>
                    </div>
                </form>
            </div>
        </div>

        <div class="table-responsive">
            <table class="table table-hover">
                <thead>
                    <tr>
                        <th th:text="#{myorders.orderid}">订单号</th>
                        <th th:text="#{myorders.buyer}">买家</th>
                        <th th:text="#{myorders.seller}">卖家</th>
                        <th th:text="#{book.title}">书籍</th>
                        <th th:text="#{myorders.amount}">交易金额</th>
                        <th th:text="#{myorders.status}">状态</th>
                        <th th:text="#{myorders.time}">下单时间</th>
                        <th th:text="#{admin.action}">操作</th>
                    </tr>
                </thead>
                <tbody>
                    <tr th:each="order : ${orders}">
                        <td><a th:href="@{/admin/order/detail/{id}(id=${order.id})}" th:text="${order.orderNo}"></a></td>
                        <td th:text="${order.buyerId}"></td>
                        <td th:text="${order.sellerId}"></td>
                        <td th:text="${order.bookId}"></td>
                        <td>₩<span th:text="${order.price}"></span></td>
                        <td>
                            <span th:if="${order.status == 0}" class="badge bg-warning text-dark" th:text="#{myorders.status.pending}"></span>
                            <span th:if="${order.status == 1}" class="badge bg-info" th:text="#{myorders.status.confirmed}"></span>
                            <span th:if="${order.status == 4}" class="badge bg-primary" th:text="#{myorders.status.paid}"></span>
                            <span th:if="${order.status == 2}" class="badge bg-success" th:text="#{myorders.status.completed}"></span>
                            <span th:if="${order.status == 3}" class="badge bg-secondary" th:text="#{myorders.status.cancelled}"></span>
                        </td>
                        <td th:text="${#temporals.format(order.createTime, 'yyyy-MM-dd HH:mm')}"></td>
                        <td>
                            <a th:href="@{/admin/order/detail/{id}(id=${order.id})}" class="btn btn-sm btn-outline-primary" th:text="#{admin.detail}">详情</a>
                        </td>
                    </tr>
                </tbody>
            </table>
        </div>
        <div th:if="${orders == null || orders.isEmpty()}" class="text-muted text-center py-4" th:text="#{admin.noorders}">暂无订单</div>
    </div>

    <div th:replace="~{fragments/layout :: scripts}"></div>
</body>
</html>
```

### 5.13 订单详情管理页 - admin-order-detail.html

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org" lang="zh-CN">
<head th:replace="~{fragments/layout :: head(#{admin.order.detail})}"></head>
<body>
    <div th:replace="~{fragments/layout :: navbar}"></div>

    <div class="container">
        <div th:replace="~{fragments/layout :: alert}"></div>
        <div th:replace="~{fragments/layout :: alert-error}"></div>

        <div class="d-flex justify-content-between align-items-center mb-4">
            <h4 th:text="#{admin.order.detail}">订单详情</h4>
            <a th:href="@{/admin/orders}" class="btn btn-sm btn-outline-primary" th:text="#{admin.back}">返回订单列表</a>
        </div>

        <div class="row">
            <div class="col-md-8">
                <div class="card mb-4">
                    <div class="card-header bg-primary text-white" th:text="#{myorders.orderinfo}">订单信息</div>
                    <div class="card-body">
                        <div class="row mb-3">
                            <div class="col-md-6">
                                <strong th:text="#{myorders.orderid}">订单号：</strong>
                                <span th:text="${order.orderNo}"></span>
                            </div>
                            <div class="col-md-6">
                                <strong th:text="#{myorders.status}">订单状态：</strong>
                                <span th:if="${order.status == 0}" class="badge bg-warning text-dark" th:text="#{myorders.status.pending}"></span>
                                <span th:if="${order.status == 1}" class="badge bg-info" th:text="#{myorders.status.confirmed}"></span>
                                <span th:if="${order.status == 4}" class="badge bg-primary" th:text="#{myorders.status.paid}"></span>
                                <span th:if="${order.status == 2}" class="badge bg-success" th:text="#{myorders.status.completed}"></span>
                                <span th:if="${order.status == 3}" class="badge bg-secondary" th:text="#{myorders.status.cancelled}"></span>
                            </div>
                        </div>
                        <div class="row mb-3">
                            <div class="col-md-6">
                                <strong th:text="#{myorders.time}">下单时间：</strong>
                                <span th:text="${#temporals.format(order.createTime, 'yyyy-MM-dd HH:mm:ss')}"></span>
                            </div>
                            <div class="col-md-6">
                                <strong th:text="#{myorders.amount}">交易金额：</strong>
                                <span class="text-primary fw-bold">₩<span th:text="${order.price}"></span></span>
                            </div>
                        </div>
                    </div>
                </div>

                <div class="card mb-4">
                    <div class="card-header bg-primary text-white" th:text="#{book.title}">商品详情</div>
                    <div class="card-body">
                        <div th:if="${book != null}">
                            <div class="row mb-3">
                                <div class="col-md-4">
                                    <img th:if="${book.coverImage != null}" th:src="@{/uploads/{image}(image=${book.coverImage})}" 
                                         class="img-fluid rounded" alt="书籍封面">
                                    <div th:else class="bg-light rounded p-4 text-center text-muted">无封面图片</div>
                                </div>
                                <div class="col-md-8">
                                    <h5 th:text="${book.title}"></h5>
                                    <p class="text-muted" th:text="${book.author}"></p>
                                    <p class="text-muted" th:text="${book.description}"></p>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

                <div class="card mb-4">
                    <div class="card-header bg-primary text-white" th:text="#{admin.order.logs}">操作日志</div>
                    <div class="card-body">
                        <div th:if="${logs != null && !logs.isEmpty()}">
                            <div class="list-group">
                                <div th:each="log : ${logs}" class="list-group-item">
                                    <div class="d-flex justify-content-between align-items-start">
                                        <div>
                                            <strong th:text="${log.action}"></strong>
                                            <p class="text-muted small mt-1" th:if="${log.operatorName != null}">
                                                <span th:text="#{admin.operator}">操作人：</span>
                                                <span th:text="${log.operatorName}"></span>
                                            </p>
                                            <p class="text-muted small mt-1" th:if="${log.operatorName == null}">
                                                <span th:text="#{admin.system}">系统操作</span>
                                            </p>
                                        </div>
                                        <span class="text-muted small" th:text="${#temporals.format(log.createTime, 'yyyy-MM-dd HH:mm:ss')}"></span>
                                    </div>
                                </div>
                            </div>
                        </div>
                        <div th:else class="text-muted text-center py-4" th:text="#{admin.nologs}">暂无操作记录</div>
                    </div>
                </div>
            </div>

            <div class="col-md-4">
                <div class="card mb-4">
                    <div class="card-header bg-primary text-white" th:text="#{myorders.buyer}">买家信息</div>
                    <div class="card-body">
                        <div th:if="${buyer != null}">
                            <p><strong>用户名：</strong><span th:text="${buyer.username}"></span></p>
                            <p><strong>昵称：</strong><span th:text="${buyer.nickname}"></span></p>
                            <p><strong>手机号：</strong><span th:text="${buyer.phone != null ? buyer.phone : '-'}"></span></p>
                        </div>
                    </div>
                </div>

                <div class="card mb-4">
                    <div class="card-header bg-primary text-white" th:text="#{myorders.seller}">卖家信息</div>
                    <div class="card-body">
                        <div th:if="${seller != null}">
                            <p><strong>用户名：</strong><span th:text="${seller.username}"></span></p>
                            <p><strong>昵称：</strong><span th:text="${seller.nickname}"></span></p>
                            <p><strong>手机号：</strong><span th:text="${seller.phone != null ? seller.phone : '-'}"></span></p>
                        </div>
                    </div>
                </div>

                <div class="card">
                    <div class="card-header bg-primary text-white" th:text="#{admin.action}">操作</div>
                    <div class="card-body">
                        <div th:if="${order.status == 0}">
                            <a th:href="@{/admin/order/confirm/{id}(id=${order.id})}" 
                               class="btn btn-sm btn-primary w-100 mb-2" th:text="#{myorders.confirm}">确认订单</a>
                            <a th:href="@{/admin/order/cancel/{id}(id=${order.id})}" 
                               class="btn btn-sm btn-outline-danger w-100" th:text="#{myorders.cancel}">取消订单</a>
                        </div>
                        <div th:if="${order.status == 1}">
                            <a th:href="@{/admin/order/complete/{id}(id=${order.id})}" 
                               class="btn btn-sm btn-success w-100" th:text="#{myorders.complete}">确认完成</a>
                        </div>
                        <div th:if="${order.status == 4}">
                            <a th:href="@{/admin/order/complete/{id}(id=${order.id})}" 
                               class="btn btn-sm btn-success w-100" th:text="#{myorders.complete}">确认完成</a>
                        </div>
                        <div th:if="${order.status == 2}" class="text-center text-success" th:text="#{myorders.status.completed}"></div>
                        <div th:if="${order.status == 3}" class="text-center text-secondary" th:text="#{myorders.status.cancelled}"></div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <div th:replace="~{fragments/layout :: scripts}"></div>
</body>
</html>
```

## 六、国际化资源文件

### 6.1 中文 - messages.properties

```properties
app.title=二手书交易平台

nav.home=首页
nav.publish=发布书籍
nav.mybooks=我的发布
nav.myorders=我的订单
nav.login=登录
nav.register=注册
nav.logout=退出登录
nav.admin=管理后台

search.placeholder=搜索书名、作者...
search.button=搜索

category.all=全部分类
category.1=教材
category.2=考研
category.3=文学
category.4=计算机
category.5=其他

book.title=书名
book.author=作者
book.isbn=ISBN
book.price=价格
book.description=描述
book.category=分类
book.condition=书籍状态
book.condition.new=全新
book.condition.good=良好
book.condition.fair=一般
book.cover=封面图片
book.status=在售
book.detail=查看详情
book.detail.sold=已售出
book.detail.off=已下架
book.buy=立即购买
book.publish=发布书籍
book.edit=编辑书籍
book.submit=提交
book.update=更新
book.cancel=取消
book.off=下架
book.login.buy=请登录后购买
book.self=不能购买自己发布的书籍
book.seller=卖家信息

user.nickname=昵称
user.phone=手机号

login.title=用户登录
login.username=用户名
login.password=密码
login.button=登录
login.register=还没有账号？点击注册

register.title=用户注册
register.username=用户名
register.password=密码
register.nickname=昵称
register.phone=手机号（选填）
register.button=注册
register.login=已有账号？点击登录

mybooks.empty=暂无发布书籍

myorders.title=我的订单
myorders.buy=我购买的
myorders.sell=我卖出的
myorders.orderid=订单号
myorders.buyer=买家
myorders.seller=卖家
myorders.amount=交易金额
myorders.status=订单状态
myorders.status.pending=待确认
myorders.status.confirmed=已确认
myorders.status.completed=已完成
myorders.status.cancelled=已取消
myorders.status.paid=已付款
myorders.time=下单时间
myorders.action=操作
myorders.detail=详情
myorders.confirm=确认订单
myorders.complete=确认完成
myorders.cancel=取消订单
myorders.back=返回订单列表
myorders.empty=暂无订单
myorders.notfound=订单不存在
myorders.orderinfo=订单信息

payment.title=付款页面
payment.pay=去付款
payment.method=支付方式
payment.card=信用卡
payment.bank=银行转账
payment.cash=现金
payment.wallet=电子钱包
payment.submit=确认付款
payment.back=返回订单详情
payment.summary=付款信息
payment.amount=付款金额
payment.notfound=订单不存在或无法付款

comment.title=留言板
comment.placeholder=写下你的留言...
comment.submit=发表留言
comment.reply=回复
comment.reply.placeholder=写下你的回复...
comment.reply.submit=发送回复
comment.login=请先登录后发表留言
comment.empty=暂无留言

admin.title=管理后台
admin.books=全部书籍
admin.sellerid=卖家ID
admin.action=操作
admin.delete=删除
admin.deleteconfirm=确定要删除吗？
admin.orders=全部订单
admin.time=下单时间
admin.all=全部
admin.back=返回管理后台
admin.search=搜索
admin.startdate=开始日期
admin.enddate=结束日期
admin.detail=详情
admin.orders.title=订单管理
admin.order.detail=订单详情
admin.order.logs=操作日志
admin.operator=操作人：
admin.system=系统操作
admin.nologs=暂无操作记录
admin.noorders=暂无订单
admin.off=已下架
admin.sold=已售

footer.copyright=© 2024 二手书交易平台
```

### 6.2 英文 - messages_en_US.properties

```properties
app.title=Secondhand Book Trading Platform

nav.home=Home
nav.publish=Publish Book
nav.mybooks=My Books
nav.myorders=My Orders
nav.login=Login
nav.register=Register
nav.logout=Logout
nav.admin=Admin

search.placeholder=Search books, authors...
search.button=Search

category.all=All Categories
category.1=Textbook
category.2=Graduate Exam
category.3=Literature
category.4=Computer
category.5=Others

book.title=Title
book.author=Author
book.isbn=ISBN
book.price=Price
book.description=Description
book.category=Category
book.condition=Condition
book.condition.new=New
book.condition.good=Good
book.condition.fair=Fair
book.cover=Cover Image
book.status=On Sale
book.detail=View Detail
book.detail.sold=Sold
book.detail.off=Off Shelf
book.buy=Buy Now
book.publish=Publish Book
book.edit=Edit Book
book.submit=Submit
book.update=Update
book.cancel=Cancel
book.off=Off Shelf
book.login.buy=Please login to buy
book.self=Cannot buy your own book
book.seller=Seller Info

user.nickname=Nickname
user.phone=Phone

login.title=User Login
login.username=Username
login.password=Password
login.button=Login
login.register=Don't have an account? Register

register.title=User Registration
register.username=Username
register.password=Password
register.nickname=Nickname
register.phone=Phone (optional)
register.button=Register
register.login=Already have an account? Login

mybooks.empty=No published books

myorders.title=My Orders
myorders.buy=Purchased
myorders.sell=Sold
myorders.orderid=Order No.
myorders.buyer=Buyer
myorders.seller=Seller
myorders.amount=Amount
myorders.status=Status
myorders.status.pending=Pending
myorders.status.confirmed=Confirmed
myorders.status.completed=Completed
myorders.status.cancelled=Cancelled
myorders.status.paid=Paid
myorders.time=Time
myorders.action=Action
myorders.detail=Detail
myorders.confirm=Confirm Order
myorders.complete=Complete Order
myorders.cancel=Cancel Order
myorders.back=Back to Orders
myorders.empty=No orders
myorders.notfound=Order not found
myorders.orderinfo=Order Info

payment.title=Payment
payment.pay=Pay Now
payment.method=Payment Method
payment.card=Credit Card
payment.bank=Bank Transfer
payment.cash=Cash
payment.wallet=E-wallet
payment.submit=Confirm Payment
payment.back=Back to Order
payment.summary=Payment Summary
payment.amount=Amount
payment.notfound=Order not found or cannot pay

comment.title=Comments
comment.placeholder=Write your comment...
comment.submit=Submit Comment
comment.reply=Reply
comment.reply.placeholder=Write your reply...
comment.reply.submit=Send Reply
comment.login=Please login to comment
comment.empty=No comments yet

admin.title=Admin Panel
admin.books=All Books
admin.sellerid=Seller ID
admin.action=Action
admin.delete=Delete
admin.deleteconfirm=Are you sure?
admin.orders=All Orders
admin.time=Order Time
admin.all=All
admin.back=Back to Admin
admin.search=Search
admin.startdate=Start Date
admin.enddate=End Date
admin.detail=Detail
admin.orders.title=Order Management
admin.order.detail=Order Detail
admin.order.logs=Operation Logs
admin.operator=Operator:
admin.system=System Operation
admin.nologs=No logs
admin.noorders=No orders
admin.off=Off Shelf
admin.sold=Sold

footer.copyright=© 2024 Secondhand Book Trading Platform
```

### 6.3 韩文 - messages_ko_KR.properties

```properties
app.title=중고책 거래 플랫폼

nav.home=홈
nav.publish=책 등록
nav.mybooks=내 책
nav.myorders=내 주문
nav.login=로그인
nav.register=회원가입
nav.logout=로그아웃
nav.admin=관리자

search.placeholder=책 제목, 저자 검색...
search.button=검색

category.all=전체 분류
category.1=교재
category.2=대학원시험
category.3=문학
category.4=컴퓨터
category.5=기타

book.title=책 제목
book.author=저자
book.isbn=ISBN
book.price=가격
book.description=설명
book.category=분류
book.condition=책 상태
book.condition.new=새것
book.condition.good=양호
book.condition.fair=보통
book.cover=표지 이미지
book.status=판매 중
book.detail=상세 보기
book.detail.sold=판매 완료
book.detail.off=판매 중지
book.buy=즉시 구매
book.publish=책 등록
book.edit=책 수정
book.submit=제출
book.update=수정
book.cancel=취소
book.off=판매 중지
book.login.buy=로그인 후 구매해 주세요
book.self=자신이 등록한 책은 구매할 수 없습니다
book.seller=판매자 정보

user.nickname=닉네임
user.phone=전화번호

login.title=사용자 로그인
login.username=사용자명
login.password=비밀번호
login.button=로그인
login.register=계정이 없으신가요? 회원가입

register.title=사용자 등록
register.username=사용자명
register.password=비밀번호
register.nickname=닉네임
register.phone=전화번호 (선택)
register.button=회원가입
register.login=이미 계정이 있으신가요? 로그인

mybooks.empty=등록된 책이 없습니다

myorders.title=내 주문
myorders.buy=구매한 것
myorders.sell=판매한 것
myorders.orderid=주문 번호
myorders.buyer=구매자
myorders.seller=판매자
myorders.amount=거래 금액
myorders.status=주문 상태
myorders.status.pending=대기 중
myorders.status.confirmed=확인됨
myorders.status.completed=완료됨
myorders.status.cancelled=취소됨
myorders.status.paid=지불됨
myorders.time=주문 시간
myorders.action=조작
myorders.detail=상세
myorders.confirm=주문 확인
myorders.complete=거래 완료
myorders.cancel=주문 취소
myorders.back=주문 목록으로 돌아가기
myorders.empty=주문이 없습니다
myorders.notfound=주문을 찾을 수 없습니다
myorders.orderinfo=주문 정보

payment.title=지불 페이지
payment.pay=지불하기
payment.method=지불 방법
payment.card=신용카드
payment.bank=은행 이체
payment.cash=현금
payment.wallet=전자 지갑
payment.submit=지불 확인
payment.back=주문 상세로 돌아가기
payment.summary=지불 정보
payment.amount=지불 금액
payment.notfound=주문을 찾을 수 없거나 지불할 수 없습니다

comment.title=댓글
comment.placeholder=댓글을 입력하세요...
comment.submit=댓글 작성
comment.reply=답글
comment.reply.placeholder=답글을 입력하세요...
comment.reply.submit=답글 보내기
comment.login=로그인 후 댓글을 작성해 주세요
comment.empty=아직 댓글이 없습니다

admin.title=관리자 패널
admin.books=모든 책
admin.sellerid=판매자 ID
admin.action=조작
admin.delete=삭제
admin.deleteconfirm=정말 삭제하시겠습니까?
admin.orders=모든 주문
admin.time=주문 시간
admin.all=전체
admin.back=관리자로 돌아가기
admin.search=검색
admin.startdate=시작 날짜
admin.enddate=종료 날짜
admin.detail=상세
admin.orders.title=주문 관리
admin.order.detail=주문 상세
admin.order.logs=작업 기록
admin.operator=작업자:
admin.system=시스템 작업
admin.nologs=기록이 없습니다
admin.noorders=주문이 없습니다
admin.off=판매 중지
admin.sold=판매 완료

footer.copyright=© 2024 중고책 거래 플랫폼
```

## 七、静态资源

### 7.1 CSS样式 - static/css/style.css

```css
body {
    min-height: 100vh;
    display: flex;
    flex-direction: column;
}

.main-content {
    flex: 1;
}

.footer {
    background-color: #f8f9fa;
    padding: 20px 0;
    text-align: center;
}

.comment-item {
    margin-bottom: 15px;
    padding: 15px;
    border-radius: 8px;
    background: #fff;
    border: 1px solid #e9ecef;
}

.reply-item {
    margin-left: 30px;
    margin-bottom: 10px;
    padding: 10px;
    background: #f8f9fa;
    border-radius: 6px;
    border-left: 3px solid #0d6efd;
}

.reply-form-container {
    margin-top: 15px;
    padding: 15px;
    background: #f8f9fa;
    border-radius: 6px;
}

.card-img-top {
    height: 200px;
    object-fit: cover;
}

@media (max-width: 768px) {
    .reply-item {
        margin-left: 15px;
    }
}
```

### 7.2 JavaScript - static/js/app.js

```javascript
document.addEventListener('DOMContentLoaded', function() {
    const replyBtns = document.querySelectorAll('.reply-btn');
    replyBtns.forEach(btn => {
        btn.addEventListener('click', function() {
            const commentId = this.getAttribute('data-comment-id');
            const formContainer = document.getElementById('reply-form-' + commentId);
            if (formContainer) {
                formContainer.style.display = formContainer.style.display === 'none' ? 'block' : 'none';
            }
        });
    });

    const paymentForm = document.getElementById('payment-form');
    if (paymentForm) {
        paymentForm.addEventListener('submit', function(e) {
            const btn = document.getElementById('pay-btn');
            if (btn) {
                btn.disabled = true;
                btn.innerHTML = '<span class="spinner-border spinner-border-sm"></span> Processing...';
            }
        });
    }
});
```

## 八、项目启动说明

### 环境要求
- JDK 17+
- MySQL 8.0+
- Maven 3.6+

### 启动步骤

1. **创建数据库**
```bash
mysql -u root -p
CREATE DATABASE book_trade DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

2. **执行初始化脚本**
```bash
mysql -u root -p book_trade < sql/init.sql
```

3. **修改数据库配置**
编辑 `application.yml`，修改数据库连接信息：
```yaml
spring:
  datasource:
    username: your_username
    password: your_password
```

4. **运行项目**
```bash
cd book-trade
mvn spring-boot:run
```

5. **访问地址**
```
http://localhost:8080
```

### 测试账号
- 管理员：admin / 123456
- 学生1：student1 / 123456  
- 学生2：student2 / 123456

### 功能清单

| 功能 | 说明 |
|------|------|
| 用户注册/登录 | 支持用户名密码登录，角色区分 |
| 书籍浏览 | 首页展示在售书籍，支持搜索和分类筛选 |
| 书籍发布/编辑 | 支持图片上传，书籍状态管理 |
| 留言板 | 书籍详情页留言功能，支持层级回复 |
| 订单管理 | 创建订单、确认订单、取消订单、完成订单 |
| 付款功能 | 独立付款页面，支持多种支付方式 |
| 管理后台 | 书籍管理、订单管理、订单详情查看 |
| 国际化 | 支持中文、英文、韩文切换 |