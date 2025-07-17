package com.pdd.service.impl;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Map;
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
    public Result<?> deleteColumn(String tableName, String fieldName) {
        String sql = "ALTER TABLE `" + tableName + "` DROP COLUMN `" + fieldName + "`";
        jdbcTemplate.execute(sql);
        return Result.success();
    }

    private String buildColumnSql(Map<String, Object> columnInfo) {
        StringBuilder sb = new StringBuilder();
        sb.append("`").append(columnInfo.get("Field")).append("` ");
        sb.append(columnInfo.get("Type")).append(" ");
        sb.append("YES".equals(columnInfo.get("Null")) ? "NULL " : "NOT NULL ");
        if (columnInfo.get("Default") != null && !"".equals(columnInfo.get("Default"))) {
            sb.append("DEFAULT '").append(columnInfo.get("Default")).append("' ");
        }
        if (columnInfo.get("Extra") != null && !"".equals(columnInfo.get("Extra"))) {
            sb.append(columnInfo.get("Extra")).append(" ");
        }
        // Key 处理可选
        return sb.toString().trim();
    }
} 