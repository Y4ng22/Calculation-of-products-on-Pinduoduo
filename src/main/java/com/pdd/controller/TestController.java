package com.pdd.controller;

import com.pdd.common.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test/api")
public class TestController {

    @GetMapping("/test")
    public Result test() {
        return new Result();
    }
}
