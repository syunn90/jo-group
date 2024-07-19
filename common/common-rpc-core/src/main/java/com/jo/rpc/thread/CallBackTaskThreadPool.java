package com.jo.rpc.thread;

import com.jo.rpc.comm.annotation.SPI;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author Jo
 * @date 2024/7/8
 */
@SPI
public interface CallBackTaskThreadPool {

    /**
     * 使用SPI获取自定义回调任务线程池
     *
     * @return 线程池
     */
    ThreadPoolExecutor getCallBackTaskThreadPool();


}
