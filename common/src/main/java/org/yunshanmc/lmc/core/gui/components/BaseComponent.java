package org.yunshanmc.lmc.core.gui.components;

import org.yunshanmc.lmc.core.gui.ClickInfo;
import org.yunshanmc.lmc.core.gui.Icon;

import java.util.Arrays;
import java.util.function.BiConsumer;

public abstract class BaseComponent {

    protected static final BiConsumer<ClickInfo, Object> EMPTY_CONSUMER = (a, b) -> {
    };

    protected final int[] slots;
    protected final Icon[] icons;

    protected final int[] slotMap;

    protected BaseComponent(int[] slots) {
        Arrays.sort(slots);
        this.slots = slots;
        this.icons = new Icon[slots.length];

        this.slotMap = new int[slots[slots.length - 1] + 1];
        for (int i = 0; i < slots.length; i++) {
            int slot = slots[i];
            this.slotMap[slot] = i;
        }
    }

    /**
     * @return 该组件占用的slot列表
     */
    public final int[] getSlots() {
        return Arrays.copyOf(this.slots, this.slots.length);
    }

    /**
     * 获取该组件的图标列表，每个图标的位置由getSlots的相同索引的值决定
     *
     * @return 该组件的图标列表
     */
    public Icon[] getIcons() {
        return this.icons;
    }

    /**
     * 当该组件被点击的时候调用
     *
     * @param slot   点击的slot位置
     * @param click  点击信息
     * @param player 玩家对象
     */
    public void onClick(int slot, ClickInfo click, Object player) {

    }
}
