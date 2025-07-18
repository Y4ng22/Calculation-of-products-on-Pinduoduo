package com.pdd.service;

import com.pdd.common.Result;
import java.util.Map;

public interface DatabaseService {
    Result<?> addColumn(String tableName, Map<String, Object> columnInfo);
    Result<?> updateColumn(String tableName, Map<String, Object> columnInfo);
    Result<?> deleteColumn(String tableName, String fieldName);
} 