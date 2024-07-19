package com.jo.support.support.processor;

import com.jo.rpc.Client;
import com.jo.rpc.Server;
import com.jo.rpc.comm.exception.RpcException;
import com.jo.rpc.reflect.RouterFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.Set;

/**
 * @author Jo
 * @date 2024/7/10
 */
public class RpcPostProcessor implements BeanPostProcessor , ApplicationContextAware {

    private static final Logger logger = LoggerFactory.getLogger(RpcPostProcessor.class);

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        try {
            if (bean instanceof Client) {
                ((Client) bean).start();
            }
            if (bean instanceof Server) {
                Set<Class<?>> routeClasses = RouterFactory.getAllRouteClasses();
                for (Class<?> routeClass : routeClasses) {
                    Object routeBean = applicationContext.getBean(routeClass);
                    RouterFactory.addRouteInstance(routeClass, routeBean);
                    RouterFactory.register(routeClass);
                }
                ((Server) bean).start();
            }
        } catch (RpcException e) {
            logger.error("Rpc postProcess failed", e);
        }
        return bean;
    }
}
