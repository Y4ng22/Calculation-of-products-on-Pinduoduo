package com.pdd.service.impl;

import com.pdd.model.TransactionRecord;
import com.pdd.repository.TransactionRecordRepository;
import com.pdd.service.TransactionRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TransactionRecordServiceImpl implements TransactionRecordService {

    @Autowired
    private TransactionRecordRepository transactionRecordRepository;

    @Override
    @Transactional
    public Map<String, Object> batchSaveRecords(List<Map<String, Object>> products) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            List<TransactionRecord> records = new ArrayList<>();
            
            for (Map<String, Object> product : products) {
                TransactionRecord record = new TransactionRecord();
                
                // 映射字段
                record.setId((String) product.get("id"));
                record.setName((String) product.get("name"));
                
                // 处理cost字段（可能是cost或purchasePrice）
                Object costObj = product.get("cost");
                if (costObj == null) {
                    costObj = product.get("purchasePrice");
                }
                if (costObj != null) {
                    if (costObj instanceof Number) {
                        record.setCost(new BigDecimal(costObj.toString()));
                    } else {
                        record.setCost(new BigDecimal(costObj.toString()));
                    }
                }
                
                // 处理price字段（可能是price或salePrice）
                Object priceObj = product.get("price");
                if (priceObj == null) {
                    priceObj = product.get("salePrice");
                }
                if (priceObj != null) {
                    if (priceObj instanceof Number) {
                        record.setPrice(new BigDecimal(priceObj.toString()));
                    } else {
                        record.setPrice(new BigDecimal(priceObj.toString()));
                    }
                }
                
                record.setCategory((String) product.get("category"));
                
                // 处理status字段（可能是status或state）
                Object statusObj = product.get("status");
                if (statusObj == null) {
                    statusObj = product.get("state");
                }
                record.setState((String) statusObj);
                
                record.setImage((String) product.get("image"));
                record.setDescription((String) product.get("description"));
                
                records.add(record);
            }
            
            // 批量插入
            int insertCount = transactionRecordRepository.batchInsert(records);
            
            result.put("success", true);
            result.put("message", "成功插入 " + insertCount + " 条记录");
            result.put("insertCount", insertCount);
            result.put("totalCount", products.size());
            
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "插入失败: " + e.getMessage());
            result.put("error", e.getMessage());
            throw new RuntimeException("批量保存交易记录失败", e);
        }
        
        return result;
    }

    @Override
    public TransactionRecord findById(String id) {
        return transactionRecordRepository.findById(id);
    }

    @Override
    public List<TransactionRecord> findAll() {
        return transactionRecordRepository.findAll();
    }

    @Override
    public List<TransactionRecord> findByCategory(String category) {
        return transactionRecordRepository.findByCategory(category);
    }

    @Override
    public List<TransactionRecord> findByState(String state) {
        return transactionRecordRepository.findByState(state);
    }
} 