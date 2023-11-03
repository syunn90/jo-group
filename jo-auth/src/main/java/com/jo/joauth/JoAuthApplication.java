package com.jo.joauth;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.jo.common","com.jo.joauth"})
@MapperScan(basePackages = {"com.jo.common.*"})
@EnableFeignClients("com.jo.api")
public class JoAuthApplication {

    public static void main(String[] args) {
        SpringApplication.run(JoAuthApplication.class, args);
    }

}
