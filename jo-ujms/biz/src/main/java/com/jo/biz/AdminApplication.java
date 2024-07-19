package com.jo.biz;

import com.jo.common.security.annotation.EnableJoResourceServer;
import com.jo.support.support.annotation.EnableRpc;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableFeignClients("com.jo.api")
@ComponentScan(basePackages = {"com.jo.common","com.jo.biz"})
@EnableJoResourceServer
@EnableRpc(basePackages = "com.jo.biz")
public class AdminApplication {

    public static void main(String[] args) {
        SpringApplication.run(AdminApplication.class, args);
    }

}
