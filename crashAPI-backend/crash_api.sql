-- 创建数据库
CREATE DATABASE IF NOT EXISTS crash_api;
USE crash_api;

-- 系统账户体系
CREATE TABLE IF NOT EXISTS users (
                                     id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '用户唯一ID',
                                     username VARCHAR(255) NOT NULL UNIQUE COMMENT '用户名(唯一)',
                                     email VARCHAR(255) NOT NULL UNIQUE COMMENT '邮箱(唯一)',
                                     password VARCHAR(255) NOT NULL COMMENT '加密密码',
                                     nickname VARCHAR(255) COMMENT '昵称',
                                     avatar VARCHAR(255) DEFAULT '/default-avatar.png' COMMENT '头像URL',
                                     created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                     updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                     enabled BOOLEAN NOT NULL DEFAULT TRUE COMMENT '账户启用状态',
                                     refresh_token VARCHAR(255) COMMENT '刷新令牌',
                                     refresh_token_expiry TIMESTAMP COMMENT '刷新令牌过期时间'
) COMMENT '系统用户表';

CREATE TABLE IF NOT EXISTS roles (
                                     id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '角色唯一ID',
                                     name VARCHAR(255) NOT NULL UNIQUE COMMENT '角色名称(唯一)',
                                     description VARCHAR(255) COMMENT '角色描述'
) COMMENT '系统角色表';

CREATE TABLE IF NOT EXISTS user_roles (
                                          user_id BIGINT NOT NULL COMMENT '用户ID',
                                          role_id BIGINT NOT NULL COMMENT '角色ID',
                                          PRIMARY KEY (user_id, role_id),
                                          FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
                                          FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
) COMMENT '用户-角色关联表';

-- 管理员申请流程（开发者专属）
CREATE TABLE IF NOT EXISTS admin_applications (
                                                  id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '申请记录ID',
                                                  user_id BIGINT NOT NULL COMMENT '申请人ID',
                                                  status ENUM('PENDING', 'APPROVED', 'REJECTED') NOT NULL DEFAULT 'PENDING' COMMENT '处理状态',
                                                  reason TEXT COMMENT '申请理由',
                                                  processor_id BIGINT COMMENT '处理人ID(必须为开发者)',
                                                  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '申请时间',
                                                  processed_at TIMESTAMP COMMENT '处理时间',
                                                  FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) COMMENT '管理员权限申请表（由开发者处理）';

-- 解决方案知识库
CREATE TABLE IF NOT EXISTS categories (
                                          id VARCHAR(50) PRIMARY KEY COMMENT '分类ID(如startup)',
                                          name VARCHAR(50) NOT NULL COMMENT '分类名称',
                                          icon VARCHAR(100) COMMENT '图标组件名',
                                          description VARCHAR(255) COMMENT '分类描述',
                                          color VARCHAR(20) COMMENT '主题颜色',
                                          created_by BIGINT NOT NULL COMMENT '创建者ID(必须是开发者)',
                                          created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                          FOREIGN KEY (created_by) REFERENCES users(id)
) COMMENT '问题分类表';

CREATE TABLE IF NOT EXISTS solutions (
                                         id VARCHAR(50) PRIMARY KEY COMMENT '方案ID(如s1)',
                                         category_id VARCHAR(50) NOT NULL COMMENT '关联分类ID',
                                         title VARCHAR(255) NOT NULL COMMENT '解决方案标题',
                                         difficulty ENUM('简单','中等','困难') NOT NULL DEFAULT '中等' COMMENT '解决难度',
                                         version VARCHAR(50) NOT NULL COMMENT '适用MC版本',
                                         description TEXT NOT NULL COMMENT '问题描述',
                                         notes TEXT COMMENT '补充说明',
                                         status ENUM('草稿','待审核','已发布') DEFAULT '待审核' COMMENT '审核状态',
                                         created_by BIGINT NOT NULL COMMENT '创建者ID(必须是管理员)',
                                         reviewed_by BIGINT COMMENT '审核者ID(必须是开发者)',
                                         created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                         updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                         FOREIGN KEY (category_id) REFERENCES categories(id),
                                         FOREIGN KEY (created_by) REFERENCES users(id),
                                         FOREIGN KEY (reviewed_by) REFERENCES users(id)
) COMMENT '解决方案主表';

CREATE TABLE IF NOT EXISTS solution_steps (
                                              id INT AUTO_INCREMENT PRIMARY KEY,
                                              solution_id VARCHAR(50) NOT NULL COMMENT '关联方案ID',
                                              step_order TINYINT UNSIGNED NOT NULL COMMENT '步骤序号(1起)',
                                              content TEXT NOT NULL COMMENT '步骤内容',
                                              FOREIGN KEY (solution_id) REFERENCES solutions(id) ON DELETE CASCADE
) COMMENT '解决方案步骤表';

CREATE TABLE IF NOT EXISTS solution_images (
                                               id INT AUTO_INCREMENT PRIMARY KEY,
                                               solution_id VARCHAR(50) NOT NULL COMMENT '关联方案ID',
                                               image_order TINYINT UNSIGNED NOT NULL COMMENT '图片序号',
                                               image_url VARCHAR(500) NOT NULL COMMENT '图片URL',
                                               FOREIGN KEY (solution_id) REFERENCES solutions(id) ON DELETE CASCADE
) COMMENT '解决方案截图表';

-- 索引优化
CREATE INDEX idx_user_roles_user ON user_roles(user_id);
CREATE INDEX idx_user_roles_role ON user_roles(role_id);
CREATE INDEX idx_admin_apps_user ON admin_applications(user_id);
CREATE INDEX idx_admin_apps_status ON admin_applications(status);
CREATE INDEX idx_admin_apps_processor ON admin_applications(processor_id);
CREATE INDEX idx_solutions_status ON solutions(status);
CREATE INDEX idx_solutions_category ON solutions(category_id);
CREATE INDEX idx_steps_solution ON solution_steps(solution_id);

-- 权限控制触发器
DELIMITER $$

-- 确保只有开发者能处理管理员申请
CREATE TRIGGER trg_admin_apps_processor_check
    BEFORE INSERT ON admin_applications
    FOR EACH ROW
BEGIN
    IF NEW.processor_id IS NOT NULL THEN
        IF NOT EXISTS (
            SELECT 1
            FROM user_roles ur
                     JOIN roles r ON ur.role_id = r.id
            WHERE ur.user_id = NEW.processor_id
              AND r.name = 'ROLE_DEV'
        ) THEN
            SIGNAL SQLSTATE '45000'
                SET MESSAGE_TEXT = '只有开发者角色能处理管理员申请';
        END IF;
    END IF;
END
$$

-- 确保只有管理员能创建解决方案
CREATE TRIGGER trg_solution_creator_check
    BEFORE INSERT ON solutions
    FOR EACH ROW
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM user_roles ur
                 JOIN roles r ON ur.role_id = r.id
        WHERE ur.user_id = NEW.created_by
          AND r.name = 'ROLE_ADMIN'
    ) THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = '只有管理员角色能创建解决方案';
    END IF;
END$$

-- 确保只有开发者能审核解决方案
CREATE TRIGGER trg_solution_reviewer_check
    BEFORE UPDATE ON solutions
    FOR EACH ROW
BEGIN
    IF NEW.reviewed_by IS NOT NULL AND OLD.status != NEW.status THEN
        IF NOT EXISTS (
            SELECT 1
            FROM user_roles ur
                     JOIN roles r ON ur.role_id = r.id
            WHERE ur.user_id = NEW.reviewed_by
              AND r.name = 'ROLE_DEV'
        ) THEN
            SIGNAL SQLSTATE '45000'
                SET MESSAGE_TEXT = '只有开发者角色能审核解决方案';
        END IF;
    END IF;
END
$$

-- 确保只有开发者能创建分类
CREATE TRIGGER trg_category_creator_check
    BEFORE INSERT ON categories
    FOR EACH ROW
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM user_roles ur
                 JOIN roles r ON ur.role_id = r.id
        WHERE ur.user_id = NEW.created_by
          AND r.name = 'ROLE_DEV'
    ) THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = '只有开发者角色能创建问题分类';
    END IF;
END$$

DELIMITER ;

-- ============= 初始化数据 ============= --

-- 初始化系统角色
INSERT INTO roles (name, description) VALUES
                                          ('ROLE_DEV', '开发者权限'),
                                          ('ROLE_ADMIN', '系统管理员'),
                                          ('ROLE_USER', '普通用户')
ON DUPLICATE KEY UPDATE description = VALUES(description);

-- 初始化示例用户（密码：Test@1234）
INSERT INTO users (username, email, password, nickname) VALUES
    ('john_doe', 'john@example.com', '$2a$10$Xp1DfT7Y2Jz5v8sQeWZzUuBd6LrCc1V0oGkZ1hY3HjKlN2mP3oR9S', 'John Doe')
ON DUPLICATE KEY UPDATE email = VALUES(email);

-- 给示例用户分配普通用户角色
INSERT INTO user_roles (user_id, role_id)
SELECT
    (SELECT id FROM users WHERE username = 'john_doe'),
    (SELECT id FROM roles WHERE name = 'ROLE_USER')
ON DUPLICATE KEY UPDATE user_id = VALUES(user_id);

-- 初始化系统管理员（密码：Admin@1234）
INSERT INTO users (username, email, password, nickname) VALUES
                                                            ('2247380761', '2247380761@qq.com', '$2a$10$SXJFAfgRnwKDHDcHlBuVzOISnlGJXYbdTwj7C96UPi7llUz5Gu7Pq', '系统管理员1'),
                                                            ('3574467868', '3574467868@qq.com', '$2a$10$f37BSMpJQ23Dl65JfwzFiOorqJHsbHl9NH03o0ccSkpsEHpDD6yz', '系统管理员2')
ON DUPLICATE KEY UPDATE email = VALUES(email);

-- 给系统管理员分配管理员角色
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u
         JOIN roles r ON r.name = 'ROLE_ADMIN'
WHERE u.username IN ('2247380761', '3574467868')
ON DUPLICATE KEY UPDATE user_id = VALUES(user_id);

-- 初始化开发者（管理员1同时也是开发者）
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u
         JOIN roles r ON r.name = 'ROLE_DEV'
WHERE u.username = '2247380761'
ON DUPLICATE KEY UPDATE user_id = VALUES(user_id);

-- 初始化管理员申请示例
INSERT INTO admin_applications (user_id, reason) VALUES
    ((SELECT id FROM users WHERE username = 'john_doe'), '申请管理员权限进行系统维护')
ON DUPLICATE KEY UPDATE reason = VALUES(reason);

-- 初始化分类（由开发者创建）
INSERT INTO categories (id, name, icon, description, color, created_by) VALUES
                                                                            ('all', '全部问题', 'MagicStick', '浏览所有类别的解决方案', '#9c27b0',
                                                                             (SELECT id FROM users WHERE username = '2247380761')),
                                                                            ('startup', '启动问题', 'Setting', '游戏启动失败、崩溃等问题', '#409EFF',
                                                                             (SELECT id FROM users WHERE username = '2247380761')),
                                                                            ('mod', '模组问题', 'Warning', '模组加载、兼容性问题', '#E6A23C',
                                                                             (SELECT id FROM users WHERE username = '2247380761')),
                                                                            ('network', '联机问题', 'Connection', '服务器连接、联机问题', '#67C23A',
                                                                             (SELECT id FROM users WHERE username = '2247380761'))
ON DUPLICATE KEY UPDATE
                     name = VALUES(name),
                     icon = VALUES(icon),
                     description = VALUES(description),
                     color = VALUES(color);

-- 初始化解决方案（由管理员创建）
INSERT INTO solutions (id, category_id, title, difficulty, version, description, notes, status, created_by)
SELECT 's1', 'startup', '游戏启动崩溃：Exit Code 1', '中等', '1.16+',
       '在启动Minecraft时，游戏崩溃并显示Exit Code 1错误。这通常是由于Java版本不兼容、显卡驱动问题或内存分配不足引起的。',
       '如果使用模组，请检查模组兼容性，确保所有模组都适用于当前Minecraft版本', '已发布', id
FROM users WHERE username = '3574467868'
ON DUPLICATE KEY UPDATE
                     title = VALUES(title),
                     description = VALUES(description),
                     notes = VALUES(notes);

INSERT INTO solutions (id, category_id, title, difficulty, version, description, notes, status, created_by)
SELECT 's2', 'network', '联机时出现"Connection Timed Out"错误', '简单', '全版本',
       '尝试加入服务器时出现连接超时错误，这可能是由于网络问题、服务器设置错误或防火墙阻止引起的。',
       '如果使用路由器，请确保端口转发规则正确设置，并且服务器使用静态IP', '已发布', id
FROM users WHERE username = '3574467868'
ON DUPLICATE KEY UPDATE
                     title = VALUES(title),
                     description = VALUES(description),
                     notes = VALUES(notes);

INSERT INTO solutions (id, category_id, title, difficulty, version, description, notes, status, created_by)
SELECT 's4', 'mod', '模组加载后游戏崩溃', '简单', '全版本',
       '安装模组后游戏无法启动或启动后崩溃。',
       '使用模组管理器如CurseForge可以避免版本冲突', '已发布', id
FROM users WHERE username = '3574467868'
ON DUPLICATE KEY UPDATE
                     title = VALUES(title),
                     description = VALUES(description),
                     notes = VALUES(notes);

-- 初始化解决方案步骤
INSERT INTO solution_steps (solution_id, step_order, content) VALUES
                                                                  ('s1', 1, '检查Java版本是否与Minecraft版本兼容（1.17+需要Java 16+）'),
                                                                  ('s1', 2, '删除或重命名.minecraft文件夹中的options.txt文件'),
                                                                  ('s1', 3, '更新显卡驱动程序到最新版本'),
                                                                  ('s1', 4, '在启动器中分配更多内存（建议4GB以上）'),
                                                                  ('s1', 5, '尝试使用不同的Java版本（如OpenJDK）'),
                                                                  ('s1', 6, '检查mods文件夹是否有损坏或不兼容的模组'),
                                                                  ('s2', 1, '检查服务器IP地址和端口是否正确'),
                                                                  ('s2', 2, '确认服务器是否在线运行'),
                                                                  ('s2', 3, '检查本地网络连接是否正常'),
                                                                  ('s2', 4, '暂时禁用防火墙和杀毒软件测试'),
                                                                  ('s2', 5, '尝试使用VPN连接服务器'),
                                                                  ('s2', 6, '如果是自建服务器，检查端口转发设置是否正确'),
                                                                  ('s4', 1, '检查模组是否与当前Minecraft版本兼容'),
                                                                  ('s4', 2, '确认模组加载器（Forge/Fabric）版本正确'),
                                                                  ('s4', 3, '检查模组依赖是否满足'),
                                                                  ('s4', 4, '逐个添加模组以找出冲突模组'),
                                                                  ('s4', 5, '查看游戏日志文件定位具体错误')
ON DUPLICATE KEY UPDATE
    content = VALUES(content);

-- 初始化解决方案截图（占位符URL）
INSERT INTO solution_images (solution_id, image_order, image_url) VALUES
                                                                      ('s1', 1, '/images/solutions/exit_code_error.png'),
                                                                      ('s1', 2, '/images/solutions/java_version_settings.png'),
                                                                      ('s2', 1, '/images/solutions/connection_error.png'),
                                                                      ('s2', 2, '/images/solutions/port_forwarding.png'),
                                                                      ('s4', 1, '/images/solutions/mod_compatibility.png'),
                                                                      ('s4', 2, '/images/solutions/mod_loader.png')
ON DUPLICATE KEY UPDATE
    image_url = VALUES(image_url);

-- 设置解决方案审核状态（由开发者审核）
UPDATE solutions
SET reviewed_by = (SELECT id FROM users WHERE username = '2247380761'),
    status = '已发布'
WHERE id IN ('s1', 's2', 's4');