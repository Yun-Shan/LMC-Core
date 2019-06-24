package org.yunshanmc.lmc.core.bukkit.gui;

import com.google.common.base.Strings;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.*;
import org.yunshanmc.lmc.core.gui.Gui;
import org.yunshanmc.lmc.core.gui.Icon;
import org.yunshanmc.lmc.core.gui.InvProvider;

import java.util.Arrays;

public class BukkitInvProvider implements InvProvider<Inventory> {

    @Override
    public Inventory buildInv(Gui gui) {
        ItemStack[] contents = Arrays
            .stream(gui.getIcons())
            .map(icon -> icon != null ? icon.buildItem(BukkitInvProvider::buildItem) : null)
            .toArray(ItemStack[]::new);
        Inventory inv = new InvHolder(gui).getInventory();
        inv.setContents(contents);
        return inv;
    }

    @SuppressWarnings("deprecation")
    private static ItemStack buildItem(Icon icon) {
        Material material = Material.matchMaterial(icon.getMaterial());
        int count = Math.min(icon.getCount(), material.getMaxStackSize());
        ItemStack item = new ItemStack(material, count, icon.getSubId());
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(icon.getName());
        if (icon.getLore() != null && !icon.getLore().isEmpty()) {
            meta.setLore(icon.getLore());
        }
        if (icon.isEnch()) {
            meta.addEnchant(Enchantment.KNOCKBACK, 1, false);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }
        if (icon.isUnbreakable()) {
            meta.setUnbreakable(true);
            meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        }
        if (material == Material.SKULL_ITEM && !Strings.isNullOrEmpty(icon.getSkullOwner())) {
            // 由于必须是用名字设置头颅所属玩家皮肤，故暂时没有不过时的方法可以用
            ((SkullMeta) meta).setOwner(icon.getSkullOwner());
        } else if (icon.getColor() != null) {
            switch (material) {
                case LEATHER_HELMET:
                case LEATHER_CHESTPLATE:
                case LEATHER_LEGGINGS:
                case LEATHER_BOOTS: {
                    ((LeatherArmorMeta) meta).setColor(Color.fromRGB(icon.getColor().getRed(), icon.getColor().getGreen(), icon.getColor().getBlue()));
                    break;
                }
                case POTION: {
                    ((PotionMeta) meta).setColor(Color.fromRGB(icon.getColor().getRed(), icon.getColor().getGreen(), icon.getColor().getBlue()));
                    break;
                }
                case MAP: {
                    ((MapMeta) meta).setColor(Color.fromRGB(icon.getColor().getRed(), icon.getColor().getGreen(), icon.getColor().getBlue()));
                    break;
                }
                default:
                    break;
            }
        }
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        item.setItemMeta(meta);
        return item;
    }

}
