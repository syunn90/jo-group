package com.jo.rpc.loadbalance;

import com.jo.rpc.comm.constant.LoadBalanceRule;
import com.jo.rpc.comm.spi.ExtensionLoader;

/**
 * @author Jo
 * @date 2024/7/8
 */
public class LoadBalancerFactory {
    public static LoadBalancer getLoadBalance(LoadBalanceRule rule) {
        ExtensionLoader<LoadBalancer> extensionLoader =
                ExtensionLoader.getExtensionLoader(LoadBalancer.class);
        LoadBalancer loadBalancer = extensionLoader.getExtension(rule.name());

        return loadBalancer;
    }
}
