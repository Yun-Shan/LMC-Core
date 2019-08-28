package org.yunshanmc.lmc.core.bungee.config;

import net.md_5.bungee.config.Configuration;
import org.yunshanmc.lmc.core.config.LMCConfiguration;
import org.yunshanmc.lmc.core.exception.ExceptionHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * @author YunShan
 */
public class BungeeCordLMCConfiguration implements LMCConfiguration {

    private static final Field F_DECLARED;
    private static final Field F_SELF;

    static {
        Field fDeclared = null;
        Field fSelf = null;
        try {
            fDeclared = Configuration.class.getDeclaredField("defaults");
            fDeclared.setAccessible(true);

            Field modifiers = Field.class.getDeclaredField("modifiers");
            modifiers.setAccessible(true);
            modifiers.setInt(fDeclared, Modifier.PUBLIC);


            fSelf = Configuration.class.getDeclaredField("self");
            fSelf.setAccessible(true);
        } catch (ReflectiveOperationException e) {
            ExceptionHandler.handle(e);
        }
        F_DECLARED = fDeclared;
        F_SELF = fSelf;
    }

    private Configuration config;

    public BungeeCordLMCConfiguration(Configuration config) {
        this.config = config;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void addDefaults(@Nonnull LMCConfiguration defaults) {
        Objects.requireNonNull(defaults, "Defaults may not be null");
        if (!(defaults instanceof BungeeCordLMCConfiguration)) {
            throw new UnsupportedOperationException("Only Support " + BungeeCordLMCConfiguration.class.getName() + "!");
        }
        try {
            Object obj = F_DECLARED.get(this.config);
            if (obj == null) {
                F_DECLARED.set(this.config, defaults);
            } else {
                // 多级缓存不会生效，不过由于应用场景较少暂不优化
                Map<String, Object> map = (Map<String, Object>) F_SELF.get(defaults);
                Configuration rawDefaults = (Configuration) obj;
                for (Map.Entry<String, Object> entry : map.entrySet()) {
                    rawDefaults.set(entry.getKey(), entry.getValue());
                }
            }
        } catch (ReflectiveOperationException e) {
            ExceptionHandler.handle(e);
        }
    }

    @Override
    public Set<String> getKeys() {
        return new HashSet<>(this.config.getKeys());
    }

    @Override
    public LMCConfiguration getSection(@Nonnull String path) {
        Configuration section = this.config.getSection(path);
        return new BungeeCordLMCConfiguration(section);
    }

    @Nullable
    @Override
    public Object get(@Nonnull String path) {
        return config.get(path);
    }

    @Nullable
    @Override
    public <T> T get(@Nonnull String path, T def) {
        return config.get(path, def);
    }

    @Nullable
    @Override
    public String getString(@Nonnull String path) {
        return config.getString(path);
    }

    @Nullable
    @Override
    public String getString(@Nonnull String path, String def) {
        return config.getString(path, def);
    }

    @Override
    public int getInt(@Nonnull String path) {
        return config.getInt(path);
    }

    @Override
    public int getInt(@Nonnull String path, int def) {
        return config.getInt(path, def);
    }
}
