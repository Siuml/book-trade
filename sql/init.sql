CREATE DATABASE IF NOT EXISTS book_trade DEFAULT CHARSET utf8mb4;
USE book_trade;

DROP TABLE IF EXISTS 
otification;
DROP TABLE IF EXISTS payment;
DROP TABLE IF EXISTS `order_log`;
DROP TABLE IF EXISTS `comment`;
DROP TABLE IF EXISTS `trade_order`;
DROP TABLE IF EXISTS `book`;
DROP TABLE IF EXISTS `category`;
DROP TABLE IF EXISTS `user`;

CREATE TABLE IF NOT EXISTS `user` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '涓婚敭',
    `username`    VARCHAR(50)  NOT NULL COMMENT '鐢ㄦ埛鍚?,
    `password`    VARCHAR(100) NOT NULL COMMENT '瀵嗙爜',
    `nickname`    VARCHAR(50)  DEFAULT NULL COMMENT '鏄电О',
    `phone`       VARCHAR(20)  DEFAULT NULL COMMENT '鎵嬫満鍙?,
    `role`        TINYINT      NOT NULL DEFAULT 0 COMMENT '瑙掕壊锛?-瀛︾敓 1-绠＄悊鍛?,
    `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
    `deleted`     TINYINT      NOT NULL DEFAULT 0 COMMENT '閫昏緫鍒犻櫎',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='鐢ㄦ埛琛?;

CREATE TABLE IF NOT EXISTS `category` (
    `id`   BIGINT      NOT NULL AUTO_INCREMENT COMMENT '涓婚敭',
    `name` VARCHAR(50) NOT NULL COMMENT '鍒嗙被鍚嶇О',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='涔︾睄鍒嗙被琛?;

CREATE TABLE IF NOT EXISTS `book` (
    `id`          BIGINT        NOT NULL AUTO_INCREMENT COMMENT '涓婚敭',
    `title`       VARCHAR(100)  NOT NULL COMMENT '涔﹀悕',
    `author`      VARCHAR(50)   DEFAULT NULL COMMENT '浣滆€?,
    `isbn`        VARCHAR(20)   DEFAULT NULL COMMENT 'ISBN',
    `price`       DECIMAL(10,2) NOT NULL COMMENT '鍞环',
    `description` TEXT          DEFAULT NULL COMMENT '鎻忚堪',
    `cover_image` VARCHAR(255)  DEFAULT NULL COMMENT '灏侀潰鍥剧墖璺緞',
    `category_id` BIGINT        DEFAULT NULL COMMENT '鍒嗙被ID',
    `seller_id`   BIGINT        NOT NULL COMMENT '鍗栧ID',
    `status`      TINYINT       NOT NULL DEFAULT 1 COMMENT '鐘舵€侊細0-涓嬫灦 1-鍦ㄥ敭 2-宸插敭',
    `condition`   TINYINT       NOT NULL DEFAULT 0 COMMENT '涔︾睄鐘舵€侊細0-鍏ㄦ柊 1-鑹ソ 2-涓€鑸?,
    `create_time` DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '鍙戝竷鏃堕棿',
    `update_time` DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '鏇存柊鏃堕棿',
    `deleted`     TINYINT       NOT NULL DEFAULT 0 COMMENT '閫昏緫鍒犻櫎',
    PRIMARY KEY (`id`),
    KEY `idx_seller_id` (`seller_id`),
    KEY `idx_category_id` (`category_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='涔︾睄琛?;

CREATE TABLE IF NOT EXISTS `trade_order` (
    `id`          BIGINT        NOT NULL AUTO_INCREMENT COMMENT '涓婚敭',
    `order_no`    VARCHAR(32)   NOT NULL COMMENT '璁㈠崟缂栧彿',
    `book_id`     BIGINT        NOT NULL COMMENT '涔︾睄ID',
    `buyer_id`    BIGINT        NOT NULL COMMENT '涔板ID',
    `seller_id`   BIGINT        NOT NULL COMMENT '鍗栧ID',
    `price`       DECIMAL(10,2) NOT NULL COMMENT '鎴愪氦浠锋牸',
    `status`      TINYINT       NOT NULL DEFAULT 0 COMMENT '鐘舵€侊細0-寰呯‘璁?1-宸茬‘璁?2-宸插畬鎴?3-宸插彇娑?4-宸蹭粯娆?,
    `create_time` DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '涓嬪崟鏃堕棿',
    `update_time` DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '鏇存柊鏃堕棿',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_order_no` (`order_no`),
    KEY `idx_buyer_id` (`buyer_id`),
    KEY `idx_seller_id` (`seller_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='璁㈠崟琛?;

CREATE TABLE IF NOT EXISTS `comment` (
    `id`          BIGINT   NOT NULL AUTO_INCREMENT COMMENT '涓婚敭',
    `book_id`     BIGINT   NOT NULL COMMENT '涔︾睄ID',
    `user_id`     BIGINT   NOT NULL COMMENT '鐢ㄦ埛ID',
    `parent_id`   BIGINT   DEFAULT NULL COMMENT '鐖惰瘎璁篒D',
    `content`     TEXT     NOT NULL COMMENT '鍐呭',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
    PRIMARY KEY (`id`),
    KEY `idx_book_id` (`book_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_parent_id` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='鐣欒█琛?;

CREATE TABLE IF NOT EXISTS `order_log` (
    `id`            BIGINT   NOT NULL AUTO_INCREMENT COMMENT '涓婚敭',
    `order_id`      BIGINT   NOT NULL COMMENT '璁㈠崟ID',
    `operator_id`   BIGINT   DEFAULT NULL COMMENT '鎿嶄綔浜篒D',
    `operator_name` VARCHAR(50) DEFAULT NULL COMMENT '鎿嶄綔浜哄悕绉?,
    `action`        VARCHAR(100) NOT NULL COMMENT '鎿嶄綔鍐呭',
    `create_time`   DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '鎿嶄綔鏃堕棿',
    PRIMARY KEY (`id`),
    KEY `idx_order_id` (`order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='璁㈠崟鏃ュ織琛?;

CREATE TABLE IF NOT EXISTS `payment` (
    `id`        BIGINT        NOT NULL AUTO_INCREMENT COMMENT '涓婚敭',
    `order_id`  BIGINT        NOT NULL COMMENT '璁㈠崟ID',
    `amount`    DECIMAL(10,2) NOT NULL COMMENT '鏀粯閲戦',
    `pay_method` VARCHAR(20)  NOT NULL COMMENT '鏀粯鏂瑰紡',
    `pay_time`  DATETIME      NOT NULL COMMENT '鏀粯鏃堕棿',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_order_id` (`order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='鏀粯琛?;

INSERT INTO `category` (`name`) VALUES
    ('鏁欐潗'),
    ('鑰冪爺'),
    ('鏂囧'),
    ('璁＄畻鏈?),
    ('鍏朵粬');

INSERT INTO `user` (`username`, `password`, `nickname`, `phone`, `role`) VALUES
    ('admin', '$2a$10$v1Q9lmVxckwBRXyEVGESY.uhA9BohoQM8J7KpBpFsdm/JmMeQF5ke', '绠＄悊鍛?, '13800000000', 1),
    ('student1', '$2a$10$v1Q9lmVxckwBRXyEVGESY.uhA9BohoQM8J7KpBpFsdm/JmMeQF5ke', '寮犱笁', '13800000001', 0),
    ('student2', '$2a$10$v1Q9lmVxckwBRXyEVGESY.uhA9BohoQM8J7KpBpFsdm/JmMeQF5ke', '鏉庡洓', '13800000002', 0);

INSERT INTO `book` (`title`, `author`, `isbn`, `price`, `description`, `category_id`, `seller_id`, `status`, `condition`) VALUES
    ('Java绋嬪簭璁捐', '寮犱笁', '978-7-111-12345-6', 35.00, '涔濇垚鏂帮紝鏃犵瑪璁?, 4, 2, 1, 1),
    ('楂樼瓑鏁板锛堜笂鍐岋級', '鍚屾祹澶у', '978-7-111-23456-7', 20.00, '鏈夊皯閲忕瑪璁帮紝涓嶅奖鍝嶉槄璇?, 1, 2, 1, 1),
    ('鑰冪爺鑻辫璇嶆眹', '鏈变紵', '978-7-111-34567-8', 15.00, '鍑犱箮鍏ㄦ柊', 2, 3, 1, 0);
CREATE TABLE IF NOT EXISTS 
otification (
    id          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    user_id     BIGINT       NOT NULL COMMENT '接收用户ID',
    	ype        VARCHAR(20)  NOT NULL COMMENT '类型：order/comment/system',
    	itle       VARCHAR(200) NOT NULL COMMENT '标题',
    content     TEXT         DEFAULT NULL COMMENT '内容',
    elated_id  BIGINT       DEFAULT NULL COMMENT '关联ID（书籍/订单）',
    is_read     TINYINT      NOT NULL DEFAULT 0 COMMENT '0-未读 1-已读',
    create_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    KEY idx_user_id (user_id),
    KEY idx_user_read (user_id, is_read)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='通知表';
