package com.jo.rpc.handler.connection;

import com.jo.rpc.connection.IConnection;
import com.jo.rpc.connection.IConnectionPool;
import com.jo.rpc.comm.net.HostAndPort;
import com.jo.rpc.node.INodeManager;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;

import java.net.InetSocketAddress;

import static com.jo.rpc.connection.Connection.CONN;

/**
 * @author Jo
 * @date 2024/7/9
 */
public class AbstractConnManagerHandler extends ChannelDuplexHandler {

    INodeManager nodeManager;

    void close(ChannelHandlerContext ctx) {
        //获取连接
        IConnection connection = ctx.channel().attr(CONN).get();

        HostAndPort node = HostAndPort.from((InetSocketAddress) ctx.channel().remoteAddress());
        //获取对应节点的连接池
        IConnectionPool connectionPool = nodeManager.getConnectionPool(node);
        //关闭连接
        connectionPool.releaseConnection(connection.getId());
    }

}
