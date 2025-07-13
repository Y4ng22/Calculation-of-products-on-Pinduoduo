package com.pdd.repository;

import com.pdd.model.TransactionRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface TransactionRecordRepository {
    
    /**
     * 批量插入交易记录
     */
    int batchInsert(@Param("records") List<TransactionRecord> records);
    
    /**
     * 根据主键ID查询记录
     */
    TransactionRecord findById(@Param("id") Long id);
    
    /**
     * 根据商品ID查询记录
     */
    TransactionRecord findByGoodId(@Param("goodId") String goodId);
    
    /**
     * 查询所有记录
     */
    List<TransactionRecord> findAll();
    
    /**
     * 根据分类查询记录
     */
    List<TransactionRecord> findByCategory(@Param("category") String category);
    
    /**
     * 根据状态查询记录
     */
    List<TransactionRecord> findByState(@Param("state") String state);
} 