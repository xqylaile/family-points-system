-- 已有数据库升级：先备份并停止应用，再在 family_points 数据库中执行一次。
-- 新安装直接使用 init.sql，不要再运行本脚本。
USE family_points;

ALTER TABLE `point_record`
  ADD COLUMN `settlement_id` BIGINT DEFAULT NULL COMMENT '结算批次，非空时禁止撤销';

ALTER TABLE `exchange_record`
  ADD COLUMN `settlement_id` BIGINT DEFAULT NULL COMMENT '结算批次，非空时禁止撤销';

CREATE TABLE `monthly_settlement` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `settlement_month` CHAR(7) NOT NULL COMMENT '结算月份，yyyy-MM',
  `create_time` DATETIME NOT NULL COMMENT '实际结算时间（北京时间）',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_settlement_month` (`settlement_month`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='月度结算批次';

CREATE TABLE `settlement_detail` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `settlement_id` BIGINT NOT NULL COMMENT '结算批次ID',
  `member_id` BIGINT NOT NULL COMMENT '成员ID',
  `member_name` VARCHAR(50) NOT NULL COMMENT '结算时成员姓名',
  `rank_no` INT NOT NULL COMMENT '结算名次，同分并列',
  `original_points` INT NOT NULL COMMENT '结算前积分',
  `settled_points` DECIMAL(12,1) NOT NULL COMMENT '结算分数，第3名减半',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_settlement_member` (`settlement_id`, `member_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='月度结算明细';
