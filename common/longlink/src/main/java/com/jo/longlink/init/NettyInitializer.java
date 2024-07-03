package com.jo.longlink.init;

import com.jo.longlink.handler.WebSocketHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.stream.ChunkedWriteHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author xtc
 * @date 2023/6/21
 */
@Component
public class NettyInitializer extends ChannelInitializer<SocketChannel> {

    static final String WEBSOCKET_PROTOCOL = "WebSocket";

    String websocketPath = "localhost:8888/webSocket";

    @Autowired
    WebSocketHandler webSocketHandler;
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast("httpServerCodec",new HttpServerCodec());
        pipeline.addLast(new ObjectEncoder());

        pipeline.addLast(new ChunkedWriteHandler());

        pipeline.addLast(new HttpObjectAggregator(8192));

//        pipeline.addLast(new WebSocketServerProtocolHandler(websocketPath,WEBSOCKET_PROTOCOL,true,65536 * 10));

        pipeline.addLast(webSocketHandler);
    }
}
