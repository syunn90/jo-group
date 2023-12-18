package com.jo.gateway.filter;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jo.common.core.constant.SecurityConstants;
import com.jo.common.core.constant.enums.CacheConstants;
import com.jo.common.core.exception.ValidateCodeException;
import com.jo.common.core.util.R;
import com.jo.common.core.util.WebUtils;
import com.jo.gateway.config.GatewayConfigProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import reactor.core.publisher.Mono;

/**
 * @author xtc
 * @date 2023/12/15
 */
@Slf4j
@RequiredArgsConstructor
public class ValidateCodeFilter extends AbstractGatewayFilterFactory<Object> {

    private final GatewayConfigProperties gatewayConfig;
    private final ObjectMapper objectMapper;
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public GatewayFilter apply(Object config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            boolean isAuthToken = CharSequenceUtil.containsAnyIgnoreCase(request.getURI().getPath(),
                    SecurityConstants.OAUTH_TOKEN_URL);

            // 不是登录请求，直接向下执行
            if (!isAuthToken) {
                return chain.filter(exchange);
            }
            // 刷新token，手机号登录（也可以这里进行校验） 直接向下执行
            String grantType = request.getQueryParams().getFirst("grant_type");
            if (StrUtil.equals(SecurityConstants.REFRESH_TOKEN, grantType)) {
                return chain.filter(exchange);
            }

            boolean isIgnoreClient = gatewayConfig.getIgnoreClients().contains(WebUtils.getClientId(request));
            try {
                // only oauth and the request not in ignore clients need check code.
                if (!isIgnoreClient) {
                    checkCode(request);
                }
            }catch (Exception e) {
                ServerHttpResponse response = exchange.getResponse();
                response.setStatusCode(HttpStatus.PRECONDITION_REQUIRED);
                response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

                final String errMsg = e.getMessage();
                return response.writeWith(Mono.create(monoSink -> {
                    try {
                        byte[] bytes = objectMapper.writeValueAsBytes(R.failed(errMsg));
                        DataBuffer dataBuffer = response.bufferFactory().wrap(bytes);

                        monoSink.success(dataBuffer);
                    }
                    catch (JsonProcessingException jsonProcessingException) {
                        log.error("对象输出异常", jsonProcessingException);
                        monoSink.error(jsonProcessingException);
                    }
                }));
            }

            return chain.filter(exchange);
        };
    }
    private void checkCode(ServerHttpRequest request) {
        String code = request.getQueryParams().getFirst("code");

        if (CharSequenceUtil.isBlank(code)) {
            throw new ValidateCodeException("验证码不能为空");
        }

        String randomStr = request.getQueryParams().getFirst("randomStr");
        if (CharSequenceUtil.isBlank(randomStr)) {
            randomStr = request.getQueryParams().getFirst(SecurityConstants.SMS_PARAMETER_NAME);
        }

        String key = CacheConstants.DEFAULT_CODE_KEY + randomStr;

        Object codeObj = redisTemplate.opsForValue().get(key);

        if (ObjectUtil.isEmpty(codeObj) || !code.equals(codeObj)) {
            throw new ValidateCodeException("验证码不合法");
        }

        redisTemplate.delete(key);
    }
}
