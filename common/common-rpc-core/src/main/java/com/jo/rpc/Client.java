package com.jo.rpc;

import com.jo.rpc.connection.IConnection;
import com.jo.rpc.comm.net.HostAndPort;
import com.jo.rpc.protocol.RpcResponse;

/**
 * @author Jo
 * @date 2024/7/8
 */
public interface Client {


    Client start();

    /**
     * 根据节点发送心跳，探测节点是否能访问
     *
     * @param nodes 节点地址端口
     * @return true:节点间通信正常  false:节点间通信失败
     */
    boolean sendHeartBeat(HostAndPort nodes);

    /**
     * 指定连接发送心跳，探测节点是否能访问
     *
     * @param connection 与节点的连接
     * @return true:连接通信正常  false:连接通信失败
     */
    boolean sendHeartBeat(IConnection connection);

    /**
     * 停止客户端
     */
    void stop();

    /**
     * 同步调用, 并将成功响应的args自动转换为T类型，指定节点
     *
     * @param mapping    服务端对应处理器的mapping标识，服务端@mapping注解的值
     * @param args       请求实体列表
     * @param resultType 响应实体类型
     * @param nodes      指定多个服务端节点
     * @param retryTimes 失败重试次数
     * @return 转换后的args响应内容实体
     */
    <T> T invoke(String mapping, Class<T> resultType, int retryTimes, Object[] args, HostAndPort... nodes);

    /**
     * 同步调用，返回整个响应内容，指定rpc服务节点，带超时重试机制
     *
     * @param mapping    服务端对应处理器的mapping标识，服务端@mapping注解的值
     * @param args       请求实体列表
     * @param nodes      指定多个服务端节点
     * @param retryTimes 失败重试次数
     * @return 响应内容
     */
    RpcResponse invoke(String mapping, int retryTimes, Object[] args, HostAndPort... nodes);

}
