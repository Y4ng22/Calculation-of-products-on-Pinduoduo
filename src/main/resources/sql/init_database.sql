-- 创建数据库（如果不存在）
CREATE DATABASE IF NOT EXISTS `pddweb` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 使用数据库
USE `pddweb`;

-- 创建交易记录测试表
CREATE TABLE IF NOT EXISTS `transacation_record_test` (
  `id` varchar(50) NOT NULL COMMENT '商品ID',
  `name` varchar(255) DEFAULT NULL COMMENT '商品名称',
  `cost` decimal(10,2) DEFAULT NULL COMMENT '进货价/成本',
  `price` decimal(10,2) DEFAULT NULL COMMENT '卖出价/售价',
  `category` varchar(100) DEFAULT NULL COMMENT '商品分类',
  `state` varchar(50) DEFAULT NULL COMMENT '商品状态',
  `image` varchar(500) DEFAULT NULL COMMENT '商品图片链接',
  `description` varchar(500) DEFAULT NULL COMMENT '商品描述',
  `sales` int DEFAULT 0 COMMENT '销量',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='交易记录测试表';

-- 插入测试数据
INSERT INTO `transacation_record_test` (`id`, `name`, `cost`, `price`, `category`, `state`, `image`, `description`, `sales`) VALUES
('P001', '立白洗衣液去渍', 80.00, 98.00, '生活用品', 'active', 'https://tse4-mm.cn.bing.net/th/id/OIP-C.45TxzesOc67PKw-3ihJriAHaHa?w=208&h=208&c=7&r=0&o=7&dpr=1.5&pid=1.7&rm=3', '洗衣液', 150),
('P002', '立白亮白护色洗衣液', 50.00, 88.00, '生活用品', 'active', 'https://tse3-mm.cn.bing.net/th/id/OIP-C.NQD0ClnokHOAXwd5PAkw6wHaHa?w=212&h=212&c=7&r=0&o=7&dpr=1.7&pid=1.7&rm=3', '洗衣液', 89),
('P003', '立白大师香氛洗衣液', 60.00, 188.00, '生活用品', 'on_sale', 'https://tse3-mm.cn.bing.net/th/id/OIP-C.QhePUmkasB5ggpZzzQYj7wHaNU?w=115&h=206&c=7&r=0&o=7&dpr=1.7&pid=1.7&rm=3', '洗衣液', 234);

-- 显示所有表
SHOW TABLES; 