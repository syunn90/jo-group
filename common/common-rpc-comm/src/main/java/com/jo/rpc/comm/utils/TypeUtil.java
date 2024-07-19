package com.jo.rpc.comm.utils;

import com.jo.rpc.comm.exception.RpcException;

/**
 * @author Jo
 * @date 2024/7/10
 */
public class TypeUtil {

    public static <T> T convert(Object object, Class<T> clazz, String errorMsg) {
        if (clazz == null || clazz.isAssignableFrom(Void.TYPE)) {
            return null;
        }
        if (clazz.isInstance(object)) {
            return (T) object;
        } else {
            throw new RpcException(errorMsg);
        }
    }
}
