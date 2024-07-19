package com.jo.rpc.comm.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Jo
 * @date 2024/7/8
 */
@AllArgsConstructor
@Getter
public enum SerializeType {
    /**
     * 自定义序列化类型，需使用SPI
     */
    CUSTOM((byte) 0x1, "custom"),

    /**
     * protostuff序列化
     */
    PROTOSTUFF((byte) 0x5, "Protostuff"),

    /**
     * kyro序列化
     */
    KRYO((byte) 0x6, "Kryo"),

    /**
     * json序列化
     */
    JSON((byte) 0x7, "JSON"),

    /**
     * JDK序列化
     */
    JDK((byte) 0x8, "Jdk");

    private byte code;
    private String name;

    public static String getName(int code) {
        for (SerializeType c : SerializeType.values()) {
            if (c.getCode() == code) {
                return c.name;
            }
        }
        return null;
    }
}
