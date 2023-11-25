package com.jo.common.security.compnent;

import cn.hutool.extra.spring.SpringUtil;
import com.jo.common.core.constant.SecurityConstants;
import com.jo.common.security.service.JoUser;
import com.jo.common.security.service.JoUserDetailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.resource.InvalidBearerTokenException;
import org.springframework.security.oauth2.server.resource.introspection.OpaqueTokenIntrospector;

import java.security.Principal;
import java.util.Comparator;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * @author xtc
 * @date 2023/11/17
 */
@Slf4j
public class JoCustomOpaqueTokenIntrospector implements OpaqueTokenIntrospector {

    private final OAuth2AuthorizationService authorizationService;

    public JoCustomOpaqueTokenIntrospector(OAuth2AuthorizationService authorizationService) {
        this.authorizationService = authorizationService;
    }

    @Override
    public OAuth2AuthenticatedPrincipal introspect(String token) {
        OAuth2Authorization oldAuthorization = authorizationService.findByToken(token, OAuth2TokenType.ACCESS_TOKEN);
        if (Objects.isNull(oldAuthorization)) {
            throw new InvalidBearerTokenException(token);
        }

//        // 客户端模式默认返回
//        if (AuthorizationGrantType.CLIENT_CREDENTIALS.equals(oldAuthorization.getAuthorizationGrantType())) {
//            return new DefaultOAuth2AuthenticatedPrincipal(oldAuthorization.getPrincipalName(),
//                    oldAuthorization.getAttributes(), AuthorityUtils.NO_AUTHORITIES);
//        }

        Map<String, JoUserDetailService> userDetailsServiceMap = SpringUtil
                .getBeansOfType(JoUserDetailService.class);

        Optional<JoUserDetailService> optional = userDetailsServiceMap.values()
                .stream()
                .filter(service -> service.support(Objects.requireNonNull(oldAuthorization).getRegisteredClientId(),
                        oldAuthorization.getAuthorizationGrantType().getValue()))
                .max(Comparator.comparingInt(Ordered::getOrder));

        UserDetails userDetails = null;
        try {
            Object principal = Objects.requireNonNull(oldAuthorization).getAttributes().get(Principal.class.getName());
            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = (UsernamePasswordAuthenticationToken) principal;
            Object tokenPrincipal = usernamePasswordAuthenticationToken.getPrincipal();
            userDetails = optional.get().loadUserByUser((JoUser) tokenPrincipal);
        }
        catch (UsernameNotFoundException notFoundException) {
            log.warn("用户不不存在 {}", notFoundException.getLocalizedMessage());
            throw notFoundException;
        }
        catch (Exception ex) {
            log.error("资源服务器 introspect Token error {}", ex.getLocalizedMessage());
        }

        // 注入扩展属性,方便上下文获取客户端ID
        JoUser user = (JoUser) userDetails;
        Objects.requireNonNull(user)
                .getAttributes()
                .put(SecurityConstants.CLIENT_ID, oldAuthorization.getRegisteredClientId());
        return user;
    }
}
