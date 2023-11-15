package com.jo.joauth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.jo.common","com.jo.joauth"})
@EnableFeignClients("com.jo.api")
public class JoAuthApplication {

    public static void main(String[] args) {
        SpringApplication.run(JoAuthApplication.class, args);
    }

}
