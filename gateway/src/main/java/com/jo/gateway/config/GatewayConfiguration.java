package com.jo.gateway.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jo.gateway.filter.PasswordDecoderFilter;
import com.jo.gateway.filter.RequestGlobalFilter;
import com.jo.gateway.filter.ValidateCodeFilter;
import com.jo.gateway.handler.ImageCodeHandler;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * @author xtc
 * @date 2023/11/15
 */
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(GatewayConfigProperties.class)
public class GatewayConfiguration {

    @Bean
    public PasswordDecoderFilter passwordDecoderFilter(GatewayConfigProperties gatewayConfigProperties){
        return new PasswordDecoderFilter(gatewayConfigProperties);
    }

    @Bean
    public RequestGlobalFilter requestGlobalFilter() {
        return new RequestGlobalFilter();
    }

    @Bean
    public ValidateCodeFilter validateCodeFilter(GatewayConfigProperties gatewayConfigProperties, ObjectMapper objectMapper, RedisTemplate redisTemplate){
        return new ValidateCodeFilter(gatewayConfigProperties,objectMapper,redisTemplate);
    }

    @Bean
    public ImageCodeHandler imageCodeHandler(RedisTemplate redisTemplate) {
        return new ImageCodeHandler(redisTemplate);
    }
}
