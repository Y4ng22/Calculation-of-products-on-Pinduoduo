package com.pdd.service;

import com.pdd.model.TransactionRecord;
import java.util.List;
import java.util.Map;

public interface TransactionRecordService {
    
    /**
     * 批量保存交易记录
     */
    Map<String, Object> batchSaveRecords(List<Map<String, Object>> products);
    
    /**
     * 根据ID查询记录
     */
    TransactionRecord findById(String id);
    
    /**
     * 查询所有记录
     */
    List<TransactionRecord> findAll();
    
    /**
     * 根据分类查询记录
     */
    List<TransactionRecord> findByCategory(String category);
    
    /**
     * 根据状态查询记录
     */
    List<TransactionRecord> findByState(String state);
    
    /**
     * 获取数据库表信息
     */
    List<Map<String, Object>> getDatabaseTables();
    
    /**
     * 测试数据库连接
     */
    List<Map<String, Object>> testConnection();
    
    /**
     * 获取表数据
     */
    Map<String, Object> getTableData(String tableName, int page, int size);
    
    /**
     * 获取表数据总数
     */
    long getTableCount(String tableName);
} 