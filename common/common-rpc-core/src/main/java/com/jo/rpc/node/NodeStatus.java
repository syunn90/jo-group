package com.jo.rpc.node;

import com.jo.rpc.comm.net.HostAndPort;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Jo
 * @date 2024/7/8
 */
public class NodeStatus {

    private HostAndPort node;
    private Integer maxErrorTimes;
    private AtomicInteger errorTimes = new AtomicInteger(0);

    public NodeStatus(HostAndPort node, Integer maxErrorTimes) {
        this.node = node;
        this.maxErrorTimes = maxErrorTimes;
    }

    public HostAndPort getNode() {
        return node;
    }

    public void setNode(HostAndPort node) {
        this.node = node;
    }

    public int getErrorTimes() {
        return errorTimes.get();
    }

    /**
     * 错误次数+1
     */
    public int errorTimesInc() {
        return errorTimes.incrementAndGet();
    }

    /**
     * 节点是否可用
     */
    public boolean isAvailable() {
        return errorTimes.get() < maxErrorTimes;
    }

    /**
     * 是否发生过错误
     */
    public boolean isErrorOccurred() {
        return errorTimes.get() > 0;
    }

    /**
     * 重置错误次数
     */
    public void resetErrorTimes() {
        errorTimes.set(0);
    }
}
