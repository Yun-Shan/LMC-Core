/*
 * Author: Yun-Shan
 * Date: 2017/07/20
 */
package org.yunshanmc.lmc.core.message;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * TODO 注释
 *
 * @author Yun-Shan
 */
public class DefaultMessageFormat implements MessageFormat {

    protected final MessageContext context;

    public DefaultMessageFormat(MessageContext context) {
        this.context = context;
    }

    private static final Pattern VARIABLE_PATTERN =
        Pattern.compile("" +
            "\\{(" +
            "(?<idx>[0-9]{1,2})" + // 按序号参数，最多两位数
            "|(?<subMsg>#[\\w.]+)" + // 引用其它信息，不限长度
            "|(?<context>%[\\w.]+)" + // 上下文变量，暂时不限长度
            // "|(?<js>&[^}]+)" + // TODO js功能
            ")}");

    @Override
    public String format(String msg, Object... args) {
        Matcher matcher = VARIABLE_PATTERN.matcher(msg);
        StringBuffer buffer = new StringBuffer();
        while (matcher.find()) {
            String val = null;
            String key;
            if ((key = matcher.group("idx")) != null) {
                int idx = Integer.parseInt(key);
                if (idx > 0 && args.length >= idx) {
                    val = String.valueOf(args[idx - 1]);
                }
            } else if ((key = matcher.group("subMsg")) != null) {
                Message message = this.context.getMessageManager().getMessage(key.substring(1));
                val = message.getMessage();

                if (val != null) {
                    val = "§r" + val + "§r";
                }
            } else if ((key = matcher.group("context")) != null) {
                val = this.context.getString(key.substring(1));
            }

            if (val == null) {
                val = matcher.group();
            }
            // '\'和'$'会在前面加反斜杠
            matcher.appendReplacement(buffer, val.replace("\\", "\\\\").replace("$", "\\$"));
        }
        matcher.appendTail(buffer);
        return buffer.toString();
    }
}
