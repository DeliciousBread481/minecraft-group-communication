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
                                                  feedback TEXT COMMENT '处理反馈',
                                                  processor_id BIGINT COMMENT '处理人ID',
                                                  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '申请时间',
                                                  processed_at TIMESTAMP COMMENT '处理时间',
                                                  FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) COMMENT '管理员权限申请表';


-- 解决方案知识库
CREATE TABLE IF NOT EXISTS categories (
                                          id VARCHAR(50) PRIMARY KEY COMMENT '分类ID(如startup)',
                                          name VARCHAR(50) NOT NULL COMMENT '分类名称',
                                          icon VARCHAR(100) COMMENT '图标组件名',
                                          description VARCHAR(255) COMMENT '分类描述',
                                          color VARCHAR(20) COMMENT '主题颜色',
                                          created_by BIGINT NOT NULL COMMENT '创建者ID',
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
                                         created_by BIGINT NOT NULL COMMENT '创建者ID',
                                         reviewed_by BIGINT COMMENT '审核者ID',
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
-- 添加复合索引提高查询效率
CREATE INDEX idx_solutions_search ON solutions(category_id, status, updated_at);

-- 公告系统索引
CREATE INDEX idx_ann_items_category ON announcement_items(category_id);
CREATE INDEX idx_ann_items_order ON announcement_items(sort_order);
CREATE INDEX idx_ann_cats_order ON announcement_categories(sort_order);
CREATE INDEX idx_ann_texts_item ON announcement_texts(item_id);
CREATE INDEX idx_ann_images_item ON announcement_images(item_id);

-- ============= 初始化数据 ============= --
START TRANSACTION;

-- 1. 初始化系统角色
INSERT IGNORE INTO roles (name, description) VALUES
                                                 ('ROLE_DEV', '开发者权限'),
                                                 ('ROLE_ADMIN', '系统管理员'),
                                                 ('ROLE_USER', '普通用户');

-- 2. 初始化用户
INSERT IGNORE INTO users (username, email, password, nickname) VALUES
                                                                   ('john_doe', 'john@example.com', '$2a$10$Xp1DfT7Y2Jz5v8sQeWZzUuBd6LrCc1V0oGkZ1hY3HjKlN2mP3oR9S', 'John Doe'),
                                                                   ('admin1', 'admin1@example.com', '$2a$10$SXJFAfgRnwKDHDcHlBuVzOISnlGJXYbdTwj7C96UPi7llUz5Gu7Pq', '系统管理员1'),
                                                                   ('admin2', 'admin2@example.com', '$2a$10$f37BSMpJQ23Dl65JfwzFiOorqJHsbHl9NH03o0ccSkpsEHpDD6yz', '系统管理员2');

-- 3. 分配角色
INSERT IGNORE INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u
         CROSS JOIN roles r
WHERE (u.username = 'john_doe' AND r.name = 'ROLE_USER')
   OR (u.username = 'admin1' AND r.name IN ('ROLE_ADMIN', 'ROLE_DEV'))
   OR (u.username = 'admin2' AND r.name = 'ROLE_ADMIN');

-- 4. 初始化解决方案分类
INSERT IGNORE INTO categories (id, name, icon, description, color, created_by)
SELECT * FROM (
                  SELECT 'all', '全部问题', 'MagicStick', '浏览所有类别的解决方案', '#9c27b0', id FROM users WHERE username = 'admin1'
                  UNION SELECT 'startup', '启动问题', 'Setting', '游戏启动失败、崩溃等问题', '#409EFF', id FROM users WHERE username = 'admin1'
                  UNION SELECT 'mod', '模组问题', 'Warning', '模组加载、兼容性问题', '#E6A23C', id FROM users WHERE username = 'admin1'
                  UNION SELECT 'network', '联机问题', 'Connection', '服务器连接、联机问题', '#67C23A', id FROM users WHERE username = 'admin1'
              ) AS tmp;

-- 5. 初始化解决方案
INSERT IGNORE INTO solutions (id, category_id, title, difficulty, version, description, notes, status, created_by, reviewed_by)
SELECT
    s.id,
    s.category_id,
    s.title,
    s.difficulty,
    s.version,
    s.description,
    s.notes,
    s.status,
    creator.id,
    reviewer.id
FROM (
         SELECT 's1' AS id, 'startup' AS category_id, '游戏启动崩溃：Exit Code 1' AS title, '中等' AS difficulty,
                '1.16+' AS version, '在启动Minecraft时...' AS description, '如果使用模组...' AS notes, '已发布' AS status,
                'admin2' AS creator, 'admin1' AS reviewer
         UNION
         SELECT 's2', 'network', '联机时出现"Connection Timed Out"错误', '简单', '全版本',
                '尝试加入服务器时出现连接超时错误...', '如果使用路由器...', '已发布', 'admin2', 'admin1'
         UNION
         SELECT 's4', 'mod', '模组加载后游戏崩溃', '简单', '全版本',
                '安装模组后游戏无法启动或启动后崩溃。', '使用模组管理器如CurseForge...', '已发布', 'admin2', 'admin1'
     ) s
         JOIN users creator ON creator.username = s.creator
         JOIN users reviewer ON reviewer.username = s.reviewer;

-- 6. 初始化解决方案步骤
INSERT IGNORE INTO solution_steps (solution_id, step_order, content)
VALUES
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
    ('s4', 5, '查看游戏日志文件定位具体错误');

-- 7. 群公告系统初始化
INSERT IGNORE INTO announcements (id, last_updated_by)
SELECT 'main_announcement', id
FROM users
WHERE username = 'admin1';

INSERT IGNORE INTO announcement_categories (id, name, sort_order) VALUES
                                                                      ('ann_crash', '游戏崩溃', 1),
                                                                      ('ann_network', '联机问题', 2),
                                                                      ('ann_perf', '性能问题', 3),
                                                                      ('ann_launcher', '启动器问题', 4);

-- 使用事务安全的内容项初始化
SET @admin_id = (SELECT id FROM users WHERE username = 'admin1' LIMIT 1);

INSERT IGNORE INTO announcement_items (category_id, item_type, sort_order, last_updated_by)
VALUES ('ann_crash', 'TEXT', 1, @admin_id),
       ('ann_crash', 'IMAGE', 2, @admin_id),
       ('ann_crash', 'TEXT', 3, @admin_id);

SET @text_item1 = (SELECT id FROM announcement_items WHERE category_id = 'ann_crash' AND sort_order = 1);
SET @image_item = (SELECT id FROM announcement_items WHERE category_id = 'ann_crash' AND sort_order = 2);
SET @text_item2 = (SELECT id FROM announcement_items WHERE category_id = 'ann_crash' AND sort_order = 3);

INSERT IGNORE INTO announcement_texts (item_id, content) VALUES
                                                             (@text_item1, '1.文件操作\n- PCL启动器: 导出错误报告...'),
                                                             (@text_item2, '2.描述情况: 请以干练的语言概括...');

INSERT IGNORE INTO announcement_images (item_id, image_url, caption) VALUES
    (@image_item, '/uploads/pcl_error_report.png', 'PCL启动器错误报告位置');

COMMIT;

-- ============= 视图 ============= --
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
         JOIN announcement_categories c ON 1=1
         LEFT JOIN announcement_items i ON c.id = i.category_id
         LEFT JOIN announcement_texts t ON i.id = t.item_id AND i.item_type = 'TEXT'
         LEFT JOIN announcement_images img ON i.id = img.item_id AND i.item_type = 'IMAGE'
ORDER BY c.sort_order, i.sort_order;