package com.pdd.repository;

import com.pdd.model.Metric;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface MetricRepository {
    
    int getTotalCount();
    
    List<Metric> findByPage(@Param("offset") int offset, @Param("limit") int limit);
    
    Metric findById(@Param("id") Long id);
    
    int insert(Metric metric);
    
    int update(Metric metric);
    
    int deleteById(@Param("id") Long id);
    
    int updateStatus(@Param("id") Long id, @Param("enabled") Boolean enabled);
    
    int countByType(@Param("type") String type);
    
    int countByEnabled(@Param("enabled") Boolean enabled);
    
    int countByLastResult(@Param("result") String result);
    
    List<Metric> findEnabledMetrics();
} 