-- 创建数据库
CREATE DATABASE IF NOT EXISTS crash_api;
USE crash_api;
-- 创建用户表
CREATE TABLE users (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '用户唯一ID',
                       username VARCHAR(255) NOT NULL UNIQUE COMMENT '用户名(唯一)',
                       email VARCHAR(255) NOT NULL UNIQUE COMMENT '邮箱(唯一)',
                       password VARCHAR(255) NOT NULL COMMENT '加密密码',
                       nickname VARCHAR(255) COMMENT '昵称',
                       avatar VARCHAR(255) COMMENT '头像URL',
                       created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                       updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                       enabled BOOLEAN NOT NULL DEFAULT TRUE COMMENT '账户启用状态',
                       refresh_token VARCHAR(255) COMMENT '刷新令牌',
                       refresh_token_expiry TIMESTAMP COMMENT '刷新令牌过期时间'
) COMMENT '系统用户表';

-- 创建角色表
CREATE TABLE roles (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '角色唯一ID',
                       name VARCHAR(255) NOT NULL UNIQUE COMMENT '角色名称(唯一)',
                       description VARCHAR(255) COMMENT '角色描述'
) COMMENT '系统角色表';

-- 创建用户角色关联表
CREATE TABLE user_roles (
                            user_id BIGINT NOT NULL COMMENT '用户ID',
                            role_id BIGINT NOT NULL COMMENT '角色ID',
                            PRIMARY KEY (user_id, role_id),
                            FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
                            FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
) COMMENT '用户-角色关联表';

-- 创建管理员申请表
CREATE TABLE admin_applications (
                                    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '申请记录ID',
                                    user_id BIGINT NOT NULL COMMENT '申请人ID',
                                    status ENUM('PENDING', 'APPROVED', 'REJECTED') NOT NULL DEFAULT 'PENDING' COMMENT '处理状态',
                                    reason TEXT COMMENT '申请理由',
                                    processor_id BIGINT COMMENT '处理人ID',
                                    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '申请时间',
                                    processed_at TIMESTAMP COMMENT '处理时间',
                                    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
                                    FOREIGN KEY (processor_id) REFERENCES users(id) ON DELETE SET NULL
) COMMENT '管理员权限申请表';

-- 创建索引优化查询性能
CREATE INDEX idx_user_roles_user ON user_roles(user_id);
CREATE INDEX idx_user_roles_role ON user_roles(role_id);
CREATE INDEX idx_admin_apps_user ON admin_applications(user_id);
CREATE INDEX idx_admin_apps_status ON admin_applications(status);
CREATE INDEX idx_admin_apps_processor ON admin_applications(processor_id);

-- ============= 初始化数据 ============= --

-- 初始化系统角色
INSERT INTO roles (name, description) VALUES
                                          ('ROLE_DEV', '开发者权限'),
                                          ('ROLE_ADMIN', '系统管理员'),
                                          ('ROLE_USER', '普通用户')
ON DUPLICATE KEY UPDATE description = VALUES(description);

-- 初始化示例用户 (密码: Test@1234)
INSERT INTO users (username, email, password, nickname, enabled) VALUES
    ('john_doe', 'john@example.com', '$2a$10$Xp1DfT7Y2Jz5v8sQeWZzUuBd6LrCc1V0oGkZ1hY3HjKlN2mP3oR9S', 'John Doe', 1)
ON DUPLICATE KEY UPDATE email = VALUES(email);

-- 给示例用户分配角色
INSERT INTO user_roles (user_id, role_id)
SELECT
    (SELECT id FROM users WHERE username = 'john_doe'),
    (SELECT id FROM roles WHERE name = 'ROLE_USER')
ON DUPLICATE KEY UPDATE user_id = VALUES(user_id);

-- 初始化管理员用户 (密码: Admin@1234)
INSERT INTO users (username, email, password, nickname, enabled) VALUES
                                                                     ('2247380761', '2247380761@qq.com', '$2a$10$SXJFAfgRnwKDHDcHlBuVzOISnlGJXYbdTwj7C96UPi7llUz5Gu7Pq', '系统管理员1', 1),
                                                                     ('3574467868', '3574467868@qq.com', '$2a$10$f37BSMpJQ23Dl65JfwzFiOorqJHsbHl9NH03o0ccSkpsEHpDD6yz', '系统管理员2', 1)
ON DUPLICATE KEY UPDATE email = VALUES(email);

-- 给管理员用户分配角色
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u
         JOIN roles r ON r.name = 'ROLE_ADMIN'
WHERE u.username IN ('2247380761', '3574467868')
ON DUPLICATE KEY UPDATE user_id = VALUES(user_id);

-- 添加开发者角色分配
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u
         JOIN roles r ON r.name = 'ROLE_DEV'
WHERE u.username IN ('2247380761')
ON DUPLICATE KEY UPDATE user_id = VALUES(user_id);

-- 初始化管理员申请示例
INSERT INTO admin_applications (user_id, reason) VALUES
    ((SELECT id FROM users WHERE username = 'john_doe'), '申请管理员权限进行系统维护')
ON DUPLICATE KEY UPDATE reason = VALUES(reason);