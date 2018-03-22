/*
 * Author: Yun-Shan
 * Date: 2017/07/20
 */
package org.yunshanmc.lmc.core.message;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * //TODO 注释
 */
public class DefaultMessageFormat implements MessageFormat {

    protected final MessageContext context;

    public DefaultMessageFormat(MessageContext context) {
        this.context = context;
    }

    private static final Pattern VARIABLE_PATTERN =
        Pattern.compile("" +
            "\\{(" +
            "(?<idx>[0-9]+)" +
            "|(?<subMsg>##[^}]+)" +
            "|(?<context>\\$[^}]+)" +
            // "|(?<js>&[^}]+)" + // TODO js功能
            ")}");

    @Override
    public String format(String msg, Object... args) {
        Matcher matcher = VARIABLE_PATTERN.matcher(msg);
        StringBuffer buffer = new StringBuffer();
        while (matcher.find()) {
            String val;
            if ((val = matcher.group("idx")) != null) {
                int idx = Integer.parseInt(val);
                if (idx > 0 && args.length >= idx) val = String.valueOf(args[idx - 1]);
            } else if ((val = matcher.group("subMsg")) != null) {
                Message message = this.context.getMessageManager().getMessage(val.substring(2));
                val = message.getMessage(null);

                if (val != null) {
                    val = "§r" + val + "§r";
                }
            } else if ((val = matcher.group("context")) != null) {
                val = this.context.getString(val.substring(1));
            }

            if (val == null) {
                val = matcher.group();
            }

            // $ 转义 start
            char[] cTmp = {40};
            String sTmp = new String(cTmp);
            while (val.contains(sTmp) || buffer.indexOf(sTmp) != -1) {
                cTmp[0]++;
                sTmp = new String(cTmp);
            }
            boolean change = false;
            char[] chars = val.toCharArray();
            for (int i = 0; i < chars.length; i++) {
                if (chars[i] == '$') {
                    chars[i] = cTmp[0];
                    change = true;
                }
            }
            if (change) val = new String(chars);
            // $ 转义 end
            // ↓↓↓所以说正则的反向引用检测不加个开关真的很烦_(:з」∠)_
            matcher.appendReplacement(buffer, val);
            if (change) {
                // $ 反转义 start
                for (int i = 0; i < buffer.length(); i++) {
                    char c = buffer.charAt(i);
                    if (c == cTmp[0]) c = '$';
                    buffer.setCharAt(i, c);
                }
                // $ 反转义 end
            }
        }
        matcher.appendTail(buffer);
        return buffer.toString();
    }
}
