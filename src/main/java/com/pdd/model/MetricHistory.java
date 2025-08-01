package com.pdd.model;

import java.time.LocalDateTime;

public class MetricHistory {
    private Long id;
    private Long metricId;
    private LocalDateTime executeTime;
    private String result; // "success", "error", "warning"
    private String data; // JSON格式的执行结果数据
    private String errorMessage;
    private Integer executionDuration; // 执行耗时(毫秒)
    private LocalDateTime createdAt;

    // 构造方法
    public MetricHistory() {}

    public MetricHistory(Long metricId, String result, String data) {
        this.metricId = metricId;
        this.result = result;
        this.data = data;
        this.executeTime = LocalDateTime.now();
        this.createdAt = LocalDateTime.now();
    }

    // Getter和Setter方法
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getMetricId() {
        return metricId;
    }

    public void setMetricId(Long metricId) {
        this.metricId = metricId;
    }

    public LocalDateTime getExecuteTime() {
        return executeTime;
    }

    public void setExecuteTime(LocalDateTime executeTime) {
        this.executeTime = executeTime;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public Integer getExecutionDuration() {
        return executionDuration;
    }

    public void setExecutionDuration(Integer executionDuration) {
        this.executionDuration = executionDuration;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "MetricHistory{" +
                "id=" + id +
                ", metricId=" + metricId +
                ", executeTime=" + executeTime +
                ", result='" + result + '\'' +
                ", executionDuration=" + executionDuration +
                '}';
    }
} 