package org.yunshanmc.lmc.core.command;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.yunshanmc.lmc.core.utils.PlatformUtils;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

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
            if (res == null) throw new ParamConverterFailException(str, this.convertTo);
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
        register(new ParameterConverter<Double>(double.class) {
            @Override
            public Double convertArg(String arg) {
                return Double.valueOf(arg);
            }

            @Override
            public Double getDefaultValue() {
                return Double.NaN;
            }
        });
        register(new ParameterConverter<Boolean>(boolean.class) {
            @Override
            public Boolean convertArg(String arg) {
                arg = arg.toLowerCase();
                switch (arg) {
                    case "true":
                    case "t":
                    case "1": return true;

                    case "false":
                    case "f":
                    case "0": return false;

                    default:
                        return null;
                }
            }

            @Override
            public Boolean getDefaultValue() {
                return false;
            }
        });

        if (PlatformUtils.isBukkit()) {
            register(new ParameterConverter<Player>(Player.class) {
                @Override
                public Player convertArg(String arg) {
                    return Bukkit.getPlayerExact(arg);
                }

                @Override
                public Player getDefaultValue() {
                    return null;
                }
            });
            register(new ParameterConverter<OfflinePlayer>(OfflinePlayer.class) {
                @Override
                public OfflinePlayer convertArg(String arg) {
                    OfflinePlayer p = Bukkit.getOfflinePlayer(arg);
                    return p.hasPlayedBefore() ? p : null;
                }

                @Override
                public OfflinePlayer getDefaultValue() {
                    return null;
                }
            });
        }
        if (PlatformUtils.isBungeeCord()) {
            register(new ParameterConverter<ProxiedPlayer>(ProxiedPlayer.class) {
                @Override
                public ProxiedPlayer convertArg(String arg) {
                    return ProxyServer.getInstance().getPlayer(arg);
                }

                @Override
                public ProxiedPlayer getDefaultValue() {
                    return null;
                }
            });
        }
    }
}
