package com.pdd.controller;

import com.pdd.common.Result;
import com.pdd.model.Metric;
import com.pdd.service.MetricService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/metrics")
@CrossOrigin(originPatterns = "*")
public class MetricsController {

    @Autowired
    private MetricService metricService;

    /**
     * 获取指标列表（分页）
     */
    @GetMapping
    public Result getMetrics(@RequestParam(defaultValue = "1") int page,
                           @RequestParam(defaultValue = "10") int size) {
        try {
            Map<String, Object> result = metricService.getMetricsByPage(page, size);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error("获取指标列表失败: " + e.getMessage());
        }
    }

    /**
     * 根据ID获取指标详情
     */
    @GetMapping("/{id}")
    public Result getMetricById(@PathVariable Long id) {
        try {
            Metric metric = metricService.getMetricById(id);
            if (metric == null) {
                return Result.error("指标不存在");
            }
            return Result.success(metric);
        } catch (Exception e) {
            return Result.error("获取指标详情失败: " + e.getMessage());
        }
    }

    /**
     * 创建新指标
     */
    @PostMapping
    public Result createMetric(@RequestBody Metric metric) {
        try {
            // 参数验证
            if (metric.getName() == null || metric.getName().trim().isEmpty()) {
                return Result.error("指标名称不能为空");
            }
            if (metric.getType() == null || metric.getType().trim().isEmpty()) {
                return Result.error("指标类型不能为空");
            }
            if (metric.getSchedule() == null || metric.getSchedule().trim().isEmpty()) {
                return Result.error("调度周期不能为空");
            }

            // API类型验证
            if ("api".equals(metric.getType())) {
                if (metric.getApiUrl() == null || metric.getApiUrl().trim().isEmpty()) {
                    return Result.error("API地址不能为空");
                }
            }

            // 设置创建时间和默认状态
            metric.setCreatedAt(LocalDateTime.now());
            metric.setUpdatedAt(LocalDateTime.now());
            metric.setEnabled(true);
            metric.setLastResult("pending");

            Metric savedMetric = metricService.createMetric(metric);
            return Result.success(savedMetric);
        } catch (Exception e) {
            return Result.error("创建指标失败: " + e.getMessage());
        }
    }

    /**
     * 更新指标
     */
    @PutMapping("/{id}")
    public Result updateMetric(@PathVariable Long id, @RequestBody Metric metric) {
        try {
            Metric existingMetric = metricService.getMetricById(id);
            if (existingMetric == null) {
                return Result.error("指标不存在");
            }

            // 参数验证
            if (metric.getName() == null || metric.getName().trim().isEmpty()) {
                return Result.error("指标名称不能为空");
            }
            if (metric.getType() == null || metric.getType().trim().isEmpty()) {
                return Result.error("指标类型不能为空");
            }
            if (metric.getSchedule() == null || metric.getSchedule().trim().isEmpty()) {
                return Result.error("调度周期不能为空");
            }

            // API类型验证
            if ("api".equals(metric.getType())) {
                if (metric.getApiUrl() == null || metric.getApiUrl().trim().isEmpty()) {
                    return Result.error("API地址不能为空");
                }
            }

            metric.setId(id);
            metric.setUpdatedAt(LocalDateTime.now());
            metric.setCreatedAt(existingMetric.getCreatedAt()); // 保持原创建时间

            Metric updatedMetric = metricService.updateMetric(metric);
            return Result.success(updatedMetric);
        } catch (Exception e) {
            return Result.error("更新指标失败: " + e.getMessage());
        }
    }

    /**
     * 删除指标
     */
    @DeleteMapping("/{id}")
    public Result deleteMetric(@PathVariable Long id) {
        try {
            Metric existingMetric = metricService.getMetricById(id);
            if (existingMetric == null) {
                return Result.error("指标不存在");
            }

            metricService.deleteMetric(id);
            return Result.success("指标删除成功");
        } catch (Exception e) {
            return Result.error("删除指标失败: " + e.getMessage());
        }
    }

    /**
     * 启用/禁用指标
     */
    @PutMapping("/{id}/status")
    public Result toggleMetricStatus(@PathVariable Long id, @RequestBody Map<String, Object> request) {
        try {
            Metric existingMetric = metricService.getMetricById(id);
            if (existingMetric == null) {
                return Result.error("指标不存在");
            }

            Boolean enabled = (Boolean) request.get("enabled");
            if (enabled == null) {
                return Result.error("状态参数不能为空");
            }

            metricService.updateMetricStatus(id, enabled);
            return Result.success(enabled ? "指标已启用" : "指标已禁用");
        } catch (Exception e) {
            return Result.error("更新指标状态失败: " + e.getMessage());
        }
    }

    /**
     * 立即运行指标并启动调度任务
     */
    @PostMapping("/{id}/run")
    public Result runMetricNow(@PathVariable Long id) {
        try {
            System.out.println("=== 开始运行指标 ===");
            System.out.println("指标ID: " + id);
            
            Metric existingMetric = metricService.getMetricById(id);
            if (existingMetric == null) {
                System.out.println("指标不存在: " + id);
                return Result.error("指标不存在");
            }

            System.out.println("找到指标: " + existingMetric.getName());
            System.out.println("指标类型: " + existingMetric.getType());

            // 立即执行指标任务
            Map<String, Object> result = metricService.executeMetric(existingMetric);
            
            // 启动调度任务
            metricService.startMetricSchedule(id);
            
            System.out.println("指标执行完成，结果: " + result);
            System.out.println("调度任务已启动");
            System.out.println("=== 指标运行结束 ===");
            
            // 返回结果中包含调度状态
            Map<String, Object> responseData = new HashMap<>(result);
            responseData.put("scheduleStarted", true);
            responseData.put("scheduleStatus", metricService.getScheduleStatus(id));
            
            return Result.success(responseData);
        } catch (Exception e) {
            System.err.println("=== 指标运行失败 ===");
            System.err.println("错误消息: " + e.getMessage());
            e.printStackTrace();
            return Result.error("运行指标失败: " + e.getMessage());
        }
    }

    /**
     * 启动指标调度任务
     */
    @PostMapping("/{id}/schedule/start")
    public Result startMetricSchedule(@PathVariable Long id) {
        try {
            Metric existingMetric = metricService.getMetricById(id);
            if (existingMetric == null) {
                return Result.error("指标不存在");
            }
            
            if (!existingMetric.getEnabled()) {
                return Result.error("指标已禁用，无法启动调度");
            }
            
            metricService.startMetricSchedule(id);
            Map<String, Object> scheduleStatus = metricService.getScheduleStatus(id);
            
            return Result.success(scheduleStatus);
        } catch (Exception e) {
            return Result.error("启动调度失败: " + e.getMessage());
        }
    }
    
    /**
     * 停止指标调度任务
     */
    @PostMapping("/{id}/schedule/stop")
    public Result stopMetricSchedule(@PathVariable Long id) {
        try {
            Metric existingMetric = metricService.getMetricById(id);
            if (existingMetric == null) {
                return Result.error("指标不存在");
            }
            
            metricService.stopMetricSchedule(id);
            
            Map<String, Object> result = new HashMap<>();
            result.put("metricId", id);
            result.put("scheduled", false);
            result.put("message", "调度任务已停止");
            
            return Result.success(result);
        } catch (Exception e) {
            return Result.error("停止调度失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取指标调度状态
     */
    @GetMapping("/{id}/schedule/status")
    public Result getMetricScheduleStatus(@PathVariable Long id) {
        try {
            Metric existingMetric = metricService.getMetricById(id);
            if (existingMetric == null) {
                return Result.error("指标不存在");
            }
            
            Map<String, Object> scheduleStatus = metricService.getScheduleStatus(id);
            return Result.success(scheduleStatus);
        } catch (Exception e) {
            return Result.error("获取调度状态失败: " + e.getMessage());
        }
    }

    /**
     * 获取指标运行历史
     */
    @GetMapping("/{id}/history")
    public Result getMetricHistory(@PathVariable Long id,
                                 @RequestParam(defaultValue = "1") int page,
                                 @RequestParam(defaultValue = "10") int size) {
        try {
            Metric existingMetric = metricService.getMetricById(id);
            if (existingMetric == null) {
                return Result.error("指标不存在");
            }

            Map<String, Object> result = metricService.getMetricHistory(id, page, size);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error("获取指标历史失败: " + e.getMessage());
        }
    }

    /**
     * 获取指标统计信息
     */
    @GetMapping("/stats")
    public Result getMetricsStats() {
        try {
            Map<String, Object> stats = metricService.getMetricsStats();
            return Result.success(stats);
        } catch (Exception e) {
            return Result.error("获取统计信息失败: " + e.getMessage());
        }
    }

    /**
     * 批量启用/禁用指标
     */
    @PutMapping("/batch/status")
    public Result batchToggleStatus(@RequestBody Map<String, Object> request) {
        try {
            @SuppressWarnings("unchecked")
            List<Long> ids = (List<Long>) request.get("ids");
            Boolean enabled = (Boolean) request.get("enabled");
            
            if (ids == null || ids.isEmpty()) {
                return Result.error("指标ID列表不能为空");
            }
            if (enabled == null) {
                return Result.error("状态参数不能为空");
            }

            int updatedCount = metricService.batchUpdateStatus(ids, enabled);
            return Result.success("成功更新 " + updatedCount + " 个指标状态");
        } catch (Exception e) {
            return Result.error("批量更新状态失败: " + e.getMessage());
        }
    }

    /**
     * 测试API连接
     */
    @PostMapping("/test/api")
    public Result testApiConnection(@RequestBody Map<String, String> request) {
        try {
            String apiUrl = request.get("apiUrl");
            String clientId = request.get("clientId");
            String clientSecret = request.get("clientSecret");
            String accessToken = request.get("accessToken");

            if (apiUrl == null || apiUrl.trim().isEmpty()) {
                return Result.error("API地址不能为空");
            }

            Map<String, Object> result = metricService.testApiConnection(apiUrl, clientId, clientSecret, accessToken);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error("API连接测试失败: " + e.getMessage());
        }
    }

    /**
     * 测试数据库连接
     */
    @PostMapping("/test/database")
    public Result testDatabaseConnection(@RequestBody Map<String, String> request) {
        try {
            String dbHost = request.get("dbHost");
            String dbName = request.get("dbName");
            String username = request.get("username");
            String password = request.get("password");

            if (dbHost == null || dbHost.trim().isEmpty()) {
                return Result.error("数据库地址不能为空");
            }
            if (dbName == null || dbName.trim().isEmpty()) {
                return Result.error("数据库名称不能为空");
            }

            Map<String, Object> result = metricService.testDatabaseConnection(dbHost, dbName, username, password);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error("数据库连接测试失败: " + e.getMessage());
        }
    }

    /**
     * 预览SQL查询结果
     */
    @PostMapping("/preview/sql")
    public Result previewSqlQuery(@RequestBody Map<String, String> request) {
        try {
            String dbHost = request.get("dbHost");
            String dbName = request.get("dbName");
            String sql = request.get("sql");
            String username = request.get("username");
            String password = request.get("password");

            if (sql == null || sql.trim().isEmpty()) {
                return Result.error("SQL语句不能为空");
            }

            Map<String, Object> result = metricService.previewSqlQuery(dbHost, dbName, sql, username, password);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error("SQL预览失败: " + e.getMessage());
        }
    }

    /**
     * 测试历史记录表连接
     */
    @GetMapping("/test-history-table")
    public Result testHistoryTable() {
        try {
            // 测试是否能连接到metric_history表
            Map<String, Object> testResult = metricService.testHistoryTable();
            return Result.success(testResult);
        } catch (Exception e) {
            return Result.error("历史记录表测试失败: " + e.getMessage());
        }
    }
} 