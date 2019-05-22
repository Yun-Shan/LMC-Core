package org.yunshanmc.lmc.core.gui.components;

import com.google.common.base.Preconditions;
import lombok.Getter;
import lombok.Setter;
import org.yunshanmc.lmc.core.gui.Icon;

import java.util.function.Predicate;

public class Container extends BaseComponent {

    @Setter
    private boolean canInput;
    @Setter
    private boolean canOutput;
    @Setter
    private Predicate checker;
    @Getter
    private final int size;

    public Container(int[] slots, Predicate checker) {
        super(slots);
        this.size = slots.length;
        this.checker = checker;
    }

    public Container(int rowX, int columnX, int rowY, int columnY, Predicate checker) {
        this(computeSlots(rowX, columnX, rowY, columnY), checker);
    }

    public Container(int[] slots) {
        this(slots, o -> true);
    }

    public Container(int rowX, int columnX, int rowY, int columnY) {
        this(computeSlots(rowX, columnX, rowY, columnY));
    }

    public void setIcons(Icon[] icons) {
        System.arraycopy(icons, 0, super.icons, 0, icons.length);
    }

    public boolean canInput() {
        return this.canInput;
    }

    public boolean canOutput() {
        return this.canInput || this.canOutput;
    }

    /**
     * 是否接受该物品输入容器
     *
     * @param item 物品实例
     * @return true接受，false不接受
     */
    @SuppressWarnings("unchecked")
    public boolean accept(Object item) {
        return this.canInput && this.checker.test(item);
    }

    private static int[] computeSlots(int rowX, int columnX, int rowY, int columnY) {
        Preconditions.checkArgument(rowX <= rowY, "need rowX <= rowY");
        Preconditions.checkArgument(columnX <= columnY, "need columnX <= column");

        int len = (rowY - rowX + 1) * (columnY - columnX + 1);
        int[] slots = new int[len];
        int idx = 0;
        for (int row = rowX; row <= rowY; row++) {
            for (int column = columnX; column <= columnY; column++) {
                slots[idx++] = (row - 1) * 9 + (column - 1);
            }
        }
        return slots;
    }
}
