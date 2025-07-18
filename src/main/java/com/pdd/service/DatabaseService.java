package com.pdd.service;

import com.pdd.common.Result;
import java.util.Map;
import java.util.List;

public interface DatabaseService {
    Result<?> addColumn(String tableName, Map<String, Object> columnInfo);
    Result<?> updateColumn(String tableName, Map<String, Object> columnInfo);
    Result<?> deleteColumn(String tableName, Map<String, Object> columnInfo);
    Map<String, Object> createTable(String tableName, String description, List<Map<String, Object>> fields);
} 