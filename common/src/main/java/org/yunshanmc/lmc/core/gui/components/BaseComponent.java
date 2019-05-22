package org.yunshanmc.lmc.core.gui.components;

import org.yunshanmc.lmc.core.gui.ClickInfo;
import org.yunshanmc.lmc.core.gui.Icon;

public abstract class BaseComponent {

    private final int[] slots;
    protected final Icon[] icons;

    protected BaseComponent(int[] slots) {
        this.slots = slots;
        this.icons = new Icon[slots.length];
    }

    /**
     * @return 该组件占用的slot列表
     */
    public final int[] getSlots() {
        return this.slots;
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
     * @param click 点击信息
     */
    public void onClick(ClickInfo click) {

    }
}
