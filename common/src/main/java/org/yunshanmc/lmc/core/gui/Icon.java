package org.yunshanmc.lmc.core.gui;

import com.google.common.base.Preconditions;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

import java.util.List;
import java.util.function.Function;

/**
 * 表示一个GUI中的图标
 * <p>
 * 该类实现了物品缓存机制，请尽量复用
 *
 * @author Yun-Shan
 */
@Getter
public class Icon {

    private String material;
    private short subId;
    private int count;

    private String name;
    private List<String> lore;
    private boolean ench;
    private boolean unbreakable;

    private String skullOwner;
    private Color color;

    @Getter(AccessLevel.NONE)
    private boolean changed;
    private Object cacheItem;

    @Builder
    public Icon(@NonNull String material, int subId, int count,
                @NonNull String name, List<String> lore,
                boolean ench, boolean unbreakable,
                String skullOwner, Color color) {
        this.material = material;
        this.setSubId(subId);
        this.count = Math.max(1, count);
        this.name = name;
        this.lore = lore;
        this.ench = ench;
        this.unbreakable = unbreakable;
        this.skullOwner = skullOwner;
        this.color = color;
    }

    public void setMaterial(@NonNull String material) {
        this.material = material;
        this.changed = true;
    }

    public void setSubId(int subId) {
        if (subId < 0 || subId > Short.MAX_VALUE) {
            this.subId = 0;
        } else {
            this.subId = (short) subId;
        }
        this.changed = true;
    }

    public void setCount(int count) {
        this.count = Math.max(1, count);
        this.changed = true;
    }

    public void setName(@NonNull String name) {
        this.name = name;
        this.changed = true;
    }

    public void setLore(List<String> lore) {
        this.lore = lore;
        this.changed = true;
    }

    public void setEnch(boolean ench) {
        this.ench = ench;
        this.changed = true;
    }

    public void setUnbreakable(boolean unbreakable) {
        this.unbreakable = unbreakable;
        this.changed = true;
    }

    @SuppressWarnings("unchecked")
    public <T> T buildItem(Function<Icon, T> itemBuilder) {
        if (this.changed || this.cacheItem == null) {
            this.cacheItem = itemBuilder.apply(this);
            this.changed = false;
        }
        return (T) this.cacheItem;
    }

    @Getter
    public static class Color {
        private final byte red;
        private final byte green;
        private final byte blue;

        public Color(int red, int green, int blue) {
            Preconditions.checkArgument((red >= 0 && red <= 255), "need (red >= 0 && red <= 255)");
            Preconditions.checkArgument((green >= 0 && green <= 255), "need (green >= 0 && green <= 255)");
            Preconditions.checkArgument((blue >= 0 && blue <= 255), "need (blue >= 0 && blue <= 255)");

            this.red = (byte) red;
            this.green = (byte) green;
            this.blue = (byte) blue;
        }
    }
}
