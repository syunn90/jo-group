package com.jo.rpc;

import com.jo.rpc.config.RegistryConfig;
import com.jo.rpc.config.TLSConfig;
import com.jo.rpc.comm.constant.RpcConstant;
import com.jo.rpc.comm.exception.RegistryException;
import io.netty.channel.epoll.Epoll;
import io.netty.handler.ssl.*;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import io.netty.handler.traffic.GlobalTrafficShapingHandler;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;

/**
 * @author Jo
 * @date 2024/7/8
 */
public abstract class AbstractRpc {
    private static final String CLASSPATH = "classpath:";
    protected static final Logger logger = LoggerFactory.getLogger(AbstractRpc.class);
    protected static int IO_THREADS = RpcConstant.DEFAULT_THREADS;
    protected SslContext sslContext;
    protected GlobalTrafficShapingHandler trafficShapingHandler;
    protected RegistryConfig registryConfig;

    protected void assertNotNull(Object object, String message) {
        if (object == null) {
            throw new IllegalArgumentException(message);
        }
    }
    protected void buildSSLContext(boolean forClient, TLSConfig tlsConfig) throws Exception {
        InputStream keyIns = null;
        InputStream certIns = null;
        InputStream trustIns = null;
        try {
            keyIns = parseInputStream(tlsConfig.getKeyPath());
            certIns = parseInputStream(tlsConfig.getCertPath());
            SslContextBuilder sslContextBuilder;
            if (forClient) {
                sslContextBuilder = SslContextBuilder.forClient().keyManager(certIns, keyIns, tlsConfig.getKeyPwd());
            } else {
                sslContextBuilder = SslContextBuilder.forServer(certIns, keyIns, tlsConfig.getKeyPwd());
                sslContextBuilder.clientAuth(parseClientAuthMode(tlsConfig.getClientAuth()));
            }
            sslContextBuilder.sslProvider(sslProvider());

            if (tlsConfig.getTrustCertPath() == null || tlsConfig.getTrustCertPath().trim().isEmpty()) {
                sslContextBuilder.trustManager(InsecureTrustManagerFactory.INSTANCE);
            } else {
                trustIns = parseInputStream(tlsConfig.getTrustCertPath());
                sslContextBuilder.trustManager(trustIns);
            }
            sslContext = sslContextBuilder.build();
        } finally {
            if (certIns != null) {
                certIns.close();
            }
            if (keyIns != null) {
                keyIns.close();
            }
            if (trustIns != null) {
                trustIns.close();
            }
        }
    }
    protected void buildTrafficMonitor(ScheduledExecutorService executor, Boolean trafficMonitorEnable,
                                       Long maxReadSpeed, Long maxWriteSpeed) {
        if (trafficMonitorEnable != null && trafficMonitorEnable) {
            if (maxReadSpeed == null) {
                maxReadSpeed = 0L;
            }
            if (maxWriteSpeed == null) {
                maxWriteSpeed = 0L;
            }
            trafficShapingHandler = new GlobalTrafficShapingHandler(executor, maxWriteSpeed, maxReadSpeed);
        }
    }
    private InputStream parseInputStream(String path) throws FileNotFoundException {
        if (path.startsWith(CLASSPATH)) {
            path = path.replaceFirst(CLASSPATH, "");
            return this.getClass().getClassLoader().getResourceAsStream(path);
        }
        return new FileInputStream(path);
    }
    protected ClientAuth parseClientAuthMode(String authMode) {
        if (authMode == null || authMode.trim().isEmpty()) {
            return ClientAuth.NONE;
        }
        for (ClientAuth clientAuth : ClientAuth.values()) {
            if (clientAuth.name().equals(authMode.toUpperCase())) {
                return clientAuth;
            }
        }
        return ClientAuth.NONE;
    }
    protected SslProvider sslProvider() {
        if (OpenSsl.isAvailable()) {
            return SslProvider.OPENSSL;
        } else {
            return SslProvider.JDK;
        }
    }
    protected boolean checkRegistryEnable() {
        if (this.registryConfig == null || !this.registryConfig.isEnableRegistry()) {
            return false;
        }
        if (CollectionUtils.isEmpty(this.registryConfig.getRegistryAddress())) {
            logger.error("registry config invalid, not configured registry address");
            return false;
        }
        return true;
    }

    protected void setConfigRegistry(String schema, List<String> registryAddress, String serviceName) {
        if (CollectionUtils.isEmpty(registryAddress)) {
            throw new RegistryException("Invalid schema or registryAddress");
        }
        //设置默认注册中心
        if (StringUtils.isBlank(schema)) {
            schema = RegistryConfig.DEFAULT_REGISTRY_SCHEMA;
        }
        this.registryConfig = new RegistryConfig()
                .setEnableRegistry(true)
                .setRegistrySchema(schema)
                .setRegistryAddress(registryAddress);
        if (StringUtils.isNotBlank(serviceName)) {
            this.registryConfig.setServiceName(serviceName);
        }
    }
    protected boolean useEpoll() {
        return SystemUtils.IS_OS_LINUX && Epoll.isAvailable();
    }
}
