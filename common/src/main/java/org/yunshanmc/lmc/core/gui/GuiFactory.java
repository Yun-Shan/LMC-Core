package org.yunshanmc.lmc.core.gui;

import org.yunshanmc.lmc.core.gui.components.BaseComponent;
import org.yunshanmc.lmc.core.gui.components.Container;
import org.yunshanmc.lmc.core.util.PlatformUtils;

import java.util.ArrayList;
import java.util.List;

public class GuiFactory {

    private static InvProvider invProvider;

    private String title;
    private int row;
    private List<BaseComponent> components = new ArrayList<>();

    public GuiFactory() {
        if (invProvider == null) {
            throw new UnsupportedOperationException("Gui Unsupported at " + PlatformUtils.getPlatform());
        }
    }

    /**
     * 设置GUI的标题
     *
     * @param title 标题
     * @return 自身实例
     */
    public GuiFactory withTitle(String title) {
        this.title = title;
        return this;
    }

    /**
     * 设置GUI的行数
     *
     * @param row 行数
     * @return 自身实例
     */
    public GuiFactory withRow(int row) {
        this.row = row;
        return this;
    }

    public GuiFactory addComponent(BaseComponent component) {
        this.components.add(component);
        return this;
    }

    public Gui build() {
        int size = this.row * 9;
        Icon[] icons = new Icon[size];
        BaseComponent[] components = new BaseComponent[size];

        for (BaseComponent component : this.components) {
            int[] slots = component.getSlots();
            Icon[] comIcons = component.getIcons();
            for (int i = 0; i < slots.length; i++) {
                int slot = slots[i];
                icons[slot] = comIcons[i];
                components[slot] = component;
            }
        }
        return new Gui(this.title, this.row,
            icons,
            components,
            this.components
                .stream()
                .filter(c -> c instanceof Container)
                .map(c -> (Container) c)
                .toArray(Container[]::new),
            invProvider);
    }

    public static void setInvProvider(InvProvider invProvider) {
        if (GuiFactory.invProvider != null) {
            throw new IllegalStateException();
        }
        GuiFactory.invProvider = invProvider;
    }
}
