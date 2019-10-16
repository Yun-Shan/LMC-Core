package org.yunshanmc.lmc.core.message;

import java.util.UUID;

public class MockMessage extends BaseMessage {

    public MockMessage(String msg, MessageContext context) {
        super(msg, context);
    }

    @Override
    protected String translateAlternateColorCodes(String msg) {
        return msg;
    }

    @Override
    public String getMessageForPlayer(Object player, Object... args) {
        return msg;
    }

    @Override
    public String[] getMessagesForPlayer(Object player, Object... args) {
        return new String[]{ msg };
    }

    @Override
    public String getMessageForPlayer(UUID playerId, Object... args) {
        return msg;
    }

    @Override
    public String[] getMessagesForPlayer(UUID playerId, Object... args) {
        return new String[]{ msg };
    }
}
