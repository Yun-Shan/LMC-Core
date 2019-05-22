package org.yunshanmc.lmc.core.gui;

import org.junit.Test;
import org.yunshanmc.lmc.core.gui.components.Border;

import static org.junit.Assert.*;

public class BorderTest {

    @Test
    public void testSlots() {
        int[] slots = new int[]{10, 11, 12, 19, 21, 28, 29, 30};
        assertArrayEquals(slots,
            new Border(2, 2, 4, 4, Icon.builder().material("").name("").build()).getSlots());
    }

}