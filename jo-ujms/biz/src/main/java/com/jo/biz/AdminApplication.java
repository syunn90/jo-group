package com.jo.biz;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients("com.jo.api")
public class AdminApplication {


    public static void main(String[] args) {
        SpringApplication.run(AdminApplication.class, args);
    }


}
