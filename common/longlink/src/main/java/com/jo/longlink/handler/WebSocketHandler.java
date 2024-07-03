package com.jo.longlink.handler;

import com.jo.longlink.config.NettyConfig;
import com.jo.longlink.server.NettyServer;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.util.AttributeKey;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Date;

import static io.netty.handler.codec.http.HttpUtil.isKeepAlive;
import static io.netty.handler.codec.http.HttpUtil.setContentLength;

/**
 * @author xtc
 * @date 2023/6/21
 */
@Component
@ChannelHandler.Sharable
public class WebSocketHandler extends SimpleChannelInboundHandler<Object> {
    private static final Logger log = LoggerFactory.getLogger(NettyServer.class);

    private WebSocketServerHandshaker webSocketServerHandshaker;


    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        log.info("有新的客户端链接：[{}]", ctx.channel().id().asLongText());
        NettyConfig.getChannelGroup().add(ctx.channel());
    }
//
//    @Override
//    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//        super.channelRead(ctx, msg);
//    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {

        log.info("服务器收到消息：{}", msg);

        if (msg instanceof FullHttpRequest){
            handleHttpRequest(ctx,(FullHttpRequest)msg);
        }else if (msg instanceof WebSocketFrame){
            handleWebSocketFrame(ctx, (WebSocketFrame)msg);
        }
    }

    private void handleWebSocketFrame(ChannelHandlerContext ctx, WebSocketFrame webSocketFrame) {
        // 判断是否是关闭链路的指令
        if (webSocketFrame instanceof CloseWebSocketFrame) {
            webSocketServerHandshaker.close(ctx.channel(), (CloseWebSocketFrame) webSocketFrame.retain());
            return;
        }

        // 判断是否是Ping消息
        if (webSocketFrame instanceof PingWebSocketFrame) {
            ctx.channel().write(new PongWebSocketFrame(webSocketFrame.content().retain()));
            return;
        }

        // 只支持文本消息
        if (!(webSocketFrame instanceof TextWebSocketFrame)) {
            throw new UnsupportedClassVersionError(String.format("%s frame type is not supported",
                    webSocketFrame.getClass().getName()));
        }

        // 返回应答消息
        String request = ((TextWebSocketFrame)webSocketFrame).text();
        String response = "\r\n【Server】: ";
        if ("你好".equals(request)) {
            response = response + "你好, 我是Netty Server";
        } else if (request.contains("几点")) {
            response = response + ", 现在时间是" + new Date().toString();
        }
        response = response + "\r\n";
        ctx.channel().write(new TextWebSocketFrame(response));
    }

    private void handleHttpRequest(ChannelHandlerContext ctx, FullHttpRequest fullHttpRequest) {
        // HTTP解码失败, 返回HTTP异常
        if (!fullHttpRequest.getDecoderResult().isSuccess()
                || !("websocket").equals(fullHttpRequest.headers().get("Upgrade"))) {
            sendHttpResponse(ctx, fullHttpRequest, new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST));
            return;
        }

        // 构建握手工厂, 创建握手处理类, 建立浏览器与服务器的通道, 进行消息处理
        WebSocketServerHandshakerFactory webSocketServerFactory =
                new WebSocketServerHandshakerFactory("ws://localhost:8888/webSocket", null, false);
        webSocketServerHandshaker = webSocketServerFactory.newHandshaker(fullHttpRequest);
        if (webSocketServerHandshaker == null) {
            // 异常
            WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
        } else {
            //  处理WebSocket消息
            webSocketServerHandshaker.handshake(ctx.channel(), fullHttpRequest);
        }
    }
    private static void sendHttpResponse(ChannelHandlerContext ctx, FullHttpRequest request, FullHttpResponse response) {

        // 返回响应给客户端
        if (response.status().code() != 200) {
            ByteBuf byteBuf = Unpooled.copiedBuffer(response.status().toString(), CharsetUtil.UTF_8);
            response.content().writeBytes(byteBuf);
            byteBuf.release();
            setContentLength(response, response.content().readableBytes());
        }

        // 非Keep-Alive, 关闭连接
        ChannelFuture future = ctx.channel().writeAndFlush(response);
        if (isKeepAlive(response) || response.status().code() != 200) {
            future.addListener(ChannelFutureListener.CLOSE);
        }


    }
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        log.info("用户下线了:{}", ctx.channel().id().asLongText());
        NettyConfig.getChannelGroup().remove(ctx.channel());
        removeUserId(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.info("异常：{}", cause.getMessage());
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    private void removeUserId(ChannelHandlerContext ctx) {
        AttributeKey<String> key = AttributeKey.valueOf("userId");
        String userId = ctx.channel().attr(key).get();
        NettyConfig.getChannelMap().remove(userId);
    }
}
