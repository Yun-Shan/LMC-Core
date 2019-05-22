package org.yunshanmc.lmc.core.network;

public abstract class AbstractPacket {

    /**
     * 获取包类型ID
     *
     * @return 包类型ID
     */
    public final int getTypeId() {
        return PacketType.getIdByType(this.getClass());
    }

    /**
     * 从缓冲区读入数据(保证缓冲区存在完整数据)
     *
     * @param buffer 缓冲区
     */
    public abstract void read(DataBuffer buffer);

    /**
     * 向缓冲区写入数据
     *
     * @param buffer 缓冲区
     */
    public abstract void write(DataBuffer buffer);
}
