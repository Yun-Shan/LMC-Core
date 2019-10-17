package org.yunshanmc.lmc.core.bungee.config;

import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import org.yunshanmc.lmc.core.config.BaseLMCConfiguration;
import org.yunshanmc.lmc.core.config.LMCConfiguration;
import org.yunshanmc.lmc.core.exception.ExceptionHandler;
import org.yunshanmc.lmc.core.resource.Resource;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * @author Yun-Shan
 */
public class BungeeCordLMCConfiguration extends BaseLMCConfiguration {

    private static final Field F_DECLARED;
    private static final Field F_SELF;
    private static final YamlConfiguration YAML;
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

        YAML = (YamlConfiguration) ConfigurationProvider.getProvider(YamlConfiguration.class);
    }

    private final Configuration root;
    private Configuration config;

    public BungeeCordLMCConfiguration(Resource resource) throws IOException {
        super(resource);
        this.reload();
        this.root = this.config;
    }

    BungeeCordLMCConfiguration(Configuration root, Configuration config) {
        this.root = root;
        this.config = config;
    }

    @Override
    public String saveToString() {
        StringWriter writer = new StringWriter();
        YAML.save(this.root, writer);
        return writer.toString();
    }

    @Override
    protected void reload0() throws IOException {
        this.config = YAML.load(new InputStreamReader(this.resource.getInputStream(), StandardCharsets.UTF_8));
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
                // TODO 多级缓存不会生效，先加进TODO，不过由于应用场景较少暂不优化
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
        return new BungeeCordLMCConfiguration(this.root, section);
    }

    @Override
    public boolean isSet(@Nonnull String path) {
        return config.get(path) != null;
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

    @Nonnull
    @Override
    public String getString(@Nonnull String path, @Nonnull String def) {
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

    @Override
    public boolean getBoolean(@Nonnull String path) {
        return config.getBoolean(path);
    }

    @Override
    public boolean getBoolean(@Nonnull String path, boolean def) {
        return config.getBoolean(path, def);
    }
}
