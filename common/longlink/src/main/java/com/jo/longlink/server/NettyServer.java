package com.jo.longlink.server;

import com.jo.longlink.init.NettyInitializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.net.InetSocketAddress;

/**
 * @author xtc
 * @date 2023/6/21
 */
@Component
public class NettyServer {
    static final Logger log = LoggerFactory.getLogger(NettyServer.class);
    int port = 8888;

    EventLoopGroup bossGroup;
    EventLoopGroup workGroup;

    @Autowired
    NettyInitializer nettyInitializer;

    @PostConstruct
    public void start() {
        new Thread(() -> {
            bossGroup = new NioEventLoopGroup();
            workGroup = new NioEventLoopGroup();

            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workGroup);

            bootstrap.channel(NioServerSocketChannel.class);

            bootstrap.localAddress(new InetSocketAddress(port));

            bootstrap.childHandler(nettyInitializer);

            ChannelFuture future ;

            try {
                future = bootstrap.bind().sync();
                log.info("Server started and listen on:{}", future.channel().localAddress());
                future.channel().closeFuture().sync();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    @PreDestroy
    public void destroy() throws InterruptedException {
        if (bossGroup != null){
            bossGroup.shutdownGracefully().sync();
        }
        if (workGroup != null){
            workGroup.shutdownGracefully().sync();
        }
    }
}
