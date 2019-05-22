package org.yunshanmc.lmc.core.network.packet;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.yunshanmc.lmc.core.network.AbstractPacket;
import org.yunshanmc.lmc.core.network.DataBuffer;

/**
 * @author Yun-Shan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TextPacket extends AbstractPacket {

    @Getter
    private String text;

    public TextPacket(String text) {
        this.text = text;
    }

    @Override
    public void read(DataBuffer buffer) {
        this.text = buffer.readString();
    }

    @Override
    public void write(DataBuffer buffer) {
        buffer.writeString(this.text);
    }
}
