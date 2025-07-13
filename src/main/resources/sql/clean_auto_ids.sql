-- 清理所有自动生成的ID记录

-- 查看当前自动生成的记录
SELECT 
    id,
    good_id,
    name,
    '将要删除' as action
FROM transacation_record_test 
WHERE good_id LIKE 'AUTO_%' 
ORDER BY CAST(SUBSTRING(good_id, 6) AS UNSIGNED);

-- 统计要删除的记录数
SELECT 
    COUNT(*) as records_to_delete,
    MIN(CAST(SUBSTRING(good_id, 6) AS UNSIGNED)) as min_auto_id,
    MAX(CAST(SUBSTRING(good_id, 6) AS UNSIGNED)) as max_auto_id
FROM transacation_record_test 
WHERE good_id LIKE 'AUTO_%';

-- 执行删除操作（取消注释以执行）
-- DELETE FROM transacation_record_test WHERE good_id LIKE 'AUTO_%';

-- 验证删除结果
SELECT 
    COUNT(*) as remaining_auto_records
FROM transacation_record_test 
WHERE good_id LIKE 'AUTO_%';

-- 查看剩余的所有记录
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