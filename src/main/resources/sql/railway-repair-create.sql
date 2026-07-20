-- Railway MySQL auto-repair (CREATE TABLE IF NOT EXISTS only 鈥?safe to run on every startup)
-- ALTER TABLE is handled by SqlRepairRunner Java code

CREATE TABLE IF NOT EXISTS `user` (
    `id` BIGINT NOT NULL AUTO_INCREMENT, `username` VARCHAR(50) NOT NULL,
    `password` VARCHAR(100) NOT NULL, `nickname` VARCHAR(50) DEFAULT NULL,
    `phone` VARCHAR(20) DEFAULT NULL, `role` TINYINT NOT NULL DEFAULT 0,
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `deleted` TINYINT NOT NULL DEFAULT 0,
    PRIMARY KEY (`id`), UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `category` (
    `id` BIGINT NOT NULL AUTO_INCREMENT, `name` VARCHAR(50) NOT NULL,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `book` (
    `id` BIGINT NOT NULL AUTO_INCREMENT, `title` VARCHAR(100) NOT NULL,
    `author` VARCHAR(50) DEFAULT NULL, `isbn` VARCHAR(20) DEFAULT NULL,
    `price` DECIMAL(10,2) NOT NULL, `description` TEXT DEFAULT NULL,
    `cover_image` VARCHAR(255) DEFAULT NULL, `category_id` BIGINT DEFAULT NULL,
    `seller_id` BIGINT NOT NULL, `status` TINYINT NOT NULL DEFAULT 1,
    `condition` TINYINT NOT NULL DEFAULT 0,
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `deleted` TINYINT NOT NULL DEFAULT 0,
    PRIMARY KEY (`id`), KEY `idx_seller_id` (`seller_id`), KEY `idx_category_id` (`category_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `trade_order` (
    `id` BIGINT NOT NULL AUTO_INCREMENT, `order_no` VARCHAR(32) NOT NULL,
    `book_id` BIGINT NOT NULL, `buyer_id` BIGINT NOT NULL, `seller_id` BIGINT NOT NULL,
    `price` DECIMAL(10,2) NOT NULL, `status` TINYINT NOT NULL DEFAULT 0,
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`), UNIQUE KEY `uk_order_no` (`order_no`),
    KEY `idx_buyer_id` (`buyer_id`), KEY `idx_seller_id` (`seller_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `comment` (
    `id` BIGINT NOT NULL AUTO_INCREMENT, `book_id` BIGINT NOT NULL,
    `user_id` BIGINT NOT NULL, `parent_id` BIGINT DEFAULT NULL,
    `content` TEXT NOT NULL,
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`), KEY `idx_book_id` (`book_id`),
    KEY `idx_user_id` (`user_id`), KEY `idx_parent_id` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `order_log` (
    `id` BIGINT NOT NULL AUTO_INCREMENT, `order_id` BIGINT NOT NULL,
    `operator_id` BIGINT DEFAULT NULL, `operator_name` VARCHAR(50) DEFAULT NULL,
    `action` VARCHAR(100) NOT NULL,
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`), KEY `idx_order_id` (`order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `payment` (
    `id` BIGINT NOT NULL AUTO_INCREMENT, `order_id` BIGINT NOT NULL,
    `amount` DECIMAL(10,2) NOT NULL, `pay_method` VARCHAR(20) NOT NULL,
    `pay_time` DATETIME NOT NULL,
    PRIMARY KEY (`id`), UNIQUE KEY `uk_order_id` (`order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `notification` (
    `id` BIGINT NOT NULL AUTO_INCREMENT, `user_id` BIGINT NOT NULL,
    `type` VARCHAR(20) NOT NULL, `title` VARCHAR(200) NOT NULL,
    `content` TEXT DEFAULT NULL, `related_id` BIGINT DEFAULT NULL,
    `is_read` TINYINT NOT NULL DEFAULT 0,
    `create_time` DATETIME NOT NULL,
    PRIMARY KEY (`id`), KEY `idx_user_id` (`user_id`), KEY `idx_user_read` (`user_id`, `is_read`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Seed data (idempotent)
INSERT IGNORE INTO `category` (`id`, `name`) VALUES (1,'Textbook'),(2,'Exam Prep'),(3,'Literature'),(4,'Computer'),(5,'Other');
INSERT IGNORE INTO `user` (`id`, `username`, `password`, `nickname`, `phone`, `role`) VALUES (1,'admin','$2a$10$v1Q9lmVxckwBRXyEVGESY.uhA9BohoQM8J7KpBpFsdm/JmMeQF5ke','Admin','13800000000',1),(2,'student1','$2a$10$v1Q9lmVxckwBRXyEVGESY.uhA9BohoQM8J7KpBpFsdm/JmMeQF5ke','Zhang San','13800000001',0),(3,'student2','$2a$10$v1Q9lmVxckwBRXyEVGESY.uhA9BohoQM8J7KpBpFsdm/JmMeQF5ke','Li Si','13800000002',0);
INSERT IGNORE INTO `book` (`id`, `title`, `author`, `isbn`, `price`, `description`, `category_id`, `seller_id`, `status`, `condition`) VALUES (1,'Java Programming','Zhang San','978-7-111-12345-6',35.00,'Like new, no notes',4,2,1,1),(2,'Advanced Mathematics Vol.1','Tongji University','978-7-111-23456-7',20.00,'Some notes, readable',1,2,1,1),(3,'Postgraduate English Vocabulary','Zhu Wei','978-7-111-34567-8',15.00,'Almost new',2,3,1,0),(4,'Computer Networks','Xie Xiren','978-7-115-35153-6',40.00,'Brand new, unopened',4,3,1,0),(5,'One Hundred Years of Solitude','Marquez','978-7-5442-4490-9',30.00,'Classic literature',3,2,1,1);
