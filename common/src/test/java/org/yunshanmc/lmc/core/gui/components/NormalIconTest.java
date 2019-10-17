package org.yunshanmc.lmc.core.gui.components;

import org.junit.Test;
import org.yunshanmc.lmc.core.gui.Icon;

import static org.junit.Assert.*;

public class NormalIconTest {

    @Test
    public void test() {
        Icon icon = Icon.builder().material("").name("").build();
        NormalIcon normalIcon = new NormalIcon(3, 4, icon);
        assertArrayEquals(new int[]{21}, normalIcon.getSlots());
        assertArrayEquals(new Icon[]{icon}, normalIcon.getIcons());
    }

}