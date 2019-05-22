package org.yunshanmc.lmc.core.gui.components;

import com.google.common.base.Preconditions;
import org.yunshanmc.lmc.core.gui.Icon;

import java.util.Arrays;
import java.util.Objects;

public class Border extends BaseComponent {

    public Border(int rowX, int columnX, int rowY, int columnY, Icon icon) {
        super(computeSlots(rowX, columnX, rowY, columnY));
        this.setIcon(icon);
    }

    public Border(int rowY, int columnY, Icon icon) {
        this(1, 1, rowY, columnY, icon);
    }

    public void setIcon(Icon icon) {
        Objects.requireNonNull(icon);
        Arrays.fill(super.icons, icon);
    }

    private static int[] computeSlots(int rowX, int columnX, int rowY, int columnY) {
        Preconditions.checkArgument(rowX < rowY, "need rowX < rowY");
        Preconditions.checkArgument(columnX < columnY, "need columnX < column");

        int len = (rowY - rowX) * 2 + (columnY - columnX) * 2;
        int[] slots = new int[len];
        int idx = 0;
        // 上边界
        for (int i = columnX - 1; i < columnY; i++) {
            slots[idx++] = (rowX - 1) * 9 + i;
        }
        // 左右边界
        for (int row = rowX + 1; row < rowY; row++) {
            slots[idx++] = (row - 1) * 9 + (columnX - 1);
            slots[idx++] = (row - 1) * 9 + (columnY - 1);
        }
        // 下边界
        for (int i = columnX - 1; i < columnY; i++) {
            slots[idx++] = (rowY - 1) * 9 + i;
        }
        return slots;
    }
}
