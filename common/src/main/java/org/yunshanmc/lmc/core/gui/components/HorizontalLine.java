package org.yunshanmc.lmc.core.gui.components;

import com.google.common.base.Preconditions;
import org.yunshanmc.lmc.core.gui.Icon;

import java.util.Arrays;

public class HorizontalLine extends BaseComponent {

    public HorizontalLine(int row, int start, int end, Icon icon) {
        super(computeSlots(row, start, end));
        Arrays.fill(super.icons, icon);
    }

    private static int[] computeSlots(int row, int start, int end) {
        Preconditions.checkArgument(start < end, "need start < end");

        int[] slots = new int[end - start + 1];
        int idx = 0;
        int offset = (row - 1) * 9;
        for (int i = start - 1; i < end; i++) {
            slots[idx++] = offset + i;
        }
        return slots;
    }
}
