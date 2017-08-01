package org.yunshanmc.lmc.core.command;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public abstract class ParameterConverter<T> {

    private static final MethodHandles.Lookup LOOKUP = MethodHandles.lookup();

    private static final Map<Class<?>, ParameterConverter<?>> converters = new HashMap<>();

    private final Class<T> convertTo;

    public ParameterConverter(Class<T> convertTo) {
        this.convertTo = convertTo;
    }

    @SuppressWarnings("unchecked")
    public static <R> ParameterConverter<R> getConverter(Class<R> convertTo) {
        return (ParameterConverter<R>) converters.get(convertTo);
    }

    public static void register(ParameterConverter<?> converter) {
        converters.put(converter.convertTo, converter);
    }

    public abstract T convert(String str);

    public final MethodHandle toMethodHandle() {
        try {
            Method mConvert = null;
            for (Method m : this.getClass().getDeclaredMethods()) {
                if (m.getName().equals("convert") && m.getParameterCount() == 1 &&
                    m.getParameterTypes()[0] == String.class) {
                    mConvert = m;
                    break;
                }
            }
            assert mConvert != null;// 不断言IDE会有mConvert可能为null的提示，很烦qwq。实际上mConvert不可能为null
            MethodHandle handle = LOOKUP.unreflect(mConvert).bindTo(this);
            handle = handle.asType(handle.type().changeReturnType(this.convertTo));
            return handle;
        } catch (IllegalAccessException e) {// 已经setAccessible，该异常不会出现
            e.printStackTrace();
            throw new UnknownError();
        }
    }
}
