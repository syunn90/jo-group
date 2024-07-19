package com.jo.rpc.thread;

import com.jo.rpc.comm.annotation.SPI;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author Jo
 * @date 2024/7/9
 */
@SPI
public interface BusinessThreadPool {
    /**
     * 使用SPI获取自定义业务线程池
     *
     * @return 线程池
     */
    ThreadPoolExecutor getBusinessThreadPool();
}
