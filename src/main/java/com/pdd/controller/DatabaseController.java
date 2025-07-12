package com.pdd.controller;

import com.pdd.common.Result;
import com.pdd.service.TransactionRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/database")
public class DatabaseController {

    @Autowired
    private TransactionRecordService transactionRecordService;

    /**
     * 写入数据库接口
     */
    @PostMapping("/write")
    public Result writeToDatabase(@RequestBody Map<String, Object> requestData) {
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
} 