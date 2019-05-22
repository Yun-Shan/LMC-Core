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
public class RegisterClientPacket extends AbstractPacket {

    @Getter
    private String name;

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
}
