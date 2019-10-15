package org.yunshanmc.lmc.core.config;

import org.yunshanmc.lmc.core.command.AbstractParameterConverter;
import org.yunshanmc.lmc.core.exception.ExceptionHandler;

import java.lang.invoke.MethodType;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Yun-Shan
 */
public final class ConfigFactory {
    private ConfigFactory() {
        // 禁止实例化
        throw new Error();
    }

    private static final Map<Class, BiFunction<LMCConfiguration, String, ?>> CONFIG_READERS = new HashMap<>();

    static {
        registerConfigReader(String.class, LMCConfiguration::getString);
        // TODO 基本类型获取的时候永不为null，考虑用户配置为其它类型时返回基本类型默认值 导致的实际默认值未生效的情况
        registerConfigReader(Integer.class, LMCConfiguration::getInt);
        registerConfigReader(Boolean.class, LMCConfiguration::getBoolean);
    }

    private static final Pattern UPPER_ALPHA_PATTERN = Pattern.compile("[A-Z]");

    /**
     * @see ConfigManager#getConfig(Class)
     */
    public static <T> T loadConfig(ConfigManager configManager, Class<T> clazz) {
        try {
            // 先实例化
            Constructor<T> constructor = clazz.getDeclaredConstructor();
            constructor.setAccessible(true);
            T obj = constructor.newInstance();

            ConfigFile configFile = clazz.getAnnotation(ConfigFile.class);
            String filePath;
            boolean needFileExists;
            if (configFile != null) {
                filePath = configFile.path();
                if (filePath.isEmpty()) {
                    filePath = configFile.value();
                }
                if (filePath.isEmpty()) {
                    filePath = "config.yml";
                }
                needFileExists = configFile.needFileExists();
            } else {
                filePath = "config.yml";
                needFileExists = false;
            }

            LMCConfiguration config = configManager.getConfig(filePath);
            if (config == null && needFileExists) {
                // TODO 自定义异常：配置文件不存在
                throw new RuntimeException();
            }
            // 配置不在根节点
            if (configFile != null && config != null && !configFile.section().isEmpty()) {
                config = config.getSection(configFile.section());
            }

            return injectConfig(config, obj);
        } catch (ReflectiveOperationException e) {
            ExceptionHandler.handle(e);
            return null;
        }
    }

    /**
     * 只读取ConfigField，调用方有义务提供配置所在节点的config对象
     */
    public static <T> T injectConfig(LMCConfiguration config, T obj) {
        for (Field field : obj.getClass().getDeclaredFields()) {
            ConfigField configField = field.getAnnotation(ConfigField.class);
            if (configField == null) {
                continue;
            }
            if (config != null) {
                String fieldPath = configField.path();
                if (fieldPath.isEmpty()) {
                    fieldPath = configField.value();
                }
                if (fieldPath.isEmpty()) {
                    fieldPath = field.getName();
                    if (!config.isSet(fieldPath)) {
                        Matcher matcher = UPPER_ALPHA_PATTERN.matcher(fieldPath);
                        StringBuffer sb = new StringBuffer();
                        while (matcher.find()) {
                            matcher.appendReplacement(sb, "-" + Character.toLowerCase(matcher.group().charAt(0)));
                        }
                        matcher.appendTail(sb);
                        fieldPath = sb.toString();
                    }
                }
                Class<?> fieldType = field.getType();
                // 基本类型转为包装类型
                if (fieldType.isPrimitive()) {
                    fieldType = MethodType.methodType(fieldType).wrap().returnType();
                }

                Object val = null;
                if (config.isSet(fieldPath)) {
                    BiFunction<LMCConfiguration, String, ?> reader = CONFIG_READERS.get(fieldType);
                    if (reader != null) {
                        val = reader.apply(config, fieldPath);
                    }
                } else {
                    if (fieldType.equals(String.class)) {
                        if (!configField.defaultValue().isEmpty() || !configField.defaultValueNull()) {
                            val = "";
                        }
                    } else if (!configField.defaultValue().isEmpty()) {
                        AbstractParameterConverter<?> converter = AbstractParameterConverter.getConverter(fieldType);
                        if (converter != null) {
                            val = converter.convert(configField.defaultValue());
                        }
                    }
                }

                if (val != null) {
                    field.setAccessible(true);
                    try {
                        field.set(obj, val);
                    } catch (IllegalAccessException e) {
                        ExceptionHandler.handle(e);
                    }
                }
            }
        }
        return obj;
    }

    public static <T> void registerConfigReader(Class<T> type, BiFunction<LMCConfiguration, String, T> reader) {
        CONFIG_READERS.put(type, reader);
    }
}
