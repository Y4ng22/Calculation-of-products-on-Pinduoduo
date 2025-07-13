-- 测试基于COUNT的ID计算逻辑

-- 查看当前数据库状态
SELECT 
    COUNT(*) as total_records,
    COUNT(CASE WHEN good_id LIKE 'AUTO_%' THEN 1 END) as auto_generated_count,
    COUNT(CASE WHEN good_id NOT LIKE 'AUTO_%' THEN 1 END) as manual_count
FROM transacation_record_test;

-- 查看所有记录
SELECT 
    id,
    good_id,
    name,
    CASE 
        WHEN good_id LIKE 'AUTO_%' THEN '自动生成'
        ELSE '手动设置'
    END as id_type
FROM transacation_record_test 
ORDER BY id;

-- 模拟新的ID计算逻辑
SELECT 
    COUNT(*) as current_total,
    COUNT(*) + 1 as next_auto_id,
    CONCAT('AUTO_', LPAD(COUNT(*) + 1, 6, '0')) as next_auto_id_formatted
FROM transacation_record_test;

-- 查看自动生成ID的分布
SELECT 
    good_id,
    CAST(SUBSTRING(good_id, 6) AS UNSIGNED) as auto_id_number,
    name
FROM transacation_record_test 
WHERE good_id LIKE 'AUTO_%' 
ORDER BY CAST(SUBSTRING(good_id, 6) AS UNSIGNED);

-- 验证ID连续性
SELECT 
    MIN(CAST(SUBSTRING(good_id, 6) AS UNSIGNED)) as min_auto_id,
    MAX(CAST(SUBSTRING(good_id, 6) AS UNSIGNED)) as max_auto_id,
    COUNT(*) as auto_id_count,
    MAX(CAST(SUBSTRING(good_id, 6) AS UNSIGNED)) - MIN(CAST(SUBSTRING(good_id, 6) AS UNSIGNED)) + 1 as expected_count
FROM transacation_record_test 
WHERE good_id LIKE 'AUTO_%'; 