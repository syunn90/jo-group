package com.jo.biz.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author xtc
 * @date 2023/11/1
 */
@RestController
@RequestMapping("/test")
public class TestController {


    @GetMapping("/controller")
    public void test() {
        System.out.println("-------------------------------");

    }

}
