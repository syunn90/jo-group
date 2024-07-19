package com.jo.rpc.comm.cache;

import java.util.Set;

/**
 * @author Jo
 * @date 2024/7/8
 */
public interface IExpireCache<K,V> {

    V put(K key, V value);

    V get(K key);

    Set<K> keySet();

    void invalidate(K key);
}
