-- 数据库迁移脚本：将 transacation_record_test 表从旧结构迁移到新结构
-- 执行前请备份数据库

-- 1. 创建临时表保存现有数据
CREATE TABLE IF NOT EXISTS `transacation_record_test_backup` AS 
SELECT * FROM `transacation_record_test`;

-- 2. 删除原表
DROP TABLE IF EXISTS `transacation_record_test`;

-- 3. 创建新表结构
CREATE TABLE `transacation_record_test` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '序号',
  `good_id` varchar(50) NOT NULL COMMENT '商品ID',
  `name` varchar(255) DEFAULT NULL COMMENT '商品名称',
  `cost` decimal(10,2) DEFAULT NULL COMMENT '进货价/成本',
  `price` decimal(10,2) DEFAULT NULL COMMENT '卖出价/售价',
  `category` varchar(100) DEFAULT NULL COMMENT '商品分类',
  `state` varchar(50) DEFAULT NULL COMMENT '商品状态',
  `image` varchar(500) DEFAULT NULL COMMENT '商品图片链接',
  `description` varchar(500) DEFAULT NULL COMMENT '商品描述',
  `sales` int DEFAULT 0 COMMENT '销量',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_good_id` (`good_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='交易记录测试表';

-- 4. 从备份表迁移数据（将原来的 id 字段作为 good_id）
INSERT INTO `transacation_record_test` (`good_id`, `name`, `cost`, `price`, `category`, `state`, `image`, `description`, `sales`)
SELECT 
  `id` as `good_id`,  -- 原来的 id 字段作为 good_id
  `name`,
  `cost`,
  `price`,
  `category`,
  `state`,
  `image`,
  `description`,
  `sales`
FROM `transacation_record_test_backup`;

-- 5. 验证数据迁移结果
SELECT '数据迁移完成，共迁移 ' || COUNT(*) || ' 条记录' as migration_result
FROM `transacation_record_test`;

-- 6. 显示新表结构
DESCRIBE `transacation_record_test`;

-- 7. 显示迁移后的数据
SELECT * FROM `transacation_record_test`;

-- 8. 可选：删除备份表（确认数据无误后执行）
-- DROP TABLE `transacation_record_test_backup`; 