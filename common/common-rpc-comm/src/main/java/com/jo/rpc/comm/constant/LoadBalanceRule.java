package com.jo.rpc.comm.constant;

/**
 * @author Jo
 * @date 2024/7/8
 */
public enum LoadBalanceRule {

    /**
     * 随机
     */
    RANDOM,

    /**
     * 轮询
     */
    ROUND,

    /**
     * 一致性hash
     */
    CONSISTENT_HASH,

    /**
     * 自定义负载均衡，需利用SPI机制
     */
    CUSTOM;


}
