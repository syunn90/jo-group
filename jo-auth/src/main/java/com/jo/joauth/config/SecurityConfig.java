package com.jo.joauth.config;

import com.jo.common.core.constant.SecurityConstants;
import com.jo.joauth.handler.JoAuthenticationFailureEventHandler;
import com.jo.joauth.handler.JoAuthenticationSuccessEventHandler;
import com.jo.joauth.support.CustomeOAuth2AccessTokenGenerator;
import com.jo.joauth.support.core.CustomeOAuth2TokenCustomizer;
import com.jo.joauth.support.core.JoDaoAuthenticationProvider;
import com.jo.joauth.support.password.OAuth2ResourceOwnerPasswordAuthenticationConverter;
import com.jo.joauth.support.password.OAuth2ResourceOwnerPasswordAuthenticationProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.authorization.token.DelegatingOAuth2TokenGenerator;
import org.springframework.security.oauth2.server.authorization.token.OAuth2RefreshTokenGenerator;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenGenerator;
import org.springframework.security.oauth2.server.authorization.web.authentication.DelegatingAuthenticationConverter;
import org.springframework.security.oauth2.server.authorization.web.authentication.OAuth2AuthorizationCodeRequestAuthenticationConverter;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationConverter;

import java.util.Arrays;


/**
 * @author xtc
 * @date 2023/10/30
 */
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final OAuth2AuthorizationService authorizationService;

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http) throws Exception {
        OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(http);
        // 使用 HttpSecurity 获取 OAuth 2.1 配置中的 OAuth2AuthorizationServerConfigurer 对象
        OAuth2AuthorizationServerConfigurer authorizationServerConfigurer =
                http.getConfigurer(OAuth2AuthorizationServerConfigurer.class);// Enable OpenID Connect 1.0

        authorizationServerConfigurer.tokenEndpoint((tokenEndpoint) -> {
            // 自定义授权端点
            // 自定义授权认证covert
            tokenEndpoint.accessTokenRequestConverter(accessTokenRequestConverter());
            // 登录成功处理器
            tokenEndpoint.accessTokenResponseHandler(new JoAuthenticationSuccessEventHandler());
            // 登录失败处理器
            tokenEndpoint.errorResponseHandler(new JoAuthenticationFailureEventHandler());
        });
        authorizationServerConfigurer.clientAuthentication( oAuth2ClientAuthenticationConfigurer -> {
            //  自定义客户端认证
            oAuth2ClientAuthenticationConfigurer.errorResponseHandler(new JoAuthenticationFailureEventHandler());
        });
        authorizationServerConfigurer.authorizationEndpoint(authorizationEndPoint -> {
            // 授权码端点个性化confirm页面
            authorizationEndPoint.consentPage(SecurityConstants.CUSTOM_CONSENT_PAGE_URI);
        });

        DefaultSecurityFilterChain securityFilterChain = authorizationServerConfigurer
                .authorizationService(authorizationService)// redis存储token的实现
                .authorizationServerSettings(
                        AuthorizationServerSettings.builder().build())
                .and()
                .build();

        // 注入自定义授权模式实现
        addCustomOAuth2GrantAuthenticationProvider(http);
        return securityFilterChain;
    }
    /**
     * request -> xToken 注入请求转换器
     * @return DelegatingAuthenticationConverter
     */
    private AuthenticationConverter accessTokenRequestConverter() {
        return new DelegatingAuthenticationConverter(
                Arrays.asList(new OAuth2ResourceOwnerPasswordAuthenticationConverter(),
                        new OAuth2AuthorizationCodeRequestAuthenticationConverter()));
    }

    /**
     * 令牌生成规则实现 </br>
     * client:username:uuid
     * @return OAuth2TokenGenerator
     */
    @Bean
    public OAuth2TokenGenerator oAuth2TokenGenerator() {
        CustomeOAuth2AccessTokenGenerator accessTokenGenerator = new CustomeOAuth2AccessTokenGenerator();
        // 注入Token 增加关联用户信息
        accessTokenGenerator.setAccessTokenCustomizer(new CustomeOAuth2TokenCustomizer());
        return new DelegatingOAuth2TokenGenerator(accessTokenGenerator, new OAuth2RefreshTokenGenerator());
    }


    /**
     * 注入授权模式实现提供方
     * <p>
     * 1. 密码模式 </br>
     * 2. 短信登录 </br>
     */
    @SuppressWarnings("unchecked")
    private void addCustomOAuth2GrantAuthenticationProvider(HttpSecurity http) {
        AuthenticationManager authenticationManager = http.getSharedObject(AuthenticationManager.class);
        OAuth2AuthorizationService authorizationService = http.getSharedObject(OAuth2AuthorizationService.class);
        // 自定义账号密码登录模式
        OAuth2ResourceOwnerPasswordAuthenticationProvider resourceOwnerPasswordAuthenticationProvider =
                new OAuth2ResourceOwnerPasswordAuthenticationProvider( authenticationManager, authorizationService, oAuth2TokenGenerator());

//        OAuth2ResourceOwnerSmsAuthenticationProvider resourceOwnerSmsAuthenticationProvider = new OAuth2ResourceOwnerSmsAuthenticationProvider(
//                authenticationManager, authorizationService, oAuth2TokenGenerator());

        // 处理 UsernamePasswordAuthenticationToken
        http.authenticationProvider(new JoDaoAuthenticationProvider());
        // 处理 OAuth2ResourceOwnerPasswordAuthenticationToken
        http.authenticationProvider(resourceOwnerPasswordAuthenticationProvider);
        // 处理 OAuth2ResourceOwnerSmsAuthenticationToken
//        http.authenticationProvider(resourceOwnerSmsAuthenticationProvider);
    }
}
