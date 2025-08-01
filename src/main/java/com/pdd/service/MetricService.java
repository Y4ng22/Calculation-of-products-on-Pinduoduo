package com.pdd.service;

import com.pdd.model.Metric;
import java.util.List;
import java.util.Map;

public interface MetricService {
    
    /**
     * 分页获取指标列表
     */
    Map<String, Object> getMetricsByPage(int page, int size);
    
    /**
     * 根据ID获取指标
     */
    Metric getMetricById(Long id);
    
    /**
     * 创建指标
     */
    Metric createMetric(Metric metric);
    
    /**
     * 更新指标
     */
    Metric updateMetric(Metric metric);
    
    /**
     * 删除指标
     */
    void deleteMetric(Long id);
    
    /**
     * 更新指标状态
     */
    void updateMetricStatus(Long id, Boolean enabled);
    
    /**
     * 执行指标
     */
    Map<String, Object> executeMetric(Metric metric);
    
    /**
     * 获取指标运行历史
     */
    Map<String, Object> getMetricHistory(Long id, int page, int size);
    
    /**
     * 获取指标统计信息
     */
    Map<String, Object> getMetricsStats();
    
    /**
     * 批量更新状态
     */
    int batchUpdateStatus(List<Long> ids, Boolean enabled);
    
    /**
     * 测试API连接
     */
    Map<String, Object> testApiConnection(String apiUrl, String clientId, String clientSecret, String accessToken);
    
    /**
     * 测试数据库连接
     */
    Map<String, Object> testDatabaseConnection(String dbHost, String dbName, String username, String password);
    
    /**
     * 预览SQL查询结果
     */
    Map<String, Object> previewSqlQuery(String dbHost, String dbName, String sql, String username, String password);
    
    /**
     * 测试历史记录表
     */
    Map<String, Object> testHistoryTable();
} 