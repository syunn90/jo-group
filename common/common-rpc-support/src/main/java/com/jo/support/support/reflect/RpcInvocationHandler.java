package com.jo.support.support.reflect;

import com.jo.rpc.Client;
import com.jo.rpc.comm.annotation.Mapping;
import com.jo.rpc.comm.annotation.RpcClient;
import com.jo.rpc.comm.net.HostAndPort;
import com.jo.rpc.comm.utils.MappingUtil;
import com.jo.support.support.registry.RpcServerAddressRegistry;
import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Jo
 * @date 2024/7/10
 */
public class RpcInvocationHandler implements InvocationHandler {

    private static final Logger logger = LoggerFactory.getLogger(RpcInvocationHandler.class);

    private Client client;
    private Class<?> type;
    private String typeName;
    private int timeoutRetryTimes;
    private Map<Method, RouterWrapper> methodCache = new HashMap<>(4);

    public RpcInvocationHandler(ApplicationContext applicationContext, Class<?> type) {
        this.type = type;
        this.client = applicationContext.getBean(Client.class);
        resolveType();
    }
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        RouterWrapper routerWrapper = methodCache.get(method);
        if (routerWrapper != null) {
            String mapping = routerWrapper.getRouterMapping();
            Class<?> returnType = routerWrapper.getReturnType();
            String serviceName = RpcServerAddressRegistry.getServiceName(typeName);
//            if (StringUtils.isNotBlank(serviceName)) {
//                return client.invokeWithRegistry(mapping, returnType, serviceName, timeoutRetryTimes, args);
//            }
            List<HostAndPort> hostAndPorts = RpcServerAddressRegistry.getHostAndPorts(typeName);
            return client.invoke(mapping, returnType, timeoutRetryTimes, args, hostAndPorts.toArray(new HostAndPort[]{}));
        }
        return ignoreMethodProcess(method, args);
    }
    private void resolveType() {
        this.typeName = type.getCanonicalName();
        RpcClient annotation = type.getAnnotation(RpcClient.class);
        int retryTimes = annotation.retryTimes();
        if (retryTimes < 0 && logger.isWarnEnabled()) {
            logger.warn("Class {} annotation @RpcClient with illegal retryTimes :{}, reset to 0",
                    typeName, retryTimes);
            retryTimes = 0;
        }
        timeoutRetryTimes = retryTimes;
        Method[] declaredMethods = type.getDeclaredMethods();
        for (Method method : declaredMethods) {
            RouterWrapper wrapper = new RouterWrapper();
            if (method.isAnnotationPresent(Mapping.class)) {
                Mapping routeMapping = method.getAnnotation(Mapping.class);
                String mapping = routeMapping.value();
                if (mapping.length() == 0) {
                    logger.error("Class {} Method {} does not have clearly Mapping", typeName, method);
                    continue;
                }
                wrapper.setRouterMapping(mapping);
            } else {
                //没有手动指定mapping的话将根据type和method生成唯一标识
                String mapping = MappingUtil.generateMapping(type, method);
                wrapper.setRouterMapping(mapping);
            }
            wrapper.setReturnType(method.getReturnType());
            methodCache.put(method, wrapper);
        }

        if (MapUtils.isEmpty(methodCache) && logger.isWarnEnabled()) {
            logger.warn("The method of the Class {} did not find any @Mapping annotation", typeName);
        }

    }

    private Object ignoreMethodProcess(Method method, Object[] args) {
        switch (method.getName()) {
            case "equals":
                return this.equals(args[0]);
            case "toString":
                return this.toString();
            case "hashCode":
                return this.hashCode();
            default:
                logger.error("invoke error, Method {} is invalid, args: {}", method.getName(), args);
                return null;
        }
    }

    static class RouterWrapper {
        /**
         * 映射路径
         */
        private String routerMapping;
        /**
         * 返回类型
         */
        private Class<?> returnType;

        String getRouterMapping() {
            return routerMapping;
        }

        RouterWrapper setRouterMapping(String routerMapping) {
            this.routerMapping = routerMapping;
            return this;
        }

        Class<?> getReturnType() {
            return returnType;
        }

        RouterWrapper setReturnType(Class<?> returnType) {
            this.returnType = returnType;
            return this;
        }
    }
}
