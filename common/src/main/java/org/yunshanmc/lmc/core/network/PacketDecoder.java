package org.yunshanmc.lmc.core.network;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Yun-Shan
 */
public class PacketDecoder extends ByteToMessageDecoder {

    private static final int INT_BYTES = 4;
    private int lastLength = -1;

    private final Map<Class<? extends AbstractPacket>, Constructor<? extends AbstractPacket>> constructors = new HashMap<>();

    private final PacketType packetType;

    public PacketDecoder(PacketType packetType) {
        this.packetType = packetType;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        int length;
        if (this.lastLength > 0) {
            if (in.readableBytes() < this.lastLength) {
                return;
            }
            length = this.lastLength;
            this.lastLength = -1;
        } else if (in.readableBytes() < INT_BYTES) {
            return;
        } else {
            length = in.readInt();
            if (in.readableBytes() < length) {
                this.lastLength = length;
                return;
            }
        }
        DataBuffer buffer = new DataBuffer(Unpooled.buffer(length));
        in.readBytes(buffer, length);
        int id = buffer.readInt();
        Class<? extends AbstractPacket> type = this.packetType.getTypeById(id);
        Constructor<? extends AbstractPacket> constructor = this.constructors.get(type);
        if (constructor == null) {
            constructor = type.getDeclaredConstructor();
            constructor.setAccessible(true);
            this.constructors.put(type, constructor);
        }
        AbstractPacket packet = constructor.newInstance();
        packet.read(buffer);
        out.add(packet);
    }
}
