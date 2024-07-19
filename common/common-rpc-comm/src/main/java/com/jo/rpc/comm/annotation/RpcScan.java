package com.jo.rpc.comm.annotation;

import java.lang.annotation.*;

/**
 * 路由扫描
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface RpcScan {
    /**
     * 自定义需要扫描的包, 默认为当前类所在的包名
     */
    String[] value() default {};
}
