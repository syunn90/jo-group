package com.jo.rpc.task;

import com.jo.rpc.comm.net.HostAndPort;
import com.jo.rpc.node.INodeManager;
import com.jo.rpc.node.NodeStatus;

import java.util.Map;

/**
 * @author Jo
 * @date 2024/7/8
 */
public class NodeHealthCheckTask implements Runnable{

    private INodeManager nodeManager;

    public NodeHealthCheckTask(INodeManager nodeManager) {
        this.nodeManager = nodeManager;
    }

    @Override
    public void run() {
        Map<HostAndPort, NodeStatus> serverStatusMap = nodeManager.getNodeStatusMap();
        for (NodeStatus nodeStatus : serverStatusMap.values()) {
            if (!nodeStatus.isErrorOccurred()) {
                //发送心跳包探测
                boolean available = nodeManager.getClient().sendHeartBeat(nodeStatus.getNode());
                if (Boolean.TRUE.equals(available)) {
                    //心跳成功则重置错误次数
                    nodeStatus.resetErrorTimes();
                }
            }
        }
    }
}
