package com.jo.support.support.annotation;

import com.jo.support.support.registry.RpcComponentRegistrar;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author Jo
 * @date 2024/7/10
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Import(RpcComponentRegistrar.class)
@Documented
public @interface EnableRpc {
    /**
     * 需扫描包路径, 需包含@RpcRoute 和 @RpcClient注解的类
     */
    String[] basePackages() default {};
}
