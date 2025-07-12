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
} 