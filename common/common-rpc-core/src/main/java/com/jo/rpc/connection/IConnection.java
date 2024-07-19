package com.jo.rpc.connection;

import com.jo.rpc.comm.net.HostAndPort;
import com.jo.rpc.protocol.Command;

/**
 * @author Jo
 * @date 2024/7/8
 */
public interface IConnection {

    /**
     * 获取连接id
     *
     * @return 连接id
     */
    Long getId();

    /**
     * 关闭连接
     */
    void close();

    /**
     * 连接是否可用
     *
     * @return
     */
    boolean isAvailable();

    /**
     * 连接对端地址
     *
     * @return
     */
    HostAndPort getRemoteAddress();

    /**
     * 发送指令
     *
     * @param command
     */
    void send(Command command);

    /**
     * 获取最后一次发送时间
     *
     * @return
     */
    long getLastSendTime();
}
