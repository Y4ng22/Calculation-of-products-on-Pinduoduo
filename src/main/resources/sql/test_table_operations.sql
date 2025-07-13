-- 测试表操作功能的SQL脚本
-- 用于验证MetadataManagement.vue页面的增删改功能

-- 1. 查看当前表结构
DESCRIBE transacation_record_test;

-- 2. 查看当前数据
SELECT * FROM transacation_record_test LIMIT 10;

-- 3. 测试新增记录（模拟前端新增操作）
INSERT INTO transacation_record_test (good_id, name, cost, price, category, state, image, description, sales) 
VALUES ('TEST_001', '测试商品1', 50.00, 80.00, '测试分类', 'active', 'https://example.com/image1.jpg', '这是一个测试商品', 100);

-- 4. 测试更新记录（模拟前端编辑操作）
UPDATE transacation_record_test 
SET name = '更新后的测试商品1', price = 85.00, sales = 120 
WHERE good_id = 'TEST_001';

-- 5. 查看更新后的数据
SELECT * FROM transacation_record_test WHERE good_id = 'TEST_001';

-- 6. 测试删除记录（模拟前端删除操作）
DELETE FROM transacation_record_test WHERE good_id = 'TEST_001';

-- 7. 验证删除结果
SELECT COUNT(*) as remaining_records FROM transacation_record_test WHERE good_id = 'TEST_001';

-- 8. 查看最终数据状态
SELECT * FROM transacation_record_test ORDER BY id DESC LIMIT 5;

-- 9. 统计信息
SELECT 
    COUNT(*) as total_records,
    COUNT(DISTINCT category) as category_count,
    AVG(cost) as avg_cost,
    AVG(price) as avg_price,
    SUM(sales) as total_sales
FROM transacation_record_test; 