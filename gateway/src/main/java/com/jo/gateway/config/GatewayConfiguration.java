package com.jo.gateway.config;

import com.jo.gateway.filter.PasswordDecoderFilter;
import com.jo.gateway.filter.RequestGlobalFilter;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
}
