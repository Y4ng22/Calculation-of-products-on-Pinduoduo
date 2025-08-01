package com.pdd.controller;

import com.pdd.common.Result;
import com.pdd.model.Product;
import com.pdd.repository.ProductRepository;
import com.pdd.model.Metric;
import com.pdd.service.MetricService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/test")
@CrossOrigin(originPatterns = "*")
public class ApiTestController {

    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private MetricService metricService;

    /**
     * 测试模拟API数据拉取
     */
    @PostMapping("/pull-data")
    public Result testPullData() {
        try {
            // 创建一个测试用的API指标
            Metric testMetric = new Metric();
            testMetric.setName("测试商品数据拉取");
            testMetric.setType("api");
            testMetric.setApiUrl("http://localhost:8089/api/mock/pdd/goods/list");
            testMetric.setTargetDbHost("localhost:3306");
            testMetric.setTargetDbName("metrics_center");
            testMetric.setTargetTableName("products");
            
            // 执行API拉取
            Map<String, Object> result = metricService.executeMetric(testMetric);
            
            return Result.success(result);
        } catch (Exception e) {
            return Result.error("数据拉取测试失败: " + e.getMessage());
        }
    }

    /**
     * 查看当前数据库中的商品数据
     */
    @GetMapping("/products")
    public Result getProducts(@RequestParam(defaultValue = "1") int page,
                            @RequestParam(defaultValue = "10") int size) {
        try {
            int offset = (page - 1) * size;
            List<Product> products = productRepository.findByPage(offset, size);
            int total = productRepository.getTotalCount();
            
            return Result.success(Map.of(
                "products", products,
                "total", total,
                "page", page,
                "size", size
            ));
        } catch (Exception e) {
            return Result.error("查询商品数据失败: " + e.getMessage());
        }
    }

    /**
     * 清空商品表数据
     */
    @DeleteMapping("/products/clear")
    public Result clearProducts() {
        try {
            productRepository.truncateTable();
            return Result.success("商品表数据已清空");
        } catch (Exception e) {
            return Result.error("清空商品表失败: " + e.getMessage());
        }
    }

    /**
     * 获取商品统计信息
     */
    @GetMapping("/products/stats")
    public Result getProductStats() {
        try {
            int totalCount = productRepository.getTotalCount();
            
            // 按分类统计
            String[] categories = {"美妆护肤", "服装鞋帽", "家居生活", "数码电器", "食品饮料", "母婴用品", "运动户外", "图书文娱"};
            Map<String, Integer> categoryStats = new java.util.HashMap<>();
            for (String category : categories) {
                List<Product> categoryProducts = productRepository.findByCategory(category);
                categoryStats.put(category, categoryProducts.size());
            }
            
            // 按状态统计
            String[] states = {"on_sale", "off_sale", "sold_out", "pending"};
            Map<String, Integer> stateStats = new java.util.HashMap<>();
            for (String state : states) {
                List<Product> stateProducts = productRepository.findByState(state);
                stateStats.put(state, stateProducts.size());
            }
            
            return Result.success(Map.of(
                "total_products", totalCount,
                "category_stats", categoryStats,
                "state_stats", stateStats,
                "last_updated", LocalDateTime.now()
            ));
        } catch (Exception e) {
            return Result.error("获取统计信息失败: " + e.getMessage());
        }
    }

    /**
     * 测试完整的指标流程
     */
    @PostMapping("/full-test")
    public Result fullTest() {
        try {
            // 1. 清空现有数据
            productRepository.truncateTable();
            
            // 2. 创建API指标并执行
            Metric apiMetric = new Metric();
            apiMetric.setName("完整测试-API数据拉取");
            apiMetric.setType("api");
            apiMetric.setApiUrl("http://localhost:8089/api/mock/pdd/goods/list");
            apiMetric.setTargetDbHost("localhost:3306");
            apiMetric.setTargetDbName("metrics_center");
            apiMetric.setTargetTableName("products");
            apiMetric.setCreatedAt(LocalDateTime.now());
            apiMetric.setUpdatedAt(LocalDateTime.now());
            
            Map<String, Object> apiResult = metricService.executeMetric(apiMetric);
            
            // 3. 验证数据库中的数据
            int finalCount = productRepository.getTotalCount();
            
            // 4. 创建数据库查询指标并执行
            Metric dbMetric = new Metric();
            dbMetric.setName("完整测试-数据库查询");
            dbMetric.setType("database");
            dbMetric.setSourceDbHost("localhost:3306");
            dbMetric.setSourceDbName("metrics_center");
            dbMetric.setSourceTableName("products");
            dbMetric.setFieldName("price");
            dbMetric.setAggregateMethod("AVG");
            dbMetric.setCreatedAt(LocalDateTime.now());
            dbMetric.setUpdatedAt(LocalDateTime.now());
            
            Map<String, Object> dbResult = metricService.executeMetric(dbMetric);
            
            return Result.success(Map.of(
                "test_status", "完整测试成功",
                "api_execution", apiResult,
                "database_execution", dbResult,
                "final_product_count", finalCount,
                "test_time", LocalDateTime.now()
            ));
            
        } catch (Exception e) {
            return Result.error("完整测试失败: " + e.getMessage());
        }
    }
} 