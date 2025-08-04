package com.pdd.service.impl;

import com.pdd.model.Metric;
import com.pdd.service.MetricService;
import com.pdd.service.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import jakarta.annotation.PreDestroy;
import java.time.Duration;
import java.time.LocalDateTime;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

@Service
public class ScheduleServiceImpl implements ScheduleService {

    @Autowired
    @Lazy
    private MetricService metricService;
    
    @Autowired
    private ThreadPoolTaskScheduler taskScheduler;
    
    // 存储正在运行的调度任务
    private final Map<Long, ScheduledFuture<?>> scheduledTasks = new ConcurrentHashMap<>();
    
    // 存储任务的调度间隔（用于计算下次执行时间）
    private final Map<Long, Duration> scheduleIntervals = new ConcurrentHashMap<>();
    
    // 存储任务的最后执行时间
    private final Map<Long, LocalDateTime> lastExecutionTimes = new ConcurrentHashMap<>();

    @Override
    public void startScheduledTask(Metric metric) {
        if (metric == null || metric.getId() == null) {
            System.err.println("无法启动调度任务：指标为空或缺少ID");
            return;
        }
        
        Long metricId = metric.getId();
        
        // 如果已存在调度任务，先停止
        stopScheduledTask(metricId);
        
        Duration interval = parseScheduleInterval(metric.getSchedule());
        if (interval == null) {
            System.err.println("无法解析调度周期：" + metric.getSchedule());
            return;
        }
        
        System.out.println("启动指标调度任务：" + metric.getName() + "，周期：" + metric.getSchedule());
        
        // 创建调度任务
        ScheduledFuture<?> scheduledFuture = taskScheduler.scheduleAtFixedRate(() -> {
            try {
                System.out.println("=== 调度执行指标：" + metric.getName() + " ===");
                lastExecutionTimes.put(metricId, LocalDateTime.now());
                
                // 重新获取最新的指标信息（防止配置变更）
                Metric currentMetric = metricService.getMetricById(metricId);
                if (currentMetric != null && currentMetric.getEnabled()) {
                    Map<String, Object> result = metricService.executeMetric(currentMetric);
                    System.out.println("调度执行完成，结果：" + result);
                } else {
                    System.out.println("指标已被禁用或删除，停止调度任务");
                    stopScheduledTask(metricId);
                }
            } catch (Exception e) {
                System.err.println("调度执行指标失败：" + e.getMessage());
                e.printStackTrace();
            }
        }, interval);
        
        // 保存调度任务信息
        scheduledTasks.put(metricId, scheduledFuture);
        scheduleIntervals.put(metricId, interval);
        
        System.out.println("指标调度任务启动成功：" + metric.getName());
    }

    @Override
    public void stopScheduledTask(Long metricId) {
        if (metricId == null) return;
        
        ScheduledFuture<?> scheduledFuture = scheduledTasks.remove(metricId);
        if (scheduledFuture != null) {
            scheduledFuture.cancel(false);
            scheduleIntervals.remove(metricId);
            lastExecutionTimes.remove(metricId);
            System.out.println("停止指标调度任务：" + metricId);
        }
    }

    @Override
    public boolean isScheduled(Long metricId) {
        if (metricId == null) return false;
        
        ScheduledFuture<?> scheduledFuture = scheduledTasks.get(metricId);
        return scheduledFuture != null && !scheduledFuture.isCancelled() && !scheduledFuture.isDone();
    }

    @Override
    public String getNextExecutionTime(Long metricId) {
        if (metricId == null || !isScheduled(metricId)) {
            return null;
        }
        
        LocalDateTime lastExecution = lastExecutionTimes.get(metricId);
        Duration interval = scheduleIntervals.get(metricId);
        
        if (lastExecution != null && interval != null) {
            LocalDateTime nextExecution = lastExecution.plus(interval);
            return nextExecution.toString();
        }
        
        return null;
    }

    @Override
    public void restartScheduledTask(Metric metric) {
        if (metric != null && metric.getId() != null) {
            stopScheduledTask(metric.getId());
            startScheduledTask(metric);
        }
    }

    @Override
    public Set<Long> getActiveScheduledMetrics() {
        // 清理已完成或取消的任务
        scheduledTasks.entrySet().removeIf(entry -> 
            entry.getValue().isCancelled() || entry.getValue().isDone());
        
        return scheduledTasks.keySet();
    }

    @Override
    @PreDestroy
    public void stopAllScheduledTasks() {
        System.out.println("停止所有调度任务...");
        scheduledTasks.values().forEach(future -> future.cancel(false));
        scheduledTasks.clear();
        scheduleIntervals.clear();
        lastExecutionTimes.clear();
        System.out.println("所有调度任务已停止");
    }
    
    /**
     * 解析调度周期字符串为Duration对象
     */
    private Duration parseScheduleInterval(String schedule) {
        if (schedule == null || schedule.trim().isEmpty()) {
            return null;
        }
        
        try {
            switch (schedule.toLowerCase()) {
                case "5min":
                    return Duration.ofMinutes(5);
                case "15min":
                    return Duration.ofMinutes(15);
                case "30min":
                    return Duration.ofMinutes(30);
                case "1hour":
                    return Duration.ofHours(1);
                case "1day":
                    return Duration.ofDays(1);
                case "1week":
                    return Duration.ofDays(7);
                default:
                    System.err.println("不支持的调度周期：" + schedule);
                    return null;
            }
        } catch (Exception e) {
            System.err.println("解析调度周期失败：" + schedule + "，错误：" + e.getMessage());
            return null;
        }
    }
}