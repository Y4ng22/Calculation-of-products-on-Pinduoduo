package com.pdd;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.pdd.repository")  // <- 扫描你的 Mapper 接口包
public class AgentBuilderApplication {
    public static void main(String[] args) {
        SpringApplication.run(AgentBuilderApplication.class, args);

    }
}
