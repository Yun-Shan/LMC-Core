/*
 * Author: Yun-Shan
 * Date: 2017/07/20
 */
package org.yunshanmc.lmc.core.message;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.bukkit.entity.Player;
import org.yunshanmc.lmc.core.utils.PlatformUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.yunshanmc.lmc.core.message.DefaultMessageSender.FAKE_PLAYER_BUKKIT;
import static org.yunshanmc.lmc.core.message.DefaultMessageSender.FAKE_PLAYER_BUNGEE;

/**
 * //TODO 注释
 */
public class DefaultMessageFormat implements MessageFormat {

    protected final MessageContext context;

    public DefaultMessageFormat(MessageContext context) {
        this.context = context;
    }

    @Override
    public String format(Player player, String msg, Object... args) {
        return format(msg, args);
    }

    @Override
    public String format(ProxiedPlayer player, String msg, Object... args) {
        return format(msg, args);
    }

    private static final Pattern VARIABLE_PATTERN = Pattern.compile("" +
            "\\{(" +
            "(?<idx>[0-9]+)" +
            "|(?<subMsg>##[a-zA-Z0-9.]+)" +
            "|(?<context>$[^}]+)" +
            // "|(?<js>&[^}]+)" + // TODO js功能
            ")}");

    private String format(String msg, Object... args) {
        Matcher matcher = VARIABLE_PATTERN.matcher(msg);
        StringBuffer buffer = new StringBuffer();
        while (matcher.find()) {
            String val;
            if ((val = matcher.group("idx")) != null) {
                int idx = Integer.parseInt(val);
                if (args.length >= idx) val = String.valueOf(args[idx - 1]);
            } else if ((val = matcher.group("subMsg")) != null) {
                Message message = this.context.getMessageManager().getMessage(val.substring(2));
                if (PlatformUtils.isBukkit()) {
                    val = message.getMessage(FAKE_PLAYER_BUKKIT);
                } else if (PlatformUtils.isBungeeCord()) {
                    val = message.getMessage(FAKE_PLAYER_BUNGEE);
                }
                if (val != null) {
                    val = "§r" + val + "§r";
                }
            } else if ((val = matcher.group("context")) != null) {
                val = String.valueOf(this.context.get(val.substring(1)));
            }

            if (val == null) {
                val = matcher.group();
            }
            matcher.appendReplacement(buffer, val);
        }
        matcher.appendTail(buffer);
        return buffer.toString();
    }
}
