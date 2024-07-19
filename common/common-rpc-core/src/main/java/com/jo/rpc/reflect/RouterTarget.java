package com.jo.rpc.reflect;

import com.google.common.base.Throwables;
import com.jo.rpc.comm.exception.RpcException;
import com.jo.rpc.protocol.RpcRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author Jo
 * @date 2024/7/9
 */
public class RouterTarget {
    private static Logger logger = LoggerFactory.getLogger(RouterTarget.class);

    private Object router;
    private Method method;

    public RouterTarget(Object router, Method method) {
        this.router = router;
        this.method = method;
    }

    public Object invoke(RpcRequest request) {
        if (router == null || method == null) {
            throw new RpcException("router instance or method is null");
        }

        Object result = null;
        try {
            result = method.invoke(router, request.getArgs());
        } catch (IllegalAccessException | InvocationTargetException e) {
            logger.error(Throwables.getStackTraceAsString(e));
        }
        return result;
    }
}
