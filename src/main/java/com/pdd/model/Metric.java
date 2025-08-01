package com.pdd.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;

public class Metric {
    private Long id;
    private String name;
    private String type; // "api" 或 "database"
    private String schedule; // 调度周期
    private String description;
    private Boolean enabled; // 是否启用
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastRun;
    private String lastResult; // "success", "error", "warning", "pending"
    private String lastError;

    // API类型字段
    private String apiUrl;
    private String clientId;
    private String clientSecret;
    private String accessToken;
    private String targetDbHost;
    private String targetDbName;
    private String targetTableName;

    // 数据库类型字段
    private String sourceDbHost;
    private String sourceDbName;
    private String sourceTableName;
    private String fieldName;
    private String aggregateMethod; // SUM, COUNT, AVG, MAX, MIN
    private String customSql;
    private Boolean enableProfitAlert;
    private Double profitThreshold;

    // 构造方法
    public Metric() {}

    public Metric(String name, String type, String schedule) {
        this.name = name;
        this.type = type;
        this.schedule = schedule;
        this.enabled = true;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.lastResult = "pending";
    }

    // Getter和Setter方法
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSchedule() {
        return schedule;
    }

    public void setSchedule(String schedule) {
        this.schedule = schedule;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public LocalDateTime getLastRun() {
        return lastRun;
    }

    public void setLastRun(LocalDateTime lastRun) {
        this.lastRun = lastRun;
    }

    public String getLastResult() {
        return lastResult;
    }

    public void setLastResult(String lastResult) {
        this.lastResult = lastResult;
    }

    public String getLastError() {
        return lastError;
    }

    public void setLastError(String lastError) {
        this.lastError = lastError;
    }

    public String getApiUrl() {
        return apiUrl;
    }

    public void setApiUrl(String apiUrl) {
        this.apiUrl = apiUrl;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getTargetDbHost() {
        return targetDbHost;
    }

    public void setTargetDbHost(String targetDbHost) {
        this.targetDbHost = targetDbHost;
    }

    public String getTargetDbName() {
        return targetDbName;
    }

    public void setTargetDbName(String targetDbName) {
        this.targetDbName = targetDbName;
    }

    public String getTargetTableName() {
        return targetTableName;
    }

    public void setTargetTableName(String targetTableName) {
        this.targetTableName = targetTableName;
    }

    public String getSourceDbHost() {
        return sourceDbHost;
    }

    public void setSourceDbHost(String sourceDbHost) {
        this.sourceDbHost = sourceDbHost;
    }

    public String getSourceDbName() {
        return sourceDbName;
    }

    public void setSourceDbName(String sourceDbName) {
        this.sourceDbName = sourceDbName;
    }

    public String getSourceTableName() {
        return sourceTableName;
    }

    public void setSourceTableName(String sourceTableName) {
        this.sourceTableName = sourceTableName;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getAggregateMethod() {
        return aggregateMethod;
    }

    public void setAggregateMethod(String aggregateMethod) {
        this.aggregateMethod = aggregateMethod;
    }

    public String getCustomSql() {
        return customSql;
    }

    public void setCustomSql(String customSql) {
        this.customSql = customSql;
    }

    public Boolean getEnableProfitAlert() {
        return enableProfitAlert;
    }

    public void setEnableProfitAlert(Boolean enableProfitAlert) {
        this.enableProfitAlert = enableProfitAlert;
    }

    public Double getProfitThreshold() {
        return profitThreshold;
    }

    public void setProfitThreshold(Double profitThreshold) {
        this.profitThreshold = profitThreshold;
    }

    @Override
    public String toString() {
        return "Metric{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", schedule='" + schedule + '\'' +
                ", enabled=" + enabled +
                ", lastResult='" + lastResult + '\'' +
                '}';
    }
} 