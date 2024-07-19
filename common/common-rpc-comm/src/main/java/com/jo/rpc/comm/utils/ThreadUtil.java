package com.jo.rpc.comm.utils;

import com.jo.rpc.comm.thread.RpcThreadFactory;

import java.util.concurrent.*;

/**
 * @author: hs
 * <p>
 * 线程池工具类
 */
public class ThreadUtil {

    public static boolean isTerminated(Executor executor) {
        if (executor instanceof ExecutorService) {
            return ((ExecutorService) executor).isTerminated();
        }
        return false;
    }

    public static void gracefulShutdown(Executor executor, int timeout) {
        if (!(executor instanceof ExecutorService) || isTerminated(executor)) {
            return;
        }
        final ExecutorService es = (ExecutorService) executor;
        try {
            // shutdown后不允许再提交新的任务
            es.shutdown();
        } catch (SecurityException | NullPointerException ex2) {
            return;
        }
        try {
            // 指定时间内等待线程池处理完剩下的任务, 未执行完也立即关闭
            if (!es.awaitTermination(timeout, TimeUnit.MILLISECONDS)) {
                es.shutdownNow();
            }
        } catch (InterruptedException ex) {
            es.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    public static ThreadPoolExecutor getFixThreadPoolExecutor(int coreThreads, int queueSize,
                                                              RejectedExecutionHandler rejectedExecutionHandler,
                                                              String threadFactoryName) {
        return new ThreadPoolExecutor(coreThreads, coreThreads, 0, TimeUnit.MILLISECONDS,
                queueSize == 0 ? new SynchronousQueue<>() :
                        (queueSize < 0 ? new LinkedBlockingQueue<>()
                                : new LinkedBlockingQueue<>(queueSize)),
                new RpcThreadFactory(threadFactoryName), rejectedExecutionHandler);
    }
}
