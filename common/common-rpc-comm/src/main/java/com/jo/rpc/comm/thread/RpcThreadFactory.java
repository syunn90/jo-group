package com.jo.rpc.comm.thread;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Jo
 * @date 2024/7/8
 */
public class RpcThreadFactory implements ThreadFactory {

    private AtomicInteger threadNumber = new AtomicInteger(1);
    private String prefix;
    private static volatile RpcThreadFactory defaultFactory;

    public RpcThreadFactory(String prefix) {
        this.prefix = prefix + "-" + "thread-";
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread thread = new Thread(r, this.prefix + threadNumber.getAndIncrement());
        thread.setDaemon(true);
        return thread;
    }

    public static RpcThreadFactory getDefault() {
        if (defaultFactory == null) {
            synchronized (RpcThreadFactory.class) {
                if (defaultFactory == null) {
                    defaultFactory = new RpcThreadFactory("rpc");
                }
            }
        }
        return defaultFactory;
    }
}
