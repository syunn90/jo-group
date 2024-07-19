package com.jo.rpc.handler.process;

import com.jo.rpc.chain.DealingChain;
import com.jo.rpc.chain.DealingContext;
import com.jo.rpc.chain.dealing.DispatchDealing;
import com.jo.rpc.config.RpcClientConfig;
import com.jo.rpc.invoke.ResponseMapping;
import com.jo.rpc.node.INodeManager;
import com.jo.rpc.protocol.Command;
import io.netty.channel.ChannelHandlerContext;

import static com.jo.rpc.connection.Connection.CONN;

/**
 * @author Jo
 * @date 2024/7/9
 */
public class ClientProcessHandler extends AbstractProcessHandler {

    private RpcClientConfig config;
    private ResponseMapping responseMapping;

    public ClientProcessHandler(INodeManager nodeManager, ResponseMapping responseMapping, RpcClientConfig config) {
        super(nodeManager);
        this.responseMapping = responseMapping;
        this.config = config;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Command command) throws Exception {
        // 生成处理责任链
        DealingChain chain = new DealingChain();

        chain.addDealing(new DispatchDealing(responseMapping));
        // 上下文，携带消息内容
        DealingContext context = new DealingContext();
        context.setClient(true);
        context.setCommand(command);
        context.setDealingChain(chain);
        context.setNodeManager(nodeManager);
        context.setConnection(channelHandlerContext.channel().attr(CONN).get());

        // 开始执行责任链
        chain.deal(context);
    }
}
