package com.pdd.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.servlet.config.annotation.AsyncSupportConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.core.task.AsyncTaskExecutor;

@Configuration
@EnableAsync
@EnableScheduling
public class AsyncConfig implements WebMvcConfigurer {

    @Override
    public void configureAsyncSupport(AsyncSupportConfigurer configurer) {
        // 设置异步请求超时时间（5分钟）
        configurer.setDefaultTimeout(300000);
        // 设置任务执行器
        configurer.setTaskExecutor(asyncTaskExecutor());
    }

    @Bean(name = "asyncTaskExecutor")
    public ThreadPoolTaskExecutor asyncTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);           // 核心线程数
        executor.setMaxPoolSize(50);            // 最大线程数
        executor.setQueueCapacity(100);         // 队列容量
        executor.setKeepAliveSeconds(60);       // 线程空闲时间
        executor.setThreadNamePrefix("Async-Stream-"); // 线程名前缀
        executor.setWaitForTasksToCompleteOnShutdown(true); // 关闭时等待任务完成
        executor.setAwaitTerminationSeconds(60); // 等待时间
        executor.initialize();
        return executor;
    }
    
    @Bean(name = "taskScheduler")
    public ThreadPoolTaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(20);              // 调度器线程池大小
        scheduler.setThreadNamePrefix("Schedule-Task-"); // 线程名前缀
        scheduler.setWaitForTasksToCompleteOnShutdown(true); // 关闭时等待任务完成
        scheduler.setAwaitTerminationSeconds(60); // 等待时间
        scheduler.initialize();
        return scheduler;
    }
} 