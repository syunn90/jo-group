package com.jo.rpc.handler.process;

import com.google.common.base.Throwables;
import com.jo.rpc.node.INodeManager;
import com.jo.rpc.protocol.Command;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Jo
 * @date 2024/7/9
 */
public abstract class AbstractProcessHandler extends SimpleChannelInboundHandler<Command> {


    private static final Logger logger = LoggerFactory.getLogger(AbstractProcessHandler.class);

    protected INodeManager nodeManager;

    public AbstractProcessHandler(INodeManager nodeManager) {
        this.nodeManager = nodeManager;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("Rpc exceptionCaught {}, {}", ctx.channel().remoteAddress(), Throwables.getStackTraceAsString(cause));
    }

}
