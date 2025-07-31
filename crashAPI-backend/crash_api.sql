-- 创建数据库
CREATE DATABASE IF NOT EXISTS crash_api;
USE crash_api;

-- 用户表
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

-- 管理员申请流程
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

-- ============= 群公告系统 ============= --
-- 主公告表
CREATE TABLE IF NOT EXISTS announcements (
                                             id VARCHAR(50) PRIMARY KEY DEFAULT 'main_announcement' COMMENT '公告ID(固定为主公告)',
                                             title VARCHAR(255) NOT NULL DEFAULT '群规及问题指南' COMMENT '公告标题',
                                             last_updated TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '最后更新时间',
                                             last_updated_by BIGINT NOT NULL COMMENT '最后更新者ID',
                                             FOREIGN KEY (last_updated_by) REFERENCES users(id)
) COMMENT '公告主表';

-- 内容分类表
CREATE TABLE IF NOT EXISTS announcement_categories (
                                                       id VARCHAR(50) PRIMARY KEY COMMENT '分类ID(如ann_crash)',
                                                       name VARCHAR(50) NOT NULL COMMENT '分类名称',
                                                       sort_order SMALLINT NOT NULL DEFAULT 0 COMMENT '排序顺序',
                                                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) COMMENT '公告分类表';

-- 内容项表（文本或图片容器）
CREATE TABLE IF NOT EXISTS announcement_items (
                                                  id INT AUTO_INCREMENT PRIMARY KEY,
                                                  category_id VARCHAR(50) NOT NULL COMMENT '关联分类ID',
                                                  item_type ENUM('TEXT', 'IMAGE') NOT NULL DEFAULT 'TEXT' COMMENT '内容类型',
                                                  sort_order SMALLINT NOT NULL DEFAULT 0 COMMENT '排序顺序',
                                                  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                                  last_updated_by BIGINT NOT NULL COMMENT '最后更新者ID',
                                                  FOREIGN KEY (category_id) REFERENCES announcement_categories(id) ON DELETE CASCADE,
                                                  FOREIGN KEY (last_updated_by) REFERENCES users(id)
) COMMENT '公告内容项表';

-- 文本内容表
CREATE TABLE IF NOT EXISTS announcement_texts (
                                                  item_id INT PRIMARY KEY COMMENT '关联内容项ID',
                                                  content TEXT NOT NULL COMMENT '文本内容',
                                                  FOREIGN KEY (item_id) REFERENCES announcement_items(id) ON DELETE CASCADE
) COMMENT '公告文本内容表';

-- 图片内容表
CREATE TABLE IF NOT EXISTS announcement_images (
                                                   item_id INT PRIMARY KEY COMMENT '关联内容项ID',
                                                   image_url VARCHAR(500) NOT NULL COMMENT '图片URL',
                                                   caption VARCHAR(255) COMMENT '图片说明',
                                                   FOREIGN KEY (item_id) REFERENCES announcement_items(id) ON DELETE CASCADE
) COMMENT '公告图片表';

-- ============= 索引优化 ============= --
-- 用户系统索引
CREATE INDEX idx_user_roles_user ON user_roles(user_id);
CREATE INDEX idx_user_roles_role ON user_roles(role_id);
CREATE INDEX idx_admin_apps_user ON admin_applications(user_id);
CREATE INDEX idx_admin_apps_status ON admin_applications(status);
CREATE INDEX idx_admin_apps_processor ON admin_applications(processor_id);

-- 解决方案索引
CREATE INDEX idx_solutions_status ON solutions(status);
CREATE INDEX idx_solutions_category ON solutions(category_id);
CREATE INDEX idx_steps_solution ON solution_steps(solution_id);
CREATE INDEX idx_images_solution ON solution_images(solution_id);

-- 公告系统索引
CREATE INDEX idx_ann_items_category ON announcement_items(category_id);
CREATE INDEX idx_ann_items_order ON announcement_items(sort_order);
CREATE INDEX idx_ann_cats_order ON announcement_categories(sort_order);
CREATE INDEX idx_ann_texts_item ON announcement_texts(item_id);
CREATE INDEX idx_ann_images_item ON announcement_images(item_id);

-- ============= 触发器 ============= --
DELIMITER $$

-- 解决方案触发器
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
END$$

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
END$$

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

-- ============= 公告系统触发器 ============= --
DELIMITER $$

-- 插入公告内容后更新主公告
CREATE TRIGGER trg_announcement_items_after_insert
    AFTER INSERT ON announcement_items
    FOR EACH ROW
BEGIN
    UPDATE announcements
    SET last_updated = CURRENT_TIMESTAMP,
        last_updated_by = NEW.last_updated_by
    WHERE id = 'main_announcement';
END$$

-- 更新公告内容后更新主公告
CREATE TRIGGER trg_announcement_items_after_update
    AFTER UPDATE ON announcement_items
    FOR EACH ROW
BEGIN
    UPDATE announcements
    SET last_updated = CURRENT_TIMESTAMP,
        last_updated_by = NEW.last_updated_by
    WHERE id = 'main_announcement';
END$$

-- 删除公告内容后更新主公告
CREATE TRIGGER trg_announcement_items_after_delete
    AFTER DELETE ON announcement_items
    FOR EACH ROW
BEGIN
    UPDATE announcements
    SET last_updated = CURRENT_TIMESTAMP,
        last_updated_by = OLD.last_updated_by
    WHERE id = 'main_announcement';
END$$

-- 管理员权限检查（创建公告内容）
CREATE TRIGGER trg_announcement_admin_check
    BEFORE INSERT ON announcement_items
    FOR EACH ROW
BEGIN
    DECLARE is_admin BOOLEAN DEFAULT FALSE;

    -- 检查用户是否具有管理员或开发者角色
    SELECT EXISTS (
        SELECT 1
        FROM user_roles ur
                 JOIN roles r ON ur.role_id = r.id
        WHERE ur.user_id = NEW.last_updated_by
          AND r.name IN ('ROLE_ADMIN', 'ROLE_DEV')
    ) INTO is_admin;

    IF NOT is_admin THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = '只有管理员或开发者角色能管理公告内容';
    END IF;
END$$

-- 管理员权限检查（更新公告内容）
CREATE TRIGGER trg_announcement_admin_update_check
    BEFORE UPDATE ON announcement_items
    FOR EACH ROW
BEGIN
    DECLARE is_admin BOOLEAN DEFAULT FALSE;

    -- 检查用户是否具有管理员或开发者角色
    SELECT EXISTS (
        SELECT 1
        FROM user_roles ur
                 JOIN roles r ON ur.role_id = r.id
        WHERE ur.user_id = NEW.last_updated_by
          AND r.name IN ('ROLE_ADMIN', 'ROLE_DEV')
    ) INTO is_admin;

    IF NOT is_admin THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = '只有管理员或开发者角色能更新公告内容';
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

-- 初始化用户
INSERT INTO users (username, email, password, nickname) VALUES
                                                            ('john_doe', 'john@example.com', '$2a$10$Xp1DfT7Y2Jz5v8sQeWZzUuBd6LrCc1V0oGkZ1hY3HjKlN2mP3oR9S', 'John Doe'),
                                                            ('2247380761', '2247380761@qq.com', '$2a$10$SXJFAfgRnwKDHDcHlBuVzOISnlGJXYbdTwj7C96UPi7llUz5Gu7Pq', '系统管理员1'),
                                                            ('3574467868', '3574467868@qq.com', '$2a$10$f37BSMpJQ23Dl65JfwzFiOorqJHsbHl9NH03o0ccSkpsEHpDD6yz', '系统管理员2')
ON DUPLICATE KEY UPDATE email = VALUES(email);

-- 分配角色
INSERT INTO user_roles (user_id, role_id)
VALUES
    ((SELECT id FROM users WHERE username = 'john_doe'), (SELECT id FROM roles WHERE name = 'ROLE_USER')),
    ((SELECT id FROM users WHERE username = '2247380761'), (SELECT id FROM roles WHERE name = 'ROLE_ADMIN')),
    ((SELECT id FROM users WHERE username = '2247380761'), (SELECT id FROM roles WHERE name = 'ROLE_DEV')),
    ((SELECT id FROM users WHERE username = '3574467868'), (SELECT id FROM roles WHERE name = 'ROLE_ADMIN'))
ON DUPLICATE KEY UPDATE user_id = VALUES(user_id);

-- 初始化管理员申请
INSERT INTO admin_applications (user_id, reason) VALUES
    ((SELECT id FROM users WHERE username = 'john_doe'), '申请管理员权限进行系统维护')
ON DUPLICATE KEY UPDATE reason = VALUES(reason);

-- 初始化解决方案分类
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

-- 初始化解决方案
INSERT INTO solutions (id, category_id, title, difficulty, version, description, notes, status, created_by)
VALUES
    ('s1', 'startup', '游戏启动崩溃：Exit Code 1', '中等', '1.16+',
     '在启动Minecraft时，游戏崩溃并显示Exit Code 1错误。这通常是由于Java版本不兼容、显卡驱动问题或内存分配不足引起的。',
     '如果使用模组，请检查模组兼容性，确保所有模组都适用于当前Minecraft版本', '已发布',
     (SELECT id FROM users WHERE username = '3574467868')),
    ('s2', 'network', '联机时出现"Connection Timed Out"错误', '简单', '全版本',
     '尝试加入服务器时出现连接超时错误，这可能是由于网络问题、服务器设置错误或防火墙阻止引起的。',
     '如果使用路由器，请确保端口转发规则正确设置，并且服务器使用静态IP', '已发布',
     (SELECT id FROM users WHERE username = '3574467868')),
    ('s4', 'mod', '模组加载后游戏崩溃', '简单', '全版本',
     '安装模组后游戏无法启动或启动后崩溃。',
     '使用模组管理器如CurseForge可以避免版本冲突', '已发布',
     (SELECT id FROM users WHERE username = '3574467868'))
ON DUPLICATE KEY UPDATE
                     title = VALUES(title),
                     description = VALUES(description),
                     notes = VALUES(notes);

-- 设置解决方案审核状态
UPDATE solutions
SET reviewed_by = (SELECT id FROM users WHERE username = '2247380761'),
    status = '已发布'
WHERE id IN ('s1', 's2', 's4');

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

-- 初始化解决方案截图
INSERT INTO solution_images (solution_id, image_order, image_url) VALUES
                                                                      ('s1', 1, '/images/solutions/exit_code_error.png'),
                                                                      ('s1', 2, '/images/solutions/java_version_settings.png'),
                                                                      ('s2', 1, '/images/solutions/connection_error.png'),
                                                                      ('s2', 2, '/images/solutions/port_forwarding.png'),
                                                                      ('s4', 1, '/images/solutions/mod_compatibility.png'),
                                                                      ('s4', 2, '/images/solutions/mod_loader.png')
ON DUPLICATE KEY UPDATE
    image_url = VALUES(image_url);

-- ============= 群公告系统初始化 ============= --
-- 创建主公告
INSERT INTO announcements (id, last_updated_by)
VALUES ('main_announcement', (SELECT id FROM users WHERE username = '2247380761'))
ON DUPLICATE KEY UPDATE last_updated_by = VALUES(last_updated_by);

-- 初始化内容分类
INSERT INTO announcement_categories (id, name, sort_order) VALUES
                                                               ('ann_crash', '游戏崩溃', 1),
                                                               ('ann_network', '联机问题', 2),
                                                               ('ann_perf', '性能问题', 3),
                                                               ('ann_launcher', '启动器问题', 4);

-- 初始化内容项（游戏崩溃分类下）
SET @admin_id = (SELECT id FROM users WHERE username = '2247380761');

INSERT INTO announcement_items (category_id, item_type, sort_order, last_updated_by) VALUES
                                                                                         ('ann_crash', 'TEXT', 1, @admin_id),  -- 文本项
                                                                                         ('ann_crash', 'IMAGE', 2, @admin_id),  -- 图片项
                                                                                         ('ann_crash', 'TEXT', 3, @admin_id);   -- 文本项

-- 添加文本内容
INSERT INTO announcement_texts (item_id, content) VALUES
                                                      (1, '1.文件操作\n- PCL启动器: 导出错误报告...'),
                                                      (3, '2.描述情况: 请以干练的语言概括...');

-- 添加图片内容
INSERT INTO announcement_images (item_id, image_url, caption) VALUES
    (2, '/uploads/pcl_error_report.png', 'PCL启动器错误报告位置');

-- ============= 视图 ============= --
-- 公告视图
CREATE OR REPLACE VIEW v_full_announcement AS
SELECT
    a.id AS announcement_id,
    a.title,
    a.last_updated,
    u.username AS last_updated_by,
    c.id AS category_id,
    c.name AS category_name,
    c.sort_order AS category_order,
    i.id AS item_id,
    i.item_type,
    i.sort_order AS item_order,
    t.content AS text_content,
    img.image_url,
    img.caption AS image_caption
FROM announcements a
         JOIN users u ON a.last_updated_by = u.id
         JOIN announcement_categories c
         LEFT JOIN announcement_items i ON c.id = i.category_id
         LEFT JOIN announcement_texts t ON i.id = t.item_id AND i.item_type = 'TEXT'
         LEFT JOIN announcement_images img ON i.id = img.item_id AND i.item_type = 'IMAGE'
ORDER BY c.sort_order, i.sort_order;

-- 用户可管理内容视图
CREATE OR REPLACE VIEW v_user_manageable_content AS
SELECT
    'solution' AS content_type,
    s.id AS content_id,
    s.title,
    s.created_by,
    s.updated_at AS last_updated
FROM solutions s
         JOIN user_roles ur ON s.created_by = ur.user_id
         JOIN roles r ON ur.role_id = r.id
WHERE r.name = 'ROLE_ADMIN'

UNION

SELECT
    'announcement' AS content_type,
    a.id AS content_id,
    a.title,
    a.last_updated_by AS created_by,
    a.last_updated
FROM announcements a
         JOIN user_roles ur ON a.last_updated_by = ur.user_id
         JOIN roles r ON ur.role_id = r.id
WHERE r.name IN ('ROLE_ADMIN', 'ROLE_DEV');

CREATE OR REPLACE VIEW v_content_with_images AS
-- 解决方案图片
SELECT
    'solution' AS content_type,
    s.id AS content_id,
    si.id AS image_id,
    si.image_order,
    si.image_url
FROM solutions s
         JOIN solution_images si ON s.id = si.solution_id

UNION

-- 公告图片
SELECT
    'announcement' AS content_type,
    a.id AS content_id,
    ai.item_id AS image_id,
    i.sort_order AS image_order,
    ai.image_url
FROM announcements a
         JOIN announcement_items i ON a.id = 'main_announcement'
         JOIN announcement_images ai ON i.id = ai.item_id
WHERE i.item_type = 'IMAGE';

-- ============= 存储过程  ============= --
-- 重新排序分类
DELIMITER $$
CREATE PROCEDURE ReorderCategories(
    IN cat_ids TEXT
)
BEGIN
    SET @sort_order = 0;
    SET @sql = CONCAT(
            'UPDATE announcement_categories SET sort_order = (CASE id ',
            (SELECT GROUP_CONCAT(
                            CONCAT('WHEN "', SUBSTRING_INDEX(SUBSTRING_INDEX(cat_ids, ',', n.digit+1), ',', -1),
                                   '" THEN ', n.digit) SEPARATOR ' ')
             FROM (SELECT 0 digit UNION SELECT 1 UNION SELECT 2 UNION SELECT 3
                   UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7) n
             WHERE n.digit < LENGTH(cat_ids) - LENGTH(REPLACE(cat_ids, ',', '')) + 1),
            ' END)'
               );
    PREPARE stmt FROM @sql;
    EXECUTE stmt;
    DEALLOCATE PREPARE stmt;

    -- 更新主公告时间
    UPDATE announcements SET last_updated = CURRENT_TIMESTAMP
    WHERE id = 'main_announcement';
END$$
DELIMITER ;

-- 重新排序内容项
DELIMITER $$
CREATE PROCEDURE ReorderItems(
    IN category_id VARCHAR(50),
    IN item_ids TEXT
)
BEGIN
    SET @sort_order = 0;
    SET @sql = CONCAT(
            'UPDATE announcement_items SET sort_order = (CASE id ',
            (SELECT GROUP_CONCAT(
                            CONCAT('WHEN ', SUBSTRING_INDEX(SUBSTRING_INDEX(item_ids, ',', n.digit+1), ',', -1),
                                   ' THEN ', n.digit) SEPARATOR ' ')
             FROM (SELECT 0 digit UNION SELECT 1 UNION SELECT 2 UNION SELECT 3
                   UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7) n
             WHERE n.digit < LENGTH(item_ids) - LENGTH(REPLACE(item_ids, ',', '')) + 1),
            ' END) WHERE category_id = "', category_id, '"'
               );
    PREPARE stmt FROM @sql;
    EXECUTE stmt;
    DEALLOCATE PREPARE stmt;

    -- 更新主公告时间
    UPDATE announcements SET last_updated = CURRENT_TIMESTAMP
    WHERE id = 'main_announcement';
END$$
DELIMITER ;