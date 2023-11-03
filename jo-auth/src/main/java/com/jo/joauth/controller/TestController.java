package com.jo.joauth.controller;

import com.jo.api.feign.TestService;
import com.jo.common.util.R;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author xtc
 * @date 2023/10/30
 */
@RestController
@RequestMapping("hello")
public class TestController {


    @Resource
    private TestService service;

    @GetMapping
    public R test(){
        return service.info();
    }
}

// 授权链接，不可以用localhost，需要用ip
// http://127.0.0.1:1001/oauth2/authorize?client_id=client&response_type=code&scope=read&redirect_uri=http://127.0.0.1:1001/hello


// 获取token 链接
//curl -i -X POST \
//        -H "Content-Type:application/x-www-form-urlencoded" \
//        -H "Authorization:Basic Y2xpZW50OjEyMzQ=" \
//        -d "code=CDBBS49CsBMgKaEr7TtKwmUM3fJbpJTxzD5qJ98s8waWlscqJChvEPgX45QJiuIGrDvN9i8p04vPyppVlCUw7kuanUq6qnxeHZ75VAer47TVaQsW7kgP71DeKbuygilx" \
//        -d "grant_type=authorization_code" \
//        -d "redirect_uri=http://127.0.0.1:8080/hello" \
//        'http://localhost:1001/oauth2/token'

// 刷新token使用

//curl -i -X POST \
//        -H "Authorization:Basic Y2xpZW50OjEyMzQ=" \
//        -H "Content-Type:application/x-www-form-urlencoded" \
//        -d "refresh_token=M9swXlGWx58jPGoMDhCQKlr-HaT70wCIeGFtXprzxkb04Ptgm8QHV7CdEbv02wgW3o8rs8l_QgigDNgCTsYqgzYDpVv4NPwh78Ok8AGR4zNWRn2t7KKiXo4MFT9JxSQi" \
//        -d "grant_type=refresh_token" \
//        -d "redirect_uri=http://127.0.0.1:8080/hello" \
//        'http://localhost:1001/oauth2/token'