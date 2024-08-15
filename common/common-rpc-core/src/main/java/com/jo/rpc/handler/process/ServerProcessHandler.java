package com.jo.rpc.handler.process;

import com.jo.rpc.chain.DealingChain;
import com.jo.rpc.chain.DealingContext;
import com.jo.rpc.chain.dealing.DispatchDealing;
import com.jo.rpc.chain.dealing.DuplicateDealing;
import com.jo.rpc.config.RpcServerConfig;
import com.jo.rpc.extension.DuplicatedMarker;
import com.jo.rpc.node.INodeManager;
import com.jo.rpc.protocol.Command;
import io.netty.channel.ChannelHandlerContext;

import java.util.concurrent.ExecutorService;

import static com.jo.rpc.connection.Connection.CONN;

/**
 * @author Jo
 * @date 2024/7/9
 */
public class ServerProcessHandler extends AbstractProcessHandler {

    private final RpcServerConfig config;
    private final DuplicatedMarker duplicatedMarker;
    private final ExecutorService businessExecutor;

    public ServerProcessHandler(INodeManager nodeManager, DuplicatedMarker duplicatedMarker,
                                RpcServerConfig config, ExecutorService businessExecutor) {
        super(nodeManager);
        this.duplicatedMarker = duplicatedMarker;
        this.config = config;
        this.businessExecutor = businessExecutor;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Command command) throws Exception {
        // 生成处理责任链
        DealingChain chain = new DealingChain();
        if (duplicatedMarker != null) {
            chain.addDealing(new DuplicateDealing(duplicatedMarker));
        }
        chain.addDealing(new DispatchDealing());
        // 上下文，携带消息内容
        DealingContext context = new DealingContext();
        context.setClient(false);
        context.setCommand(command);
        context.setDealingChain(chain);
        context.setNodeManager(nodeManager);
        context.setConnection(ctx.channel().attr(CONN).get());
        context.setPrintHeartbeatInfo(config.getPrintHearBeatPacketInfo());
        // 开始执行责任链
        if (businessExecutor != null) {
            businessExecutor.execute(() -> chain.deal(context));
        } else {
            chain.deal(context);
        }
    }
}
