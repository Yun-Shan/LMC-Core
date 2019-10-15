package org.yunshanmc.lmc.core.bukkit.gui.sign;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.BlockPosition;
import com.comphenix.protocol.wrappers.WrappedBlockData;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.yunshanmc.lmc.core.exception.ExceptionHandler;
import org.yunshanmc.lmc.core.gui.sign.SignHelper;
import org.yunshanmc.lmc.core.internal.LMCCoreUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class BukkitSignEditorProvider implements SignHelper.SignEditorProvider<Player> {

    private static final BukkitSignEditorProvider INSTANCE = new BukkitSignEditorProvider();

    public static BukkitSignEditorProvider getInstance() {
        return INSTANCE;
    }

    private final boolean protocolLibEnable;

    private final Map<BlockPosition, Consumer<String[]>> callbackMap = new HashMap<>();

    private BukkitSignEditorProvider() {
        if (Bukkit.getPluginManager().isPluginEnabled("ProtocolLib")) {
            ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();
            protocolManager.addPacketListener(new PacketAdapter(PacketAdapter
                .params()
                .plugin((Plugin) LMCCoreUtils.getLMCCorePlugin())
                .types(PacketType.Play.Client.UPDATE_SIGN)
            ) {
                @Override
                public void onPacketReceiving(PacketEvent event) {
                    PacketContainer packet = event.getPacket();
                    Consumer<String[]> callback = callbackMap.remove(packet.getBlockPositionModifier().read(0));
                    if (callback != null) {
                        callback.accept(packet.getStringArrays().read(0));
                    }
                }
            });
            protocolLibEnable = true;
        } else {
            protocolLibEnable = false;
        }
    }

    @Override
    public void openSignEditor(Player player, String[] lines, Consumer<String[]> callback) {
        if (!this.protocolLibEnable) {
            throw new UnsupportedOperationException("ProtocolLib not installed/enabled");
        }
        Location blockLoc = player.getLocation().clone();
        blockLoc.setY(0);
        Block rawBlock = blockLoc.getBlock();
        BlockPosition pos = new BlockPosition(blockLoc.toVector());
        try {
            ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();

            PacketContainer packet = new PacketContainer(PacketType.Play.Server.BLOCK_CHANGE);
            packet.getBlockPositionModifier().write(0, pos);
            packet.getBlockData().write(0, WrappedBlockData.createData(Material.WALL_SIGN));
            protocolManager.sendServerPacket(player, packet);

            player.sendSignChange(blockLoc, lines);

            packet = new PacketContainer(PacketType.Play.Server.OPEN_SIGN_EDITOR);
            packet.getBlockPositionModifier().write(0, pos);
            protocolManager.sendServerPacket(player, packet);

            callbackMap.put(pos, ((Consumer<String[]>) newLines -> {
                PacketContainer rePacket = new PacketContainer(PacketType.Play.Server.BLOCK_CHANGE);
                rePacket.getBlockPositionModifier().write(0, pos);
                rePacket.getBlockData().write(0, WrappedBlockData.createData(rawBlock.getType()));
                try {
                    protocolManager.sendServerPacket(player, rePacket);
                } catch (InvocationTargetException e) {
                    ExceptionHandler.handle(e);
                }
            }).andThen(callback));
        } catch (InvocationTargetException e) {
            ExceptionHandler.handle(e);
        }
    }
}
