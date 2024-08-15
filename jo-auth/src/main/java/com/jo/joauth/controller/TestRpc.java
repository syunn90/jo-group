package com.jo.joauth.controller;

import com.jo.api.rpc.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Jo
 * @date 2024/7/12
 */
@RestController
@RequestMapping("/test")
public class TestRpc  {

    @Autowired
    private TestService testService;

    @GetMapping("/test")
    public void onApplicationEvent() {

        testService.testRpc();

    }
}
