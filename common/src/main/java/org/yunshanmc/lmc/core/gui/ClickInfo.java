package org.yunshanmc.lmc.core.gui;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ClickInfo {

    private final ClickType type;

    public ClickType getType() {
        return this.type;
    }

    /**
     * 是否是左键单击<p>
     * 注意：双击时该方法返回false，请使用{@link #isDoubleClick()}判断左键双击
     *
     * @return 左键单击(包括shift + 左键)时返回true，其它时候返回false
     */
    public final boolean isLeftClick() {
        return this.type == ClickType.LEFT || this.type == ClickType.SHIFT_LEFT;
    }

    public final boolean isRightClick() {
        return this.type == ClickType.RIGHT || this.type == ClickType.SHIFT_RIGHT;
    }

    public final boolean isMiddleClick() {
        return this.type == ClickType.MIDDLE;
    }

    public final boolean isShiftClick() {
        return this.type == ClickType.SHIFT_LEFT || this.type == ClickType.SHIFT_RIGHT;
    }

    public final boolean isDoubleClick() {
        return this.type == ClickType.DOUBLE_CLICK;
    }

    public final boolean isNumberClick() {
        return this.type == ClickType.NUMBER_KEY;
    }

    public final boolean isDropClick() {
        return this.type == ClickType.DROP;
    }

    public final boolean isControlDropClick() {
        return this.type == ClickType.CONTROL_DROP;
    }

    public enum ClickType {
        /**
         * Ctrl+Q
         */
        CONTROL_DROP,
        /**
         * Q
         */
        DROP,
        /**
         * 左键
         */
        LEFT,
        /**
         * 鼠标中键
         */
        MIDDLE,
        /**
         * 右键
         */
        RIGHT,
        /**
         * 双击左键
         */
        DOUBLE_CLICK,
        /**
         * Shift+左键
         */
        SHIFT_LEFT,
        /**
         * Shift+右键
         */
        SHIFT_RIGHT,
        /**
         * 数字键(非小键盘区域的)
         */
        NUMBER_KEY,

        /**
         * 未知按键
         */
        UNKNOWN
    }
}
