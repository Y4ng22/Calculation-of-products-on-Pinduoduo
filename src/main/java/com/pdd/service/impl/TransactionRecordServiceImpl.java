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
            
            // 获取当前数据库中的记录总数，用于生成自增ID
            long currentMaxId = getCurrentMaxId();
            long nextId = currentMaxId + 1;
            System.out.println("开始生成自动ID，当前记录总数: " + currentMaxId + ", 下一个ID将从: " + nextId + " 开始");
            
            for (Map<String, Object> product : products) {
                TransactionRecord record = new TransactionRecord();
                
                // 处理商品ID字段 - 优先使用 good_id，如果没有则使用 id，如果都没有则自动生成
                String goodId = (String) product.get("good_id");
                if (goodId == null || goodId.trim().isEmpty()) {
                    goodId = (String) product.get("id");
                }
                if (goodId == null || goodId.trim().isEmpty()) {
                    // 自动生成商品ID，格式为 "AUTO_" + 自增序号
                    goodId = "AUTO_" + String.format("%06d", nextId);
                    System.out.println("自动生成商品ID: " + goodId + " (商品名称: " + product.get("name") + ")");
                    nextId++;
                }
                record.setGoodId(goodId);
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
    public TransactionRecord findById(Long id) {
        return transactionRecordRepository.findById(id);
    }

    @Override
    public TransactionRecord findByGoodId(String goodId) {
        return transactionRecordRepository.findByGoodId(goodId);
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
                
                // 确保所有列信息都是基本类型，避免序列化问题
                List<Map<String, Object>> serializedColumns = new ArrayList<>();
                for (Map<String, Object> column : columns) {
                    Map<String, Object> columnInfo = new HashMap<>();
                    for (Map.Entry<String, Object> entry : column.entrySet()) {
                        Object value = entry.getValue();
                        // 将所有值转换为字符串，确保JSON序列化兼容
                        columnInfo.put(entry.getKey(), value != null ? value.toString() : null);
                    }
                    serializedColumns.add(columnInfo);
                }
                
                tableInfo.put("columns", serializedColumns);
                tableInfo.put("columnCount", serializedColumns.size());
                
                // 获取记录数
                String countSql = "SELECT COUNT(*) as count FROM " + tableName;
                Map<String, Object> countResult = jdbcTemplate.queryForMap(countSql);
                Object countValue = countResult.get("count");
                // 确保记录数是Long类型，避免序列化问题
                tableInfo.put("recordCount", countValue != null ? ((Number) countValue).longValue() : 0L);
                
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
            
            // 确保所有数据都是JSON可序列化的基本类型
            List<Map<String, Object>> serializedData = new ArrayList<>();
            for (Map<String, Object> row : data) {
                Map<String, Object> serializedRow = new HashMap<>();
                for (Map.Entry<String, Object> entry : row.entrySet()) {
                    Object value = entry.getValue();
                    // 处理特殊的数据类型，确保JSON序列化兼容
                    if (value instanceof java.sql.Timestamp) {
                        serializedRow.put(entry.getKey(), value.toString());
                    } else if (value instanceof java.sql.Date) {
                        serializedRow.put(entry.getKey(), value.toString());
                    } else if (value instanceof java.sql.Time) {
                        serializedRow.put(entry.getKey(), value.toString());
                    } else if (value instanceof java.time.LocalDateTime) {
                        serializedRow.put(entry.getKey(), value.toString());
                    } else if (value instanceof java.time.LocalDate) {
                        serializedRow.put(entry.getKey(), value.toString());
                    } else if (value instanceof java.time.LocalTime) {
                        serializedRow.put(entry.getKey(), value.toString());
                    } else {
                        serializedRow.put(entry.getKey(), value);
                    }
                }
                serializedData.add(serializedRow);
            }
            
            // 获取总记录数
            long total = getTableCount(tableName);
            
            result.put("data", serializedData);
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
    
    /**
     * 获取当前数据库中最大的ID值，用于生成自增ID
     */
    private long getCurrentMaxId() {
        try {
            // 使用COUNT(*)返回的数字+1作为起始计数
            String sql = "SELECT COUNT(*) as total_count FROM transacation_record_test";
            Map<String, Object> result = jdbcTemplate.queryForMap(sql);
            long totalCount = ((Number) result.get("total_count")).longValue();
            
            // 添加调试日志
            System.out.println("数据库总记录数: " + totalCount + ", 下一个自动生成ID将从: " + (totalCount + 1) + " 开始");
            
            return totalCount; // 返回当前总数，这样nextId = totalCount + 1
        } catch (Exception e) {
            // 如果查询失败，返回0作为起始值
            System.out.println("获取记录总数失败，使用默认值0: " + e.getMessage());
            return 0;
        }
    }
    
    @Override
    public void disableAutoIncrement() {
        try {
            String sql = "ALTER TABLE transacation_record_test MODIFY COLUMN id INT NOT NULL";
            jdbcTemplate.execute(sql);
            System.out.println("主键自动递增已关闭");
        } catch (Exception e) {
            System.err.println("关闭主键自动递增失败: " + e.getMessage());
            throw new RuntimeException("关闭主键自动递增失败", e);
        }
    }
    
    @Override
    public void enableAutoIncrement() {
        try {
            String sql = "ALTER TABLE transacation_record_test MODIFY COLUMN id INT NOT NULL AUTO_INCREMENT";
            jdbcTemplate.execute(sql);
            System.out.println("主键自动递增已开启");
        } catch (Exception e) {
            System.err.println("开启主键自动递增失败: " + e.getMessage());
            throw new RuntimeException("开启主键自动递增失败", e);
        }
    }
    
    @Override
    public Map<String, Object> addTableRecord(String tableName, Map<String, Object> recordData) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 构建INSERT SQL
            StringBuilder columns = new StringBuilder();
            StringBuilder values = new StringBuilder();
            List<Object> params = new ArrayList<>();
            
            for (Map.Entry<String, Object> entry : recordData.entrySet()) {
                if (entry.getValue() != null && !entry.getValue().toString().trim().isEmpty()) {
                    if (columns.length() > 0) {
                        columns.append(", ");
                        values.append(", ");
                    }
                    columns.append(entry.getKey());
                    values.append("?");
                    params.add(entry.getValue());
                }
            }
            
            String sql = "INSERT INTO " + tableName + " (" + columns.toString() + ") VALUES (" + values.toString() + ")";
            
            // 执行插入
            int affectedRows = jdbcTemplate.update(sql, params.toArray());
            
            if (affectedRows > 0) {
                // 获取插入后的记录
                String selectSql = "SELECT * FROM " + tableName + " WHERE id = LAST_INSERT_ID()";
                Map<String, Object> insertedRecord = jdbcTemplate.queryForMap(selectSql);
                
                // 确保插入的记录数据是JSON可序列化的
                Map<String, Object> serializedRecord = new HashMap<>();
                for (Map.Entry<String, Object> entry : insertedRecord.entrySet()) {
                    Object value = entry.getValue();
                    if (value instanceof java.sql.Timestamp) {
                        serializedRecord.put(entry.getKey(), value.toString());
                    } else if (value instanceof java.sql.Date) {
                        serializedRecord.put(entry.getKey(), value.toString());
                    } else if (value instanceof java.sql.Time) {
                        serializedRecord.put(entry.getKey(), value.toString());
                    } else if (value instanceof java.time.LocalDateTime) {
                        serializedRecord.put(entry.getKey(), value.toString());
                    } else if (value instanceof java.time.LocalDate) {
                        serializedRecord.put(entry.getKey(), value.toString());
                    } else if (value instanceof java.time.LocalTime) {
                        serializedRecord.put(entry.getKey(), value.toString());
                    } else {
                        serializedRecord.put(entry.getKey(), value);
                    }
                }
                
                result.put("success", true);
                result.put("message", "记录新增成功");
                result.put("affectedRows", affectedRows);
                result.put("data", serializedRecord);
            } else {
                result.put("success", false);
                result.put("message", "新增记录失败");
            }
            
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "新增记录失败: " + e.getMessage());
            throw new RuntimeException("新增表记录失败", e);
        }
        
        return result;
    }
    
    @Override
    public Map<String, Object> updateTableRecord(String tableName, Map<String, Object> recordData) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 获取主键ID
            Object id = recordData.get("id");
            if (id == null) {
                result.put("success", false);
                result.put("message", "缺少主键ID");
                return result;
            }
            
            // 构建UPDATE SQL
            StringBuilder setClause = new StringBuilder();
            List<Object> params = new ArrayList<>();
            
            for (Map.Entry<String, Object> entry : recordData.entrySet()) {
                if (!"id".equals(entry.getKey()) && entry.getValue() != null) {
                    if (setClause.length() > 0) {
                        setClause.append(", ");
                    }
                    setClause.append(entry.getKey()).append(" = ?");
                    params.add(entry.getValue());
                }
            }
            
            // 添加WHERE条件
            params.add(id);
            String sql = "UPDATE " + tableName + " SET " + setClause.toString() + " WHERE id = ?";
            
            // 执行更新
            int affectedRows = jdbcTemplate.update(sql, params.toArray());
            
            if (affectedRows > 0) {
                // 获取更新后的记录
                String selectSql = "SELECT * FROM " + tableName + " WHERE id = ?";
                Map<String, Object> updatedRecord = jdbcTemplate.queryForMap(selectSql, id);
                
                // 确保更新的记录数据是JSON可序列化的
                Map<String, Object> serializedRecord = new HashMap<>();
                for (Map.Entry<String, Object> entry : updatedRecord.entrySet()) {
                    Object value = entry.getValue();
                    if (value instanceof java.sql.Timestamp) {
                        serializedRecord.put(entry.getKey(), value.toString());
                    } else if (value instanceof java.sql.Date) {
                        serializedRecord.put(entry.getKey(), value.toString());
                    } else if (value instanceof java.sql.Time) {
                        serializedRecord.put(entry.getKey(), value.toString());
                    } else if (value instanceof java.time.LocalDateTime) {
                        serializedRecord.put(entry.getKey(), value.toString());
                    } else if (value instanceof java.time.LocalDate) {
                        serializedRecord.put(entry.getKey(), value.toString());
                    } else if (value instanceof java.time.LocalTime) {
                        serializedRecord.put(entry.getKey(), value.toString());
                    } else {
                        serializedRecord.put(entry.getKey(), value);
                    }
                }
                
                result.put("success", true);
                result.put("message", "记录更新成功");
                result.put("affectedRows", affectedRows);
                result.put("data", serializedRecord);
            } else {
                result.put("success", false);
                result.put("message", "更新记录失败，可能记录不存在");
            }
            
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "更新记录失败: " + e.getMessage());
            throw new RuntimeException("更新表记录失败", e);
        }
        
        return result;
    }
    
    @Override
    public Map<String, Object> deleteTableRecord(String tableName, Map<String, Object> recordData) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 获取主键ID
            Object id = recordData.get("id");
            if (id == null) {
                result.put("success", false);
                result.put("message", "缺少主键ID");
                return result;
            }
            
            // 构建DELETE SQL
            String sql = "DELETE FROM " + tableName + " WHERE id = ?";
            
            // 执行删除
            int affectedRows = jdbcTemplate.update(sql, id);
            
            if (affectedRows > 0) {
                result.put("success", true);
                result.put("message", "记录删除成功");
                result.put("affectedRows", affectedRows);
            } else {
                result.put("success", false);
                result.put("message", "删除记录失败，可能记录不存在");
            }
            
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "删除记录失败: " + e.getMessage());
            throw new RuntimeException("删除表记录失败", e);
        }
        
        return result;
    }

} 