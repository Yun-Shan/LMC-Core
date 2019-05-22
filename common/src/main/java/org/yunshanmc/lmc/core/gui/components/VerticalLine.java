package org.yunshanmc.lmc.core.gui.components;

import com.google.common.base.Preconditions;
import org.yunshanmc.lmc.core.gui.Icon;

import java.util.Arrays;

public class VerticalLine extends BaseComponent {

    public VerticalLine(int column, int start, int end, Icon icon) {
        super(computeSlots(column, start, end));
        Arrays.fill(super.icons, icon);
    }

    private static int[] computeSlots(int column, int start, int end) {
        Preconditions.checkArgument(start < end, "need start < end");

        int[] slots = new int[end - start + 1];
        int idx = 0;
        int offset = column - 1;
        for (int i = start - 1; i < end; i++) {
            slots[idx++] = (i - 1) * 9 + offset;
        }
        return slots;
    }
}
