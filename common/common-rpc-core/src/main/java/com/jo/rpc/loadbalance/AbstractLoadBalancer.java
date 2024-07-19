package com.jo.rpc.loadbalance;

import com.jo.rpc.comm.net.HostAndPort;
import com.jo.rpc.protocol.RpcRequest;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @author Jo
 * @date 2024/7/8
 */
public abstract class AbstractLoadBalancer implements LoadBalancer {

    @Override
    public HostAndPort selectNode(List<HostAndPort> nodes, RpcRequest request) {
        if (CollectionUtils.isEmpty(nodes)) {
            return null;
        }
        if (nodes.size() == 1) {
            return nodes.get(0);
        }
        return doSelect(nodes, request);
    }

    protected abstract HostAndPort doSelect(List<HostAndPort> nodes, RpcRequest request);

}
