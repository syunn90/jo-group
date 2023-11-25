package com.jo.common.security.component;

import cn.hutool.core.util.StrUtil;
import com.jo.common.core.constant.SecurityConstants;
import com.jo.common.security.annotation.Inner;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotationUtils;

import javax.servlet.http.HttpServletRequest;
import java.nio.file.AccessDeniedException;

/**
 * @author xtc
 * @date 2023/11/25
 */
@Aspect
@RequiredArgsConstructor
public class InnerAspect implements Ordered {

    private final HttpServletRequest request;

    @SneakyThrows
    @Before("@within(inner) || @annotation(inner)")
    public void around(JoinPoint point, Inner inner)  {
        if (inner == null){
            Class<?> clazz = point.getTarget().getClass();
            inner = AnnotationUtils.findAnnotation(clazz,Inner.class);
        }
        String header = request.getHeader(SecurityConstants.FROM);
        if (inner.value() && !StrUtil.equals(SecurityConstants.FROM_IN,header)){
            throw new AccessDeniedException("Access denied");
        }
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 1;
    }
}
