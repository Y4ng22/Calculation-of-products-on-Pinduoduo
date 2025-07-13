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
     * 根据主键ID查询记录
     */
    TransactionRecord findById(Long id);
    
    /**
     * 根据商品ID查询记录
     */
    TransactionRecord findByGoodId(String goodId);
    
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
    
    /**
     * 关闭主键自动递增
     */
    void disableAutoIncrement();
    
    /**
     * 开启主键自动递增
     */
    void enableAutoIncrement();
    
    /**
     * 新增表记录
     */
    Map<String, Object> addTableRecord(String tableName, Map<String, Object> recordData);
    
    /**
     * 更新表记录
     */
    Map<String, Object> updateTableRecord(String tableName, Map<String, Object> recordData);
    
    /**
     * 删除表记录
     */
    Map<String, Object> deleteTableRecord(String tableName, Map<String, Object> recordData);
} 