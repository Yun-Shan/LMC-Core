package org.yunshanmc.lmc.core.network;

import io.netty.util.collection.IntObjectHashMap;
import org.yunshanmc.lmc.core.network.packet.RegisterClientPacket;
import org.yunshanmc.lmc.core.network.packet.TextPacket;

import java.util.HashMap;

/**
 * @author Yun-Shan
 */

public final class PacketType {

    private static final IntObjectHashMap<Class<? extends AbstractPacket>> BY_ID = new IntObjectHashMap<>();
    private static final HashMap<Class<? extends AbstractPacket>, Integer> TYPE_TO_ID = new HashMap<>();

    static {
        final int startId = 600000;
        // 纯字符串
        register(startId + 1, TextPacket.class);
        // 子服信息
        register(startId + 2, RegisterClientPacket.class);
    }

    public static Class<? extends AbstractPacket> getTypeById(int id) {
        return BY_ID.get(id);
    }

    public static int getIdByType(Class<? extends AbstractPacket> cls) {
        return TYPE_TO_ID.getOrDefault(cls, -1);
    }

    /**
     * 注册数据包
     *
     * @param id  包ID，必须大于0且不和已有的重复
     * @param cls 数据包类型
     * @return 注册成功返回true，id小于等于0或已有重复id时返回false
     */
    public static boolean register(int id, Class<? extends AbstractPacket> cls) {
        if (id <= 0) {
            return false;
        }
        if (BY_ID.putIfAbsent(id, cls) == null) {
            TYPE_TO_ID.put(cls, id);
            return true;
        } else {
            return false;
        }
    }

    private PacketType() {
    }
}
