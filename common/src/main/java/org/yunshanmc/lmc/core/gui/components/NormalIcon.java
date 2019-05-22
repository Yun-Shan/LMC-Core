package org.yunshanmc.lmc.core.gui.components;

import org.yunshanmc.lmc.core.gui.Icon;

public class NormalIcon extends BaseComponent {

    public NormalIcon(int row, int column) {
        super(new int[]{(row - 1) * 9 + (column - 1)});
    }

    public NormalIcon(int row, int column, Icon icon) {
        this(row, column);
        this.setIcon(icon);
    }

    public void setIcon(Icon icon) {
        this.icons[0] = icon;
    }
}
