package com.jo.rpc.codec;


import com.jo.rpc.compress.Compress;
import com.jo.rpc.comm.constant.CompressType;
import com.jo.rpc.comm.constant.RpcConstant;
import com.jo.rpc.comm.constant.SerializeType;
import com.jo.rpc.protocol.Command;
import com.jo.rpc.protocol.RpcRequest;
import com.jo.rpc.protocol.RpcResponse;
import com.jo.rpc.serialize.Serializer;
import com.jo.rpc.comm.spi.ExtensionLoader;
import com.jo.rpc.comm.utils.ByteUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author: hs
 */
public class RpcPacketDecoder extends LengthFieldBasedFrameDecoder {
    private static final Logger logger = LoggerFactory.getLogger(RpcPacketDecoder.class);

    private ExtensionLoader<Serializer> serializerLoader;
    private ExtensionLoader<Compress> compressLoader;

    public RpcPacketDecoder() {
        this(RpcConstant.MAX_FRAME_LENGTH, 5, 4);
    }

    public RpcPacketDecoder(int maxFrameLength, int lengthFieldOffset, int lengthFieldLength) {
        super(maxFrameLength, lengthFieldOffset, lengthFieldLength);
        serializerLoader = ExtensionLoader.getExtensionLoader(Serializer.class);
        compressLoader = ExtensionLoader.getExtensionLoader(Compress.class);
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        ByteBuf frame = (ByteBuf) super.decode(ctx, in);
        if (frame == null) {
            return null;
        }
        try {
            //校验魔数
            checkMagicNumber(ctx, frame);
            //校验版本
            checkVersion(ctx, frame);

            short readByte = frame.readUnsignedByte();

            String serializeType = SerializeType.getName(ByteUtil.getHeight4(readByte));
            if (serializeType == null) {
                throw new DecoderException("unSupport serialize type");
            }
            String compressType = CompressType.getName(ByteUtil.getLow4(readByte));
            if (compressType == null) {
                throw new DecoderException("unSupport compress type");
            }

            boolean isRequest = frame.readBoolean();

            int length = frame.readInt();
            if (length <= 0) {
                return null;
            }

            byte[] bytes = new byte[length];
            frame.readBytes(bytes);
            byte[] decompress = compressLoader.getExtension(compressType).decompress(bytes);

            Command command;
            if (isRequest) {
                command = serializerLoader.getExtension(serializeType).deserialize(decompress, RpcRequest.class);
            } else {
                command = serializerLoader.getExtension(serializeType).deserialize(decompress, RpcResponse.class);
            }
            command.setRequest(isRequest);
            return command;
        } catch (Exception e) {
            logger.error("frame decode failed", e);
            throw new DecoderException();
        } finally {
            ReferenceCountUtil.release(frame);
        }
    }

    private void checkMagicNumber(ChannelHandlerContext ctx, ByteBuf in) {
        short magicNumber = in.readShort();
        if (magicNumber != RpcConstant.MAGIC_NUMBER) {
            ctx.close();
            throw new DecoderException("Unknown magic code: " + magicNumber);
        }
    }

    private void checkVersion(ChannelHandlerContext ctx, ByteBuf in) {
        int version = in.readUnsignedByte();
        if (version != RpcConstant.VERSION) {
            ctx.close();
            throw new DecoderException("version isn't compatible" + version);
        }
    }

}
