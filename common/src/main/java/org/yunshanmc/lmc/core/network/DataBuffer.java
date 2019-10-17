package org.yunshanmc.lmc.core.network;

import io.netty.buffer.ByteBuf;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Delegate;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

/**
 * @author Yun-Shan
 */
@EqualsAndHashCode(of = "buffer", callSuper = false)
@ToString(of = "buffer")
public class DataBuffer extends ByteBuf {

    @Delegate
    private final ByteBuf buffer;

    public DataBuffer(ByteBuf buffer) {
        this.buffer = buffer;
    }

    public String readString() {
        int length = this.readInt();
        return this.readBytes(length).toString(StandardCharsets.UTF_8);
    }

    public void writeString(String str) {
        byte[] bytes = str.getBytes(StandardCharsets.UTF_8);
        this.writeInt(bytes.length);
        this.writeBytes(bytes);
    }

    public UUID readUUID() {
        return new UUID(this.readLong(), this.readLong());
    }

    public void writeUUID(UUID uuid) {
        this.writeLong(uuid.getMostSignificantBits());
        this.writeLong(uuid.getLeastSignificantBits());
    }
}
