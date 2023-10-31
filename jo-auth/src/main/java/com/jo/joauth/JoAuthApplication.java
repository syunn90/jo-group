package com.jo.joauth;

import org.apache.ibatis.annotations.Mapper;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.jo.common","com.jo.joauth"})
@MapperScan(basePackages = {"com.jo.common.*"})
public class JoAuthApplication {

    public static void main(String[] args) {
        SpringApplication.run(JoAuthApplication.class, args);
    }

}
