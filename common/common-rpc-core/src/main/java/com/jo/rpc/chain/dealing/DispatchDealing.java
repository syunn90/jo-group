package com.jo.rpc.chain.dealing;

import com.jo.rpc.chain.Dealing;
import com.jo.rpc.chain.DealingContext;
import com.jo.rpc.comm.constant.RpcConstant;
import com.jo.rpc.comm.exception.RpcException;
import com.jo.rpc.invoke.ResponseFuture;
import com.jo.rpc.invoke.ResponseMapping;
import com.jo.rpc.protocol.Command;
import com.jo.rpc.protocol.RpcRequest;
import com.jo.rpc.protocol.RpcResponse;
import com.jo.rpc.reflect.RouterFactory;
import com.jo.rpc.reflect.RouterTarget;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 分发处理器
 * @author Jo
 * @date 2024/7/9
 */
public class DispatchDealing implements Dealing {

    private static final Logger logger = LoggerFactory.getLogger(DispatchDealing.class);

    private ResponseMapping responseMapping;

    public DispatchDealing() {
    }

    public DispatchDealing(ResponseMapping responseMapping) {
        this.responseMapping = responseMapping;
    }

    @Override
    public void deal(DealingContext context) {

        Command command = context.getCommand();

        if (command.isRequest()) {
            // 请求分发处理
            requestDispatch((RpcRequest) command, context);
        } else {
            // 响应处理
            responseProcess((RpcResponse) command);
        }
    }

    private void requestDispatch(RpcRequest request, DealingContext context) {
        if (request.isHeartBeat()) {
            heartBeatProcess(request, context);
            return;
        }
        String mapping = request.getMapping();
        if (StringUtils.isBlank(mapping)) {
            throw new RpcException("request mapping is null");
        }
        Object result;
        try {
            // 获取对应router
            RouterTarget target = RouterFactory.getRouter(mapping);
            result = target.invoke(request);
        } catch (Exception e) {
            logger.error("error occurred on the RpcServer", e);
            context.getConnection().send(RpcResponse.serverError(request.getSeq()));
            return;
        }
        // 响应
        context.getConnection().send(RpcResponse.success(request.getSeq(), request.getMapping(), result));
    }

    private void responseProcess(RpcResponse response) {
        ResponseFuture responseFuture = responseMapping.getResponseFuture(response.getSeq());
        if (responseFuture == null) {
            // 获取不到，可能是服务端处理超时
            if (logger.isWarnEnabled()) {
                logger.warn("Response mismatch request, seq: {}, mapping: {}", response.getSeq(), response.getMapping());
            }
            return;
        }
        // 设置响应内容，用于客户端获取
        responseFuture.setRpcResponse(response);
        // 恢复客户端调用线程，并执行回调
        responseFuture.receipt();
    }

    private void heartBeatProcess(RpcRequest command, DealingContext context) {
        Object[] args = command.getArgs();
        if (args == null || args.length == 0) {
            throw new RpcException("heartBeat packet body null");
        }
        if (RpcConstant.PING.equals(args[0])) {
            //服务端收到ping处理
            if (context.isPrintHeartbeatInfo()) {
                logger.info("[heartBeat]connection:{} receive a heartbeat packet from client", context.getConnection().getId());
            }
            RpcResponse pong = new RpcResponse();
            pong.setSeq(command.getSeq());
            pong.setHeartBeat(true);
            pong.setBody(RpcConstant.PONG);
            context.getConnection().send(pong);
        }
    }
}
