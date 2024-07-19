package com.jo.rpc.invoke;

import com.jo.rpc.protocol.RpcResponse;

/**
 * @author Jo
 * @date 2024/7/8
 */
@FunctionalInterface
public interface RpcCallback {

    void callback(RpcResponse response);
}
