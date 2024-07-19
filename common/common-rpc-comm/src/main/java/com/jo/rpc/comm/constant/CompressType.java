package com.jo.rpc.comm.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Jo
 * @date 2024/7/8
 */
@SuppressWarnings("unused")
@AllArgsConstructor
@Getter
public enum CompressType {

    /**
     * 不压缩
     */
    NONE((byte) 0x0, "none"),

    /**
     * 自定义压缩，需使用SPI实现
     */
    CUSTOM((byte) 0x1, "custom"),

    /**
     * GZIP压缩
     */
    GZIP((byte) 0x5, "Gzip"),

    /**
     * LZ4压缩
     */
    LZ4((byte) 0x6, "LZ4"),

    /**
     * Bzip2压缩
     */
    BZIP2((byte) 0x7, "Bzip2"),

    /**
     * Deflater压缩
     */
    DEFLATER((byte) 0x8, "Deflater"),

    /**
     * Lzo压缩
     */
    LZO((byte) 0x9, "Lzo"),

    /**
     * snappy压缩
     */
    SNAPPY((byte) 0xA, "Snappy");

    private byte code;
    private String name;



    public CompressType setCode(byte code) {
        this.code = code;
        return this;
    }


    public CompressType setName(String name) {
        this.name = name;
        return this;
    }

    public static String getName(int code) {
        for (CompressType c : CompressType.values()) {
            if (c.getCode() == code) {
                return c.name;
            }
        }
        return null;
    }

}
