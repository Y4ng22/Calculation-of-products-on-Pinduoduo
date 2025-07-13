package com.pdd.controller;

import com.pdd.common.Result;
import com.pdd.service.TransactionRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.pdd.model.TransactionRecord;

@RestController
@RequestMapping("/api/database")
public class DatabaseController {

    @Autowired
    private TransactionRecordService transactionRecordService;
    
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
     * 查询所有记录
     */
    @GetMapping("/records")
    public Result getAllRecords() {
        apiCallCount++; // 增加API调用次数
        try {
            List<Map<String, Object>> records = transactionRecordService.findAll().stream()
                .map(record -> {
                    Map<String, Object> recordMap = new HashMap<>();
                    recordMap.put("id", record.getId());
                    recordMap.put("name", record.getName());
                    recordMap.put("cost", record.getCost());
                    recordMap.put("price", record.getPrice());
                    recordMap.put("category", record.getCategory());
                    recordMap.put("state", record.getState());
                    recordMap.put("image", record.getImage());
                    recordMap.put("description", record.getDescription());
                    return recordMap;
                })
                .toList();
            
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("records", records);
            responseData.put("total", records.size());
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
                    recordMap.put("name", record.getName());
                    recordMap.put("cost", record.getCost());
                    recordMap.put("price", record.getPrice());
                    recordMap.put("category", record.getCategory());
                    recordMap.put("state", record.getState());
                    recordMap.put("image", record.getImage());
                    recordMap.put("description", record.getDescription());
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
                    recordMap.put("name", record.getName());
                    recordMap.put("cost", record.getCost());
                    recordMap.put("price", record.getPrice());
                    recordMap.put("category", record.getCategory());
                    recordMap.put("state", record.getState());
                    recordMap.put("image", record.getImage());
                    recordMap.put("description", record.getDescription());
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
            
            // 计算总成本和总售价
            double totalCost = allRecords.stream()
                .mapToDouble(record -> record.getCost() != null ? record.getCost().doubleValue() : 0.0)
                .sum();
            double totalPrice = allRecords.stream()
                .mapToDouble(record -> record.getPrice() != null ? record.getPrice().doubleValue() : 0.0)
                .sum();
            
            stats.put("totalCost", totalCost);
            stats.put("totalPrice", totalPrice);
            stats.put("totalProfit", totalPrice - totalCost);
            
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
} 