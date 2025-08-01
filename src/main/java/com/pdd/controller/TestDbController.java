package com.pdd.controller;

import com.pdd.common.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/test")
@CrossOrigin(originPatterns = "*")
public class TestDbController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 测试数据库连接
     */
    @GetMapping("/db-connection")
    public Result testDbConnection() {
        try {
            // 简单的数据库连接测试
            List<Map<String, Object>> result = jdbcTemplate.queryForList("SELECT 1 as test");
            return Result.success("数据库连接正常: " + result);
        } catch (Exception e) {
            return Result.error("数据库连接失败: " + e.getMessage());
        }
    }

    /**
     * 测试查询metrics表
     */
    @GetMapping("/metrics-table")
    public Result testMetricsTable() {
        try {
            // 检查表是否存在并查询数据
            String sql = "SELECT COUNT(*) as count FROM metrics";
            Integer count = jdbcTemplate.queryForObject(sql, Integer.class);
            return Result.success("metrics表查询成功，数据条数: " + count);
        } catch (Exception e) {
            return Result.error("metrics表查询失败: " + e.getMessage());
        }
    }

    /**
     * 查询metrics表的前几条数据
     */
    @GetMapping("/metrics-data")
    public Result getMetricsData() {
        try {
            String sql = "SELECT * FROM metrics LIMIT 5";
            List<Map<String, Object>> result = jdbcTemplate.queryForList(sql);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error("查询metrics数据失败: " + e.getMessage());
        }
    }

    /**
     * 检查数据库表结构
     */
    @GetMapping("/table-structure")
    public Result getTableStructure() {
        try {
            String sql = "DESCRIBE metrics";
            List<Map<String, Object>> result = jdbcTemplate.queryForList(sql);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error("查询表结构失败: " + e.getMessage());
        }
    }
} 