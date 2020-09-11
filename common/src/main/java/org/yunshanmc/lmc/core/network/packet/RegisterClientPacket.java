package org.yunshanmc.lmc.core.network.packet;

import org.yunshanmc.lmc.core.network.AbstractPacket;
import org.yunshanmc.lmc.core.network.DataBuffer;

/**
 * @author Yun-Shan
 */
public class RegisterClientPacket extends AbstractPacket {

    private String name;

    private RegisterClientPacket() {
    }

    public RegisterClientPacket(String name) {
        this.name = name;
    }

    @Override
    public void read(DataBuffer buffer) {
        this.name = buffer.readString();
    }

    @Override
    public void write(DataBuffer buffer) {
        buffer.writeString(this.name);
    }

    public String getName() {
        return this.name;
    }
}
