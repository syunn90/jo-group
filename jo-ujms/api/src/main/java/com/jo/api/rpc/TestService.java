package com.jo.api.rpc;

import com.jo.rpc.comm.annotation.RpcClient;

/**
 * @author Jo
 * @date 2024/7/11
 */
@RpcClient(nodes = "192.168.50.6:9957")
public interface TestService {

    String testRpc();
}
