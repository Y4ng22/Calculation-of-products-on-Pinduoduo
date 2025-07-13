-- 测试自动生成ID功能的SQL脚本

-- 清空测试表（可选，用于重置测试环境）
-- DELETE FROM transacation_record_test WHERE good_id LIKE 'AUTO_%';

-- 查看当前表中的数据
SELECT * FROM transacation_record_test ORDER BY id;

-- 查看当前自动生成的ID的最大值
SELECT COALESCE(MAX(CAST(SUBSTRING(good_id, 6) AS UNSIGNED)), 0) as max_auto_id 
FROM transacation_record_test 
WHERE good_id LIKE 'AUTO_%';

-- 查看表结构
DESCRIBE transacation_record_test;

-- 查看表的索引
SHOW INDEX FROM transacation_record_test; 