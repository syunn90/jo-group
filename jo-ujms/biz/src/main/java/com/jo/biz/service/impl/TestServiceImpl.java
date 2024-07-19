package com.jo.biz.service.impl;

import com.jo.api.rpc.TestService;
import com.jo.rpc.comm.annotation.RpcRoute;

/**
 * @author Jo
 * @date 2024/7/11
 */
@RpcRoute
public class TestServiceImpl implements TestService {

    @Override
    public void testRpc() {
        System.out.println("rpc-----------");
    }
}
