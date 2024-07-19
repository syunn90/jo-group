package com.jo.rpc.compress;

import com.jo.rpc.comm.annotation.SPI;

import java.io.IOException;

/**
 * @author Jo
 * @date 2024/7/9
 */
@SPI
public interface Compress {
    /**
     * 压缩
     *
     * @param bytes 原始字节数组
     * @return 压缩后的字节数组
     */
    byte[] compress(byte[] bytes) throws IOException;

    /**
     * 解压
     *
     * @param bytes 压缩后的字节数组
     * @return 原始字节数组
     */
    byte[] decompress(byte[] bytes) throws IOException;
}
