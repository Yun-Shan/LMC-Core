package org.yunshanmc.lmc.core.gui.components;

import com.google.common.base.Preconditions;
import lombok.Getter;
import lombok.Setter;
import org.yunshanmc.lmc.core.gui.ClickInfo;
import org.yunshanmc.lmc.core.gui.Icon;

import java.util.function.BiConsumer;
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
    @Getter
    private final BiConsumer<ClickInfo, Object>[] handlers;

    @SuppressWarnings("unchecked")
    public Container(int[] slots, Predicate checker) {
        super(slots);
        this.size = slots.length;
        this.checker = checker;
        this.handlers = new BiConsumer[this.size];
    }

    public Container(int[] slots) {
        this(slots, o -> true);
    }

    public Container(int rowX, int columnX, int rowY, int columnY, Predicate checker) {
        this(computeSlots(rowX, columnX, rowY, columnY), checker);
    }

    public Container(int rowX, int columnX, int rowY, int columnY) {
        this(rowX, columnX, rowY, columnY, null);
    }

    public void setIcons(Icon[] icons) {
        System.arraycopy(icons, 0, super.icons, 0, icons.length);
    }

    public void setHandlers(BiConsumer<ClickInfo, Object>[] handlers) {
        System.arraycopy(handlers, 0, this.handlers, 0, handlers.length);
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

    @Override
    public void onClick(int slot, ClickInfo click, Object player) {
        BiConsumer<ClickInfo, Object> handler = this.handlers[this.slotMap[slot]];
        if (handler != null) {
            handler.accept(click, player);
        }
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
