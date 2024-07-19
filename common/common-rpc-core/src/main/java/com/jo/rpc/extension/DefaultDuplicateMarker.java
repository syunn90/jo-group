package com.jo.rpc.extension;

import com.jo.rpc.comm.cache.IExpireCache;
import com.jo.rpc.comm.cache.ScheduleEvictExpireCache;

import java.util.concurrent.TimeUnit;

/**
 * @author Jo
 * @date 2024/7/9
 */
public class DefaultDuplicateMarker implements DuplicatedMarker{

    private IExpireCache<Long, Boolean> expireCache;

    @Override
    public void initMarkerConfig(int expireTime, long maxSize) {
        expireCache = new ScheduleEvictExpireCache<>(expireTime, TimeUnit.SECONDS, maxSize);
    }

    @Override
    public boolean mark(Long seq) {
        return expireCache.put(seq, Boolean.TRUE) != null;
    }
}
