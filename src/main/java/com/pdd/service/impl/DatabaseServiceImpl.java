package com.pdd.service.impl;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.List;
import java.util.HashMap;
import com.pdd.common.Result;
import com.pdd.service.DatabaseService;

@Service
public class DatabaseServiceImpl implements DatabaseService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public Result<?> addColumn(String tableName, Map<String, Object> columnInfo) {
        String sql = "ALTER TABLE `" + tableName + "` ADD COLUMN " + buildColumnSql(columnInfo);
        jdbcTemplate.execute(sql);
        return Result.success();
    }

    @Override
    public Result<?> updateColumn(String tableName, Map<String, Object> columnInfo) {
        String sql = "ALTER TABLE `" + tableName + "` MODIFY COLUMN " + buildColumnSql(columnInfo);
        jdbcTemplate.execute(sql);
        return Result.success();
    }

    @Override
    public Result<?> deleteColumn(String tableName, Map<String, Object> columnInfo) {
        String fieldName = (String) columnInfo.get("fieldName");
        String sql = "ALTER TABLE `" + tableName + "` DROP COLUMN `" + fieldName + "`";
        jdbcTemplate.execute(sql);
        return Result.success();
    }

    @Override
    public Map<String, Object> createTable(String tableName, String description, List<Map<String, Object>> fields) {
        Map<String, Object> result = new HashMap<>();
        try {
            // 构建CREATE TABLE SQL
            StringBuilder sql = new StringBuilder();
            sql.append("CREATE TABLE `").append(tableName).append("` (");
            
            // 添加字段定义
            for (int i = 0; i < fields.size(); i++) {
                Map<String, Object> field = fields.get(i);
                if (i > 0) sql.append(", ");
                sql.append(buildColumnSql(field));
            }
            
            // 添加主键约束
            String primaryKey = findPrimaryKey(fields);
            if (primaryKey != null) {
                sql.append(", PRIMARY KEY (`").append(primaryKey).append("`)");
            }
            
            sql.append(") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci");
            
            // 执行创建表SQL
            jdbcTemplate.execute(sql.toString());
            
            result.put("success", true);
            result.put("message", "表创建成功");
            result.put("sql", sql.toString());
            
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "创建表失败: " + e.getMessage());
        }
        
        return result;
    }

    private String findPrimaryKey(List<Map<String, Object>> fields) {
        for (Map<String, Object> field : fields) {
            if ("PRI".equals(field.get("Key"))) {
                return (String) field.get("Field");
            }
        }
        return null;
    }

    private String buildColumnSql(Map<String, Object> columnInfo) {
        StringBuilder sb = new StringBuilder();
        sb.append("`").append(columnInfo.get("Field")).append("` ");
        
        // 处理类型和长度
        String type = (String) columnInfo.get("Type");
        String length = (String) columnInfo.get("Length");
        if (length != null && !length.trim().isEmpty() && 
            (type.equals("varchar") || type.equals("int") || type.equals("decimal"))) {
            sb.append(type).append("(").append(length).append(") ");
        } else {
            sb.append(type).append(" ");
        }
        
        sb.append("YES".equals(columnInfo.get("Null")) ? "NULL " : "NOT NULL ");
        if (columnInfo.get("Default") != null && !"".equals(columnInfo.get("Default"))) {
            sb.append("DEFAULT '").append(columnInfo.get("Default")).append("' ");
        }
        if (columnInfo.get("Extra") != null && !"".equals(columnInfo.get("Extra"))) {
            sb.append(columnInfo.get("Extra")).append(" ");
        }
        return sb.toString().trim();
    }
} 