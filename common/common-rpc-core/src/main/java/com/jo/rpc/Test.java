package com.jo.rpc;

import com.jo.rpc.client.RpcClient;
import com.jo.rpc.config.RpcClientConfig;

/**
 * @author Jo
 * @date 2024/7/9
 */
public class Test {

    public static void main(String[] args) {

        RpcClient.builder().config(new RpcClientConfig()).start();
    }
}
