-- 已有数据库升级：先备份并停止应用，再在 family_points 数据库中执行一次。
-- 新安装直接使用 init.sql，不要再运行本脚本。
-- 功能：积分变更支持自定义规则，可选择是否加入规则列表。
--   加入规则列表  -> 新建 point_rule 记录，rule_id 指向该规则
--   不加入规则列表 -> rule_id 为空，规则名称存入 custom_rule_name
USE family_points;

ALTER TABLE `point_record`
  MODIFY COLUMN `rule_id` BIGINT DEFAULT NULL COMMENT '规则ID（自定义规则未加入规则列表时为空）',
  ADD COLUMN `custom_rule_name` VARCHAR(100) DEFAULT NULL COMMENT '自定义规则名称（规则未加入规则列表时记录）' AFTER `rule_id`;
