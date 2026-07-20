CREATE TABLE IF NOT EXISTS `user` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `username`    VARCHAR(50)  NOT NULL COMMENT '用户名',
    `password`    VARCHAR(100) NOT NULL COMMENT '密码(BCrypt加密)',
    `nickname`    VARCHAR(50)  DEFAULT NULL COMMENT '昵称',
    `phone`       VARCHAR(20)  DEFAULT NULL COMMENT '手机号',
    `role`        TINYINT      NOT NULL DEFAULT 0 COMMENT '角色：0-学生 1-管理员',
    `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `deleted`     TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除 1-已删除',
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
    `condition`   TINYINT       NOT NULL DEFAULT 0 COMMENT '书籍状态：0-全新 1-良好 2-一般',
    `create_time` DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '发布时间',
    `update_time` DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`     TINYINT       NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除 1-已删除',
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
    `status`      TINYINT       NOT NULL DEFAULT 0 COMMENT '状态：0-待确认 1-已确认 2-已完成 3-已取消 4-已付款',
    `create_time` DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '下单时间',
    `update_time` DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_order_no` (`order_no`),
    KEY `idx_buyer_id` (`buyer_id`),
    KEY `idx_seller_id` (`seller_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表';

CREATE TABLE IF NOT EXISTS `comment` (
    `id`          BIGINT   NOT NULL AUTO_INCREMENT COMMENT '主键',
    `book_id`     BIGINT   NOT NULL COMMENT '书籍ID',
    `user_id`     BIGINT   NOT NULL COMMENT '用户ID',
    `parent_id`   BIGINT   DEFAULT NULL COMMENT '父评论ID',
    `content`     TEXT     NOT NULL COMMENT '内容',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_book_id` (`book_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_parent_id` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='留言表';

CREATE TABLE IF NOT EXISTS `order_log` (
    `id`            BIGINT        NOT NULL AUTO_INCREMENT COMMENT '主键',
    `order_id`      BIGINT        NOT NULL COMMENT '订单ID',
    `operator_id`   BIGINT        DEFAULT NULL COMMENT '操作人ID',
    `operator_name` VARCHAR(50)   DEFAULT NULL COMMENT '操作人名称',
    `action`        VARCHAR(100)  NOT NULL COMMENT '操作内容',
    `create_time`   DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
    PRIMARY KEY (`id`),
    KEY `idx_order_id` (`order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单日志表';

CREATE TABLE IF NOT EXISTS `payment` (
    `id`        BIGINT        NOT NULL AUTO_INCREMENT COMMENT '主键',
    `order_id`  BIGINT        NOT NULL COMMENT '订单ID',
    `amount`    DECIMAL(10,2) NOT NULL COMMENT '支付金额',
    `pay_method` VARCHAR(20)  NOT NULL COMMENT '支付方式',
    `pay_time`  DATETIME      NOT NULL COMMENT '支付时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_order_id` (`order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='支付表';

CREATE TABLE IF NOT EXISTS `notification` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `user_id`     BIGINT       NOT NULL COMMENT '接收用户ID',
    `type`        VARCHAR(20)  NOT NULL COMMENT '类型：order/comment/system',
    `title`       VARCHAR(200) NOT NULL COMMENT '标题',
    `content`     TEXT         DEFAULT NULL COMMENT '内容',
    `related_id`  BIGINT       DEFAULT NULL COMMENT '关联ID（书籍/订单）',
    `is_read`     TINYINT      NOT NULL DEFAULT 0 COMMENT '0-未读 1-已读',
    `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_user_read` (`user_id`, `is_read`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='通知表';