package com.jo.rpc.codec;

import com.jo.rpc.compress.Compress;
import com.jo.rpc.comm.constant.CompressType;
import com.jo.rpc.comm.constant.RpcConstant;
import com.jo.rpc.comm.constant.SerializeType;
import com.jo.rpc.protocol.Command;
import com.jo.rpc.serialize.Serializer;
import com.jo.rpc.comm.spi.ExtensionLoader;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.EncoderException;
import io.netty.handler.codec.MessageToByteEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author: hs
 */
public class RpcPacketEncoder extends MessageToByteEncoder<Command> {
    private static final Logger logger = LoggerFactory.getLogger(RpcPacketEncoder.class);

    private CompressType compressType;
    private Compress compress;
    private SerializeType serializerType;
    private Serializer serializer;

    public RpcPacketEncoder(CompressType compressType, SerializeType serializerType) {
        this.compressType = compressType;
        this.serializerType = serializerType;
        setCompress();
        setSerializer();
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, Command command, ByteBuf out) throws Exception {
        try {
            out.writeShort(RpcConstant.MAGIC_NUMBER);
            out.writeByte(RpcConstant.VERSION);

            out.writeByte((byte) (serializerType.getCode() << 4 | compressType.getCode() & 0xff));
            out.writeBoolean(command.isRequest());

            byte[] serializeBytes = serializer.serialize(command);
            byte[] compressBytes = this.compress.compress(serializeBytes);
            out.writeInt(compressBytes.length);

            out.writeBytes(compressBytes);
        } catch (Exception e) {
            logger.error("frame encode failed", e);
            throw new EncoderException();
        }
    }

    private void setCompress() {
        ExtensionLoader<Compress> compressLoader = ExtensionLoader.getExtensionLoader(Compress.class);
        this.compress = compressLoader.getExtension(compressType.getName());
    }

    private void setSerializer() {
        ExtensionLoader<Serializer> serializerLoader = ExtensionLoader.getExtensionLoader(Serializer.class);
        this.serializer = serializerLoader.getExtension(serializerType.getName());
    }
}
