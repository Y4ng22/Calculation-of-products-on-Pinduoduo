package com.pdd;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@MapperScan("com.pdd.repository")  // <- 扫描你的 Mapper 接口包
@EnableTransactionManagement  // <- 启用事务管理
public class AgentBuilderApplication {
    public static void main(String[] args) {
        SpringApplication.run(AgentBuilderApplication.class, args);

    }
}
