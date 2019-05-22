package org.yunshanmc.lmc.core.network;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.junit.Test;
import org.yunshanmc.lmc.core.network.packet.TextPacket;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class PacketDecoderTest {

    private static final String TEXT = "测试文本";

    @Test
    public void decode() throws Exception {
        PacketDecoder decoder = new PacketDecoder();
        ByteBuf buffer = Unpooled.buffer();
        List<Object> list = new ArrayList<>();
        ByteBuf packet = Unpooled.buffer();
        packet.writeInt(PacketType.getIdByType(TextPacket.class));
        byte[] bytes = TEXT.getBytes(StandardCharsets.UTF_8);
        packet.writeInt(bytes.length);
        packet.writeBytes(bytes);
        buffer.writeInt(packet.readableBytes());
        decoder.decode(null, buffer, list);
        assertEquals(0, buffer.readableBytes());
        buffer.writeBytes(packet);
        decoder.decode(null, buffer, list);
        assertEquals(0, buffer.readableBytes());
        assertEquals(TEXT, ((TextPacket) list.get(0)).getText());
    }
}