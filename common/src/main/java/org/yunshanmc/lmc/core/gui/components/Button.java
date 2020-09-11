package org.yunshanmc.lmc.core.gui.components;

import org.yunshanmc.lmc.core.gui.ClickInfo;
import org.yunshanmc.lmc.core.gui.Icon;

import java.util.Objects;
import java.util.function.BiConsumer;

/**
 * @author Yun Shan
 */
public class Button extends NormalIcon {

    private BiConsumer<ClickInfo, Object> handle;

    public Button(int row, int column, BiConsumer<ClickInfo, Object> handle) {
        this(row, column, null, handle);
    }

    public Button(int row, int column, Icon icon, BiConsumer<ClickInfo, Object> handle) {
        super(row, column, icon);
        this.setHandle(handle);
    }

    public Button(int row, int column) {
        this(row, column, EMPTY_CONSUMER);
    }

    public Button(int row, int column, Icon icon) {
        this(row, column, icon, EMPTY_CONSUMER);
    }

    public void setHandle(BiConsumer<ClickInfo, Object> handle) {
        this.handle = Objects.requireNonNull(handle);
    }

    @Override
    public void onClick(int slot, ClickInfo click, Object player) {
        this.handle.accept(click, player);
    }
}
