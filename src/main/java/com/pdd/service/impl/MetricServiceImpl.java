package com.pdd.service.impl;

import com.pdd.model.Metric;
import com.pdd.model.MetricHistory;
import com.pdd.model.Product;
import com.pdd.repository.MetricRepository;
import com.pdd.repository.MetricHistoryRepository;
import com.pdd.repository.ProductRepository;
import com.pdd.service.MetricService;
import com.pdd.service.ScheduleService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;
import javax.sql.DataSource;

@Service
public class MetricServiceImpl implements MetricService {

    @Autowired
    private MetricRepository metricRepository;
    
    @Autowired
    private MetricHistoryRepository metricHistoryRepository;
    
    @Autowired
    private ScheduleService scheduleService;
    
    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private DataSource dataSource;
    
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Map<String, Object> getMetricsByPage(int page, int size) {
        int offset = (page - 1) * size;
        List<Metric> records = metricRepository.findByPage(offset, size);
        int total = metricRepository.getTotalCount();
        
        Map<String, Object> result = new HashMap<>();
        result.put("records", records);
        result.put("total", total);
        result.put("page", page);
        result.put("size", size);
        result.put("pages", (int) Math.ceil((double) total / size));
        
        return result;
    }

    @Override
    public Metric getMetricById(Long id) {
        return metricRepository.findById(id);
    }

    @Override
    public Metric createMetric(Metric metric) {
        metric.setCreatedAt(LocalDateTime.now());
        metric.setUpdatedAt(LocalDateTime.now());
        metricRepository.insert(metric);
        return metric;
    }

    @Override
    public Metric updateMetric(Metric metric) {
        Metric existingMetric = metricRepository.findById(metric.getId());
        if (existingMetric != null) {
            metric.setUpdatedAt(LocalDateTime.now());
            metricRepository.update(metric);
            return metric;
        }
        throw new RuntimeException("指标不存在");
    }

    @Override
    public void deleteMetric(Long id) {
        Metric existingMetric = metricRepository.findById(id);
        if (existingMetric != null) {
            metricRepository.deleteById(id);
        } else {
            throw new RuntimeException("指标不存在");
        }
    }

    @Override
    public void updateMetricStatus(Long id, Boolean enabled) {
        Metric metric = metricRepository.findById(id);
        if (metric != null) {
            metricRepository.updateStatus(id, enabled);
        } else {
            throw new RuntimeException("指标不存在");
        }
    }

    @Override
    @Transactional
    public Map<String, Object> executeMetric(Metric metric) {
        Map<String, Object> result = new HashMap<>();
        long startTime = System.currentTimeMillis();
        
        try {
            // 更新最后运行时间
            metric.setLastRun(LocalDateTime.now());
            
            if ("api".equals(metric.getType())) {
                result = executeApiMetric(metric);
            } else if ("database".equals(metric.getType())) {
                result = executeDatabaseMetric(metric);
            }
            
            metric.setLastResult("success");
            metric.setLastError(null);
            
        } catch (Exception e) {
            metric.setLastResult("error");
            metric.setLastError(e.getMessage());
            result.put("error", e.getMessage());
        }
        
        // 计算执行耗时
        long endTime = System.currentTimeMillis();
        int executionDuration = (int) (endTime - startTime);
        
        // 保存历史记录 - 使用单独的事务确保数据保存
        try {
            MetricHistory history = new MetricHistory();
            history.setMetricId(metric.getId());
            history.setExecuteTime(metric.getLastRun());
            history.setResult(metric.getLastResult());
            history.setErrorMessage(metric.getLastError());
            history.setExecutionDuration(executionDuration);
            
            // 将结果转换为JSON格式保存
            String dataJson = objectMapper.writeValueAsString(result);
            history.setData(dataJson);
            
            System.out.println("=== 开始保存历史记录 ===");
            System.out.println("MetricId: " + metric.getId());
            System.out.println("Result: " + metric.getLastResult());
            System.out.println("ExecuteTime: " + metric.getLastRun());
            System.out.println("DataJson长度: " + (dataJson != null ? dataJson.length() : 0));
            
            // 执行插入操作
            int insertResult = metricHistoryRepository.insert(history);
            
            System.out.println("插入结果: " + insertResult);
            System.out.println("生成的历史记录ID: " + history.getId());
            System.out.println("=== 历史记录保存完成 ===");
            
            // 验证是否真的插入成功
            if (history.getId() != null) {
                MetricHistory saved = metricHistoryRepository.findById(history.getId());
                System.out.println("验证查询结果: " + (saved != null ? "找到记录" : "未找到记录"));
            }
            
        } catch (Exception e) {
            // 记录历史失败不应该影响主流程
            System.err.println("=== 保存指标历史记录失败 ===");
            System.err.println("错误消息: " + e.getMessage());
            System.err.println("错误类型: " + e.getClass().getSimpleName());
            e.printStackTrace();
        }
        
        metric.setUpdatedAt(LocalDateTime.now());
        metricRepository.update(metric);
        
        return result;
    }

    private Map<String, Object> executeApiMetric(Metric metric) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            String apiUrl = metric.getApiUrl();
            
            // 实际调用API
            ResponseEntity<String> response = restTemplate.getForEntity(apiUrl + "?page=1&size=20", String.class);
            
            if (response.getStatusCode().is2xxSuccessful()) {
                String responseBody = response.getBody();
                
                // 解析API响应
                @SuppressWarnings("unchecked")
                Map<String, Object> apiResponse = objectMapper.readValue(responseBody, Map.class);
                
                result.put("api_response_status", "success");
                result.put("api_url", apiUrl);
                result.put("response_time", LocalDateTime.now().toString());
                
                // 解析商品数据
                List<Product> products = parseProductsFromApiResponse(apiResponse);
                result.put("records_fetched", products.size());
                
                // 写入数据库
                if (!products.isEmpty()) {
                    int savedCount = productRepository.batchInsert(products);
                    result.put("database_write", "success");
                    result.put("records_saved", savedCount);
                    result.put("target_table", "products");
                } else {
                    result.put("database_write", "no_data");
                    result.put("records_saved", 0);
                }
                
                result.put("summary", String.format("成功拉取 %d 条商品数据并写入数据库", products.size()));
                
            } else {
                throw new RuntimeException("API响应状态异常: " + response.getStatusCode());
            }
            
        } catch (Exception e) {
            throw new RuntimeException("API调用失败: " + e.getMessage());
        }
        
        return result;
    }
    
    /**
     * 从API响应中解析商品数据
     */
    @SuppressWarnings("unchecked")
    private List<Product> parseProductsFromApiResponse(Map<String, Object> apiResponse) {
        List<Product> products = new ArrayList<>();
        
        try {
            // 根据模拟API的响应格式解析数据
            Map<String, Object> data = (Map<String, Object>) apiResponse.get("data");
            if (data != null) {
                List<Map<String, Object>> goodsList = (List<Map<String, Object>>) data.get("goods_list");
                
                if (goodsList != null) {
                    for (Map<String, Object> goodsData : goodsList) {
                        Product product = new Product();
                        
                        // 解析字段并转换类型
                        product.setGoodId(String.valueOf(goodsData.get("good_id")));
                        product.setName(String.valueOf(goodsData.get("name")));
                        
                        // 处理价格字段 - 可能是Double或BigDecimal
                        Object costObj = goodsData.get("cost");
                        if (costObj != null) {
                            if (costObj instanceof BigDecimal) {
                                product.setCost((BigDecimal) costObj);
                            } else {
                                product.setCost(new BigDecimal(costObj.toString()));
                            }
                        }
                        
                        Object priceObj = goodsData.get("price");
                        if (priceObj != null) {
                            if (priceObj instanceof BigDecimal) {
                                product.setPrice((BigDecimal) priceObj);
                            } else {
                                product.setPrice(new BigDecimal(priceObj.toString()));
                            }
                        }
                        
                        // 处理销量字段
                        Object salesObj = goodsData.get("sales");
                        if (salesObj != null) {
                            if (salesObj instanceof Long) {
                                product.setSales((Long) salesObj);
                            } else {
                                product.setSales(Long.valueOf(salesObj.toString()));
                            }
                        }
                        
                        product.setCategory(String.valueOf(goodsData.get("category")));
                        product.setState(String.valueOf(goodsData.get("state")));
                        product.setImage(String.valueOf(goodsData.get("image")));
                        product.setDescription(String.valueOf(goodsData.get("description")));
                        
                        products.add(product);
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("解析API响应数据失败: " + e.getMessage());
        }
        
        return products;
    }

    private Map<String, Object> executeDatabaseMetric(Metric metric) {
        Map<String, Object> result = new HashMap<>();
        long startTime = System.currentTimeMillis();
        String sql = null; // 将sql声明移到外面
        
        try {
            if (metric.getCustomSql() != null && !metric.getCustomSql().trim().isEmpty()) {
                sql = metric.getCustomSql().trim();
                
                // 🔧 安全检查：防止自定义SQL被错误设置为聚合方法
                if (sql.equals("求和") || sql.equals("求总") || sql.equals("求总售价") || 
                    sql.equals("计数") || sql.equals("平均值") || sql.equals("最大值") || sql.equals("最小值") ||
                    sql.equals("SUM") || sql.equals("COUNT") || sql.equals("AVG") || sql.equals("MAX") || sql.equals("MIN")) {
                    // 自动切换为构建SQL模式
                    sql = buildSqlQuery(metric);
                }
            } else {
                // 构建SQL语句
                sql = buildSqlQuery(metric);
            }
            
            // 执行真实的SQL查询
            Object queryResult = executeRealSqlQuery(sql);
            long endTime = System.currentTimeMillis();
            
            result.put("sql_executed", sql);
            result.put("query_result", queryResult);
            result.put("execution_time", LocalDateTime.now().toString());
            result.put("execution_duration_ms", endTime - startTime);
            
            // 如果启用了利润率监控
            if (Boolean.TRUE.equals(metric.getEnableProfitAlert()) && metric.getProfitThreshold() != null) {
                checkProfitAlert(metric, result, result);
            }
            
        } catch (Exception e) {
            throw new RuntimeException("数据库查询失败: " + e.getMessage());
        }
        
        return result;
    }
    
    /**
     * 执行真实的SQL查询
     */
    private Object executeRealSqlQuery(String sql) throws SQLException {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            
            // 判断SQL类型
            String sqlType = sql.trim().toUpperCase();
            
            if (sqlType.startsWith("SELECT")) {
                // SELECT查询语句
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        // 获取第一列的值作为结果
                        return rs.getObject(1);
                    }
                    return 0;
                }
            } else if (sqlType.startsWith("INSERT") || 
                      sqlType.startsWith("UPDATE") || 
                      sqlType.startsWith("DELETE")) {
                // INSERT/UPDATE/DELETE语句
                int affectedRows = stmt.executeUpdate();
                return affectedRows;
            } else {
                // 其他语句（如DDL）
                boolean hasResultSet = stmt.execute();
                if (hasResultSet) {
                    try (ResultSet rs = stmt.getResultSet()) {
                        if (rs.next()) {
                            return rs.getObject(1);
                        }
                    }
                } else {
                    // 返回影响的行数
                    return stmt.getUpdateCount();
                }
                return 0;
            }
        }
    }

    private String buildSqlQuery(Metric metric) {
        StringBuilder sql = new StringBuilder();
        
        if (metric.getAggregateMethod() != null && metric.getFieldName() != null) {
            // 将中文聚合方法转换为标准SQL函数名
            String sqlFunction = getStandardSqlFunction(metric.getAggregateMethod());
            sql.append("SELECT ").append(sqlFunction)
                .append("(").append(metric.getFieldName()).append(") as result ");
        } else {
            sql.append("SELECT COUNT(*) as result ");
        }
        
        sql.append("FROM ").append(metric.getSourceTableName());
        
        return sql.toString();
    }
    
    /**
     * 获取标准SQL函数名（处理中文到英文的映射）
     */
    private String getStandardSqlFunction(String method) {
        if (method == null) {
            return "COUNT";
        }
        
        String trimmedMethod = method.trim();
        
        // 处理中文到英文的映射
        switch (trimmedMethod) {
            case "求和":
            case "求总":
            case "求总售价":
            case "求和(SUM)":
            case "SUM":
                return "SUM";
            case "计数":
            case "计数(COUNT)":
            case "COUNT":
                return "COUNT";
            case "平均值":
            case "平均值(AVG)":
            case "AVG":
                return "AVG";
            case "最大值":
            case "最大值(MAX)":
            case "MAX":
                return "MAX";
            case "最小值":
            case "最小值(MIN)":
            case "MIN":
                return "MIN";
            default:
                // 如果是未知的方法，尝试转为大写，如果不是标准SQL函数则默认为COUNT
                String upperMethod = trimmedMethod.toUpperCase();
                
                if (upperMethod.equals("SUM") || upperMethod.equals("COUNT") || 
                    upperMethod.equals("AVG") || upperMethod.equals("MAX") || 
                    upperMethod.equals("MIN")) {
                    return upperMethod;
                } else {
                    return "COUNT";
                }
        }
    }

    private void checkProfitAlert(Metric metric, Map<String, Object> queryResult, Map<String, Object> result) {
        // 利润率监控和警告逻辑
        Object queryValue = queryResult.get("query_result");
        if (queryValue instanceof Number) {
            double value = ((Number) queryValue).doubleValue();
            
            // 检查查询结果是否低于设定的利润率阈值
            if (value < metric.getProfitThreshold()) {
                result.put("profit_alert", true);
                result.put("alert_message", 
                    String.format("指标值 %.2f 低于利润率阈值 %.2f，请关注！", 
                                value, metric.getProfitThreshold()));
                
                // 更新指标状态为警告
                metric.setLastResult("warning");
            } else {
                result.put("profit_alert", false);
                result.put("alert_message", 
                    String.format("指标值 %.2f 正常，高于阈值 %.2f", 
                                value, metric.getProfitThreshold()));
            }
        }
    }

    @Override
    public Map<String, Object> getMetricHistory(Long id, int page, int size) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            int offset = (page - 1) * size;
            List<MetricHistory> historyRecords = metricHistoryRepository.findByMetricId(id, offset, size);
            int total = metricHistoryRepository.countByMetricId(id);
            
            // 转换为前端需要的格式
            List<Map<String, Object>> records = new ArrayList<>();
            for (MetricHistory history : historyRecords) {
                Map<String, Object> record = new HashMap<>();
                record.put("id", history.getId());
                record.put("metricId", history.getMetricId());
                record.put("executeTime", history.getExecuteTime());
                record.put("result", history.getResult());
                record.put("errorMessage", history.getErrorMessage());
                record.put("executionDuration", history.getExecutionDuration());
                
                // 解析JSON格式的详细数据
                try {
                    if (history.getData() != null && !history.getData().trim().isEmpty()) {
                        @SuppressWarnings("unchecked")
                        Map<String, Object> dataMap = objectMapper.readValue(history.getData(), Map.class);
                        record.put("detailData", dataMap);
                        
                        // 生成摘要信息
                        String summary = generateSummary(dataMap, history.getResult());
                        record.put("summary", summary);
                    }
                } catch (Exception e) {
                    record.put("summary", "数据解析失败: " + e.getMessage());
                }
                
                records.add(record);
            }
            
            result.put("records", records);
            result.put("total", total);
            result.put("page", page);
            result.put("size", size);
            result.put("pages", (int) Math.ceil((double) total / size));
            
        } catch (Exception e) {
            result.put("error", "获取历史记录失败: " + e.getMessage());
            result.put("records", new ArrayList<>());
            result.put("total", 0);
        }
        
        return result;
    }
    
    /**
     * 根据执行结果生成摘要信息
     */
    private String generateSummary(Map<String, Object> dataMap, String result) {
        if ("error".equals(result)) {
            return "执行失败: " + dataMap.getOrDefault("error", "未知错误");
        }
        
        StringBuilder summary = new StringBuilder();
        
        // API指标摘要
        if (dataMap.containsKey("records_fetched")) {
            summary.append("拉取了 ").append(dataMap.get("records_fetched")).append(" 条数据");
            if (dataMap.containsKey("records_saved")) {
                summary.append("，保存了 ").append(dataMap.get("records_saved")).append(" 条到数据库");
            }
        }
        
        // 数据库指标摘要
        if (dataMap.containsKey("query_result")) {
            summary.append("查询结果: ").append(dataMap.get("query_result"));
        }
        
        // 利润率警告
        if (dataMap.containsKey("profit_alert") && Boolean.TRUE.equals(dataMap.get("profit_alert"))) {
            summary.append("，").append(dataMap.getOrDefault("alert_message", "触发利润率警告"));
        }
        
        return summary.length() > 0 ? summary.toString() : "执行成功";
    }

    @Override
    public Map<String, Object> getMetricsStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("total", metricRepository.getTotalCount());
        stats.put("api_count", metricRepository.countByType("api"));
        stats.put("database_count", metricRepository.countByType("database"));
        stats.put("enabled_count", metricRepository.countByEnabled(true));
        stats.put("error_count", metricRepository.countByLastResult("error"));
        stats.put("warning_count", metricRepository.countByLastResult("warning"));
        
        return stats;
    }

    @Override
    public int batchUpdateStatus(List<Long> ids, Boolean enabled) {
        int count = 0;
        for (Long id : ids) {
            Metric metric = metricRepository.findById(id);
            if (metric != null) {
                metricRepository.updateStatus(id, enabled);
                count++;
            }
        }
        return count;
    }

    @Override
    public Map<String, Object> testApiConnection(String apiUrl, String clientId, String clientSecret, String accessToken) {
        Map<String, Object> result = new HashMap<>();
        long startTime = System.currentTimeMillis();
        
        try {
            // 构建测试API URL，添加必要的参数
            String testUrl = apiUrl;
            if (!testUrl.contains("?")) {
                testUrl += "?test=1";
            } else {
                testUrl += "&test=1";
            }
            
            // 执行真实的API连接测试
            ResponseEntity<String> response = restTemplate.getForEntity(testUrl, String.class);
            long endTime = System.currentTimeMillis();
            
            if (response.getStatusCode().is2xxSuccessful()) {
                result.put("status", "success");
                result.put("message", "API连接测试成功");
                result.put("response_time_ms", endTime - startTime);
                result.put("http_status", response.getStatusCode().value());
                result.put("test_time", LocalDateTime.now().toString());
                
                // 尝试解析响应内容
                String responseBody = response.getBody();
                if (responseBody != null && responseBody.length() < 1000) {
                    result.put("response_preview", responseBody);
                }
            } else {
                result.put("status", "error");
                result.put("message", "API返回错误状态: " + response.getStatusCode());
                result.put("http_status", response.getStatusCode().value());
            }
            
        } catch (Exception e) {
            long endTime = System.currentTimeMillis();
            result.put("status", "error");
            result.put("message", "API连接测试失败: " + e.getMessage());
            result.put("response_time_ms", endTime - startTime);
            result.put("test_time", LocalDateTime.now().toString());
        }
        
        return result;
    }

    @Override
    public Map<String, Object> testDatabaseConnection(String dbHost, String dbName, String username, String password) {
        Map<String, Object> result = new HashMap<>();
        long startTime = System.currentTimeMillis();
        
        try {
            // 构建数据库连接URL
            String url = String.format("jdbc:mysql://%s/%s?useSSL=false&serverTimezone=UTC&characterEncoding=utf8", 
                                     dbHost, dbName);
            
            // 测试数据库连接
            try (Connection connection = DriverManager.getConnection(url, username, password)) {
                // 执行简单查询测试连接
                try (PreparedStatement stmt = connection.prepareStatement("SELECT 1");
                     ResultSet rs = stmt.executeQuery()) {
                    
                    if (rs.next()) {
                        long endTime = System.currentTimeMillis();
                        result.put("status", "success");
                        result.put("message", "数据库连接测试成功");
                        result.put("test_time", LocalDateTime.now().toString());
                        result.put("response_time_ms", endTime - startTime);
                        result.put("database_version", connection.getMetaData().getDatabaseProductVersion());
                    }
                }
            }
            
        } catch (Exception e) {
            long endTime = System.currentTimeMillis();
            result.put("status", "error");
            result.put("message", "数据库连接测试失败: " + e.getMessage());
            result.put("test_time", LocalDateTime.now().toString());
            result.put("response_time_ms", endTime - startTime);
        }
        
        return result;
    }

    @Override
    public Map<String, Object> previewSqlQuery(String dbHost, String dbName, String sql, String username, String password) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 构建数据库连接URL
            String url = String.format("jdbc:mysql://%s/%s?useSSL=false&serverTimezone=UTC&characterEncoding=utf8", 
                                     dbHost, dbName);
            
            List<Map<String, Object>> rows = new ArrayList<>();
            List<String> columns = new ArrayList<>();
            
            // 执行真实的SQL查询预览
            try (Connection connection = DriverManager.getConnection(url, username, password);
                 PreparedStatement stmt = connection.prepareStatement(sql)) {
                
                // 判断SQL类型
                String sqlType = sql.trim().toUpperCase();
                
                if (sqlType.startsWith("SELECT")) {
                    // SELECT查询语句 - 返回数据预览
                    stmt.setMaxRows(10); // 限制查询结果数量
                    
                    try (ResultSet rs = stmt.executeQuery()) {
                        ResultSetMetaData metaData = rs.getMetaData();
                        int columnCount = metaData.getColumnCount();
                        
                        // 获取列名
                        for (int i = 1; i <= columnCount; i++) {
                            columns.add(metaData.getColumnName(i));
                        }
                        
                        // 获取数据行
                        while (rs.next()) {
                            Map<String, Object> row = new HashMap<>();
                            for (int i = 1; i <= columnCount; i++) {
                                Object value = rs.getObject(i);
                                row.put(metaData.getColumnName(i), value);
                            }
                            rows.add(row);
                        }
                    }
                } else if (sqlType.startsWith("INSERT") || 
                          sqlType.startsWith("UPDATE") || 
                          sqlType.startsWith("DELETE")) {
                    // 非查询语句 - 返回执行结果信息
                    int affectedRows = stmt.executeUpdate();
                    
                    columns.add("affected_rows");
                    columns.add("operation_type");
                    
                    Map<String, Object> row = new HashMap<>();
                    row.put("affected_rows", affectedRows);
                    row.put("operation_type", sqlType.split("\\s+")[0]);
                    rows.add(row);
                } else {
                    // 其他语句（如DDL）
                    boolean hasResultSet = stmt.execute();
                    
                    if (hasResultSet) {
                        try (ResultSet rs = stmt.getResultSet()) {
                            ResultSetMetaData metaData = rs.getMetaData();
                            int columnCount = metaData.getColumnCount();
                            
                            for (int i = 1; i <= columnCount; i++) {
                                columns.add(metaData.getColumnName(i));
                            }
                            
                            while (rs.next() && rows.size() < 10) {
                                Map<String, Object> row = new HashMap<>();
                                for (int i = 1; i <= columnCount; i++) {
                                    Object value = rs.getObject(i);
                                    row.put(metaData.getColumnName(i), value);
                                }
                                rows.add(row);
                            }
                        }
                    } else {
                        // DDL语句执行结果
                        int updateCount = stmt.getUpdateCount();
                        columns.add("execution_result");
                        
                        Map<String, Object> row = new HashMap<>();
                        row.put("execution_result", updateCount >= 0 ? "SUCCESS" : "COMPLETED");
                        rows.add(row);
                    }
                }
            }
            
            result.put("status", "success");
            result.put("columns", columns);
            result.put("rows", rows);
            result.put("preview_count", rows.size());
            result.put("sql_executed", sql);
            
        } catch (Exception e) {
            result.put("status", "error");
            result.put("message", "SQL预览失败: " + e.getMessage());
            result.put("sql_executed", sql);
        }
        
        return result;
    }

    @Override
    public Map<String, Object> testHistoryTable() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 1. 测试能否查询历史记录表
            List<MetricHistory> testQuery = metricHistoryRepository.findByMetricId(1L, 0, 1);
            result.put("table_exists", true);
            result.put("query_success", true);
            result.put("query_count", testQuery.size());
            
            // 2. 测试能否插入一条测试记录
            MetricHistory testHistory = new MetricHistory();
            testHistory.setMetricId(999999L); // 使用一个不存在的metric_id进行测试
            testHistory.setResult("test");
            testHistory.setData("{\"test\": \"data\"}");
            testHistory.setExecutionDuration(100);
            testHistory.setExecuteTime(LocalDateTime.now());
            testHistory.setCreatedAt(LocalDateTime.now());
            
            int insertResult = metricHistoryRepository.insert(testHistory);
            result.put("insert_success", insertResult > 0);
            result.put("insert_result", insertResult);
            result.put("test_history_id", testHistory.getId());
            
            // 3. 删除测试记录
            if (testHistory.getId() != null) {
                metricHistoryRepository.deleteById(testHistory.getId());
                result.put("cleanup_success", true);
            }
            
            result.put("status", "success");
            result.put("message", "历史记录表测试成功");
            
        } catch (Exception e) {
            result.put("status", "error");
            result.put("message", "历史记录表测试失败: " + e.getMessage());
            result.put("error_type", e.getClass().getSimpleName());
            result.put("table_exists", false);
            
            // 打印详细错误信息
            e.printStackTrace();
        }
        
        return result;
    }
    
    @Override
    public void startMetricSchedule(Long metricId) {
        if (metricId == null) {
            throw new IllegalArgumentException("指标ID不能为空");
        }
        
        Metric metric = getMetricById(metricId);
        if (metric == null) {
            throw new IllegalArgumentException("指标不存在: " + metricId);
        }
        
        if (!metric.getEnabled()) {
            throw new IllegalStateException("指标已禁用，无法启动调度: " + metric.getName());
        }
        
        if (metric.getSchedule() == null || metric.getSchedule().trim().isEmpty()) {
            throw new IllegalStateException("指标未配置调度周期: " + metric.getName());
        }
        
        try {
            scheduleService.startScheduledTask(metric);
            System.out.println("成功启动指标调度: " + metric.getName() + ", 周期: " + metric.getSchedule());
        } catch (Exception e) {
            System.err.println("启动指标调度失败: " + e.getMessage());
            throw new RuntimeException("启动调度失败: " + e.getMessage(), e);
        }
    }
    
    @Override
    public void stopMetricSchedule(Long metricId) {
        if (metricId == null) {
            throw new IllegalArgumentException("指标ID不能为空");
        }
        
        try {
            scheduleService.stopScheduledTask(metricId);
            System.out.println("成功停止指标调度: " + metricId);
        } catch (Exception e) {
            System.err.println("停止指标调度失败: " + e.getMessage());
            throw new RuntimeException("停止调度失败: " + e.getMessage(), e);
        }
    }
    
    @Override
    public boolean isMetricScheduled(Long metricId) {
        if (metricId == null) {
            return false;
        }
        
        return scheduleService.isScheduled(metricId);
    }
    
    @Override
    public Map<String, Object> getScheduleStatus(Long metricId) {
        Map<String, Object> status = new HashMap<>();
        
        if (metricId == null) {
            status.put("error", "指标ID不能为空");
            return status;
        }
        
        try {
            Metric metric = getMetricById(metricId);
            if (metric == null) {
                status.put("error", "指标不存在");
                return status;
            }
            
            status.put("metricId", metricId);
            status.put("metricName", metric.getName());
            status.put("schedule", metric.getSchedule());
            status.put("enabled", metric.getEnabled());
            status.put("scheduled", scheduleService.isScheduled(metricId));
            
            String nextExecutionTime = scheduleService.getNextExecutionTime(metricId);
            if (nextExecutionTime != null) {
                status.put("nextExecutionTime", nextExecutionTime);
            }
            
            // 获取所有正在调度的指标数量
            Set<Long> activeSchedules = scheduleService.getActiveScheduledMetrics();
            status.put("totalActiveSchedules", activeSchedules.size());
            
            // 添加调度周期的可读描述
            String scheduleDescription = getScheduleDescription(metric.getSchedule());
            status.put("scheduleDescription", scheduleDescription);
            
        } catch (Exception e) {
            status.put("error", "获取调度状态失败: " + e.getMessage());
            System.err.println("获取调度状态失败: " + e.getMessage());
        }
        
        return status;
    }
    
    /**
     * 获取调度周期的可读描述
     */
    private String getScheduleDescription(String schedule) {
        if (schedule == null) return "未配置";
        
        switch (schedule.toLowerCase()) {
            case "5min":
                return "每5分钟执行一次";
            case "15min":
                return "每15分钟执行一次";
            case "30min":
                return "每30分钟执行一次";
            case "1hour":
                return "每小时执行一次";
            case "1day":
                return "每天执行一次";
            case "1week":
                return "每周执行一次";
            default:
                return "自定义周期: " + schedule;
        }
    }
} 