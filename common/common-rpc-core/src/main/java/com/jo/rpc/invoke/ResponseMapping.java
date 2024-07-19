package com.jo.rpc.invoke;

import com.jo.rpc.comm.cache.IExpireCache;
import com.jo.rpc.comm.cache.ScheduleEvictExpireCache;

import java.util.concurrent.TimeUnit;

/**
 * @author Jo
 * @date 2024/7/8
 */
public class ResponseMapping {
    private IExpireCache<Long, ResponseFuture> expireCache;

    public ResponseMapping(int expiredTime) {
        expireCache = new ScheduleEvictExpireCache<>(expiredTime, TimeUnit.SECONDS);
    }

    public void putResponseFuture(Long requestSeq, ResponseFuture responseFuture) {
        expireCache.put(requestSeq, responseFuture);
    }

    public ResponseFuture getResponseFuture(Long requestId) {
        ResponseFuture responseFuture = expireCache.get(requestId);
        expireCache.invalidate(requestId);
        return responseFuture;
    }

    public void invalidate(Long requestId) {
        expireCache.invalidate(requestId);
    }
}
