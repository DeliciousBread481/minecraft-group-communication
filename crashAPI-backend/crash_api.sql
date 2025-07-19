-- 创建数据库
CREATE DATABASE IF NOT EXISTS crash_api;
USE crash_api;

-- 创建表结构
CREATE TABLE IF NOT EXISTS users (
                                     id INT AUTO_INCREMENT PRIMARY KEY,
                                     username VARCHAR(255) NOT NULL UNIQUE,
                                     email VARCHAR(255) NOT NULL UNIQUE,
                                     password VARCHAR(255) NOT NULL,
                                     nickname VARCHAR(255),
                                     avatar VARCHAR(512),
                                     created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                     updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                     enabled BOOLEAN DEFAULT TRUE,
                                     refresh_token VARCHAR(512) DEFAULT NULL,
                                     refresh_token_expiry TIMESTAMP DEFAULT NULL
);

CREATE TABLE IF NOT EXISTS roles (
                                     id INT AUTO_INCREMENT PRIMARY KEY,
                                     name VARCHAR(50) NOT NULL UNIQUE,
                                     description VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS user_roles (
                                          user_id INT NOT NULL,
                                          role_id INT NOT NULL,
                                          PRIMARY KEY (user_id, role_id),
                                          FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
                                          FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
);

-- 插入初始角色数据
INSERT IGNORE INTO roles (name, description)
VALUES
    ('ROLE_ADMIN', '系统管理员'),
    ('ROLE_USER', '普通用户');

-- 插入初始用户数据（添加刷新令牌字段）
INSERT IGNORE INTO users (username, email, password, nickname, enabled)
VALUES
    ('john_doe', 'john@example.com', '$2a$10$Xp1DfT7Y2Jz5v8sQeWZzUuBd6LrCc1V0oGkZ1hY3HjKlN2mP3oR9S', 'John Doe', true); -- 示例密码: Passw0rd!

-- 给用户分配角色
INSERT IGNORE INTO user_roles (user_id, role_id)
SELECT
    (SELECT id FROM users WHERE username = 'john_doe'),
    (SELECT id FROM roles WHERE name = 'ROLE_USER');

-- 插入初始化管理员用户
INSERT IGNORE INTO users(username, email, password, nickname, enabled)
VALUES
    ('2247380761','2247380761@qq.com','$2a$10$SXJFAfgRnwKDHDcHlBuVzOISnlGJXYbdTwj7C96UPi7llUz5Gu7Pq', '管理员1', true),
    ('3574467868','3574467868@qq.com','$2a$10$f37BSMpJQ23Dl65JfwzFiOorqJHsbHl9NH03o0ccSkpsEHpDD6yz', '管理员2', true);

-- 分配管理员角色
INSERT IGNORE INTO user_roles (user_id, role_id)
SELECT
    u.id,
    (SELECT id FROM roles WHERE name = 'ROLE_ADMIN')
FROM users u
WHERE u.username IN ('2247380761', '3574467868');


-- 如果已有数据表请用以下sql更新
ALTER TABLE users
    ADD COLUMN nickname VARCHAR(255) DEFAULT NULL COMMENT '用户昵称';

ALTER TABLE users
    ADD COLUMN avatar VARCHAR(512) DEFAULT NULL COMMENT '用户头像URL';