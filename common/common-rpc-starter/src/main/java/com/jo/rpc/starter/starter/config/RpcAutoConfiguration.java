package com.jo.rpc.starter.starter.config;

import com.jo.rpc.Client;
import com.jo.rpc.Server;
import com.jo.rpc.client.RpcClient;
import com.jo.rpc.config.RegistryConfig;
import com.jo.rpc.config.RpcClientConfig;
import com.jo.rpc.config.RpcServerConfig;
import com.jo.rpc.server.RpcServer;
import com.jo.rpc.starter.starter.properties.RpcClientProperties;
import com.jo.rpc.starter.starter.properties.RpcServerProperties;
import org.springframework.beans.BeanUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;

/**
 * @author Jo
 * @date 2024/7/11
 */
@Configuration
@Import({RpcClientProperties.class, RpcServerProperties.class})
public class RpcAutoConfiguration {
//    private static final String APPLICATION_NAME_KEY = "spring.application.name";

    @Bean
    @ConditionalOnMissingBean(Server.class)
    public Server rpcServer(RpcServerProperties rpcServerProperties, Environment environment) {
//        String serviceName = environment.getProperty(APPLICATION_NAME_KEY);
        RpcServerConfig config = new RpcServerConfig();
        RegistryConfig registryConfig = new RegistryConfig();
        BeanUtils.copyProperties(rpcServerProperties, config);
        BeanUtils.copyProperties(rpcServerProperties, registryConfig);

        Server server = RpcServer.builder()
                .serverConfig(config)
                .sourceClass(Void.class);
//        if (registryConfig.isEnableRegistry()) {
//            server.configRegistry(registryConfig.getRegistrySchema(), registryConfig.getRegistryAddress(), serviceName);
//        }
        return server;
    }

    @Bean
    @ConditionalOnMissingBean(Client.class)
    public Client rpcClient(RpcClientProperties rpcClientProperties) {
        RpcClientConfig config = new RpcClientConfig();
        RegistryConfig registryConfig = new RegistryConfig();
        BeanUtils.copyProperties(rpcClientProperties, config);
        BeanUtils.copyProperties(rpcClientProperties, registryConfig);

        Client client = RpcClient.builder()
                .config(config);
//        if (registryConfig.isEnableRegistry()) {
//            client.configRegistry(registryConfig.getRegistrySchema(), registryConfig.getRegistryAddress());
//        }
        return client;
    }
}
