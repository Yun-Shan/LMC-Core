package org.yunshanmc.lmc.core.command;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public abstract class ParameterConverter<T> {

    private static final MethodHandles.Lookup LOOKUP;

    private static Method M_Convert;

    static {
        LOOKUP = MethodHandles.lookup();
        for (Method m : ParameterConverter.class.getDeclaredMethods()) {
            if ("convert".equals(m.getName())) {
                M_Convert = m;
                break;
            }
        }
    }

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

    public T convert(String str) {
        if (str == null) return this.getDefaultValue();
        try {
            T res = this.convertArg(str);
            if (res == null) throw new ArgConverterFailException(str, this.convertTo);
            return res;
        } catch (ArgConverterFailException e) {
            throw e;
        } catch (Throwable t) {
            throw new ArgConverterFailException(str, this.convertTo, t);
        }
    }

    public T getDefaultValue() {
        return null;
    }

    protected abstract T convertArg(String str);

    public final MethodHandle toMethodHandle() {
        try {
            MethodHandle handle = LOOKUP.unreflect(M_Convert).bindTo(this);
            handle = handle.asType(handle.type().changeReturnType(this.convertTo));
            return handle;
        } catch (IllegalAccessException e) {// 已经setAccessible，该异常不会出现
            e.printStackTrace();
            throw new UnknownError();
        }
    }

    static {
        register(new ParameterConverter<String>(String.class) {
            @Override
            public String convertArg(String str) {
                return str;
            }
        });
        register(new ParameterConverter<Integer>(int.class) {
            @Override
            public Integer convertArg(String arg) {
                return Integer.valueOf(arg);
            }

            @Override
            public Integer getDefaultValue() {
                return -1;
            }
        });
    }
}
