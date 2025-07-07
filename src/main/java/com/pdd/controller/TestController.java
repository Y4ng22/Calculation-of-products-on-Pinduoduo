package com.pdd.controller;

import com.pdd.common.Result;
import com.pdd.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.URISyntaxException;

@RestController
@RequestMapping("/test/api")
public class TestController {

    @Autowired
    private TestService testService;

    @GetMapping("/test")
    public Result test() throws URISyntaxException, IOException {
        String res = testService.test();
        return Result.success(res);
    }
}
