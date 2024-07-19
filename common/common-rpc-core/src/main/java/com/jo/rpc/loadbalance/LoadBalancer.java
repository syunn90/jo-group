package com.jo.rpc.loadbalance;


import com.jo.rpc.comm.annotation.SPI;
import com.jo.rpc.comm.net.HostAndPort;
import com.jo.rpc.protocol.RpcRequest;

import java.util.List;

/**
 * @author Jo
 * @date 2024/7/8
 */
@SPI
public interface LoadBalancer {

    HostAndPort selectNode(List<HostAndPort> nodes, RpcRequest request);

}
