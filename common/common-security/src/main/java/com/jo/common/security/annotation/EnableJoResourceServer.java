package com.jo.common.security.annotation;

import com.jo.common.security.compnent.JoResourceServerAutoConfiguration;
import com.jo.common.security.compnent.JoResourceServerConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;

import java.lang.annotation.*;

/**
 * @author Jo
 * @date 2023-11-25
 * <p>
 * 资源服务注解
 */
@Documented
@Inherited
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@EnableGlobalMethodSecurity(prePostEnabled = true)
@Import({ JoResourceServerAutoConfiguration.class, JoResourceServerConfiguration.class })
public @interface EnableJoResourceServer {

}
