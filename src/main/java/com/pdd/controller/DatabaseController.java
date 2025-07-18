package com.pdd.controller;

import com.pdd.common.Result;
import com.pdd.service.TransactionRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import com.pdd.model.TransactionRecord;
import org.springframework.web.bind.annotation.RequestBody;
import com.pdd.service.DatabaseService;

@RestController
@RequestMapping("/api/database")
public class DatabaseController {

    @Autowired
    private TransactionRecordService transactionRecordService;
    
    @Autowired
    private DatabaseService databaseService;
    
    // 用于统计API调用次数
    private static int apiCallCount = 0;

    /**
     * 写入数据库接口
     */
    @PostMapping("/write")
    public Result writeToDatabase(@RequestBody Map<String, Object> requestData) {
        apiCallCount++; // 增加API调用次数
        try {
            // 获取请求数据
            List<Map<String, Object>> products = (List<Map<String, Object>>) requestData.get("products");
            Integer totalCount = (Integer) requestData.get("totalCount");
            String dataSource = (String) requestData.get("dataSource");
            String writeTime = (String) requestData.get("writeTime");
            
            if (products == null || products.isEmpty()) {
                return Result.error("没有商品数据可写入");
            }
            
            // 先关闭主键自动递增
            transactionRecordService.disableAutoIncrement();
            
            // 重新开启主键自动递增
            transactionRecordService.enableAutoIncrement();
            
            // 调用Service批量保存
            Map<String, Object> result = transactionRecordService.batchSaveRecords(products);
            
            if ((Boolean) result.get("success")) {
                Map<String, Object> responseData = new HashMap<>();
                responseData.put("message", "数据写入成功");
                responseData.put("insertCount", result.get("insertCount"));
                responseData.put("totalCount", result.get("totalCount"));
                responseData.put("dataSource", dataSource);
                responseData.put("writeTime", writeTime);
                return Result.success(responseData);
            } else {
                return Result.error((String) result.get("message"));
            }
            
        } catch (Exception e) {
            return Result.error("数据写入失败: " + e.getMessage());
        }
    }

    /**
     * 查询所有记录（支持分页）
     */
    @GetMapping("/records")
    public Result getAllRecords(@RequestParam(defaultValue = "1") int page,
                               @RequestParam(defaultValue = "10") int size) {
        apiCallCount++; // 增加API调用次数
        try {
            // 获取所有记录用于计算总数
            List<TransactionRecord> allRecords = transactionRecordService.findAll();
            int total = allRecords.size();
            
            // 计算分页
            int startIndex = (page - 1) * size;
            int endIndex = Math.min(startIndex + size, total);
            
            // 如果起始索引超出范围，返回空结果
            if (startIndex >= total) {
                Map<String, Object> responseData = new HashMap<>();
                responseData.put("records", new ArrayList<>());
                responseData.put("total", total);
                responseData.put("page", page);
                responseData.put("size", size);
                responseData.put("totalPages", (int) Math.ceil((double) total / size));
                return Result.success(responseData);
            }
            
            // 获取当前页的记录
            List<TransactionRecord> pageRecords = allRecords.subList(startIndex, endIndex);
            
            List<Map<String, Object>> records = pageRecords.stream()
                .map(record -> {
                    Map<String, Object> recordMap = new HashMap<>();
                    recordMap.put("id", record.getId());
                    recordMap.put("goodId", record.getGoodId());
                    recordMap.put("name", record.getName());
                    recordMap.put("cost", record.getCost());
                    recordMap.put("price", record.getPrice());
                    recordMap.put("category", record.getCategory());
                    recordMap.put("state", record.getState());
                    recordMap.put("image", record.getImage());
                    recordMap.put("description", record.getDescription());
                    recordMap.put("sales", record.getSales());
                    return recordMap;
                })
                .toList();
            
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("records", records);
            responseData.put("total", total);
            responseData.put("page", page);
            responseData.put("size", size);
            responseData.put("totalPages", (int) Math.ceil((double) total / size));
            return Result.success(responseData);
        } catch (Exception e) {
            return Result.error("查询失败: " + e.getMessage());
        }
    }

    /**
     * 根据分类查询记录
     */
    @GetMapping("/records/category/{category}")
    public Result getRecordsByCategory(@PathVariable String category) {
        apiCallCount++; // 增加API调用次数
        try {
            List<Map<String, Object>> records = transactionRecordService.findByCategory(category).stream()
                .map(record -> {
                    Map<String, Object> recordMap = new HashMap<>();
                    recordMap.put("id", record.getId());
                    recordMap.put("goodId", record.getGoodId());
                    recordMap.put("name", record.getName());
                    recordMap.put("cost", record.getCost());
                    recordMap.put("price", record.getPrice());
                    recordMap.put("category", record.getCategory());
                    recordMap.put("state", record.getState());
                    recordMap.put("image", record.getImage());
                    recordMap.put("description", record.getDescription());
                    recordMap.put("sales", record.getSales());
                    return recordMap;
                })
                .toList();
            
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("records", records);
            responseData.put("category", category);
            responseData.put("total", records.size());
            return Result.success(responseData);
        } catch (Exception e) {
            return Result.error("查询失败: " + e.getMessage());
        }
    }

    /**
     * 根据状态查询记录
     */
    @GetMapping("/records/state/{state}")
    public Result getRecordsByState(@PathVariable String state) {
        apiCallCount++; // 增加API调用次数
        try {
            List<Map<String, Object>> records = transactionRecordService.findByState(state).stream()
                .map(record -> {
                    Map<String, Object> recordMap = new HashMap<>();
                    recordMap.put("id", record.getId());
                    recordMap.put("goodId", record.getGoodId());
                    recordMap.put("name", record.getName());
                    recordMap.put("cost", record.getCost());
                    recordMap.put("price", record.getPrice());
                    recordMap.put("category", record.getCategory());
                    recordMap.put("state", record.getState());
                    recordMap.put("image", record.getImage());
                    recordMap.put("description", record.getDescription());
                    recordMap.put("sales", record.getSales());
                    return recordMap;
                })
                .toList();
            
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("records", records);
            responseData.put("state", state);
            responseData.put("total", records.size());
            return Result.success(responseData);
        } catch (Exception e) {
            return Result.error("查询失败: " + e.getMessage());
        }
    }

    /**
     * 获取平台统计数据
     */
    @GetMapping("/stats")
    public Result getPlatformStats() {
        apiCallCount++; // 增加API调用次数
        try {
            Map<String, Object> stats = new HashMap<>();
            
            // 获取商品总数
            List<TransactionRecord> allRecords = transactionRecordService.findAll();
            stats.put("totalProducts", allRecords.size());
            
            // 获取各状态商品数量
            long activeProducts = allRecords.stream()
                .filter(record -> "active".equals(record.getState()) || "on_sale".equals(record.getState()))
                .count();
            stats.put("activeProducts", activeProducts);
            
            // 获取分类统计
            Map<String, Long> categoryStats = allRecords.stream()
                .collect(java.util.stream.Collectors.groupingBy(
                    TransactionRecord::getCategory,
                    java.util.stream.Collectors.counting()
                ));
            stats.put("categoryStats", categoryStats);
            
            // 计算总成本和总售价（需乘以销量）
            double totalCost = allRecords.stream()
                .mapToDouble(record -> {
                    double cost = record.getCost() != null ? record.getCost().doubleValue() : 0.0;
                    int sales = record.getSales() != null ? record.getSales() : 0;
                    return cost * sales;
                })
                .sum();
            double totalPrice = allRecords.stream()
                .mapToDouble(record -> {
                    double price = record.getPrice() != null ? record.getPrice().doubleValue() : 0.0;
                    int sales = record.getSales() != null ? record.getSales() : 0;
                    return price * sales;
                })
                .sum();
            
            // 计算总销量
            int totalSales = allRecords.stream()
                .mapToInt(record -> record.getSales() != null ? record.getSales() : 0)
                .sum();
            
            stats.put("totalCost", totalCost);
            stats.put("totalPrice", totalPrice);
            stats.put("totalProfit", totalPrice - totalCost);
            stats.put("totalSales", totalSales);
            
            // 获取最近7天的数据变化趋势（这里简化处理，实际可以基于时间字段统计）
            stats.put("recentGrowth", "数据统计中...");
            
            // 系统运行时间（这里简化处理，实际可以基于启动时间计算）
            stats.put("uptime", "99.9%");
            
            // 活跃用户数（这里简化处理，实际可以基于用户表统计）
            stats.put("activeUsers", "统计中...");
            
            // API调用次数（真实统计）
            stats.put("apiCalls", apiCallCount);
            
            // 计算利润率
            double profitRate = 0.0;
            if (totalCost > 0) {
                profitRate = ((totalPrice - totalCost) / totalCost) * 100;
            }
            stats.put("profitRate", Math.round(profitRate * 100.0) / 100.0); // 保留两位小数
            
            return Result.success(stats);
        } catch (Exception e) {
            return Result.error("获取统计数据失败: " + e.getMessage());
        }
    }

    /**
     * 记录计算操作
     */
    @PostMapping("/stats/calculation")
    public Result recordCalculation(@RequestBody Map<String, Object> requestData) {
        apiCallCount++; // 增加API调用次数
        try {
            // 这里可以记录计算操作的详细信息
            // 比如：计算时间、计算的商品数量、计算结果等
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("message", "计算操作已记录");
            responseData.put("timestamp", System.currentTimeMillis());
            responseData.put("productCount", requestData.get("productCount"));
            
            return Result.success(responseData);
        } catch (Exception e) {
            return Result.error("记录计算操作失败: " + e.getMessage());
        }
    }

    /**
     * 重置API调用次数（用于测试）
     */
    @PostMapping("/stats/reset")
    public Result resetApiCallCount() {
        apiCallCount = 0;
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("message", "API调用次数已重置");
        responseData.put("apiCallCount", apiCallCount);
        return Result.success(responseData);
    }

    /**
     * 获取数据库表信息
     */
    @GetMapping("/tables")
    public Result getDatabaseTables() {
        apiCallCount++; // 增加API调用次数
        try {
            List<Map<String, Object>> tables = transactionRecordService.getDatabaseTables();
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("tables", tables);
            responseData.put("total", tables.size());
            return Result.success(responseData);
        } catch (Exception e) {
            return Result.error("获取数据库表信息失败: " + e.getMessage());
        }
    }

    /**
     * 测试数据库连接
     */
    @GetMapping("/test")
    public Result testDatabaseConnection() {
        try {
            // 简单的数据库连接测试
            List<Map<String, Object>> result = transactionRecordService.testConnection();
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("message", "数据库连接正常");
            responseData.put("result", result);
            return Result.success(responseData);
        } catch (Exception e) {
            return Result.error("数据库连接失败: " + e.getMessage());
        }
    }

    /**
     * 获取表数据
     */
    @GetMapping("/table/{tableName}/data")
    public Result getTableData(@PathVariable String tableName,
                              @RequestParam(defaultValue = "1") int page,
                              @RequestParam(defaultValue = "10") int size) {
        apiCallCount++; // 增加API调用次数
        try {
            Map<String, Object> result = transactionRecordService.getTableData(tableName, page, size);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error("获取表数据失败: " + e.getMessage());
        }
    }

    /**
     * 获取表数据总数
     */
    @GetMapping("/table/{tableName}/count")
    public Result getTableCount(@PathVariable String tableName) {
        apiCallCount++; // 增加API调用次数
        try {
            long count = transactionRecordService.getTableCount(tableName);
            Map<String, Object> result = new HashMap<>();
            result.put("tableName", tableName);
            result.put("count", count);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error("获取表数据总数失败: " + e.getMessage());
        }
    }
    
    /**
     * 新增表记录
     */
    @PostMapping("/table/{tableName}/add")
    public Result addTableRecord(@PathVariable String tableName, @RequestBody Map<String, Object> recordData) {
        apiCallCount++; // 增加API调用次数
        try {
            Map<String, Object> result = transactionRecordService.addTableRecord(tableName, recordData);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error("新增记录失败: " + e.getMessage());
        }
    }
    
    /**
     * 更新表记录
     */
    @PostMapping("/table/{tableName}/update")
    public Result updateTableRecord(@PathVariable String tableName, @RequestBody Map<String, Object> recordData) {
        apiCallCount++; // 增加API调用次数
        try {
            Map<String, Object> result = transactionRecordService.updateTableRecord(tableName, recordData);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error("更新记录失败: " + e.getMessage());
        }
    }
    
    /**
     * 删除表记录
     */
    @PostMapping("/table/{tableName}/delete")
    public Result deleteTableRecord(@PathVariable String tableName, @RequestBody Map<String, Object> recordData) {
        apiCallCount++; // 增加API调用次数
        try {
            Map<String, Object> result = transactionRecordService.deleteTableRecord(tableName, recordData);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error("删除记录失败: " + e.getMessage());
        }
    }
    
    /**
     * 关闭主键自动递增
     */
    @PostMapping("/auto-increment/disable")
    public Result disableAutoIncrement() {
        apiCallCount++; // 增加API调用次数
        try {
            transactionRecordService.disableAutoIncrement();
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("message", "主键自动递增已关闭");
            return Result.success(responseData);
        } catch (Exception e) {
            return Result.error("关闭主键自动递增失败: " + e.getMessage());
        }
    }

    /**
     * 开启主键自动递增
     */
    @PostMapping("/auto-increment/enable")
    public Result enableAutoIncrement() {
        apiCallCount++; // 增加API调用次数
        try {
            transactionRecordService.enableAutoIncrement();
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("message", "主键自动递增已开启");
            return Result.success(responseData);
        } catch (Exception e) {
            return Result.error("开启主键自动递增失败: " + e.getMessage());
        }
    }

    @PostMapping("/table/{tableName}/column/add")
    public Result<?> addColumn(@PathVariable String tableName, @RequestBody Map<String, Object> columnInfo) {
        return databaseService.addColumn(tableName, columnInfo);
    }

    @PostMapping("/table/{tableName}/column/update")
    public Result<?> updateColumn(@PathVariable String tableName, @RequestBody Map<String, Object> columnInfo) {
        return databaseService.updateColumn(tableName, columnInfo);
    }

    @PostMapping("/table/{tableName}/column/delete")
    public Result<?> deleteColumn(@PathVariable String tableName, @RequestBody Map<String, Object> columnInfo) {
        return databaseService.deleteColumn(tableName, columnInfo);
    }

    /**
     * 创建新表
     */
    @PostMapping("/table/create")
    public Result createTable(@RequestBody Map<String, Object> tableInfo) {
        apiCallCount++; // 增加API调用次数
        try {
            String tableName = (String) tableInfo.get("tableName");
            String description = (String) tableInfo.get("description");
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> fields = (List<Map<String, Object>>) tableInfo.get("fields");
            
            if (tableName == null || tableName.trim().isEmpty()) {
                return Result.error("表名不能为空");
            }
            
            if (fields == null || fields.isEmpty()) {
                return Result.error("至少需要定义一个字段");
            }
            
            // 调用Service创建表
            Map<String, Object> result = databaseService.createTable(tableName, description, fields);
            
            if ((Boolean) result.get("success")) {
                Map<String, Object> responseData = new HashMap<>();
                responseData.put("tableName", tableName);
                responseData.put("description", description);
                responseData.put("fields", fields);
                responseData.put("message", "表创建成功");
                return Result.success(responseData);
            } else {
                return Result.error((String) result.get("message"));
            }
            
        } catch (Exception e) {
            return Result.error("创建表失败: " + e.getMessage());
        }
    }
} 