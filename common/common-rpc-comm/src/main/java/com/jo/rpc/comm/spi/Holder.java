package com.jo.rpc.comm.spi;

import lombok.Data;

/**
 * @author Jo
 * @date 2024/7/8
 */
public class Holder<T> {

    private volatile T value;

    public T get() {
        return value;
    }

    public void set(T value) {
        this.value = value;
    }
}
