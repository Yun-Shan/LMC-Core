package org.yunshanmc.lmc.core.bukkit.config;

import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;
import org.yunshanmc.lmc.core.config.LMCConfiguration;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * @author YunShan
 */
public class BukkitLMCConfiguration implements LMCConfiguration {

    private ConfigurationSection config;

    public BukkitLMCConfiguration(ConfigurationSection config) {
        this.config = config;
    }

    @Override
    public void addDefaults(@Nonnull LMCConfiguration defaults) {
        Objects.requireNonNull(defaults, "Defaults may not be null");
        if (!(defaults instanceof BukkitLMCConfiguration)) {
            throw new UnsupportedOperationException("Only Support " + BukkitLMCConfiguration.class.getName() + "!");
        }
        if (!(this.config instanceof Configuration)) {
            Map<String, Object> map = this.config.getValues(true);
            MemoryConfiguration yaml = new MemoryConfiguration();
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                yaml.set(entry.getKey(), entry.getValue());
            }
            this.config = yaml;
        }
        ((Configuration) this.config).addDefaults(((BukkitLMCConfiguration) defaults).config.getValues(true));
    }

    @Override
    public Set<String> getKeys() {
        return this.config.getKeys(false);
    }

    @Override
    public LMCConfiguration getSection(@Nonnull String path) {
        ConfigurationSection section = this.config.getConfigurationSection(path);
        return new BukkitLMCConfiguration(section);
    }

    @Nullable
    @Override
    public Object get(@Nonnull String path) {
        return config.get(path);
    }

    @SuppressWarnings("unchecked")
    @Nullable
    @Override
    public <T> T get(@Nonnull String path, T def) {
        return (T) config.get(path, def);
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
