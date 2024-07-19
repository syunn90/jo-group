package com.jo.rpc;

import com.jo.rpc.comm.annotation.Nullable;
import com.jo.rpc.config.RpcServerConfig;

import java.util.List;

/**
 * @author Jo
 * @date 2024/7/9
 */
public interface Server {

    /**
     * 设置rpc服务端配置
     *
     * @param config 配置
     * @return Server
     */
    Server serverConfig(RpcServerConfig config);

    /**
     * 包含@RouteScan注解的类
     *
     * @param clazz clazz
     * @return Server
     */
    Server sourceClass(Class<?> clazz);

    /**
     * 设置注册中心地址
     *
     * @param schema          注册中心模式[缺省zookeeper]
     * @param registryAddress 注册中心地址
     * @param serviceName     需要发布到注册中心上的服务名称[服务提供端需要配置]
     * @return Server
     */
    Server configRegistry(@Nullable String schema, List<String> registryAddress, String serviceName);
//
//    /**
//     * 指定端口,未指定的话则使用ServerConfig配置里的端口
//     *
//     * @param port 端口
//     * @return Server
//     */
//    Server port(int port);

    /**
     * 启动服务端
     */
    Server start();

    /**
     * 停止服务端
     */
    void stop();

}
