package org.yunshanmc.lmc.core.message;

import org.junit.Test;
import org.yunshanmc.lmc.core.MockPlugin;

public class DefaultMessageFormatTest {

    @Test
    public void format() {
        MockPlugin plugin = MockPlugin.newInstance();
        MessageContext context = new MessageContext(plugin, plugin.getMessageManager());
        DefaultMessageFormat format = new DefaultMessageFormat(context);
        System.out.println(format.format("{%PluginName}"));
    }
}