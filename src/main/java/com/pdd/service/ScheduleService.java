package com.pdd.service;

import com.pdd.model.Metric;
import java.util.concurrent.ScheduledFuture;

public interface ScheduleService {
    
    /**
     * 启动指标的定时调度任务
     */
    void startScheduledTask(Metric metric);
    
    /**
     * 停止指标的定时调度任务
     */
    void stopScheduledTask(Long metricId);
    
    /**
     * 检查指标是否正在调度中
     */
    boolean isScheduled(Long metricId);
    
    /**
     * 获取调度任务的下次执行时间
     */
    String getNextExecutionTime(Long metricId);
    
    /**
     * 停止所有调度任务
     */
    void stopAllScheduledTasks();
    
    /**
     * 重启指标的调度任务（如果已存在则先停止再启动）
     */
    void restartScheduledTask(Metric metric);
    
    /**
     * 获取所有正在调度的指标ID
     */
    java.util.Set<Long> getActiveScheduledMetrics();
}