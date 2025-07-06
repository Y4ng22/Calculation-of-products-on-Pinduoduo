package com.pdd.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/test")
public class TestController {
    @GetMapping("/api/test")
    public Map<String, Object> testApi() {
        return Map.of(
                "message", "✅ 调用成功",
                "timestamp", System.currentTimeMillis()
        );
    }
}
