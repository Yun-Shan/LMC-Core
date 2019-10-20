package org.yunshanmc.lmc.core.command;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Yun-Shan
 * TODO 由于现在同时被命令模块和配置模块使用，需要独立出一个模块，考虑定为表达式模块
 */
public abstract class AbstractParameterConverter<T> {

    private static final MethodHandles.Lookup LOOKUP;

    private static Method M_Convert;
    private static Class<?> LMCSenderCls;

    static {
        LOOKUP = MethodHandles.lookup();
        for (Method m : AbstractParameterConverter.class.getDeclaredMethods()) {
            if ("convert".equals(m.getName())) {
                M_Convert = m;
                break;
            }
        }
    }

    private static final Map<Class<?>, AbstractParameterConverter<?>> CONVERTERS = new HashMap<>();

    public static void register(AbstractParameterConverter<?> converter) {
        CONVERTERS.put(converter.convertTo, converter);
    }

    @SuppressWarnings("unchecked")
    public static <R> AbstractParameterConverter<R> getConverter(Class<R> convertTo) {
        return (AbstractParameterConverter<R>) CONVERTERS.get(convertTo);
    }

    public static synchronized void registerLMCSenderClass(Class<?> lmcSenderCls) {
        if (LMCSenderCls != null) {
            throw new IllegalStateException();
        }
        LMCSenderCls = lmcSenderCls;
    }

    public static Class<?> getLMCSenderClass() {
        return LMCSenderCls;
    }

    private final Class<T> convertTo;

    public AbstractParameterConverter(Class<T> convertTo) {
        this.convertTo = convertTo;
    }

    public T convert(String str) {
        if (str == null) {
            return this.getDefaultValue();
        }
        try {
            T res = this.convertArg(str);
            if (res == null) {
                throw new ParamConverterFailException(str, this.convertTo);
            }
            return res;
        } catch (ParamConverterFailException e) {
            throw e;
        } catch (Throwable t) {
            throw new ParamConverterFailException(str, this.convertTo, t);
        }
    }

    public T getDefaultValue() {
        return null;
    }

    /**
     * 转换参数
     *
     * @param str 字符串参数
     * @return 转换后的参数
     */
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
        register(new AbstractParameterConverter<String>(String.class) {
            @Override
            public String convertArg(String str) {
                return str;
            }
        });
        register(new AbstractParameterConverter<Integer>(int.class) {
            @Override
            public Integer convertArg(String arg) {
                return Integer.valueOf(arg);
            }

            @Override
            public Integer getDefaultValue() {
                return -1;
            }
        });
        register(new AbstractParameterConverter<Integer>(Integer.class) {
            @Override
            public Integer convertArg(String arg) {
                return Integer.valueOf(arg);
            }

            @Override
            public Integer getDefaultValue() {
                return -1;
            }
        });
        register(new AbstractParameterConverter<Double>(double.class) {
            @Override
            public Double convertArg(String arg) {
                return Double.valueOf(arg);
            }

            @Override
            public Double getDefaultValue() {
                return Double.NaN;
            }
        });
        register(new AbstractParameterConverter<Double>(Double.class) {
            @Override
            public Double convertArg(String arg) {
                return Double.valueOf(arg);
            }

            @Override
            public Double getDefaultValue() {
                return Double.NaN;
            }
        });
        register(new AbstractParameterConverter<Boolean>(boolean.class) {
            @Override
            public Boolean convertArg(String arg) {
                arg = arg.toLowerCase();
                switch (arg) {
                    case "true":
                    case "t":
                    case "1":
                        return true;
                    case "false":
                    case "f":
                    case "0":
                        return false;
                    default:
                        return null;
                }
            }

            @Override
            public Boolean getDefaultValue() {
                return false;
            }
        });
        register(new AbstractParameterConverter<Boolean>(Boolean.class) {
            @Override
            public Boolean convertArg(String arg) {
                arg = arg.toLowerCase();
                switch (arg) {
                    case "true":
                    case "t":
                    case "1":
                        return true;
                    case "false":
                    case "f":
                    case "0":
                        return false;
                    default:
                        return null;
                }
            }

            @Override
            public Boolean getDefaultValue() {
                return false;
            }
        });
    }
}
