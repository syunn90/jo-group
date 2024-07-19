package com.jo.rpc.serialize.impl;

import com.jo.rpc.comm.exception.SerializeException;
import com.jo.rpc.serialize.Serializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * @author: hs
 * <p>
 * JDK序列化，Object参数需要实现Serializable接口
 */
public class JdkSerializer implements Serializer {
    private static final Logger logger = LoggerFactory.getLogger(JdkSerializer.class);

    @Override
    public byte[] serialize(Object object) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);) {
            objectOutputStream.writeObject(object);
            return byteArrayOutputStream.toByteArray();
        } catch (Exception e) {
            logger.error("Jdk serialize failed", e);
            throw new SerializeException();
        }
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clazz) {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        try (ObjectInputStream inputStream = new ObjectInputStream(byteArrayInputStream);
        ) {
            return (T) inputStream.readObject();
        } catch (Exception e) {
            logger.error("Jdk deserialize failed", e);
            throw new SerializeException();
        }
    }
}
