package org.yunshanmc.lmc.core.network;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * @author Yun-Shan
 */
public class PacketEncoder extends MessageToByteEncoder<AbstractPacket> {

    @Override
    protected void encode(ChannelHandlerContext ctx, AbstractPacket msg, ByteBuf out) throws Exception {
        DataBuffer buffer = new DataBuffer(Unpooled.buffer());
        buffer.writeInt(msg.getTypeId());
        msg.write(buffer);
        out.writeInt(buffer.readableBytes());
        out.writeBytes(buffer);
    }
}
