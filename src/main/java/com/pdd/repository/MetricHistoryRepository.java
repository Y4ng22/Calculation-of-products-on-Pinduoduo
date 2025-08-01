package com.pdd.repository;

import com.pdd.model.MetricHistory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MetricHistoryRepository {
    
    /**
     * 插入指标历史记录
     */
    int insert(MetricHistory metricHistory);
    
    /**
     * 根据ID查询历史记录
     */
    MetricHistory findById(Long id);
    
    /**
     * 根据指标ID分页查询历史记录
     */
    List<MetricHistory> findByMetricId(@Param("metricId") Long metricId, 
                                      @Param("offset") int offset, 
                                      @Param("size") int size);
    
    /**
     * 根据指标ID统计历史记录总数
     */
    int countByMetricId(@Param("metricId") Long metricId);
    
    /**
     * 获取指标最近的历史记录
     */
    MetricHistory findLatestByMetricId(@Param("metricId") Long metricId);
    
    /**
     * 删除指标的所有历史记录
     */
    int deleteByMetricId(@Param("metricId") Long metricId);
    
    /**
     * 根据ID删除历史记录
     */
    int deleteById(@Param("id") Long id);
} 