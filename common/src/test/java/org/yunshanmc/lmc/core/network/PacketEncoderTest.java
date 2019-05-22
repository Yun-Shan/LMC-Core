package org.yunshanmc.lmc.core.network;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.junit.Test;
import org.yunshanmc.lmc.core.network.packet.TextPacket;

import java.nio.charset.StandardCharsets;

import static org.junit.Assert.*;

public class PacketEncoderTest {

    private static final String TEXT = "测试文本";

    @Test
    public void encode() throws Exception {
        PacketEncoder encoder = new PacketEncoder();
        ByteBuf data = Unpooled.buffer();
        ByteBuf packet = Unpooled.buffer();
        packet.writeInt(PacketType.getIdByType(TextPacket.class));
        byte[] bytes = TEXT.getBytes(StandardCharsets.UTF_8);
        packet.writeInt(bytes.length);
        packet.writeBytes(bytes);
        data.writeInt(packet.readableBytes());
        data.writeBytes(packet);
        ByteBuf buffer = Unpooled.buffer();
        encoder.encode(null, new TextPacket(TEXT), buffer);
        assertArrayEquals(data.array(), buffer.array());
    }
}