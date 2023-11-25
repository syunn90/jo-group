package com.jo.common.security.component;

import cn.hutool.core.util.ArrayUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.server.resource.introspection.OpaqueTokenIntrospector;
import org.springframework.security.web.SecurityFilterChain;

/**
 * @author xtc
 * @date 2023/11/17
 */
@RequiredArgsConstructor
public class JoResourceServerConfiguration {
    private final ResourceAuthExceptionEntryPoint resourceAuthExceptionEntryPoint;

    private final PermitAllUrlProperties permitAllUrlProperties;

    private final OpaqueTokenIntrospector customOpaqueTokenIntrospector;

    private final JoBearerTokenExtractor joBearerTokenExtractor;


    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http.authorizeRequests(authorizeRequests -> authorizeRequests
                .antMatchers(ArrayUtil.toArray(permitAllUrlProperties.getUrls(), String.class))
                .permitAll()
                .anyRequest()
                .authenticated())
                .oauth2ResourceServer(
                        oauth2 -> oauth2.opaqueToken(token -> token.introspector(customOpaqueTokenIntrospector))
                                .authenticationEntryPoint(resourceAuthExceptionEntryPoint)
                                .bearerTokenResolver(joBearerTokenExtractor))
                .headers()
                .frameOptions()
                .disable()
                .and()
                .csrf()
                .disable();

        return http.build();


    }


}
