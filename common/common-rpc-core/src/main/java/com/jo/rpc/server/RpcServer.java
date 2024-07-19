package com.jo.rpc.server;

import com.google.common.base.Throwables;
import com.jo.rpc.AbstractRpc;
import com.jo.rpc.Server;
import com.jo.rpc.codec.RpcPacketDecoder;
import com.jo.rpc.codec.RpcPacketEncoder;
import com.jo.rpc.comm.constant.RpcConstant;
import com.jo.rpc.comm.exception.RegistryException;
import com.jo.rpc.comm.exception.RpcException;
import com.jo.rpc.comm.spi.ExtensionLoader;
import com.jo.rpc.comm.thread.RpcThreadFactory;
import com.jo.rpc.comm.utils.ThreadUtil;
import com.jo.rpc.config.RpcServerConfig;
import com.jo.rpc.extension.DefaultDuplicateMarker;
import com.jo.rpc.extension.DuplicatedMarker;
import com.jo.rpc.handler.connection.NettyServerConnManagerHandler;
import com.jo.rpc.handler.process.ServerProcessHandler;
import com.jo.rpc.node.INodeManager;
import com.jo.rpc.node.NodeManager;
import com.jo.rpc.reflect.RouteScanner;
import com.jo.rpc.task.ConnectionNumCountTask;
import com.jo.rpc.thread.BusinessThreadPool;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.epoll.EpollChannelOption;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollMode;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Jo
 * @date 2024/7/9
 */
public class RpcServer extends AbstractRpc implements Server {

    private final ServerBootstrap serverBootstrap = new ServerBootstrap();
    private Class<?> primarySource;
    private RpcServerConfig serverConfig;
    private EventLoopGroup eventLoopGroupBoss;
    private EventLoopGroup eventLoopGroupSelector;
    private DefaultEventExecutorGroup defaultEventExecutorGroup;
    private ThreadPoolExecutor businessThreadPool;
    private INodeManager nodeManager = new NodeManager(false);
    private AtomicBoolean isServerStart = new AtomicBoolean(false);
//    private ServicePublisher servicePublisher;
    protected Thread shutdownHook;
    private DuplicatedMarker duplicatedMarker;
    private Integer port;

    private RpcServer() {
    }

    public static RpcServer builder() {
        return new RpcServer();
    }

    @Override
    public Server serverConfig(RpcServerConfig config) {
        this.serverConfig = config;
        return this;
    }

    @Override
    public Server sourceClass(Class<?> source) {
        primarySource = source;
        return this;
    }

    @Override
    public Server configRegistry(String schema, List<String> registryAddress, String serviceName) {
        if (StringUtils.isBlank(serviceName)) {
            throw new RegistryException("serviceName can not be null");
        }
        setConfigRegistry(schema, registryAddress, serviceName);
        return this;
    }

    @Override
    public Server start() {
        if (isServerStart.compareAndSet(false, true)) {
            try {
                //1.初始化配置
                initConfig();

                //2.扫描RpcRoute并注册
                scanRpcRoute();

                //3.初始化rpc服务端
                initServer();

                //4.初始化注册中心配置并发布服务
                registryInitAndPublish();

                //5.注册shutdownHook
                registerShutdownHook(this::stop);

            } catch (Exception e) {
                logger.error("RpcServer started failed");
                stop();
                throw e;
            }
        } else {
            logger.warn("RpcServer has started!");
        }
        return this;
    }
    @Override
    public void stop() {
        if (isServerStart.compareAndSet(true, false)) {
            logger.info("RpcServer stopping...");
            try {
                // 关闭连接管理器
                nodeManager.closeManager();

                if (this.defaultEventExecutorGroup != null) {
                    this.defaultEventExecutorGroup.shutdownGracefully();
                }
                if (this.eventLoopGroupSelector != null) {
                    this.eventLoopGroupSelector.shutdownGracefully();
                }
                if (this.eventLoopGroupBoss != null) {
                    this.eventLoopGroupBoss.shutdownGracefully();
                }
                if (businessThreadPool != null) {
                    ThreadUtil.gracefulShutdown(businessThreadPool, 5);
                }
                //清除注册中心节点
//                clearRpcRegistryService();

            } catch (Exception e) {
                logger.error("RpcServer stop exception, {}", Throwables.getStackTraceAsString(e));
            }
            logger.info("RpcServer stop success");
        } else {
            logger.info("RpcServer already closed");
        }

    }
    /**
     * 注册shutdown hook
     *
     * @param runnable
     */
    protected void registerShutdownHook(Runnable runnable) {
        if (this.shutdownHook == null) {
            this.shutdownHook = new Thread("RpcShutdownHook") {
                @Override
                public void run() {
                    runnable.run();
                }
            };
            Runtime.getRuntime().addShutdownHook(this.shutdownHook);
        }
    }
    private void initConfig() {
        assertNotNull(serverConfig, "serverConfig can't be null, Please confirm that you have configured");
        assertNotNull(primarySource, "sourceClass can't be null, Please confirm that you have configured");
        ExtensionLoader<BusinessThreadPool> loader = ExtensionLoader.getExtensionLoader(BusinessThreadPool.class);
        BusinessThreadPool customBusinessThreadPool = loader.getExtension(RpcConstant.SPI_CUSTOM_IMPL);
        if (customBusinessThreadPool != null) {
            //若自定义了业务线程池则使用自定义的线程池
            businessThreadPool = customBusinessThreadPool.getBusinessThreadPool();
            logger.info("Use the custom businessThreadPool [{}]", businessThreadPool.getClass().getCanonicalName());
        } else {
            if (serverConfig.getBusinessThreads() != null && serverConfig.getBusinessThreads() > 0) {
                Integer coreThreads = serverConfig.getBusinessThreads();
                Integer queueSize = serverConfig.getBusinessQueueSize();
                businessThreadPool = ThreadUtil.getFixThreadPoolExecutor(coreThreads, queueSize,
                        new ThreadPoolExecutor.AbortPolicy(), "rpc-server-business");
            }
        }
        if (port != null) {
            serverConfig.setPort(port);
        }
        if (serverConfig.getPrintConnectionNumInterval() != null && serverConfig.getPrintConnectionNumInterval() > 0) {
            Executors.newSingleThreadScheduledExecutor(RpcThreadFactory.getDefault())
                    .scheduleAtFixedRate(new ConnectionNumCountTask(nodeManager), 5, 60, TimeUnit.SECONDS);
        }
        if (serverConfig.isDeDuplicateEnable()) {
            buildDuplicatedMarker(serverConfig.getDuplicateCheckTime(), serverConfig.getDuplicateMaxSize());
        }
    }
    private void buildDuplicatedMarker(int checkTime, long maxSize) {
        ExtensionLoader<DuplicatedMarker> loader = ExtensionLoader.getExtensionLoader(DuplicatedMarker.class);
        DuplicatedMarker customDuplicatedMarker = loader.getExtension(RpcConstant.SPI_CUSTOM_IMPL);
        if ((this.duplicatedMarker = customDuplicatedMarker) == null) {
            this.duplicatedMarker = new DefaultDuplicateMarker();
        } else {
            logger.info("Use the custom DuplicateMarker [{}]", duplicatedMarker.getClass().getCanonicalName());
        }
        this.duplicatedMarker.initMarkerConfig(checkTime, maxSize);
    }
    private void scanRpcRoute() {
        if (!this.primarySource.equals(Void.class)) {
            logger.info("RpcRouter scanning ...");
            new RouteScanner(this.primarySource).san();
        }
    }
    private void initServer() {
        logger.info("RpcServer server init");
        if (useEpoll()) {
            this.eventLoopGroupBoss = new EpollEventLoopGroup(1);
            this.eventLoopGroupSelector = new EpollEventLoopGroup(IO_THREADS);
        } else {
            this.eventLoopGroupBoss = new NioEventLoopGroup(1);
            this.eventLoopGroupSelector = new NioEventLoopGroup(IO_THREADS);
        }

        this.defaultEventExecutorGroup = new DefaultEventExecutorGroup(serverConfig.getChannelWorkerThreads());
        // 流控
        buildTrafficMonitor(defaultEventExecutorGroup,
                serverConfig.isTrafficMonitorEnable(), serverConfig.getMaxReadSpeed(), serverConfig.getMaxWriteSpeed());

        //tls加密
        if (serverConfig.getUseTLS() != null && serverConfig.getUseTLS()) {
            try {
                buildSSLContext(false, serverConfig);
            } catch (Exception e) {
                throw new RpcException("rpcServer initialize SSLContext fail!", e);
            }
        }

        this.serverBootstrap.group(this.eventLoopGroupBoss, this.eventLoopGroupSelector)
                .channel(useEpoll() ? EpollServerSocketChannel.class : NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 2048)
                .option(ChannelOption.SO_REUSEADDR, true)
                .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                .childOption(ChannelOption.SO_KEEPALIVE, false)
                .childOption(ChannelOption.TCP_NODELAY, true)
                .childOption(ChannelOption.SO_SNDBUF, serverConfig.getSendBuf())
                .childOption(ChannelOption.SO_RCVBUF, serverConfig.getReceiveBuf())
                .childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                .childOption(ChannelOption.WRITE_BUFFER_WATER_MARK,
                        new WriteBufferWaterMark(serverConfig.getLowWaterLevel(), serverConfig.getHighWaterLevel()))
                .childHandler(new ServerChannel());

        if (useEpoll()) {
            this.serverBootstrap.option(EpollChannelOption.EPOLL_MODE, EpollMode.EDGE_TRIGGERED);
        }

        Integer bindPort = this.serverConfig.getPort();

        try {
            this.serverBootstrap.bind(bindPort).sync();
        } catch (InterruptedException e) {
            throw new RpcException("RpcServer bind Interrupted!", e);
        }

        if (logger.isInfoEnabled()) {
            logger.info("RpcServer started success, Listening port:[{}]", bindPort);
        }
    }
    /**
     * RPC服务端channel
     */
    class ServerChannel extends ChannelInitializer<SocketChannel> {

        @Override
        public void initChannel(SocketChannel ch) {
            ChannelPipeline pipeline = ch.pipeline();
            // 流控
            if (null != trafficShapingHandler) {
                pipeline.addLast(defaultEventExecutorGroup, "trafficShapingHandler", trafficShapingHandler);
            }
            //tls加密
            if (null != sslContext) {
                pipeline.addLast(defaultEventExecutorGroup, "sslHandler", sslContext.newHandler(ch.alloc()));
            }
            // 添加压缩编解码
            pipeline.addLast(
                    defaultEventExecutorGroup,
                    new RpcPacketDecoder(),
                    new RpcPacketEncoder(serverConfig.getCompressType(), serverConfig.getSerializeType()),

                    // 3min没收到或没发送数据则认为空闲
                    new IdleStateHandler(serverConfig.getConnectionIdleTime(), serverConfig.getConnectionIdleTime(), 0),
                    new NettyServerConnManagerHandler(nodeManager, serverConfig),
                    new ServerProcessHandler(nodeManager, duplicatedMarker, serverConfig, businessThreadPool));
        }
    }

    private void registryInitAndPublish() {
        if (!checkRegistryEnable()) {
            return;
        }
//        try {
//            ExtensionLoader<ServicePublisher> loader = ExtensionLoader.getExtensionLoader(ServicePublisher.class);
//
//            String registrySchema = this.registryConfig.getRegistrySchema();
//
//            logger.info("use the registry schema: [{}]", registrySchema);
//
//            this.servicePublisher = loader.getExtension(registrySchema);
//
//            this.servicePublisher.initRegistry(this.registryConfig.getRegistryAddress());
//
//        } catch (Exception e) {
//            logger.error("registry init failed");
//            throw e;
//        }
//
//        //注册服务
//        publishRpcService();
    }
}
