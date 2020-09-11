package org.yunshanmc.lmc.core.gui;

import com.google.common.base.Preconditions;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

/**
 * 表示一个GUI中的图标
 * <p>
 * 该类实现了物品缓存机制，请尽量复用
 *
 * @author Yun-Shan
 */
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

    private boolean changed;
    private Object cacheItem;

    public Icon(@Nonnull String material, @Nonnull String name) {
    }

    public Icon(@Nonnull String material, int subId, int count, @Nonnull String name, List<String> lore,
                boolean ench, boolean unbreakable, String skullOwner, Color color) {
        Objects.requireNonNull(material);
        Objects.requireNonNull(name);
        this.material = material;
        this.setSubId(subId);
        // 最小1，最大64
        this.count = Math.min(Math.max(1, count), 64);
        this.name = name;
        this.lore = lore;
        this.ench = ench;
        this.unbreakable = unbreakable;
        this.skullOwner = skullOwner;
        this.color = color;
    }

    @SuppressWarnings("unchecked")
    public <T> T buildItem(Function<Icon, T> itemBuilder) {
        if (this.changed || this.cacheItem == null) {
            this.cacheItem = itemBuilder.apply(this);
            this.changed = false;
        }
        return (T) this.cacheItem;
    }

    // region setter

    public void setMaterial(@Nonnull String material) {
        this.material = Objects.requireNonNull(material);
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

    public void setName(@Nonnull String name) {
        this.name = Objects.requireNonNull(name);
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

    public void setSubId(short subId) {
        this.subId = subId;
        this.changed = true;
    }

    public void setSkullOwner(String skullOwner) {
        this.skullOwner = skullOwner;
        this.changed = true;
    }

    public void setColor(Color color) {
        this.color = color;
        this.changed = true;
    }
    // endregion

    // region getter

    public String getMaterial() {
        return this.material;
    }

    public short getSubId() {
        return this.subId;
    }

    public int getCount() {
        return this.count;
    }

    public String getName() {
        return this.name;
    }

    public List<String> getLore() {
        return this.lore;
    }

    public boolean isEnch() {
        return this.ench;
    }

    public boolean isUnbreakable() {
        return this.unbreakable;
    }

    public String getSkullOwner() {
        return this.skullOwner;
    }

    public Color getColor() {
        return this.color;
    }

    public Object getCacheItem() {
        return this.cacheItem;
    }

    // endregion

    // region builder

    public static IconBuilder builder() {
        return IconBuilder.anIcon();
    }

    public static final class IconBuilder {
        private final Icon icon;

        private IconBuilder() {
            icon = new Icon("STONE", "NONE_NAME");
        }

        public static IconBuilder anIcon() {
            return new IconBuilder();
        }

        public IconBuilder material(@Nonnull String material) {
            icon.setMaterial(material);
            return this;
        }

        public IconBuilder subId(short subId) {
            icon.setSubId(subId);
            return this;
        }

        public IconBuilder count(int count) {
            icon.setCount(count);
            return this;
        }

        public IconBuilder name(@Nonnull String name) {
            icon.setName(name);
            return this;
        }

        public IconBuilder lore(List<String> lore) {
            icon.setLore(lore);
            return this;
        }

        public IconBuilder ench(boolean ench) {
            icon.setEnch(ench);
            return this;
        }

        public IconBuilder unbreakable(boolean unbreakable) {
            icon.setUnbreakable(unbreakable);
            return this;
        }

        public IconBuilder skullOwner(String skullOwner) {
            icon.setSkullOwner(skullOwner);
            return this;
        }

        public IconBuilder color(Color color) {
            icon.setColor(color);
            return this;
        }

        public Icon build() {
            return icon;
        }
    }

    // endregion

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

        // region getter

        public byte getRed() {
            return this.red;
        }

        public byte getGreen() {
            return this.green;
        }

        public byte getBlue() {
            return this.blue;
        }

        // endregion
    }

}
