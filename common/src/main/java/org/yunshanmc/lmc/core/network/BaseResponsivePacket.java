package org.yunshanmc.lmc.core.network;

import lombok.Getter;
import lombok.Setter;

import java.util.concurrent.atomic.AtomicInteger;

public abstract class BaseResponsivePacket extends AbstractPacket {

    @Getter
    @Setter
    protected int responseId;

    private static final AtomicInteger COUNTER = new AtomicInteger();

    protected BaseResponsivePacket() {
        this.responseId = COUNTER.incrementAndGet();
    }

    @Override
    public final void read(DataBuffer buffer) {
        this.responseId = buffer.readInt();
        this.read0(buffer);
    }

    @Override
    public final void write(DataBuffer buffer) {
        buffer.writeInt(this.responseId);
        this.write0(buffer);
    }

    protected abstract void read0(DataBuffer buffer);

    protected abstract void write0(DataBuffer buffer);
}
