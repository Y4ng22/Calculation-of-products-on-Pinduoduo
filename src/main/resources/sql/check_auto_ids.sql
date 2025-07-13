-- 检查当前数据库中的自动生成ID

-- 查看所有自动生成的ID
SELECT 
    id,
    good_id,
    name,
    created_at
FROM transacation_record_test 
WHERE good_id LIKE 'AUTO_%' 
ORDER BY CAST(SUBSTRING(good_id, 6) AS UNSIGNED);

-- 查看当前最大的自动生成ID
SELECT 
    COALESCE(MAX(CAST(SUBSTRING(good_id, 6) AS UNSIGNED)), 0) as max_auto_id,
    COUNT(*) as auto_id_count
FROM transacation_record_test 
WHERE good_id LIKE 'AUTO_%';

-- 查看所有记录（包括非自动生成的ID）
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

-- 统计信息
SELECT 
    COUNT(*) as total_records,
    COUNT(CASE WHEN good_id LIKE 'AUTO_%' THEN 1 END) as auto_generated_count,
    COUNT(CASE WHEN good_id NOT LIKE 'AUTO_%' THEN 1 END) as manual_count
FROM transacation_record_test; 