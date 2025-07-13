package com.pdd.service.impl;

import com.pdd.model.TransactionRecord;
import com.pdd.repository.TransactionRecordRepository;
import com.pdd.service.TransactionRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.jdbc.core.JdbcTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TransactionRecordServiceImpl implements TransactionRecordService {

    @Autowired
    private TransactionRecordRepository transactionRecordRepository;
    
    @Autowired
    private JdbcTemplate jdbcTemplate;

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
                
                // 处理sales字段
                Object salesObj = product.get("sales");
                if (salesObj != null) {
                    if (salesObj instanceof Number) {
                        record.setSales(((Number) salesObj).intValue());
                    } else {
                        record.setSales(Integer.parseInt(salesObj.toString()));
                    }
                } else {
                    record.setSales(0); // 默认销量为0
                }
                
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
    
    @Override
    public List<Map<String, Object>> getDatabaseTables() {
        String sql = "SHOW TABLES";
        List<Map<String, Object>> tables = jdbcTemplate.queryForList(sql);
        
        // 为每个表添加详细信息
        List<Map<String, Object>> tableDetails = new ArrayList<>();
        for (Map<String, Object> table : tables) {
            String tableName = table.values().iterator().next().toString();
            Map<String, Object> tableInfo = new HashMap<>();
            tableInfo.put("tableName", tableName);
            
            // 获取表结构信息
            try {
                String descSql = "DESCRIBE " + tableName;
                List<Map<String, Object>> columns = jdbcTemplate.queryForList(descSql);
                tableInfo.put("columns", columns);
                tableInfo.put("columnCount", columns.size());
                
                // 获取记录数
                String countSql = "SELECT COUNT(*) as count FROM " + tableName;
                Map<String, Object> countResult = jdbcTemplate.queryForMap(countSql);
                tableInfo.put("recordCount", countResult.get("count"));
                
            } catch (Exception e) {
                tableInfo.put("error", "获取表详情失败: " + e.getMessage());
            }
            
            tableDetails.add(tableInfo);
        }
        
        return tableDetails;
    }
    
    @Override
    public List<Map<String, Object>> testConnection() {
        String sql = "SELECT 1 as test";
        return jdbcTemplate.queryForList(sql);
    }
    
    @Override
    public Map<String, Object> getTableData(String tableName, int page, int size) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 计算偏移量
            int offset = (page - 1) * size;
            
            // 构建查询SQL（使用LIMIT进行分页）
            String sql = "SELECT * FROM " + tableName + " LIMIT " + size + " OFFSET " + offset;
            
            // 执行查询
            List<Map<String, Object>> data = jdbcTemplate.queryForList(sql);
            
            // 获取总记录数
            long total = getTableCount(tableName);
            
            result.put("data", data);
            result.put("total", total);
            result.put("page", page);
            result.put("size", size);
            result.put("totalPages", (long) Math.ceil((double) total / size));
            
        } catch (Exception e) {
            throw new RuntimeException("获取表数据失败: " + e.getMessage(), e);
        }
        
        return result;
    }
    
    @Override
    public long getTableCount(String tableName) {
        try {
            String sql = "SELECT COUNT(*) as count FROM " + tableName;
            Map<String, Object> result = jdbcTemplate.queryForMap(sql);
            return ((Number) result.get("count")).longValue();
        } catch (Exception e) {
            throw new RuntimeException("获取表数据总数失败: " + e.getMessage(), e);
        }
    }
} 