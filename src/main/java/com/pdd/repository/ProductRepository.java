package com.pdd.repository;

import com.pdd.model.Product;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface ProductRepository {
    
    /**
     * 插入商品（如果存在则更新）
     */
    int insertOrUpdate(Product product);
    
    /**
     * 批量插入商品
     */
    int batchInsert(@Param("products") List<Product> products);
    
    /**
     * 根据goodId查询商品
     */
    Product findByGoodId(@Param("goodId") String goodId);
    
    /**
     * 查询所有商品
     */
    List<Product> findAll();
    
    /**
     * 分页查询商品
     */
    List<Product> findByPage(@Param("offset") int offset, @Param("limit") int limit);
    
    /**
     * 获取商品总数
     */
    int getTotalCount();
    
    /**
     * 根据分类查询商品
     */
    List<Product> findByCategory(@Param("category") String category);
    
    /**
     * 根据状态查询商品
     */
    List<Product> findByState(@Param("state") String state);
    
    /**
     * 删除商品
     */
    int deleteByGoodId(@Param("goodId") String goodId);
    
    /**
     * 清空表（用于测试）
     */
    int truncateTable();
} 