package com.jo.common.security.component;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.jo.common.core.constant.SecurityConstants;
import com.jo.common.core.util.WebUtils;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;

/**
 * @author xtc
 * @date 2023/11/17
 */
@Slf4j
@RequiredArgsConstructor
public class JoOAuthRequestInterceptor implements RequestInterceptor {

    private final BearerTokenResolver tokenResolver;
    /**
     * Create a template with the header of provided name and extracted extract </br>
     *
     * 1. 如果使用 非web 请求，header 区别 </br>
     *
     * 2. 根据authentication 还原请求token
     * @param template
     */
    @Override
    public void apply(RequestTemplate template) {
        Collection<String> fromHeader = template.headers().get(SecurityConstants.FROM);
        // 带from 请求直接跳过
        if (CollUtil.isNotEmpty(fromHeader) && fromHeader.contains(SecurityConstants.FROM_IN)) {
            return;
        }

        // 非web 请求直接跳过
        if (!WebUtils.getRequest().isPresent()) {
            return;
        }
        HttpServletRequest request = WebUtils.getRequest().get();
        // 避免请求参数的 query token 无法传递
        String token = tokenResolver.resolve(request);
        if (StringUtils.isBlank(token)) {
            return;
        }
        template.header(HttpHeaders.AUTHORIZATION,
                String.format("%s %s", OAuth2AccessToken.TokenType.BEARER.getValue(), token));
    }
}
