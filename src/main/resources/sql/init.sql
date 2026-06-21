-- 创建数据库
CREATE DATABASE IF NOT EXISTS family_points DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE family_points;

-- 1. 家庭成员表
CREATE TABLE `family_member` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` VARCHAR(50) NOT NULL COMMENT '成员姓名',
  `role` VARCHAR(20) NOT NULL COMMENT '成员角色（爸爸/妈妈/孩子等）',
  `age` INT DEFAULT NULL COMMENT '年龄',
  `avatar` VARCHAR(255) DEFAULT NULL COMMENT '头像路径',
  `current_points` INT DEFAULT 0 COMMENT '当前积分余额',
  `total_earned_points` INT DEFAULT 0 COMMENT '累计获得积分',
  `total_spent_points` INT DEFAULT 0 COMMENT '累计消费积分',
  `member_type` VARCHAR(20) NOT NULL DEFAULT 'CHILD' COMMENT '成员类型（PARENT-家长/CHILD-小朋友）',
  `status` TINYINT DEFAULT 1 COMMENT '状态（0-停用 1-启用）',
  `join_date` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '加入日期',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除（0-未删除 1-已删除）',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='家庭成员表';

-- 2. 积分规则表
CREATE TABLE `point_rule` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `rule_name` VARCHAR(100) NOT NULL COMMENT '规则名称',
  `rule_desc` VARCHAR(500) DEFAULT NULL COMMENT '规则描述',
  `rule_type` VARCHAR(20) NOT NULL COMMENT '规则类型（ADD-加分/DEDUCT-扣分）',
  `point_value` INT NOT NULL COMMENT '积分数值',
  `category` VARCHAR(50) DEFAULT NULL COMMENT '规则分类（学习类/家务类/行为习惯类/其他）',
  `apply_to` VARCHAR(20) NOT NULL DEFAULT 'ALL' COMMENT '适用对象（PARENT-家长/CHILD-小朋友/ALL-通用）',
  `status` TINYINT DEFAULT 1 COMMENT '状态（0-停用 1-启用）',
  `use_count` INT DEFAULT 0 COMMENT '使用次数统计',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除（0-未删除 1-已删除）',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='积分规则表';

-- 3. 积分变更记录表
CREATE TABLE `point_record` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `member_id` BIGINT NOT NULL COMMENT '成员ID',
  `rule_id` BIGINT NOT NULL COMMENT '规则ID',
  `change_type` VARCHAR(20) NOT NULL COMMENT '变更类型（ADD-加分/DEDUCT-扣分）',
  `point_value` INT NOT NULL COMMENT '积分变化值',
  `before_points` INT NOT NULL COMMENT '变更前积分',
  `after_points` INT NOT NULL COMMENT '变更后积分',
  `remark` VARCHAR(500) DEFAULT NULL COMMENT '变更说明/备注',
  `image_path` VARCHAR(255) DEFAULT NULL COMMENT '凭证图片路径',
  `status` TINYINT DEFAULT 1 COMMENT '状态（0-已撤销 1-正常）',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除（0-未删除 1-已删除）',
  PRIMARY KEY (`id`),
  KEY `idx_member_id` (`member_id`),
  KEY `idx_rule_id` (`rule_id`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='积分变更记录表';

-- 4. 奖品表
CREATE TABLE `reward_item` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `item_name` VARCHAR(100) NOT NULL COMMENT '奖品名称',
  `item_desc` VARCHAR(500) DEFAULT NULL COMMENT '奖品描述',
  `image_path` VARCHAR(255) DEFAULT NULL COMMENT '奖品图片路径',
  `required_points` INT NOT NULL COMMENT '所需积分',
  `stock` INT DEFAULT 0 COMMENT '库存数量',
  `category` VARCHAR(50) DEFAULT NULL COMMENT '奖品分类（玩具/零食/特权/现金/其他）',
  `status` TINYINT DEFAULT 1 COMMENT '状态（0-下架 1-上架）',
  `exchange_count` INT DEFAULT 0 COMMENT '兑换次数统计',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除（0-未删除 1-已删除）',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='奖品表';

-- 5. 兑换记录表
CREATE TABLE `exchange_record` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `member_id` BIGINT NOT NULL COMMENT '成员ID',
  `item_id` BIGINT NOT NULL COMMENT '奖品ID',
  `item_name` VARCHAR(100) NOT NULL COMMENT '奖品名称',
  `quantity` INT NOT NULL DEFAULT 1 COMMENT '兑换数量',
  `total_points` INT NOT NULL COMMENT '消耗总积分',
  `exchange_status` VARCHAR(20) DEFAULT 'EXCHANGED' COMMENT '兑换状态（EXCHANGED-已兑换/DELIVERED-已发放/COMPLETED-已完成）',
  `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '兑换时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除（0-未删除 1-已删除）',
  PRIMARY KEY (`id`),
  KEY `idx_member_id` (`member_id`),
  KEY `idx_item_id` (`item_id`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='兑换记录表';

-- 6. 系统配置表
CREATE TABLE `system_config` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `config_key` VARCHAR(100) NOT NULL COMMENT '配置键',
  `config_value` VARCHAR(500) NOT NULL COMMENT '配置值',
  `config_desc` VARCHAR(200) DEFAULT NULL COMMENT '配置描述',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_config_key` (`config_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统配置表';

-- 插入默认系统配置
INSERT INTO `system_config` (`config_key`, `config_value`, `config_desc`) VALUES
('POINT_TO_MONEY_RATIO', '1', '积分与金额换算比例（1积分=1元）'),
('SYSTEM_NOTICE', '欢迎使用家庭积分系统！', '系统公告/提示信息');

-- 插入示例数据（可选）
-- 示例成员
INSERT INTO `family_member` (`name`, `role`, `age`, `member_type`, `current_points`, `status`) VALUES
('爸爸', '爸爸', 35, 'PARENT', 0, 1),
('妈妈', '妈妈', 33, 'PARENT', 0, 1),
('小明', '孩子', 8, 'CHILD', 0, 1);

-- 示例规则（小朋友）
INSERT INTO `point_rule` (`rule_name`, `rule_desc`, `rule_type`, `point_value`, `category`, `apply_to`, `status`) VALUES
('完成作业', '按时独立完成当天作业', 'ADD', 10, '学习类', 'CHILD', 1),
('考试满分', '考试获得满分', 'ADD', 50, '学习类', 'CHILD', 1),
('帮忙做家务', '主动帮忙做家务', 'ADD', 20, '家务类', 'CHILD', 1),
('按时起床', '早上按时起床不赖床', 'ADD', 5, '行为习惯类', 'CHILD', 1),
('不做作业', '未完成当天作业', 'DEDUCT', 20, '学习类', 'CHILD', 1),
('乱扔玩具', '玩具不收拾乱扔', 'DEDUCT', 10, '行为习惯类', 'CHILD', 1);

-- 示例规则（家长）
INSERT INTO `point_rule` (`rule_name`, `rule_desc`, `rule_type`, `point_value`, `category`, `apply_to`, `status`) VALUES
('陪孩子阅读', '陪伴孩子阅读30分钟以上', 'ADD', 15, '其他', 'PARENT', 1),
('准时接送', '准时接送孩子上下学', 'ADD', 10, '其他', 'PARENT', 1),
('发脾气', '对孩子发脾气', 'DEDUCT', 30, '行为习惯类', 'PARENT', 1);

-- 示例规则（通用）
INSERT INTO `point_rule` (`rule_name`, `rule_desc`, `rule_type`, `point_value`, `category`, `apply_to`, `status`) VALUES
('打扫卫生', '主动打扫家庭卫生', 'ADD', 15, '家务类', 'ALL', 1),
('浪费食物', '浪费食物', 'DEDUCT', 15, '行为习惯类', 'ALL', 1);

-- 示例奖品
INSERT INTO `reward_item` (`item_name`, `item_desc`, `required_points`, `stock`, `category`, `status`) VALUES
('玩具小汽车', '精美小汽车玩具一个', 50, 10, '玩具', 1),
('零食礼包', '各种零食组合礼包', 30, 20, '零食', 1),
('看电视1小时', '额外获得看电视1小时的特权', 20, 999, '特权', 1),
('现金10元', '现金10元', 10, 999, '现金', 1),
('游乐场门票', '游乐场门票一张', 100, 5, '其他', 1);
