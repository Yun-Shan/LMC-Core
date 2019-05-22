package org.yunshanmc.lmc.core.gui.components;

import lombok.NonNull;
import org.yunshanmc.lmc.core.gui.ClickInfo;
import org.yunshanmc.lmc.core.gui.Icon;

import java.util.function.Consumer;

public class Button extends NormalIcon {

    @NonNull
    private Consumer<ClickInfo> handle;

    public Button(int row, int column, Consumer<ClickInfo> handle) {
        super(row, column);
        this.handle = handle;
    }

    public Button(int row, int column, Icon icon, Consumer<ClickInfo> handle) {
        super(row, column, icon);
        this.handle = handle;
    }

    @Override
    public void onClick(ClickInfo click) {
        this.handle.accept(click);
    }
}
