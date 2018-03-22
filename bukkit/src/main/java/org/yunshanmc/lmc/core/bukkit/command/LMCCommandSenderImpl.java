package org.yunshanmc.lmc.core.bukkit.command;

import org.bukkit.command.CommandSender;
import org.yunshanmc.lmc.core.command.LMCCommandSender;
import org.yunshanmc.lmc.core.message.MessageSender;

public class LMCCommandSenderImpl extends LMCCommandSender {

    public LMCCommandSenderImpl(CommandSender handle, MessageSender messageSender) {
        super(handle, messageSender);
    }

    @Override
    public void debug(int debugLevel, String msgKey, Object... args) {
        this.messageSender.debug(debugLevel, (CommandSender)getHandle(), msgKey, args);
    }

    @Override
    public void error(String msgKey, Object... args) {
        this.messageSender.error((CommandSender)getHandle(), msgKey, args);
    }

    @Override
    public void warning(String msgKey, Object... args) {
        this.messageSender.warning((CommandSender)getHandle(), msgKey, args);
    }

    @Override
    public void info(String msgKey, Object... args) {
        this.messageSender.info((CommandSender)getHandle(), msgKey, args);
    }

    @Override
    public void message(String type, String msgKey, Object... args) {
        this.messageSender.error((CommandSender)getHandle(), msgKey, args);
    }
}
