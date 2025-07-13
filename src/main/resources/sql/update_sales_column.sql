-- 为现有表添加sales列
USE `pddweb`;

-- 检查列是否存在，如果不存在则添加
SET @sql = (SELECT IF(
    (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS 
     WHERE TABLE_SCHEMA = 'pddweb' 
     AND TABLE_NAME = 'transacation_record_test' 
     AND COLUMN_NAME = 'sales') = 0,
    'ALTER TABLE transacation_record_test ADD COLUMN sales int DEFAULT 0 COMMENT ''销量''',
    'SELECT ''sales列已存在'' as message'
));
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 更新现有数据的销量（为演示目的设置一些随机值）
UPDATE transacation_record_test SET sales = 150 WHERE id = 'P001';
UPDATE transacation_record_test SET sales = 89 WHERE id = 'P002';
UPDATE transacation_record_test SET sales = 234 WHERE id = 'P003';

-- 显示更新后的表结构
DESCRIBE transacation_record_test;

-- 显示更新后的数据
SELECT * FROM transacation_record_test; 