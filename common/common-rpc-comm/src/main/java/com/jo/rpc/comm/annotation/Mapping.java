package com.jo.rpc.comm.annotation;

import java.lang.annotation.*;

/**
 * @author Jo
 * @date 2024/7/9
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Mapping {
    String value() default "";
}
