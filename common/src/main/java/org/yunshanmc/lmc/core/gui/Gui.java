package org.yunshanmc.lmc.core.gui;

import org.yunshanmc.lmc.core.gui.components.BaseComponent;
import org.yunshanmc.lmc.core.gui.components.Container;

import java.util.Arrays;
import java.util.Objects;

/**
 * @author Yun Shan
 */
public class Gui {

    private final String title;
    private final int row;
    private final int size;

    private final Icon[] icons;
    private final BaseComponent[] components;
    private final Container[] containers;
    private final Container[] inputs;
    private final Container[] outputs;

    private final InvProvider invProvider;
    private Object invSingleton;

    public Gui(String title, int row, Icon[] icons, BaseComponent[] components, Container[] containers, InvProvider invProvider) {
        this.title = title;
        this.row = row;
        this.size = row * 9;
        this.icons = icons;
        this.components = components;
        this.containers = containers;
        this.invProvider = Objects.requireNonNull(invProvider);
        this.inputs = new Container[icons.length];
        this.outputs = new Container[icons.length];
        Arrays.stream(containers)
            .filter(Container::canInput)
            .forEach(c -> {
                for (int slot : c.getSlots()) {
                    this.inputs[slot] = c;
                }
            });
        Arrays.stream(containers)
            .filter(Container::canOutput)
            .forEach(c -> {
                for (int slot : c.getSlots()) {
                    this.outputs[slot] = c;
                }
            });
    }

    public Icon[] getIcons() {
        return Arrays.copyOf(this.icons, this.icons.length);
    }

    public Container[] getContainers() {
        return Arrays.copyOf(this.containers, this.containers.length);
    }

    public boolean canInput(int slot, Object item) {
        Container input = this.inputs[slot];
        return input != null && input.accept(item);
    }

    public boolean canOutput(int slot) {
        return this.outputs[slot] != null;
    }

    public void onClick(int slot, ClickInfo clickInfo, Object player) {
        BaseComponent clickable = this.components[slot];
        if (clickable != null) {
            clickable.onClick(slot, clickInfo, player);
        }
    }

    @SuppressWarnings("unchecked")
    public <T> T buildNewInv() {
        return (T) this.invProvider.buildInv(this);
    }

    @SuppressWarnings("unchecked")
    public <T> T getInvSingleton() {
        if (this.invSingleton == null) {
            this.invSingleton = this.buildNewInv();
        }
        return (T) this.invSingleton;
    }

    // region getter

    public String getTitle() {
        return this.title;
    }

    public int getRow() {
        return this.row;
    }

    public int getSize() {
        return this.size;
    }

    // endregion
}
