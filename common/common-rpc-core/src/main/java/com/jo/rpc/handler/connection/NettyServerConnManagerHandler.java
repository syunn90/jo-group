package com.jo.rpc.handler.connection;

import com.jo.rpc.config.RpcServerConfig;
import com.jo.rpc.connection.Connection;
import com.jo.rpc.connection.IConnectionPool;
import com.jo.rpc.comm.id.IdGenerator;
import com.jo.rpc.comm.net.HostAndPort;
import com.jo.rpc.node.INodeManager;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

import static com.jo.rpc.connection.Connection.CONN;

/**
 * @author Jo
 * @date 2024/7/9
 */
public class NettyServerConnManagerHandler extends AbstractConnManagerHandler{

    private static final Logger logger = LoggerFactory.getLogger(NettyServerConnManagerHandler.class);

    private RpcServerConfig serverConfig;

    public NettyServerConnManagerHandler(INodeManager nodeManager, RpcServerConfig serverConfig) {
        super.nodeManager = nodeManager;
        this.serverConfig = serverConfig;
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("channel register");
        }
        super.channelRegistered(ctx);
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("channel unregister");
        }
        super.channelUnregistered(ctx);
    }

    @Override
    public void close(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
        close(ctx);
        super.close(ctx, promise);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Connection conn = new Connection(IdGenerator.getId(), ctx.channel());
        HostAndPort node = HostAndPort.from((InetSocketAddress) ctx.channel().remoteAddress());
        nodeManager.addNode(node);
        IConnectionPool connectionPool = nodeManager.getConnectionPool(node);
        if (connectionPool != null) {
            connectionPool.addConnection(conn);
        } else {
            logger.warn("connectionPool is null, node:{}", node);
        }
        ctx.channel().attr(CONN).set(conn);
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("channel inactive");
        }
        ctx.channel().close();
        super.channelInactive(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.channel().close();
        super.exceptionCaught(ctx, cause);
    }
}
