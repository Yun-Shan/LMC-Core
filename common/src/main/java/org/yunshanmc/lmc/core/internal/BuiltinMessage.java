package org.yunshanmc.lmc.core.internal;

import org.yunshanmc.lmc.core.util.ReflectUtils;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * 内置信息.
 * <p>
 *
 * @author Yun-Shan
 */
public class BuiltinMessage {

    private static final Map<Locale, Map<String, String>> MESSAGES;

    private static Map<String, String> current;

    static {
        MESSAGES = new HashMap<>();
        Map<String, String> chinese = new HashMap<>();
        chinese.put("DefaultErrorHandler", "" +
            "§7[§aLMC-Core§7]§e[0]§b插件出现异常，异常信息如下：\n" +
            "§7[§aLMC-Core§7]§6异常类型：§a[1]\n" +
            "§7[§aLMC-Core§7]§6异常说明：§a[2]\n" +
            "§7[§aLMC-Core§7]§6异常栈：§f\n" +
            "[3]\n" +
            "§7[§aLMC-Core§7]§b以上为§e[0]§b插件的异常信息");
        chinese.put("InExceptionHandler_ExceptionDescription", "$$读取插件信息失败$$");
        chinese.put("MissingLanguage", "§c未知的提示信息模板： [0]");

        Map<String, String> english = new HashMap<>();
        english.put("MissingLanguage", "§cMissingLanguage: [0]");
        english.put("InExceptionHandler_ExceptionDescription", "$$Failed To Read Plugin Info$$");
        english.put("DefaultErrorHandler", "" +
            "§7[§aLMC-Core§7]§e[0]§bPlugin throw an exception: \n" +
            "§7[§aLMC-Core§7]§6Type：§a[1]\n" +
            "§7[§aLMC-Core§7]§6Message：§a[2]\n" +
            "§7[§aLMC-Core§7]§6StackTrace：§f\n" +
            "[3]\n" +
            "§7[§aLMC-Core§7]§bThe above is the exception information of the §e[0]§b plugin.");

        MESSAGES.put(Locale.CHINESE, chinese);
        MESSAGES.put(Locale.ENGLISH, english);
        setLocale(Locale.CHINESE);
    }

    public static String getMessage(String key, Object... args) {
        String msg = current.get(key);
        if (msg == null) {
            return getMessage("MissingLanguage", "null");
        }
        for (int i = 0; i < args.length; i++) {
            msg = msg.replace("[" + i + "]", String.valueOf(args[i]));
        }
        return msg;
    }

    public static void setLocale(Locale locale) {
        ReflectUtils.checkSafeCall();
        current = MESSAGES.getOrDefault(locale, current);
    }

}
