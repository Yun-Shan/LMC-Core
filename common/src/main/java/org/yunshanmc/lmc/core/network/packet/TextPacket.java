package org.yunshanmc.lmc.core.network.packet;

import org.yunshanmc.lmc.core.network.AbstractPacket;
import org.yunshanmc.lmc.core.network.DataBuffer;

/**
 * @author Yun-Shan
 */
public class TextPacket extends AbstractPacket {

    private String text;

    private TextPacket() {
    }

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

    public String getText() {
        return this.text;
    }
}
