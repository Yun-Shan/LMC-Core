package org.yunshanmc.lmc.core.gui.components;

import org.junit.Test;
import org.yunshanmc.lmc.core.gui.Icon;

import java.util.Arrays;

import static org.junit.Assert.*;

public class BorderTest {

    @Test
    public void test() {
        int[] slots = new int[]{10, 11, 12, 19, 21, 28, 29, 30};
        Icon icon = Icon.builder().material("").name("").build();
        Border border = new Border(2, 2, 4, 4, icon);
        assertArrayEquals(slots, border.getSlots());
        Icon[] icons = new Icon[border.getIcons().length];
        Arrays.fill(icons, icon);
        assertArrayEquals(icons, border.getIcons());
        try {
            new Border(3, 2, 2, 4, null);
            fail();
        } catch (IllegalArgumentException ex) {
            assertEquals("need rowX < rowY", ex.getMessage());
        }
        try {
            new Border(2, 3, 4, 2, null);
            fail();
        } catch (IllegalArgumentException ex) {
            assertEquals("need columnX < columnY", ex.getMessage());
        }
    }

}