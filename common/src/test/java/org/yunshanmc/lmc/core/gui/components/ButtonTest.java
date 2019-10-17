package org.yunshanmc.lmc.core.gui.components;

import org.junit.Test;
import org.yunshanmc.lmc.core.gui.ClickInfo;
import org.yunshanmc.lmc.core.gui.Icon;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.*;

public class ButtonTest {

    @Test
    public void test() {
        Button button = new Button(3, 4);
        button.onClick(0, null, null);
        Icon icon = Icon.builder().material("").name("").build();
        button = new Button(3, 4, icon);
        assertArrayEquals(new int[]{21}, button.getSlots());
        assertArrayEquals(new Icon[]{icon}, button.getIcons());

        AtomicBoolean ref = new AtomicBoolean(false);
        Object mockPlayer = new Object();
        // setHandle
        button.setHandle((info, player) -> {
            assertEquals(ClickInfo.ClickType.CONTROL_DROP, info.getType());
            assertEquals(mockPlayer, player);
            ref.set(true);
        });
        // onClick
        button.onClick(0, new ClickInfo(ClickInfo.ClickType.CONTROL_DROP), mockPlayer);
        assertTrue(ref.get());
    }
}